/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.ArrayList;

/**
 *
 * @author joao
 */
public class Primitive<N extends Number> {
    private N number;

    public Primitive(){
    }

    public Primitive(N value){
        this.number = value;
    }

    public N getValue(){
        return number;
    }

    public void setValue(N newValue){
        this.number = newValue;
    }

    @Override
    public boolean equals(Object other){
        return number.equals(other);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    public static int[] toArray(ArrayList<Integer> array){
        int[] values = new int[array.size()];
        for(int i=0;i<values.length;i++){
            values[i] = array.get(i);
        }
        return values;
    }

    public static int[] toArray(Integer[] array){
        int[] values = new int[array.length];
        System.arraycopy(array, 0, values, 0, array.length);
        return values;
    }
}
