/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import util.FileUtils;
import util.statistics.StatisticCenter;
/**
 *
 * @author joao
 */
public class ListStorage<E extends PersistentEntry> {
    private static final String ENTRY_BLOCKS_FILE_EXTENSION = ".ebm";

    static final int LIST_STORAGE_BLOCK_SIZE = FileUtils.DISK_PAGE_SIZE;
    private static final String FILE_EXTENSION = ".lst";
    private RandomAccessFile entriesFile;
    private EmptyBlocksManager emptyBlocksManager;
    private BlockOutputStream entriesBlockOutput;
    private DataOutputStream entriesDataOutputStream;
    /*
     * This block column file maps an id to a pointer in the block column
     * file where the entries can be retrieved. Each block of this file
     * has two integers <int:pointer, int:numEntries>.
     */
    private static final String MAPPING_FILE_EXTENSION = ".map";
    private static final int MAPPING_ENTRY_SIZE = (Long.SIZE + Integer.SIZE)/Byte.SIZE;
    private BlockColumnFile id2EntriesFile;
    private byte[] id2EntriesBuffer;
    private DataInputStream ids2EntriesInput;
    private BlockOutputStream ids2EntriesBlockOutput;
    private DataOutputStream ids2EntriesDataOutput;

    private final String prefixFile;
    private final StatisticCenter statistiCenter;    
    private final int entrySizeInBytes;
    private final PersistentEntryFactory<E> factory;    
    private final String statisticCenterId;

    /**
     *
     * @param statistiCenter
     * @param id idenfifes the statics comming from this object in the statistic center
     * @param prefixFile
     * @param blockSize
     * @param entrySize
     * @param factory
     */
    public ListStorage(StatisticCenter statistiCenter, String statisticCenterId,
            String prefixFile, int entrySize, PersistentEntryFactory<E> factory){
        this.prefixFile = prefixFile;
        this.statistiCenter = statistiCenter;        
        this.entrySizeInBytes = entrySize;
        this.factory = factory;        
        this.statisticCenterId = statisticCenterId;
    }

    public void open()throws ColumnFileException, FileNotFoundException, IOException{

        FileUtils.createDirectories(prefixFile);
       

        id2EntriesFile = new BlockColumnFileImpl(prefixFile + MAPPING_FILE_EXTENSION,
                MAPPING_ENTRY_SIZE, FileUtils.GB / MAPPING_ENTRY_SIZE);


        if(statistiCenter!=null){
            id2EntriesFile.setStatisticCenter(statistiCenter, statisticCenterId+"_map");
        }

        id2EntriesBuffer = new byte[id2EntriesFile.getBlockSize()];
        ids2EntriesInput = new DataInputStream(new ByteArrayInputStream(id2EntriesBuffer));
        ids2EntriesBlockOutput = new BlockOutputStream(id2EntriesFile.getBlockSize());
        ids2EntriesDataOutput = new DataOutputStream(ids2EntriesBlockOutput);

        entriesFile = new RandomAccessFile(prefixFile + FILE_EXTENSION, "rw");

        emptyBlocksManager = new EmptyBlocksManager(prefixFile+ENTRY_BLOCKS_FILE_EXTENSION,
                entriesFile.length());
        emptyBlocksManager.open();

        entriesBlockOutput = new BlockOutputStream(LIST_STORAGE_BLOCK_SIZE);
        entriesDataOutputStream = new DataOutputStream(entriesBlockOutput);
    }

    public List<E> getList(int id) throws ColumnFileException, IOException{
        List<E> list = null;
        ListIterator<E> it = this.getEntries(id);
        if(it!=null){
            list = new LinkedList<E>();
            while(it!=null && it.hasNext()){
                list.add(it.next());
            }
        }
        return list;
    }

    /**
     * 
     * @param listId the id of the list to be appended.
     * @param value the new value to be added
     * @param unique if true, perform a check in the list and appends only it the item
     * is not present.
     */
    public void addEntry(int listId, E value, boolean unique) throws ColumnFileException, IOException {
        List<E> list = getList(listId);
        if(list==null){
            list = new LinkedList<E>();
        }else if(unique && list.contains(value)){
            return;
        }
        list.add(value);
        putList(listId, list);
    }

    public void addList(int listId, List<E> newList, boolean unique) throws ColumnFileException, IOException {
        List<E> list = getList(listId);
        if(list==null){
            list = newList;
        }else if(!unique){
            list.addAll(newList);
        }else{ //unique
            HashSet<E> curList = new HashSet<E>(list);
            for(E e:newList){
                if(!curList.contains(e)){
                    list.add(e);
                }
            }
        }
        putList(listId, list);
    }

    public void removeEntry(int listId, E value) throws ColumnFileException, IOException {
        List<E> list = getList(listId);
        if(list!=null){
            list.remove(value);
            putList(listId, list);
        }
    }

    public boolean containsList(int id) throws ColumnFileException, IOException{        
        return getListSize(id) > 0;
    }

    public String getPrefixFile(){
        return this.prefixFile;
    }

