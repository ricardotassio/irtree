/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 *
 * @author joao
 */
public class BlockOutputStream extends ByteArrayOutputStream{

    public BlockOutputStream(int blockSize) {
        super(blockSize);
    }

    @Override
    public synchronized byte toByteArray()[] {
        return Arrays.copyOf(buf, buf.length);
    }

    public byte[] getBuffer(){
        return buf;
    }
}
