/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import util.Util;
import util.file.PersistentEntry;
import util.file.PersistentEntryFactory;

/**
 *
 * @author joao
 */
public class PostingListEntry implements PersistentEntry {
    public static int SIZE = (Integer.SIZE+Double.SIZE)/Byte.SIZE;

    private int id;
    private double weight;

    public PostingListEntry(int termId, double weight) {
        this.id = termId;
        this.weight = weight;
    }

    public PostingListEntry() {
        this(-1, -1.0);
    }

    public int getDocId(){
        return id;
    }

    public void setWeight(double newWeight){
        this.weight = newWeight;
    }

    public double getWeight(){
        return weight;
    }

    @Override
    public String toString(){
        return id+":"+ Util.cast(weight,2);
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeDouble(weight);
    }

    public void read(DataInputStream in) throws IOException {
        this.id = in.readInt();
        this.weight = in.readDouble();
    }

    public static PersistentEntryFactory<PostingListEntry> FACTORY = new PersistentEntryFactory<PostingListEntry>(){
        public PostingListEntry produce(DataInputStream input) throws IOException {
            PostingListEntry entry = new PostingListEntry();
            entry.read(input);
            return entry;
        }
    };
}