    /**
     * Returns an iterator for the given id
     * @param id
     * @return PostingList
     * @throws SSEExeption
     */
    public ListIterator<E> getEntries(int id) throws ColumnFileException, IOException{
        try {
            id2EntriesFile.select(id, id2EntriesBuffer);
            long ptr = ids2EntriesInput.readLong();
            int numEntries = ids2EntriesInput.readInt();
            ids2EntriesInput.reset();
            if(ptr!=0){
                return new ListIterator<E>(entriesFile, ptr, numEntries, entrySizeInBytes, factory);
            }
        }catch (DataNotFoundException ex) { }
        return null;
    }

    public  void  putList(int id, List<E> list) throws ColumnFileException, IOException{
        if(list!=null){
            putList(id, list.iterator(), list.size());
        }
    }


    /**
     * Stores the list for a given id. This method must by synchronized.
     * @param id
     * @param list, the posting list to be inserted
     * @throws SSEExeption
     */
    public  void  putList(int id, Iterator<E> entries, int entriesSize) throws ColumnFileException, IOException{
        long ptr = 0;
        int numEntries=0;
        try{
            id2EntriesFile.select(id, id2EntriesBuffer);

            ptr = ids2EntriesInput.readLong();
            numEntries = ids2EntriesInput.readInt();
            ids2EntriesInput.reset();
        }catch (DataNotFoundException ex) {/* no op*/}

        //Current number of bytes used by this list
        int curNumBytes = numEntries*entrySizeInBytes;


        //New number of blocks used by this list
        int newNumBytes = entriesSize*entrySizeInBytes;

        long pointer;
        if(ptr!=0 && newNumBytes==curNumBytes){
            pointer = ptr; //Use the same blocks.
        }else{
            if(ptr!=0){
                //relase the current space used
                emptyBlocksManager.markEmpty(ptr, curNumBytes);
            }
            //allocate a new space
            pointer = emptyBlocksManager.getPointer(newNumBytes);
        }


        int bytesUsed=0;
        while(entries.hasNext()){
           E entry = entries.next();

            if( (bytesUsed+entrySizeInBytes) > LIST_STORAGE_BLOCK_SIZE){
                entriesDataOutputStream.flush();

                entriesFile.seek(pointer);
                entriesFile.write(entriesBlockOutput.toByteArray(), 0, bytesUsed);
                pointer+=bytesUsed;

                entriesBlockOutput.reset();
                bytesUsed = 0;
            }
            entry.write(entriesDataOutputStream);
            bytesUsed += entrySizeInBytes;
        }

        entriesDataOutputStream.flush();

        entriesFile.seek(pointer);
        entriesFile.write(entriesBlockOutput.toByteArray(), 0, bytesUsed);
        pointer+=bytesUsed;

        entriesBlockOutput.reset();

        upateIds2List(id, pointer-newNumBytes, entriesSize);
    }

    //upate the pointer in the ids2List file
    private void upateIds2List(int id, long ptr, int size) throws IOException, ColumnFileException{
        ids2EntriesDataOutput.writeLong(ptr);
        ids2EntriesDataOutput.writeInt(size);
        id2EntriesFile.insert(id, ids2EntriesBlockOutput.toByteArray());
        ids2EntriesBlockOutput.reset();
    }

     public void close() throws ColumnFileException, IOException{
        entriesFile.close();
        entriesDataOutputStream.close();
        emptyBlocksManager.close();

        id2EntriesFile.close();
        ids2EntriesInput.close();
        ids2EntriesDataOutput.close();
    }

    public void remove(int id) throws ColumnFileException, IOException{
        long ptr = 0;
        int numEntries=0;
        try{
            id2EntriesFile.select(id, id2EntriesBuffer);

            ptr = ids2EntriesInput.readLong();
            numEntries = ids2EntriesInput.readInt();
            ids2EntriesInput.reset();
        }catch (DataNotFoundException ex) {
            return; //The object was already removed
        }

        //There is no pointer for such a list. Which means that it has been deleted.
        if(ptr==0){
            return;
        }else{
             upateIds2List(id, 0, 0);
            emptyBlocksManager.markEmpty(ptr, numEntries*entrySizeInBytes);
        }
    }

    /**
     * Returns the number of entries in the list.
     * @param listId
     * @return
     * @throws SSEExeption
     */
    public int getListSize(int id) throws ColumnFileException, IOException{
        int size = 0;
        try{
            id2EntriesFile.select(id, id2EntriesBuffer);
            ids2EntriesInput.readLong(); // ignore the pointer
            size = ids2EntriesInput.readInt();
            ids2EntriesInput.reset();
        }catch(DataNotFoundException e){
            //no op, return size==0
        }
        return size;
    }

