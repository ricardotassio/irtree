/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xxl.util;

import xxl.core.indexStructures.ORTree.IndexEntry;
import xxl.core.spatial.KPE;
import xxl.core.spatial.rectangles.Rectangle;

/**
 *
 * @author joao
 */
public class TreeHeapEntry implements Comparable{
    private final Object entry;
    private double score;    

    public TreeHeapEntry(Object entry){
        this(entry, -1);
    }

    public TreeHeapEntry(Object entry, double score){
        this.entry = entry;        
        this.score = score;
    }

    public int getId() {
        if(getItem() instanceof IndexEntry){
            return Integer.parseInt(((IndexEntry) getItem()).id().toString());
        }else{
            return Integer.parseInt(((KPE) getItem()).getID().toString());
        }
    }

    /**
     * @return the ranking
     */
    public double getScore() {
        return score;
    }

    public void setScore(double score){
        this.score = score;
    }

    /**
     * @return the entry
     */
    public Object getItem() {
        return entry;
    }

    public boolean isNode() {
        return  getItem() instanceof IndexEntry;
    }

    public boolean isData(){
        return getItem() instanceof KPE;
    }

    public Rectangle getMBR(){
        return (Rectangle) (isData() ? ((KPE) entry).getData() : ((IndexEntry) entry).descriptor());
    }

    @Override
    public String toString(){
          return "[id="+this.getId()+", score="+ String.format("%.2f", getScore())+"]";

    }

    public int compareTo(Object o) {
        TreeHeapEntry other = (TreeHeapEntry)o;
        double diff = this.getScore() - other.getScore();
        if(diff > 0){
            return 1;
        }else if(diff<0){
            return -1;
        }else{ //if they have the same score
            diff = (this.isNode()?1:0)-(other.isNode()?1:0);
            if(diff > 0){
                return 1;
            }else if(diff<0){
                return -1;
            }else{ //if they have the same score, check the id
                diff = other.getId()-this.getId();
                if(diff > 0){
                    return 1;
                }else if(diff<0){
                    return -1;
                }else{
                    return 0;
                }
            }
        }
    }
}
