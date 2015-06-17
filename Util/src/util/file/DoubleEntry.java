package util.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author joao
 */
public class DoubleEntry implements PersistentEntry {
    public static int SIZE = Double.SIZE/Byte.SIZE;

    private double value;

    public DoubleEntry(){
        this(-1.0);
    }

    public DoubleEntry(double value){
        this.value = value;
    }

    public double getValue(){
        return value;
    }

    public void setValue(int newValue) {
        value = newValue;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeDouble(value);
    }

    public void read(DataInputStream in) throws IOException {
        value = in.readDouble();
    }

    @Override
    public String toString(){
        return String.valueOf(value);
    }

    public static PersistentEntryFactory<DoubleEntry> FACTORY = new PersistentEntryFactory<DoubleEntry>(){
        public DoubleEntry produce(DataInputStream input) throws IOException {
            DoubleEntry entry = new DoubleEntry();
            entry.read(input);
            return entry;
        }
    };

}