    public Iterator<Integer> getIdsIterator() throws ColumnFileException, IOException{
        return new Iterator<Integer>() {
            int count=0;
            int data;
            int size = id2EntriesFile.size();
            boolean hasData=false;

            public boolean hasNext() {
                if(!hasData){
                    hasData = next()!=null;
                }
                return hasData;
            }

            public Integer next() {
                try{
                    if(!hasData){
                        //produce data
                        count++;
                        while(!hasData && count<=id2EntriesFile.size()){                            
                            if(getListSize(count)>0){
                                data = count;
                                hasData = true;
                            }else{
                                count++;
                            }
                        }
                    }
                    if(hasData){ //consume data
                        hasData = false;
                        return data;
                    }else{
                        return null;
                    }
                }catch(Exception e){
                    throw new RuntimeException(e);
                }
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    public List<Integer> getIds() throws ColumnFileException, IOException{
        LinkedList<Integer> listIds = new LinkedList<Integer>();
        
        for(int i=1;i<=id2EntriesFile.size();i++){            
            if(getListSize(i)>0){
                listIds.add(i);
            }
        }
        
        return listIds;
    }

    /**
     * Returns the largest id used by this list.
     * @return
     */
    public int getLargestId() throws ColumnFileException{
        return id2EntriesFile.size();
    }

    /**
     * Returns a map of ids->listSize
     * @return
     */
    public Set<Entry<Integer,Integer>> entrySet() throws ColumnFileException, IOException{
        HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();

        int listSize;
        for(int i=1;i<=id2EntriesFile.size();i++){            
            listSize = getListSize(i);
            if(listSize>0){
                map.put(i, listSize);
            }
        }

         return map.entrySet();
    }

    public long getSizeInBytes() throws ColumnFileException, DataNotFoundException, IOException{        
        long totalNumEntries = 0;
        Iterator<Integer> idsIterator = getIdsIterator();
        while(idsIterator.hasNext()){
            totalNumEntries+=this.getListSize(idsIterator.next());
        }

        return totalNumEntries*this.entrySizeInBytes+
            id2EntriesFile.size()*id2EntriesFile.getBlockSize();
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        try {            
            str.append('[');            
            for(int listId:getIds()){
                str.append("("+listId+")");
                str.append(this.getList(listId).toString());
                str.append(',');
            }
            if(str.charAt(str.length()-1)==',') str.delete(str.length()-1, str.length());
            str.append(']');
        } catch (ColumnFileException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return str.toString();
    }

    public static void delete(String fileName) {
        new File(fileName + FILE_EXTENSION).delete();
        new File(fileName + MAPPING_FILE_EXTENSION).delete();
        new File(fileName + ENTRY_BLOCKS_FILE_EXTENSION).delete();
        
        for(int fileNum = 2; new File(fileName + MAPPING_FILE_EXTENSION+"."+ fileNum).exists(); fileNum++) {
            new File(fileName + MAPPING_FILE_EXTENSION + "."+ fileNum).delete();
        }
    }

    /**
     * Create a copy of the files used by the list storage.
     * @param newPrefixFile the new prefix used by all new files (copied form this list files).
     * @throws ColumnFileException
     * @throws IOException
     */
    public void copy(String newPrefixFile) throws ColumnFileException, IOException {        
        FileUtils.copyFile(prefixFile + FILE_EXTENSION,
                newPrefixFile + FILE_EXTENSION);
        FileUtils.copyFile(prefixFile + MAPPING_FILE_EXTENSION,
                newPrefixFile + MAPPING_FILE_EXTENSION);
        FileUtils.copyFile(prefixFile + ENTRY_BLOCKS_FILE_EXTENSION,
                newPrefixFile + ENTRY_BLOCKS_FILE_EXTENSION);
        
        for(int fileNum = 2; new File(prefixFile + MAPPING_FILE_EXTENSION+"."+ fileNum).exists(); fileNum++) {
            FileUtils.copyFile(prefixFile+MAPPING_FILE_EXTENSION+"."+ fileNum,
                    newPrefixFile+MAPPING_FILE_EXTENSION+"."+ fileNum);
        }     
    }

    public void delete() throws ColumnFileException, IOException {
        close();
        delete(prefixFile);
    }

    public static void main(String[] args) throws Exception{
        ListStorage<IntegerEntry> storage;
        storage= new ListStorage<IntegerEntry>(null, null, "temp",
                IntegerEntry.SIZE, IntegerEntry.FACTORY);

        storage.open();
        storage.putList(1, Arrays.asList(new IntegerEntry[]{ie(50)}));
        storage.putList(2, Arrays.asList(new IntegerEntry[]{ie(10), ie(11)}));
        storage.putList(3, Arrays.asList(new IntegerEntry[]{ie(6), ie(7), ie(8)}));
        storage.putList(4, Arrays.asList(new IntegerEntry[]{ie(9)}));
        storage.putList(5, Arrays.asList(new IntegerEntry[]{ie(1), ie(2), ie(3), ie(4), ie(5)}));

        storage.remove(3);

        for(int i=1;i<6;i++){
            if(storage.getList(i)!=null){
                System.out.println("List "+i+":" + storage.getList(i).toString());
            }
        }
        
        storage.close();
    }

    public static IntegerEntry ie(int value){
        return new IntegerEntry(value);
    }
}
