//   RTree.java
//   Based on Rtree.java by Java Spatial Index Library

package util.rtree;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;


public class RTree {
    private static final String version = "1.0b2p1";
  
    private int maxNodeEntries;
    protected int minNodeEntries;
   
    // internal consistency checking - set to true if debugging tree corruption
    protected final static boolean INTERNAL_CONSISTENCY_CHECKING = false;
  
    // used to mark the status of entries during a node split
    protected final static byte ENTRY_STATUS_ASSIGNED = 0;
    protected final static byte ENTRY_STATUS_UNASSIGNED = 1;
    protected byte[] entryStatus = null;
    
    // stacks used to store nodeId and entry index of each node
    // from the root down to the leaf. Enables fast lookup
    // of nodes when a split is propagated up the tree.
    private Stack<Integer> parents = new Stack<Integer>();
    private Stack<Integer> parentsEntry = new Stack<Integer>();
  
    // initialisation
    private int treeHeight = 1; // leaves are always level 1
    private int rootNodeId = 1;
    protected int size = 0;
  
    // Enables creation of new nodes
    private int highestUsedNodeId = rootNodeId;
  
    // Deleted node objects are retained in the nodeMap,
    // so that they can be reused. Store the IDs of nodes
    // which can be reused.
    private Stack<Integer> deletedNodeIds = new Stack<Integer>();    

    /**
    * Used by delete(). Ensures that all nodes from the passed node
    * up to the root have the minimum number of entries.
    *
    * Note that the parent and parentEntry stacks are expected to
    * contain the nodeIds of all parents up to the root.
    */
    private Rectangle oldRectangle;

    protected StorageManager storageManager;    
  
    private String outputPath;    

    /**
     * Constructor. Use init() method to initialize parameters of the RTree.
     */
    public RTree(int dimensions, StorageManager storageManager, String outputPath) {
        this.storageManager = storageManager;
        this.outputPath = outputPath;
        this.oldRectangle = new Rectangle(dimensions);
    }

    public StorageManager getStorageManager(){
        return this.storageManager;
    }


    private void diskInit(File diskStorageHeadFile) throws RTreeException{
        DataInputStream input;
        try {
            //Load data from head file
            input = new DataInputStream(new FileInputStream(diskStorageHeadFile));
            rootNodeId = input.readInt();

            maxNodeEntries = input.readInt();

            minNodeEntries = input.readInt();

            size = input.readInt();

            treeHeight = input.readInt();

            highestUsedNodeId = input.readInt();

            entryStatus = new byte[getMaxNodeEntries()];
            input.readFully(entryStatus);

            input.close();
        } catch (FileNotFoundException ex) {
            throw new RTreeException(ex);
        } catch(IOException ex){
            throw new RTreeException(ex);
        }
    }

    private void memoryInit(int minNodeEntries, int maxNodeEntries) throws RTreeException{
        this.minNodeEntries = minNodeEntries;
        this.maxNodeEntries = maxNodeEntries;
        
        // Obviously a node with less than 2 entries cannot be split.
        // The node splitting algorithm will work with only 2 entries
        // per node, but will be inefficient.
        if (maxNodeEntries < 2) {
            throw new RTreeException("MinNodeEntries must be between 1 and MaxNodeEntries / 2");
        }

        // The MinNodeEntries must be less than or equal to (int) (MaxNodeEntries / 2)
        if (minNodeEntries < 1 || minNodeEntries > maxNodeEntries / 2) {
            throw new RTreeException("MinNodeEntries must be between 1 and MaxNodeEntries / 2");
        }

        
        Node root = createNode(rootNodeId, 1);

        storageManager.store(rootNodeId, root);

        entryStatus = new byte[getMaxNodeEntries()];

    }
    /**
    * <p>Initialize implementation dependent properties of the RTree.
    * Currently implemented properties are:
    * <ul>
    * <li>MaxNodeEntries</li> This specifies the maximum number of entries
    * in a node. The default value is 10, which is used if the property is
    * not specified, or is less than 2.
    * <li>MinNodeEntries</li> This specifies the minimum number of entries
    * in a node. The default value is half of the MaxNodeEntries value (rounded
    * down), which is used if the property is not specified or is less than 1.
    * </ul></p>
    *
    *
    * @param props The set of properties used to initialize the spatial index.
    */
    public void init(String storage, int minNodeEntries, int maxNodeEntries) throws RTreeException{

        if(storage.equals("DiskStorage")){
            String dir = outputPath.substring(0,outputPath.lastIndexOf("/"));
            if(dir.length()>0){
                File fileDir = new File(dir);
                if(!fileDir.exists()) fileDir.mkdirs();
            }

            File file = new File(outputPath);

            if(file.exists()){
                diskInit(file);
            }else{
                memoryInit(minNodeEntries, maxNodeEntries);
            }

        }else{
            memoryInit(minNodeEntries, maxNodeEntries);
        }
        
        //System.out.println("init() " + " MaxNodeEntries = " + maxNodeEntries + ", MinNodeEntries = " + minNodeEntries);
    }

