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
public class Term implements PersistentEntry{
    public static int SIZE = (Integer.SIZE+Double.SIZE)/Byte.SIZE;
    private int occurrences;
    private int id;
    private double weight;
    
    public Term(int id){
        this(id, -1.0);
    }

    public Term(int termId, double weight) {
        this.id = termId;
        this.weight = weight;
        this.occurrences = 0;
    }

    public Term() {
        super();
    }

    public void incNumOccurrences(){
        this.occurrences++;
    }

    public int getNumOccurrences(){
        return occurrences;
    }

    public int getTermId(){
        return id;
    }

    public void setWeight(double newWeight){
        this.weight = newWeight;
    }

    public double getWeight(){
        return weight;
    }


    @Override
    public boolean equals(Object obj){
        Term other = (Term) obj;
        return this.getTermId()==other.getTermId() &&
                this.getWeight()==other.getWeight();
    }

    @Override
    public String toString(){
        return id+":"+ Util.cast(weight,2);
    }


    @Override
    public Term clone(){
        return new Term(this.getTermId(), this.getWeight());
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeDouble(weight);
    }

    public void read(DataInputStream in) throws IOException {
        this.id = in.readInt();
        this.weight = in.readDouble();
    }

    public static PersistentEntryFactory<Term> FACTORY = new PersistentEntryFactory<Term>(){
        public Term produce(DataInputStream input) throws IOException {
            Term entry = new Term();
            entry.read(input);
            return entry;
        }
    };
}
