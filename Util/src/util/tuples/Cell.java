/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.tuples;

import java.io.Serializable;

/**
 *
 * @author JOAO
 */
public class Cell implements Serializable{
    private Tuple min;
    private Tuple max;
    private int numDimensions;
    private int cardinality;

    public Cell(){    
    }
    
    public Cell(int numDimensions){    
        this(new Tuple(numDimensions), new Tuple(numDimensions));
    }
    
    public Cell(Tuple min, Tuple max){        
        if(min.getCols()!=max.getCols()){
            throw new RuntimeException("Min and max tulples should have the same number of columns.");
        }
        numDimensions = min.getCols();
        this.setMin(min);
        this.setMax(max);
    }
    
    public Tuple getMin() {
        return min;
    }           

    public void setMin(Tuple min) {
        this.min = min;
    }

    public Tuple getMax() {
        return max;
    }

    /**
     * Two cells are comparable if the maximum point of one is dominated by the
     * minimum of the other, of if the maximum of the other is domininated by the
     * minimim of the one.
     * @param other group
     * @return true if the groups are comparable.
    */
    public boolean isComparable(Cell other){
        return this.getMax().isDominated(other.getMin()) ||
                other.getMax().isDominated(this.getMin());
    }

    public boolean isDominated(Cell[] others){
        for(Cell other:others){
            Tuple[] worstPossible = other.getWorsePossibleSet();
            for(Tuple tuple:worstPossible){
                if(this.getMin().isDominated(tuple)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setMax(Tuple max) {
        this.max = max;
    }

    /**
     * return true if the tuple is inside the mbr
     * @param tuple
     * @return
     */
    public boolean contains(Tuple tuple){
        for(int i=0;i<tuple.getCols();i++){
            if(tuple.getValue(i)<this.getMinValue(i) || tuple.getValue(i)>this.getMaxValue(i)){
                return false;
            }
        }
        return true;
    }
    
    public double getMinValue(int dimension){
        return this.getMin().getValue(dimension);
    }
    
    public void setMinValue(int dimension, double value){
        this.getMin().setValue(dimension, value);
    }

    public double getMaxValue(int dimension){
        return this.getMax().getValue(dimension);
    }

    public void setMaxValue(int dimension, double value){
        this.getMax().setValue(dimension, value);
    }
    
    public void setValues(int dimension, double min, double max){
        this.setMinValue(dimension, min);
        this.setMaxValue(dimension, max);
    }
        
    public int getNumDimensions(){
        return this.numDimensions;
    }    

    public double getVolume(){
        return Tuple.computeArea(this.getMin(), this.getMax());
    }

    /**
     * This method returs a porcentage of the area of a given cell dominated by
     * this point.
     * @param cell
     * @return the percenteage of the area dominated, zero if no area is dominated.
     */
    public double getVolumeDominated(Tuple tuple){
        double dominatedArea = 0;
        //If is it completely dominated
        if(this.getMin().isDominated(tuple)){
            return this.getVolume();
        }else if(this.getMax().isDominated(tuple)){
            //The reference point is a point inside the MBR of another cell
            // computed using the max value for each dimension of the minimim
            // point of another cell and this.minimum corner.
            Tuple reference = new Tuple(this.numDimensions);
            for(int i=0;i<reference.getCols();i++){
                reference.setValue(i, Math.max(this.getMin().getValue(i),
                                               tuple.getValue(i)));
            }
            dominatedArea = Tuple.computeArea(reference, this.getMax());
        }
        return dominatedArea;
    }
        

    
    public Tuple[] getWorsePossibleSet() {
        Tuple[] worsePossibleSet = new Tuple[min.getCols()];
        //A worse possible point is the worst possible point where one of its
        // Cell comes from best possible point
        for(int i=0;i<worsePossibleSet.length;i++){
            worsePossibleSet[i] = max.clone();
            worsePossibleSet[i].setValue(i,min.getValue(i));            
        }   
        return worsePossibleSet;
    }
    
    /**
     * Returns a clone of current Cell
     * @return
     */
    @Override
    public Cell clone(){
        return new Cell(this.min.clone(), this.max.clone());
    }
    
    @Override
    public String toString(){
        return "["+min.toString()+", "+max.toString()+"]";
    }

    @Override
    public boolean equals(Object obj){
        if(obj!=null && obj instanceof Cell){
            Cell another = (Cell) obj;
            if(this.getNumDimensions()==another.getNumDimensions() && //They have the same number of numDimensions
                    this.getMin().equals(another.getMin()) && //The values for the min constraint are the same
                    this.getMax().equals(another.getMax())){ //The values for the max constraint are the same
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.min != null ? this.min.hashCode() : 0);
        hash = 71 * hash + (this.max != null ? this.max.hashCode() : 0);
        return hash;
    }    

    /**
     * @return the cardinality
     */
    public int getCardinality() {
        return cardinality;
    }

    /**
     * @param cardinality the cardinality to set
     */
    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }
}