    public void close() throws RTreeException{
        //System.out.println("Closing "+this.getClass().getSimpleName()+", StorageManager >>>>>"+storageManager.info());
        storageManager.close();

        this.save();
    }

    public void save() throws RTreeException{
        try {
            if(storageManager instanceof DiskStorageManager){
                File file = new File(outputPath);
                if(!file.exists()){
                    file.createNewFile();
                }
                //Save DiskStorage infor in the head file
                DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
                
                output.writeInt(rootNodeId);

                output.writeInt(getMaxNodeEntries());

                output.writeInt(minNodeEntries);

                output.writeInt(size);

                output.writeInt(treeHeight);

                output.writeInt(highestUsedNodeId);

                output.write(entryStatus);

                output.close();
            }
        } catch (IOException ex) {
            throw new RTreeException(ex);
        }
    }

    public int getNumLeafEntries() throws RTreeException{
        int count=0;
        this.reset();
        while(this.hasNextLeaf()){
            this.nextLeaf();
            count++;
        }
        return count;
    }


    /**
     *##############################  NAVIGATION  ##############################
     */
    class Item{
        int nodeId;
        int nodeLevel;
        public Item(int nodeId, int nodeLevel){
            this.nodeId = nodeId;
            this.nodeLevel = nodeLevel;
        }
    }
    private Queue<Item> queue = new LinkedList<Item>();
    
    public void reset() throws RTreeException{
        queue.clear();
        queue.add(new Item(rootNodeId,treeHeight));
    }

    public boolean hasNextLevelOneEntry(){
        return hasNextEntry(2);
    }
    
    public Node nextLevelOneEntry() throws RTreeException{
        return nextEntry(2);
    }

    public boolean hasNextLeaf(){
        return hasNextEntry(1);
    }
    
    public Node nextLeaf() throws RTreeException{
        return nextEntry(1);
    }

    private boolean hasNextEntry(int level){
        return (!queue.isEmpty()) && (queue.peek().nodeLevel>=level);
    }

    private Node nextEntry(int level) throws RTreeException{
        Node n = storageManager.getNode(queue.poll().nodeId);

        while(n.getLevel()!=level) {
            for(int i=0;i<n.entryCount;i++){
                queue.add(new Item(n.entries[i].getID(), n.getLevel()-1));
            }

            n = storageManager.getNode(queue.poll().nodeId);            
        }        
        return n;
    }

    /**
     *##############################  END OF NAVIGATION   ##########################
     */

    protected Node createNode(int id, int level){
        return new Node(id, level, getMaxNodeEntries());
    }

    /**
    * @return The ID of the rectangle to add to the spatial index.
    */
    public void add(int id, Rectangle r) throws RTreeException{
        add(new Entry(id, r.copy()), 1);
    
        size++;        
    }
  
    /**
    * Adds a new entry at a specified level in the tree
    */
    protected void add(Entry entry, int level) throws RTreeException{
        // I1 [Find position for new record] Invoke ChooseLeaf to select a
        // leaf node L in which to place r
        Node n = chooseLeaf(entry.getMBR(), level);
        Node newLeaf = null;
    
        // I2 [Add record to leaf node] If L has room for another entry,
        // install E. Otherwise invoke SplitNode to obtain L and LL containing
        // E and all the old entries of L
        if (n.entryCount < getMaxNodeEntries()) {
            n.addEntry(entry);
        } else {
            newLeaf = splitNode(n, entry);
        }

        storageManager.store(n.getID(), n);

        // I3 [Propagate changes upwards] Invoke AdjustTree on L, also passing LL
        // if a split was performed
        Node newNode = adjustTree(n, newLeaf);

        // I4 [Grow tree taller] If node split propagation caused the root to
        // split, create a new root whose children are the two resulting nodes.
        if (newNode != null) {
            int oldRootNodeId = rootNodeId;
            Node oldRoot = storageManager.getNode(oldRootNodeId);

            rootNodeId = getNextNodeId();
            treeHeight++;
            Node root = createNode(rootNodeId, treeHeight);
            root.addEntry(newNode);
            root.addEntry(oldRoot);
            storageManager.store(rootNodeId, root);
            storageManager.store(newNode.getID(), newNode);
        }

        if (INTERNAL_CONSISTENCY_CHECKING) {
            checkConsistency(rootNodeId, treeHeight, null);
        }        
  } 
  
