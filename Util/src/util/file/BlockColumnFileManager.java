/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import util.RandomUtil;


/**
 * This class manager the used blocks of a blockColumnFile. A block is
 * considered empty if it starts with -1 (input.readInt()-1) If yours blocks do
 * not follow this pattern, you should over hide this class implementing a new
 * isEmpty(DataInputStrem input) method.
 *
 * @author joao
 */
public class BlockColumnFileManager {
    private BlockColumnFile file;
    private HashMap<Integer, LinkedList<Integer>> emptyBlocks;
    private int lastUsedBlock; //pointer for the last used block
    private int maxNumBlocks=0; //Stores the maxNumBlocks that are empty

    public BlockColumnFileManager(BlockColumnFile file) throws ColumnFileException{
        this.file = file;

       //prepare the manager for usage
        try {
            emptyBlocks= new HashMap<Integer, LinkedList<Integer>>();
            byte[] buffer = new byte[file.getBlockSize()];
            DataInputStream input = new DataInputStream(new ByteArrayInputStream(buffer));

            lastUsedBlock = file.size();
            int pointer = -1;
            for(int i=1;i<=file.size();i++){
                file.select(i, buffer);
                if(isEmpty(input)){
                    if(pointer==-1){
                        pointer = i;
                    }
                }else if(pointer!=-1){
                    addEmptyBlocks(pointer, i-pointer);
                    pointer=-1;
                    lastUsedBlock = i;
                }
                input.reset();
            }
            input.close();
        } catch (IOException ex) {
            throw new ColumnFileException(ex);
        } catch (DataNotFoundException ex) {
            //Normal exit.
        }
    }

    /**
     * Returns a pointer for the number of blocks required. The numBlocks are
     * consecutive blocks in the BlockColumnFiles. These blocks are marked as
     * used by the manager.
     * @param numBlocks
     * @return
     */
    public int getPointer(int numBlocks) throws ColumnFileException{
        int pointer = 0;
        for(int i=numBlocks; i <=maxNumBlocks; i++){
            if(emptyBlocks.get(i)!=null && emptyBlocks.get(i).size()>0){
                pointer = emptyBlocks.get(i).removeFirst();

                //The group of blocks has more blocks than required.
                if(i>numBlocks){
                    markEmpty(pointer+numBlocks, i-numBlocks);
                }
                return pointer;
            }
        }
        pointer = lastUsedBlock+1;

        lastUsedBlock+=numBlocks;
        return pointer;
    }

    /**
     *
     * @param pointer
     * @param numBlocks
     */
    public void markEmpty(int pointer, int numBlocks) throws ColumnFileException{
        addEmptyBlocks(pointer, numBlocks);

        try{
            BlockOutputStream byteArrayOutput = new BlockOutputStream(file.getBlockSize());
            DataOutputStream output = new DataOutputStream(byteArrayOutput);
            for(int i=pointer;i<(pointer+numBlocks);i++){                
                writeBlockIsEmpty(output);
                file.insert(i, byteArrayOutput.toByteArray());
                byteArrayOutput.reset();
            }
            output.close();
        } catch (IOException ex) {
            throw new ColumnFileException(ex);
        }
    }

    /**
     * If yours blocks do not start with -1, you should over hide this method
     * indicating for the manager how to identify empty blocks.
     * @param input
     * @return
     * @throws ColumnFileException
     */
    public boolean isEmpty(DataInputStream input) throws ColumnFileException{
        try {
            return input.readInt() == -1;
        } catch (IOException ex) {
            throw new ColumnFileException(ex);
        }
    }

    private void writeBlockIsEmpty(DataOutputStream output) throws ColumnFileException{
        try {
            output.writeInt(-1);
        } catch (IOException ex) {
            throw new ColumnFileException(ex);
        }
    }

    private void addEmptyBlocks(int pointer, int numBlocks){
        if(emptyBlocks.get(numBlocks)==null){
            emptyBlocks.put(numBlocks, new LinkedList<Integer>());
        }
        emptyBlocks.get(numBlocks).add(pointer);
        maxNumBlocks = Math.max(maxNumBlocks, numBlocks);
    }

    
    public static void main(String[] args) throws Exception{
        BlockColumnFile file = new BlockColumnFileImpl("managerTest.dat", 4, 1000);

        BlockOutputStream byteArrayOutput = new BlockOutputStream(file.getBlockSize());
        DataOutputStream output = new DataOutputStream(byteArrayOutput);
        for(int i=1;i<11;i++){
            if(RandomUtil.random()>0.5){
                output.writeInt(-1);
            }else{
                output.writeInt(1);
            }
            file.insert(i, byteArrayOutput.toByteArray());
            byteArrayOutput.reset();
        }
        output.close();

        BlockColumnFileManager manager = new BlockColumnFileManager(file);
        System.out.println("pointer for 1 block "+manager.getPointer(1));
        System.out.println("pointer for 2 blocks "+manager.getPointer(2));
        System.out.println("pointer for 3 blocks "+manager.getPointer(3));
        System.out.println("pointer for 4 blocks "+manager.getPointer(4));
        System.out.println("pointer for 1 block "+manager.getPointer(1));
        file.close();
    }
}