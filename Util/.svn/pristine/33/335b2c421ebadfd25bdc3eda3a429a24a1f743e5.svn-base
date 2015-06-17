/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.rtree;

/**
 *  @author joao
 */
public class Entry {
    protected int id;
    protected Rectangle mbr;
    
    public Entry(int id, Rectangle mbr){
        this.id = id;
        this.mbr = mbr;
    }

    public Entry copy() {
        return new Entry(id, mbr.copy());
    }

    public int getID(){
        return id;
    }

    public Rectangle getMBR(){
        return mbr;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Entry){
            Entry e = (Entry) obj;
            if(this.id == e.id && this.mbr.equals(e.getMBR())){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        hash = 67 * hash + (this.mbr != null ? this.mbr.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        return "[id="+this.id+", "+this.mbr+"]";
    }
}