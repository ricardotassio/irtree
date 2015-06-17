/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.tuples;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import util.Util;

/**
 *
 * @author JOAO
 */
public class Tuple implements Comparable,Serializable{
    /**
     * The min value for each dimension of a tuple
     */
    public static double UNIVERSE_MIN_VALUE=0;        

     /**
     * The max value for each dimension of a tuple
     */
    public static double UNIVERSE_MAX_VALUE=100;
    
    private final double[] data;
    
    public Tuple(int numDimensions){
        this(new double[numDimensions]);
    }    
     
    public Tuple(double[] values){
        data = values;
    }

    public int getNumDimensions(){
        return data.length;
    }
    
    /**
     * Create a tuple and intialize all the elements with value.
     * @param cols the number of dimensions
     * @param value the value which will be putted in all the elements of this tuple
     */
    public Tuple(int cols, double value){
        this(cols);
        Arrays.fill(data, value);
    }
        
    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof Tuple)) {
            Tuple another=(Tuple)obj;
            return Arrays.equals(data, another.getValues());
        }
        return false;                
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.data != null ? Arrays.hashCode(data): 0);
        return hash;
    }

    public int[] toCell(double cellSize){        
        int[] cell = new int[this.getNumDimensions()];
        for(int i=0;i<cell.length;i++){
            cell[i]= (int)((this.getValue(i)-Tuple.UNIVERSE_MIN_VALUE)/cellSize);
        }
        return cell;
    }
    
    public int getCols(){
        return data.length;
    }
    
    public double[] getValues(){
        return data;
    }
    
    public double getValue(int dimension){   
        return data[dimension];
    }
    
    @Override
    public Tuple clone() {
        Tuple copy = new Tuple(data.length);
        copy.setValues(data);        
        return copy;
    }

    public void setValues(double[] values){        
        System.arraycopy(values, 0, data, 0, values.length);
    }
    
    /**
     * Fill all dimensions of the tuple if the given value.
     * @param value
     */
    public void fill(double value){
        Arrays.fill(data, value);
    }
    
    public void setValue(int dimension, double value){
        data[dimension]=value;
    }  
        
    void setAll(Tuple aTuple) {
        this.setValues(aTuple.getValues());
    }
                   
    public int compareTo(Object other) {
        if(other instanceof Tuple){
            Tuple otherTuple = (Tuple) other;
            double thisRank = this.getRank();
            double otherRank = otherTuple.getRank();
            if(thisRank < otherRank){
                return 1;
            }else if(thisRank > otherRank){
                return -1;
            }else{//They are equals
                return 0;
            }
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
          
    Tuple getBorder(ArrayList<Tuple> processed, double maxValue){
        Tuple border = new Tuple(data.length);        
        //Initialize border with max and min values
        for(int i = 0; i< data.length; i++){            
            border.setValue(i, maxValue);    
        }    
       
        if(processed!=null){
            for(Tuple curTuple:processed){
                for(int i = 0; i< data.length; i++){ 
                    double curValue = curTuple.getValue(i);
                    double value = getValue(i);
                    if(value<curValue && curValue<border.getValue(i)){
                        border.setValue(i, curValue);                            
                    }                                                                                                     
                }
            }
        }
        return border;
    }
    
    /**
     * @return the euclidian distance of the tuple to the min corner of the universe.
     * prune others.
     
    public double getRank(){
        double[] minCornerOfUniverse = new double[this.data.length];
        Arrays.fill(minCornerOfUniverse, Tuple.UNIVERSE_MIN_VALUE);
        
        double squareSum=0;
        for(int i=0;i<data.length;i++){
            squareSum+= Math.pow(data[i]-minCornerOfUniverse[i], 2);
        }        
        return Math.sqrt(squareSum);
    }
    */
    
    /**
     * 
     * @return maximum area dominated by this point.
     */
    public double getRank(){
        return computeArea(this, new Tuple(this.getCols(),Tuple.UNIVERSE_MAX_VALUE));
    }

    public static double computeArea(Tuple min, Tuple max){
        double area=1;
        for(int i=0;i<min.getCols();i++){
            area*=(Math.abs(max.getValue(i)-min.getValue(i)));
        }
        return area;
    }  

    /**
     * Returns true if the tuple is dominated by the windows
     * @param theTuple
     * @param window
     * @return
     */
    public static boolean isDominated(Tuple theTuple, ArrayList<Tuple> window){
         for(int i=0; i< window.size(); i++){
            if(theTuple.isDominated(window.get(i))){
                return true;
            }
         }
         return false;
    }

    public boolean isDominated(Tuple another){
        if(getCols()==another.getCols()){
            for(int i=0; i< this.getCols(); i++){
                if(this.data[i]<another.getValue(i)){
                    return false;
                }
            }
        }else{
            throw new RuntimeException("The tuples are not comparable!");
        }
        return true;
    }

    public static void skylineComparison(ArrayList<Tuple> tuples, ArrayList<Tuple> window){
        for(Tuple tuple:tuples){
            skylineComparison(tuple, window);
        }
    }

    public static void skylineComparison(Tuple theTuple, ArrayList<Tuple> window){        
        for(int i=0; i< window.size(); i++){            
            if(theTuple.isDominated(window.get(i))){
                return;                
            }else if(window.get(i).isDominated(theTuple)){
                //Is the tuple in the window dominated by theTuple
                window.remove(i--);                
            }
        }
        window.add(theTuple);
    }   
    
    
    public static String toString(ArrayList<Tuple> tuples){
        if(tuples==null){
            return "";
        }else{
            StringBuffer str = new StringBuffer();
            for(Tuple tuple:tuples){
                str.append(tuple.toString());
            }
            return str.toString();
        }     
    }
    
    public static ArrayList<Tuple> toArrayList(Iterator<Tuple> it){
        ArrayList<Tuple> result = new ArrayList<Tuple>();
        while(it.hasNext()){
            result.add(it.next());
        }
        return result;
    }
        
    @Override
    public String toString(){        
        StringBuffer str=new StringBuffer();

        str.append('(');
        for(int i=0; i<data.length;i++){
            //str.append(Util.cast(this.getValue(i),2));
            str.append(this.getValue(i));
            if((i+1)<data.length){
                str.append(", ");
            }    
        }        
        str.append(')');
        return str.toString();
    }        
}