  /**
   * @see com.infomatiq.jsi.SpatialIndex#delete(Rectangle, int)
   */
    public boolean delete(Rectangle r, int id) throws RTreeException{
       // FindLeaf algorithm inlined here. Note the "official" algorithm
       // searches all overlapping entries. This seems inefficient to me,
       // as an entry is only worth searching if it contains (NOT overlaps)
       // the rectangle we are searching for.
       //
       // Also the algorithm has been changed so that it is not recursive.

        // FL1 [Search subtrees] If root is not a leaf, check each entry
        // to determine if it contains r. For each entry found, invoke
        // findLeaf on the node pointed to by the entry, until r is found or
        // all entries have been checked.
        parents.clear();
        parents.push(rootNodeId);

        parentsEntry.clear();
        parentsEntry.push(-1);
        Node n = null;
        int foundIndex = -1;  // index of entry to be deleted in leaf

        while (foundIndex == -1 && parents.size() > 0) {
            n = storageManager.getNode(parents.peek());
            int startIndex = parentsEntry.peek() + 1;

            if (!n.isLeaf()) {
                //System.out.println("searching node " + n.nodeId + ", from index " + startIndex);
                boolean contains = false;
                for (int i = startIndex; i < n.entryCount; i++) {
                    if (n.entries[i].getMBR().contains(r)) {
                        parents.push(n.entries[i].getID());
                        parentsEntry.pop();
                        parentsEntry.push(i); // this becomes the start index when the child has been searched
                        parentsEntry.push(-1);
                        contains = true;
                        break; // ie go to next iteration of while()
                    }
                }
                if (contains) {
                    continue;
                }
            } else {
                foundIndex = n.findEntry(new Entry(id, r));
            }

            parents.pop();
            parentsEntry.pop();
        } // while not found

        if (foundIndex != -1) {
            n.deleteEntry(foundIndex, minNodeEntries);
            storageManager.store(n.getID(), n);
            condenseTree(n);
            size--;
        }

        // shrink the tree if possible (i.e. if root node has exactly one entry,and that
        // entry is not a leaf node, delete the root (it's entry becomes the new root)
        Node root = storageManager.getNode(rootNodeId);
        while (root.entryCount == 1 && treeHeight > 1){
            root.entryCount = 0;
            rootNodeId = root.entries[0].getID();
            treeHeight--;
            root = storageManager.getNode(rootNodeId);
        }

        return (foundIndex != -1);
    }
  
    /**
    * Finds all entries that are nearest to the passed point.
    *
    * @param p The point for which this method finds the
    * nearest neighbours.
    *
    * @param distance The furthest distance away from the rectangle
    * to search. Rectangles further than this will not be found.
    *
    * This should be as small as possible to minimise
    * the search time.
    *
    * Use double.POSITIVE_INFINITY to guarantee that the nearest rectangle is found,
    * no matter how far away, although this will slow down the algorithm.
    */
    public Iterator<Entry> nearest(Point p, double furthestDistance) throws RTreeException{
        Node rootNode = storageManager.getNode(rootNodeId);
   
        return nearest(p, rootNode, furthestDistance);
   }
    /**
    * Recursively searches the tree for the nearest entry. Other queries
    * call execute() on an IntProcedure when a matching entry is found;
    * however nearest() must store the entry Ids as it searches the tree,
    * in case a nearer entry is found.
    * Uses the member variable nearestIds to store the nearest
    * entry IDs.
    *
    * [x] TODO rewrite this to be non-recursive?
    */
    private Iterator<Entry> nearest(Point p, Node n, double nearestDistance) throws RTreeException{
        ArrayList<Entry> nearestEntries = new ArrayList<Entry>();
        nearest(p, n, nearestDistance, nearestEntries);
        return nearestEntries.iterator();
    }
    
    private double nearest(Point p, Node n, double nearestDistance, ArrayList<Entry> nearestEntries) throws RTreeException{
        for (int i = 0; i < n.entryCount; i++) {
            double tempDistance = n.entries[i].getMBR().distance(p);
            if (n.isLeaf()) { // for leaves, the distance is an actual nearest distance
                if (tempDistance < nearestDistance) {
                    nearestDistance = tempDistance;
                    nearestEntries.clear();
                }
                if (tempDistance <= nearestDistance) {
                    nearestEntries.add(n.entries[i]);
                }
            } else { // for index nodes, only go into them if they potentially could have
                // a rectangle nearer than actualNearest
                if (tempDistance <= nearestDistance) {
                    // search the child node
                    nearestDistance = nearest(p,  storageManager.getNode(n.entries[i].getID()), nearestDistance, nearestEntries);
                }
            }
        }
        return nearestDistance;
    }

   
  /**
   * Finds all entries that intersect the passed rectangle.
   *
   * @param  r The rectangle for which this method finds
   *           intersecting rectangles.
   *
   * @param ip The IntProcedure whose execute() method is is called
   *           for each intersecting rectangle.
   */
    public Iterator<Entry> intersects(Rectangle r) throws RTreeException{
        Node rootNode = storageManager.getNode(rootNodeId);
        ArrayList<Entry> entries = new ArrayList<Entry>();
        intersects(r, rootNode, entries);
        return entries.iterator();
    }

