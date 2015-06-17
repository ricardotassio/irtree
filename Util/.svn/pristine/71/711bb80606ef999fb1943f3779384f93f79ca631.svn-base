package util.file;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import util.FileUtils;
import util.statistics.StatisticCenter;
/**
 *
 * @author joao
 */
public class EntryStorage<E extends PersistentEntry> {
    private static final byte EMPTY = 0;
    private static final byte FULL = 1;

    private static final String FILE_EXTENSION = ".ent";
    /*
     * This block column file maps an id to a pointer in the block column
     * file where the entries can be retrieved. Each block of this file
     * has two integers <int:pointer, int:numEntries>.
     */
    private BlockColumnFile file;
    private byte[] buffer;
    private DataInputStream input;
    private BlockOutputStream blockOutput;
    private DataOutputStream dataOutput;

    private final String prefixFile;
    private final StatisticCenter statistiCenter;    
    private final int blockSize;
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
    public EntryStorage(StatisticCenter statistiCenter, String statisticCenterId,
            String prefixFile, int entrySizeInBytes, PersistentEntryFactory<E> factory){
        this.prefixFile = prefixFile;
        this.statistiCenter = statistiCenter;        
        this.blockSize = entrySizeInBytes+1; //One extra byte is used to store meta-information
        this.factory = factory;        
        this.statisticCenterId = statisticCenterId;
    }

    public void open()throws ColumnFileException, FileNotFoundException, IOException{
        FileUtils.createDirectories(prefixFile);

        file = new BlockColumnFileImpl(prefixFile + FILE_EXTENSION,
                blockSize, FileUtils.GB / blockSize);


        if(statistiCenter!=null){
            file.setStatisticCenter(statistiCenter, statisticCenterId);
        }

        buffer = new byte[file.getBlockSize()];
        input = new DataInputStream(new ByteArrayInputStream(buffer));
        blockOutput = new BlockOutputStream(file.getBlockSize());
        dataOutput = new DataOutputStream(blockOutput);
    }


    public boolean containsEntry(int id) throws ColumnFileException, IOException{
        return getEntry(id)!=null;
    }

    public String getPrefixFile(){
        return this.prefixFile;
    }

    public E getEntry(int id) throws IOException, ColumnFileException{
        try{
            input.reset();
            file.select(id, buffer);
            if(input.readByte()==EMPTY){
                return null;
            }
            return factory.produce(input);
        }catch(DataNotFoundException e){
            return null;
        }
    }

    public  void  putEntry(int id, E entry) throws ColumnFileException, IOException{
        blockOutput.reset();
        dataOutput.writeByte(FULL);
        entry.write(dataOutput);
        file.insert(id, blockOutput.toByteArray());
    }


    public void remove(int id) throws ColumnFileException, IOException{
        blockOutput.reset();
        dataOutput.writeByte(EMPTY);
        file.insert(id, blockOutput.toByteArray());
    }


    public void close() throws ColumnFileException, IOException{
        file.close();
        input.close();
        blockOutput.close();
        dataOutput.close();
    }

    /**
     * Returns a map of ids->listSize
     * @return
     */
    public Set<Entry<Integer,E>> entrySet() throws ColumnFileException, IOException{
        HashMap<Integer,E> map = new HashMap<Integer,E>();

        E entry;
        for(int i=1;i<=file.size();i++){            
            entry = getEntry(i);
            if(entry!=null){
                map.put(i, entry);
            }
        }

        return map.entrySet();
    }

    /**
     * Returns the number of entries stored. This method is expensive and should
     * be used with careful.
     * @return
     */
    public long size() throws ColumnFileException, IOException{
        int size=0;
        E entry;
        for(int i=1;i<=file.size();i++){
            entry = getEntry(i);
            if(entry!=null){
                size++;
            }
        }
        return size;
    }

    public int getLargestId() throws ColumnFileException {
        return file.size();
    }

    public long getSizeInBytes() throws ColumnFileException, IOException{
        long numEntries=0;
        for(int i=1;i<=file.size();i++){
            if(getEntry(i)!=null){
                numEntries++;
            }
        }
        return numEntries*this.blockSize;
    }

    public static void delete(String fileName) {
        new File(fileName + FILE_EXTENSION).delete();
    }

    public void delete() throws ColumnFileException, IOException {
        close();
        delete(prefixFile);
    }

    /**
     * Create a copy of the file used by the entry storage.
     * @param newPrefixFile the new prefix used by the new file (copied form this entry storage file).
     * @throws ColumnFileException
     * @throws IOException
     */
    public void copy(String newPrefixFile) throws ColumnFileException, IOException {
        FileUtils.copyFile(prefixFile + FILE_EXTENSION,
                newPrefixFile + FILE_EXTENSION);
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        try {
            str.append('[');
            for(Entry<Integer,E> e:entrySet()){
                str.append("[id=").append(e.getKey());
                str.append("(").append(e.getValue().toString()).append(")],");
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
}
