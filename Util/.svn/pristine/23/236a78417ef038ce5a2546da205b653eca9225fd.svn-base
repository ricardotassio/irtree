/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import util.FileUtils;

/**
 *
 * @author joao
 */
public class PersistentHashMap<K extends Serializable, V extends Serializable> extends HashMap<K, V> {
    private final String fileName;
    public PersistentHashMap(String filename){
        super();
        this.fileName = filename;
    }

    /**
     * Load the hash with the data stored in the file
     */
    public void open() throws IOException, ClassNotFoundException{
        File file = new File(fileName);
        if(file.exists()){
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)));

            try{
                for(Object obj=ois.readObject();  obj != null  ; obj=ois.readObject()){
                    this.put((K)obj, (V)ois.readObject());
                }
            } catch (EOFException ex) {}
            ois.close();
        }
    }

    /**
     * stores the data persistently
     */
    public void flush() throws IOException{
        FileUtils.createDirectories(fileName);

        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));

        for(Map.Entry<K,V> entry : this.entrySet()){
            oos.writeObject(entry.getKey());
            oos.writeObject(entry.getValue());
        }
        oos.close();
    }

    public void close() throws IOException{
        flush();
    }

}