   /**
    * Recursively searches the tree for all intersecting entries.
    * Immediately calls execute() on the passed IntProcedure when
    * a matching entry is found.
    *
    * [x] TODO rewrite this to be non-recursive? Make sure it
    * doesn't slow it down.
    */
    private void intersects(Rectangle r, Node n, ArrayList<Entry> ids) throws RTreeException{
        for (int i = 0; i < n.entryCount; i++) {
            if (r.intersects(n.entries[i].getMBR())) {
                if (n.isLeaf()) {
                    ids.add(n.entries[i]);
                } else {
                    Node childNode = storageManager.getNode(n.entries[i].getID());
                    intersects(r, childNode, ids);
                }
            }
        }
    }


   /**
    * Finds all entries contained by the passed rectangle.
    *
    * @param r The rectangle for which this method finds
    *           contained rectangles.
    *
    * @param v The visitor whose visit() method is is called
    *           for each contained rectangle.
    */
    public Iterator<Entry> contains(Rectangle r) throws RTreeException{
        // find all rectangles in the tree that are contained by the passed rectangle
        // written to be non-recursive (should model other searches on this?)

        ArrayList<Entry> list = new ArrayList<Entry>();
        parents.clear();
        parents.push(rootNodeId);

        parentsEntry.clear();
        parentsEntry.push(-1);

        // TODO: possible shortcut here - could test for intersection with the
        // MBR of the root node. If no intersection, return immediately.

        while (parents.size() > 0) {
            Node n = storageManager.getNode(parents.peek());
            int startIndex = parentsEntry.peek() + 1;

            if (!n.isLeaf()) {
                // go through every entry in the index node to check
                // if it intersects the passed rectangle. If so, it
                // could contain entries that are contained.
                boolean intersects = false;
                for (int i = startIndex; i < n.entryCount; i++) {
                    if (r.intersects(n.entries[i].getMBR())) {
                        parents.push(n.entries[i].getID());
                        parentsEntry.pop();
                        parentsEntry.push(i); // this becomes the start index when the child has been searched
                        parentsEntry.push(-1);
                        intersects = true;
                        break; // ie go to next iteration of while()
                    }
                }
                if (intersects) {
                    continue;
                }
            } else {
                // go through every entry in the leaf to check if
                // it is contained by the passed rectangle
                for (int i = 0; i < n.entryCount; i++) {
                    if (r.contains(n.entries[i].getMBR())) {
                        list.add(n.entries[i]);
                    }
                }
            }

            parents.pop();
            parentsEntry.pop();
        }
        return list.iterator();
    }

   /**
    * Returns the number of entries in the spatial index
    */
    public int size() {
        return size;
    }

    /**
     * Returns the bounds of all the entries in the spatial index,
     * or null if there are no entries.
     */
    public Rectangle getBounds() throws RTreeException{
        Rectangle bounds = null;

        Node n = storageManager.getNode(getRootNodeId());
        if (n != null && n.getMBR() != null) {
            bounds = n.getMBR().copy();
        }
        return bounds;
    }
    
   /**
    * Returns a string identifying the type of
    * spatial index, and the version number,
    * eg "SimpleIndex-0.1"
    */
    public String getVersion() {
        return "RTree-" + version;
    }
  
    /**
    * Get the next available node ID. Reuse deleted node IDs if
    * possible
    */
    protected int getNextNodeId() {
        int nextNodeId = 0;
        if (deletedNodeIds.size() > 0) {
            nextNodeId = deletedNodeIds.pop();
        } else {
            nextNodeId = 1 + highestUsedNodeId++;
        }
        return nextNodeId;
    }

    /**
    * Get the highest used node ID
    */
    public int getHighestUsedNodeId() {
        return highestUsedNodeId;
    }

    /**
    * Get the root node ID
    */
    public int getRootNodeId() {
        return rootNodeId;
    }

