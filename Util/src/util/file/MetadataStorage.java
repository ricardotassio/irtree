package util.file;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import util.FileUtils;
/**
 *
 * @author joao
 */
public class MetadataStorage<E extends PersistentEntry> {
    private static final byte EMPTY = 0;
    private static final byte FULL = 1;

    private static final String FILE_EXTENSION = ".mtd";
    
    private DataInputStream input;
    private BlockOutputStream blockOutput;
    private DataOutputStream dataOutput;

    private final int blockSize;
    private final String prefixFile;
    private final PersistentEntryFactory<E> factory;
    
    public MetadataStorage(String prefixFile, int entrySizeInBytes, PersistentEntryFactory<E> factory){
        this.prefixFile = prefixFile;
        this.factory = factory;
        this.blockSize = entrySizeInBytes + 1;
    }

    public void open() throws IOException{
        FileUtils.createDirectories(prefixFile);
        
        blockOutput = new BlockOutputStream(blockSize);
        dataOutput = new DataOutputStream(blockOutput);
        input = new DataInputStream(new ByteArrayInputStream(blockOutput.getBuffer()));

        if(new File(prefixFile + FILE_EXTENSION).exists()){
            FileInputStream file = new FileInputStream(prefixFile + FILE_EXTENSION);
            file.read(blockOutput.getBuffer());
            file.close();
        }        
    }

    public E getEntry() throws IOException{
        input.reset();
        if(input.readByte()==EMPTY){
            return null;
        }
        return factory.produce(input);
    }

    public  void  putEntry(E entry) throws IOException{
        blockOutput.reset();
        dataOutput.writeByte(FULL);
        entry.write(dataOutput);
    }

    public void flush() throws IOException{
        FileOutputStream file = new FileOutputStream(prefixFile + FILE_EXTENSION);
        file.write(blockOutput.getBuffer());
        file.close();
    }

    public void close() throws IOException{
        flush();

        input.close();
        blockOutput.close();
        dataOutput.close();
    }

    public long getSizeInBytes(){
        return new File(prefixFile + FILE_EXTENSION).length();
    }

    public static void delete(String fileName) {
        new File(fileName + FILE_EXTENSION).delete();
    }

    public void delete(){
        delete(prefixFile);
    }

    @Override
    public String toString(){        
        try {
            return getEntry().toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }        
    }

    public void copy(String newPrefixFile) throws IOException {
        FileUtils.copyFile(prefixFile + FILE_EXTENSION,
                newPrefixFile + FILE_EXTENSION);
    }
}