/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author joao
 */
public class IntegerEntry implements PersistentEntry {
    public static int SIZE = Integer.SIZE/Byte.SIZE;

    private int value;

    public IntegerEntry(){
        this(-1);
    }

    public IntegerEntry(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public void setValue(int newValue) {
        value = newValue;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(value);
    }

    public void read(DataInputStream in) throws IOException {
        value = in.readInt();
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof IntegerEntry){
            return this.value==((IntegerEntry)other).getValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.value;
        return hash;
    }
    
    @Override
    public String toString(){
        return String.valueOf(value);
    }

    public static Collection<Integer> asInteger(Collection<? extends IntegerEntry> set){
        ArrayList<Integer> result = new ArrayList<Integer>(set.size());
        for(IntegerEntry e:set){
            result.add(e.getValue());
        }
        return result;
    }

    public static PersistentEntryFactory<IntegerEntry> FACTORY = new PersistentEntryFactory<IntegerEntry>(){
        public IntegerEntry produce(DataInputStream input) throws IOException {
            IntegerEntry entry = new IntegerEntry();
            entry.read(input);
            return entry;
        }
    };

}