    /**
    * Split a node. Algorithm is taken pretty much verbatim from
    * Guttman's original paper.
    *
    * @return new node object.
    */
    protected Node splitNode(Node n, Entry entry) throws RTreeException{
        // [Pick first entry for each group] Apply algorithm pickSeeds to
        // choose two entries to be the first elements of the groups. Assign
        // each to a group.

        Arrays.fill(entryStatus, ENTRY_STATUS_UNASSIGNED);

        Node newNode = null;
        newNode = createNode(getNextNodeId(), n.level);

        pickSeeds(n, entry, newNode); // this also sets the entryCount to 1

        // [Check if done] If all entries have been assigned, stop. If one
        // group has so few entries that all the rest must be assigned to it in
        // order for it to have the minimum number m, assign them and stop.
        while (n.entryCount + newNode.entryCount < getMaxNodeEntries() + 1) {
            if (getMaxNodeEntries() + 1 - newNode.entryCount == minNodeEntries) {
                // assign all remaining entries to original node
                for (int i = 0; i < getMaxNodeEntries(); i++) {
                    if (entryStatus[i] == ENTRY_STATUS_UNASSIGNED) {
                        entryStatus[i] = ENTRY_STATUS_ASSIGNED;
                        n.mbr.add(n.entries[i].getMBR());//update only the MBR
                        n.entryCount++;
                    }
                }
                break;
            }
            if (getMaxNodeEntries() + 1 - n.entryCount == minNodeEntries) {
                // assign all remaining entries to new node
                for (int i = 0; i < getMaxNodeEntries(); i++) {
                    if (entryStatus[i] == ENTRY_STATUS_UNASSIGNED) {
                        entryStatus[i] = ENTRY_STATUS_ASSIGNED;
                        newNode.addEntry(n.entries[i]);
                        n.entries[i] = null;
                    }
                }
                break;
            }

            // [Select entry to assign] Invoke algorithm pickNext to choose the
            // next entry to assign. Add it to the group whose covering rectangle
            // will have to be enlarged least to accommodate it. Resolve ties
            // by adding the entry to the group with smaller area, then to the
            // the one with fewer entries, then to either. Repeat from S2
            pickNext(n, newNode);
        }

        n.reorganize(this);

        // check that the MBR stored for each node is correct.
        if (INTERNAL_CONSISTENCY_CHECKING) {
            if (!n.mbr.equals(calculateMBR(n))) {
                throw new RTreeException("Error: splitNode old node MBR wrong");
            }

            if (!newNode.mbr.equals(calculateMBR(newNode))) {
                throw new RTreeException("Error: splitNode new node MBR wrong");
            }
        }
        
        storageManager.store(n.getID(), n);
        storageManager.store(newNode.getID(), newNode);
        return newNode;
    }

    /**
    * Pick the seeds used to split a node.
    * Select two entries to be the first elements of the groups
    */
    protected void pickSeeds(Node n, Entry newEntry, Node newNode) throws RTreeException{
        int s1=0;
        int s2=0; //stores an index
        double maxWaste=0;
        double waste = 0;

        double areaE1 = newEntry.getMBR().area();
        double areaE2 =0;
        for (int i = 0; i < n.getEntryCount(); i++) {
            areaE2 =n.getEntry(i).getMBR().area();
            waste = newEntry.getMBR().enlargement(n.getEntry(i).getMBR()) - areaE1 - areaE2;
            if(waste > maxWaste){
                maxWaste = waste;
                s1 = -1; //Indicates that newEntry is the seed
                s2 = i;
            }
        }

        for (int i = 0; i < n.getEntryCount(); i++) {
            areaE1 = n.getEntry(i).getMBR().area();
            for (int v = i+1; v < n.getEntryCount(); v++) {
                areaE2 =n.getEntry(v).getMBR().area();
                waste = n.getEntry(i).getMBR().enlargement(n.getEntry(v).getMBR()) - areaE1 - areaE2;
                if(waste > maxWaste){
                    maxWaste = waste;
                    s1 = i;
                    s2 = v;
                }
            }
        }


        // newEntry is the seed for the new node.
        if (s1 == -1) {
            newNode.addEntry(newEntry);
        } else {
            newNode.addEntry(n.entries[s1]);
            n.entries[s1] = null;

            // move the new rectangle into the space vacated by the seed for the new node
            //The new newEntry will be picked next because it slot is UNSSIGNED.
            n.entries[s1] = newEntry.copy();
        }

        entryStatus[s2] = ENTRY_STATUS_ASSIGNED;
        n.entryCount = 1;
        n.mbr.set(n.entries[s2].getMBR().min, n.entries[s2].getMBR().max);
    }

