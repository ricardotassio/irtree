/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.rtree;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import util.file.BlockOutputStream;
import util.file.BlockColumnFile;
import util.file.BlockColumnFileImpl;
import util.file.ColumnFileException;
import util.file.DataNotFoundException;

/**
 *
 * @author joao
 */
public class DiskStorageManager implements StorageManager {
    private static final int EOF = -1;
    private static int ID;
    private final int id;
    private final int dimensions;
    protected final int maxNodeEntries;

    protected DataOutputStream dataOutput;
    protected DataInputStream dataInput;
    private final BlockOutputStream output;
    private final ByteArrayInputStream input;

    private final byte[] buffer;
    private BlockColumnFile data;    
    private DiskBuffer diskBuffer;
    
    public DiskStorageManager(DiskBuffer diskBuffer, String prefix, 
            int dimensions, int blockSize, int blocksPerFile,
            int maxNodeEntries) throws RTreeException{
        try {
            String dir = prefix.substring(0,prefix.lastIndexOf("/"));
            if(dir.length()>0){
                File fileDir = new File(dir);
                if(!fileDir.exists()) fileDir.mkdirs();
            }
            this.id = ID++; //Identifies this DiskStorageManager

            this.maxNodeEntries =  maxNodeEntries;
            this.data = new BlockColumnFileImpl(prefix, blockSize, blocksPerFile);                        
            this.diskBuffer = diskBuffer;
            this.dimensions = dimensions;
            this.buffer = new byte[blockSize];
            this.output = new BlockOutputStream(buffer.length);
            this.input = new ByteArrayInputStream(buffer);
            this.dataOutput = new DataOutputStream(output);
            this.dataInput = new DataInputStream(input);

        } catch (ColumnFileException ex) {
            throw new RTreeException(ex);
        }
    }

    public int getId(){
        return id;
    }

    public boolean equals(Object o){
        return this.getId()==((DiskStorageManager)o).getId();
    }

    public void store(int nodeId, Node node) throws RTreeException {
        diskBuffer.store(nodeId, node.copy(), this);
    }

    public Node getNode(int nodeId) throws RTreeException {
        return diskBuffer.read(nodeId,this).copy();
    }

    protected void storeNodeInDisk(int id, Node node) throws RTreeException{
        try{
            data.insert(id, nodeToByteArray(node));
        }catch(ColumnFileException e){
            throw new RTreeException(e);
        }
    }

    protected Node readNodeFromDisk(int nodeId) throws RTreeException{
        try{
            data.select(nodeId, buffer);
            return byteArrayToNode();
        }catch(ColumnFileException e){
            throw new RTreeException(e);
        }catch(DataNotFoundException e){
            throw new RTreeException(e);
        }
    }

    protected void writeNode(Node node) throws IOException{
        //write Node ID
        dataOutput.writeInt(node.getID());

        //write Node Level
        dataOutput.writeShort(node.getLevel());
    }

    protected void writeEntry(Entry entry) throws IOException{
        //write entry ID
        dataOutput.writeInt(entry.getID());

        //write Rectangle.
        writeRectangle(entry.getMBR());
    }

    private byte[] nodeToByteArray(Node node) throws RTreeException{
        try {
            output.reset();

            //nodeID (4 bytes) + nodeLevel (2 bytes)
            writeNode(node);

            for(int i=0;i<node.entryCount;i++){
                Entry entry =node.getEntry(i);
                //Entry ID (4 bytes) + Rectangle (4 * 8 double = 32 bytes)
                writeEntry(entry);
            }
            //End of file
            dataOutput.writeInt(EOF);
        } catch (IOException ex) {
            throw new RTreeException(ex);
        }

        return output.toByteArray();
    }

    protected Rectangle readRectangle() throws IOException{
        double[] min = new double[dimensions];
        double[] max = new double[dimensions];
        for(int i=0;i<dimensions;i++){
            min[i] = dataInput.readDouble();
            max[i] = dataInput.readDouble();
        }
        return new Rectangle(min,max);
    }

    protected  void writeRectangle(Rectangle rect) throws IOException{
        for(int i=0;i<dimensions;i++){
            dataOutput.writeDouble(rect.getMin()[i]);
            dataOutput.writeDouble(rect.getMax()[i]);
        }
    }

    protected Node readNode() throws IOException{
        int nodeId = this.dataInput.readInt();
        int level = this.dataInput.readShort();

        return new Node(nodeId, level, maxNodeEntries);
    }

    protected Entry readEntry(int id) throws IOException{
        Rectangle rect = readRectangle();

        return new Entry(id, rect);
    }

    protected Node byteArrayToNode() throws RTreeException{
        try {
            input.reset();

            Node node = readNode();

            int entryId = dataInput.readInt();
            while(entryId!=EOF){
                node.addEntry(readEntry(entryId));
                entryId = dataInput.readInt();
            }

            return node;
        } catch (IOException ex) {
            throw new RTreeException(ex);
        }
    }



    public String info() throws RTreeException{
        StringBuffer str = new StringBuffer();
        str.append("DiskStorageManager, ");
        str.append(diskBuffer.toString());
        str.append('.');
        return str.toString();
    }

    public void close() throws RTreeException{
        try {
            //Should be closed before others...
            if(diskBuffer!=null){
                this.diskBuffer.close();
                this.diskBuffer = null;
            }

            if(dataInput!=null){
                this.dataInput.close();
                this.dataInput = null;
            }

            if(dataOutput!=null){
                this.dataOutput.close();
                this.dataOutput = null;
            }            

            this.data.close();
            this.data = null;
        } catch (ColumnFileException ex) {
            throw new RTreeException(ex);
        } catch (IOException ex){
            throw new RTreeException(ex);
        }
    }
}
