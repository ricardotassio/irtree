//   Node.java
//   Based on Node.java by Java Spatial Index Library

package util.rtree;


/**
 * <p>Used by RTree. There are no public methods in this class.</p>
 * 
 * @author aled@sourceforge.net
 * @version 1.0b2p1
 */
public class Node extends Entry{
    protected Entry[] entries = null;
    protected int level;
    protected int entryCount;

    public Node(int nodeId, int level, int maxNodeEntries) {
        super(nodeId, null);
        this.level = level;
        entries = new Entry[maxNodeEntries];
    }


    @Override
    public Node copy(){
        Node copy = new Node(id, level, entries.length);
        copy.mbr = mbr!=null?this.mbr.copy():null;
        copy.entries = new Entry[this.entries.length];
        copy.entryCount = entryCount;
        System.arraycopy(this.entries, 0, copy.entries, 0, entryCount);
        return copy;
    }
   
    public void addEntry(Entry entry) {
        entries[entryCount] = entry;
        entryCount++;
        if (mbr == null) {
            mbr = entry.getMBR().copy();
        } else {
            mbr.add(entry.getMBR());
        }
    }
  
    // Return the index of the found entry, or -1 if not found
    int findEntry(Entry e) {
        for (int i = 0; i < entryCount; i++) {
            if (entries[i].equals(e)) {
                return i;
            }
        }
        return -1;
    }
  
    // delete entry. This is done by setting it to null and copying the last entry into its space.
    protected void deleteEntry(int i, int minNodeEntries) {
	int lastIndex = entryCount - 1;
	Entry deletedEntry = entries[i];
        entries[i] = null;
	if (i != lastIndex) {
            entries[i] = entries[lastIndex];
            entries[lastIndex] = null;
	}
        entryCount--;
    
        // if there are at least minNodeEntries, adjust the MBR.
        // otherwise, don't bother, as the node will be
        // eliminated anyway.
        if (entryCount >= minNodeEntries) {
            recalculateMBR(deletedEntry.getMBR());
        }
    }
  
    // oldRectangle is a rectangle that has just been deleted or made smaller.
    // Thus, the MBR is only recalculated if the OldRectangle influenced the old MBR
    void recalculateMBR(Rectangle deletedRectangle) {
        if (mbr.edgeOverlaps(deletedRectangle)) {
            mbr.set(entries[0].getMBR().min, entries[0].getMBR().max);
      
            for (int i = 1; i < entryCount; i++) {
                mbr.add(entries[i].getMBR());
            }
        }
    }
   
    public int getEntryCount() {
        return entryCount;
    }
  
    public Entry getEntry(int index) {
        if (index < entryCount) {
            return entries[index];
        }
        return null;
    }
  
    public int getId(int index) {
        if (index < entryCount) {
            return entries[index].getID();
        }
        return -1;
    }
  
    /**
    * eliminate null entries, move all entries to the start of the source node
    */
    void reorganize(RTree rtree) {
        int countdownIndex = rtree.getMaxNodeEntries() - 1;
        for (int index = 0; index < entryCount; index++) {
            if (entries[index] == null) {
                while (entries[countdownIndex] == null && countdownIndex > index) {
                    countdownIndex--;
                }
                entries[index] = entries[countdownIndex];
                entries[countdownIndex] = null;
            }
        }
    }
  
    public boolean isLeaf() {
        return (level == 1);
    }
  
    public int getLevel() {
        return level;
    }
  
    public Rectangle getMBR() {
        return mbr;
    }

    public String toString(){
        return "[id="+this.id+", l="+this.level+", "+this.mbr+"]";
    }
}