    /**
    * Pick the next entry to be assigned to a group during a node split.
    *
    * [Determine cost of putting each entry in each group] For each
    * entry not yet in a group, calculate the area increase required
    * in the covering rectangles of each group
    */
    protected int pickNext(Node n, Node newNode) throws RTreeException{
        double maxDifference = Double.NEGATIVE_INFINITY;
        int next = 0;
        int nextGroup = 0;

        maxDifference = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < getMaxNodeEntries(); i++) {
            if (entryStatus[i] == ENTRY_STATUS_UNASSIGNED) {

                if (n.entries[i] == null) {
                    throw new RTreeException("Error: Node " + n.getID() + ", entry " + i + " is null");
                }

                double nIncrease = n.mbr.enlargement(n.entries[i].getMBR());
                double newNodeIncrease = newNode.mbr.enlargement(n.entries[i].getMBR());
                double difference = Math.abs(nIncrease - newNodeIncrease);

                if (difference > maxDifference) {
                    next = i;

                    if (nIncrease < newNodeIncrease) {
                        nextGroup = 0;
                    } else if (newNodeIncrease < nIncrease) {
                        nextGroup = 1;
                    } else if (n.mbr.area() < newNode.mbr.area()) {
                        nextGroup = 0;
                    } else if (newNode.mbr.area() < n.mbr.area()) {
                        nextGroup = 1;
                    } else if (newNode.entryCount < getMaxNodeEntries() / 2) {
                        nextGroup = 0;
                    } else {
                        nextGroup = 1;
                    }
                    maxDifference = difference;
                }
            }
        }
    
        entryStatus[next] = ENTRY_STATUS_ASSIGNED;
      
        if (nextGroup == 0) {
            n.mbr.add(n.entries[next].getMBR());//update the mbr of n
            n.entryCount++;
        } else {
            // move to new node.
            newNode.addEntry(n.entries[next]);
            n.entries[next] = null;
        }

        return next;
    }
 

    private void condenseTree(Node l) throws RTreeException{
        // CT1 [Initialize] Set n=l. Set the list of eliminated
        // nodes to be empty.
        Node n = l;
        Node parent = null;
        int parentEntry = 0;

        Stack<Integer> eliminatedNodeIds = new Stack<Integer>();

        // CT2 [Find parent entry] If N is the root, go to CT6. Otherwise
        // let P be the parent of N, and let En be N's entry in P
        while (n.level != treeHeight) {
            parent = storageManager.getNode(parents.pop());
            parentEntry = parentsEntry.pop();
      
            // CT3 [Eliminiate under-full node] If N has too few entries,
            // delete En from P and add N to the list of eliminated nodes
            if (n.entryCount < minNodeEntries) {
                parent.deleteEntry(parentEntry, minNodeEntries);
                eliminatedNodeIds.push(n.getID());
            } else {
                // CT4 [Adjust covering rectangle] If N has not been eliminated,
                // adjust EnI to tightly contain all entries in N
                if (!n.mbr.equals(parent.entries[parentEntry].getMBR())) {
                    oldRectangle.set(parent.entries[parentEntry].getMBR().min, parent.entries[parentEntry].getMBR().max);
                    parent.entries[parentEntry].getMBR().set(n.mbr.min, n.mbr.max);
                    parent.recalculateMBR(oldRectangle);
                }
            }
            // CT5 [Move up one level in tree] Set N=P and repeat from CT2
            n = parent;
            this.storageManager.store(n.getID(), n);
        }
    
        // CT6 [Reinsert orphaned entries] Reinsert all entries of nodes in set Q.
        // Entries from eliminated leaf nodes are reinserted in tree leaves as in
        // Insert(), but entries from higher level nodes must be placed higher in
        // the tree, so that leaves of their dependent subtrees will be on the same
        // level as leaves of the main tree
        while (eliminatedNodeIds.size() > 0) {
            Node e = storageManager.getNode(eliminatedNodeIds.pop());
            for (int j = 0; j < e.entryCount; j++) {
                add(e.entries[j], e.level);
                e.entries[j] = null;
            }
            e.entryCount = 0;
            deletedNodeIds.push(e.getID());
        }
    }

    /**
     * @return the maxNodeEntries
     */
    public int getMaxNodeEntries() {
        return maxNodeEntries;
    }
    /**
    *  Used by add(). Chooses a leaf to add the rectangle to.
    */
    private Node chooseLeaf(Rectangle r, int level) throws RTreeException{
        // CL1 [Initialize] Set N to be the root node
        Node n = storageManager.getNode(rootNodeId);
        parents.clear();
        parentsEntry.clear();


        // CL2 [Leaf check] If N is a leaf, return N
        while (true) {
            if (n == null) {
                throw new RTreeException("Could not get root node (" + rootNodeId + ")");
            }

            
            if (n.level == level) {
                return n;
            }

            // CL3 [Choose subtree] If N is not at the desired level, let F be the entry in N
            // whose rectangle FI needs least enlargement to include EI. Resolve
            // ties by choosing the entry with the rectangle of smaller area.
            double indexEnlargment = n.getEntry(0).getMBR().enlargement(r);
            double indexArea=0;
            int index = 0; // index of rectangle in subtree

            double tempEnlargement=0;
            double tempArea = 0;
            for (int i = 1; i < n.entryCount; i++) {
                Entry tempEntry = n.getEntry(i);
                tempEnlargement = tempEntry.getMBR().enlargement(r);
                tempArea = tempEntry.getMBR().area() ;
                if ((tempEnlargement < indexEnlargment) ||
                    ((tempEnlargement == indexEnlargment) && (tempArea< indexArea))) {
                    index = i;
                    indexArea = tempArea;
                    indexEnlargment = tempEnlargement;
                }
            }

            parents.push(n.getID());
            parentsEntry.push(index);

            // CL4 [Descend until a leaf is reached] Set N to be the child node
            // pointed to by Fp and repeat from CL2
            n = storageManager.getNode(n.entries[index].getID());
        }
    }
  
    /**
    * Ascend from a leaf node L to the root, adjusting covering rectangles and
    * propagating node splits as necessary.
    */
    private Node adjustTree(Node n, Node nn) throws RTreeException{
        // AT1 [Initialize] Set N=L. If L was split previously, set NN to be
        // the resulting second node.
    
        // AT2 [Check if done] If N is the root, stop
        while (n.level != treeHeight) {
            // AT3 [Adjust covering rectangle in parent entry] Let P be the parent
            // node of N, and let En be N's entry in P. Adjust EnI so that it tightly
            // encloses all entry rectangles in N.
            Node parent = storageManager.getNode(parents.pop());
            int entry = parentsEntry.pop();
      
            if (parent.entries[entry].getID() != n.getID()) {
                throw new RTreeException("Error: entry " + entry + " in node " +
                parent.getID() + " should point to node " +
                n.getID() + "; actually points to node " + parent.entries[entry].getID());
            }
      
            if (!parent.entries[entry].getMBR().equals(n.mbr)) {
                parent.entries[entry].getMBR().set(n.mbr.min, n.mbr.max);
                parent.mbr.set(parent.entries[0].getMBR().min, parent.entries[0].getMBR().max);
                for (int i = 1; i < parent.entryCount; i++) {
                    parent.mbr.add(parent.entries[i].getMBR());
                }
            }
            if(parent instanceof AggField){
                  ((AggField)parent.entries[entry]).setAggValue(((AggField)n).getAggValue());
            }

      
            // AT4 [Propagate node split upward] If N has a partner NN resulting from
            // an earlier split, create a new entry Enn with Ennp pointing to NN and
            // Enni enclosing all rectangles in NN. Add Enn to P if there is room.
            // Otherwise, invoke splitNode to produce P and PP containing Enn and
            // all P's old entries.
            Node newNode = null;
            if (nn != null) {
                if (parent.entryCount < getMaxNodeEntries()) {
                    parent.addEntry(nn);
                } else {
                    newNode = splitNode(parent, nn.copy());
                }
            }

            this.storageManager.store(parent.getID(), parent);
            // AT5 [Move up to next level] Set N = P and set NN = PP if a split
            // occurred. Repeat from AT2
            n = parent;
            nn = newNode;

            parent = null;
            newNode = null;
        }    
        return nn;
    }
  
    /**
    * Check the consistency of the tree.
    */
    private void checkConsistency(int nodeId, int expectedLevel, Rectangle expectedMBR) throws RTreeException{
        // go through the tree, and check that the internal data structures of
        // the tree are not corrupted.
        Node n = storageManager.getNode(nodeId);
    
        if (n == null) {
            throw new RTreeException("Error: Could not read node " + nodeId);
        }
    
        if (n.level != expectedLevel) {
            throw new RTreeException("Error: Node " + nodeId + ", expected level " + expectedLevel + ", actual level " + n.level);
        }
    
        Rectangle calculatedMBR = calculateMBR(n);
    
        if (!n.mbr.equals(calculatedMBR)) {
            throw new RTreeException("Error: Node " + nodeId + ", calculated MBR does not equal stored MBR");
        }
    
        if (expectedMBR != null && !n.mbr.equals(expectedMBR)) {
            throw new RTreeException("Error: Node " + nodeId + ", expected MBR (from parent) does not equal stored MBR");
        }
    
        // Check for corruption where a parent entry is the same object as the child MBR
        if (expectedMBR != null && n.mbr.equals(expectedMBR)) {
            throw new RTreeException("Error: Node " + nodeId + " MBR using same rectangle object as parent's entry");
        }
    
        for (int i = 0; i < n.entryCount; i++) {
            if (n.entries[i] == null) {
                throw new RTreeException("Error: Node " + nodeId + ", Entry " + i + " is null");
            }
      
            if (n.level > 1) { // if not a leaf
                checkConsistency(n.entries[i].getID(), n.level - 1, n.entries[i].getMBR());
            }
        }
    }
  
    /**
    * Given a node object, calculate the node MBR from it's entries.
    * Used in consistency checking
    */
    protected Rectangle calculateMBR(Node n) {
        Rectangle mbr = new Rectangle(n.entries[0].getMBR().min, n.entries[0].getMBR().max);
    
        for (int i = 1; i < n.entryCount; i++) {
            mbr.add(n.entries[i].getMBR());
        }
        return mbr;
    }

    public String toString(Node n, int level){
        StringBuffer str = new StringBuffer();
        StringBuffer space = new StringBuffer();
        for(int i=n.level; i<this.treeHeight;i++){
            space.append(' ');
            space.append(' ');
        }
        str.append(space);
        str.append(n.toString());
        str.append('\n');

        space.append(' ');
        space.append(' ');
        if(n.isLeaf()){
            for(int i=0;i < n.entryCount;i++){
                str.append(space);
                str.append(n.entries[i].toString());
                str.append('\n');
            }
        }else{
            for(int i=0;i < n.entryCount;i++){
                try {
                    if((n.getLevel()-1)>=level){
                        str.append(toString(storageManager.getNode(n.entries[i].getID()), level));
                    }
                } catch (RTreeException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex.getMessage());
                }
            }
        }
        return str.toString();

    }

    public Node getNode(int nodeId) throws RTreeException{
        return storageManager.getNode(nodeId);
    }


    public String toString(int level){
        try {
            Node n = storageManager.getNode(this.getRootNodeId());
            return toString(n,level);
        } catch (RTreeException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }


    @Override
    public String toString(){
        return toString(0);
    }

   public static double[] getValues(String line, int dims){
        StringTokenizer tokens = new StringTokenizer(line);

        double[] values = new double[dims];
        int d=0;
        String token=null;
        for(int i=0;d<dims && tokens.hasMoreTokens();i++){
            token = tokens.nextToken();
            values[d] =Double.parseDouble(token);
            d++;
        }

        return values;
    }
   
    public static void main(String[] args) throws Exception{
        Properties properties = new Properties();
        int minNodeEntries= 38;
        int maxNodeEntries = 113;
        properties.load(new FileInputStream("rtree.properties"));

        StorageManager storageManager=null;
        DiskBuffer buffer = new DiskBuffer(null, 10000);
            
        storageManager =  new DiskStorageManager(buffer, "rtreeData/index.dat", //Rtree data
                    2 , 4096, 4096, maxNodeEntries);
        
        RTree rTree = new RTree(2, storageManager,"rtreeData/index.res");

        rTree.init("DiskStorage",minNodeEntries, maxNodeEntries);

        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("index.txt")));
        for(String line=input.readLine(); line!=null; line=input.readLine()){
            double[] values = RTree.getValues(line, 2);
             rTree.add(1, new Rectangle(values, values));
        }

        System.out.println(rTree.toString(2));

        input.close();
/*
        rTree.delete(new Rectangle(10, 10, 20, 20), 12);
        rTree.add(new Rectangle(20, 20, 30, 30));
*/

/*
        rTree.add(new Rectangle(20, 90, 20, 90));
        rTree.add(new Rectangle(40, 70, 40, 70));
        rTree.add(new Rectangle(30, 50, 30, 50));
        rTree.add(new Rectangle(70, 70, 70, 70));
        rTree.add(new Rectangle(60, 90, 60, 90));
        rTree.add(new Rectangle(90, 90, 90, 90));

        rTree.add(1, new Rectangle(new double[]{10,80}, new double[]{10,80}));
        rTree.add(2, new Rectangle(new double[]{10,60}, new double[]{10,60}));
        rTree.add(3, new Rectangle(new double[]{30,40}, new double[]{30,40}));
        rTree.add(4, new Rectangle(new double[]{40,80}, new double[]{40,80}));
        rTree.add(5, new Rectangle(new double[]{70,60}, new double[]{70,60}));
        rTree.add(6, new Rectangle(new double[]{80,80}, new double[]{80,80}));

        System.out.print("rTree= \n"+rTree.toString());
        System.out.println("---------------------");
        System.out.println("Contains Rectangle(30,30,70,70)");
        Iterator<Entry> it = rTree.contains(new Rectangle(new double[]{30,30},new double[]{30,30}));
        while(it.hasNext()){
            System.out.println(it.next());
        }

        System.out.println("---------------------");
        System.out.println("Nearest Point(100,20)");
        it = rTree.nearest(new Point(new double[]{70,100}), Double.POSITIVE_INFINITY);
        while(it.hasNext()){
            System.out.println(it.next());
        }

        System.out.println("---------------------");
        System.out.println("Intersects Rectangle(0,0,100,100)");
        it = rTree.intersects(new Rectangle(new double[]{0,0}, new double[]{20,100}));
        while(it.hasNext()){
            System.out.println(it.next());
        }
*/
        rTree.save();
        rTree.close();
    }
}