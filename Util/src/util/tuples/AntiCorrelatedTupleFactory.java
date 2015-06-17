/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.tuples;

import util.Util;

/**
 *
 * @author joao
 */
public class AntiCorrelatedTupleFactory extends CorrelatedTupleFactory{

    public AntiCorrelatedTupleFactory(int numDimensions, int clusters, double maxValue, int precision){
        super(numDimensions, clusters, maxValue, precision);
    }

    @Override
    public Tuple produce() {
        //change the values of the tuples pruduced by the CorrelatedTupleFactory
        Tuple tuple = super.produce();
        for (int s=0;s<dimensions-1;s++){
            tuple.setValue(s, Util.cast(maxValue-tuple.getValue(s), precision));
        }
        return tuple;
    }

    public static void main(String[] args){
        int size = 10000;
        int NumDimensions=2;
        int numClusters = 5;
        int precision=2;

        AntiCorrelatedTupleFactory tupleFactory = new AntiCorrelatedTupleFactory(NumDimensions, numClusters, Tuple.UNIVERSE_MAX_VALUE, precision);

        Tuple[] tuples = new Tuple[size];
        for(int i=0;i<size;i++){
            tuples[i] = tupleFactory.produce();
        }

        Cell[] mbrs = tupleFactory.getClustersMBRs();
        System.out.println("\n\n ##############  MBRS  #############");
        for(int i=0;i<mbrs.length;i++){
            System.out.println(mbrs[i]);
        }

        //check if all the points are inside the MBRs
        boolean allIn=true;

        for(int i=0;allIn && i<tuples.length;i++){
            Tuple tuple = tuples[i];
            allIn=false;
            for(int v=0;!allIn && v<mbrs.length;v++){
                if(mbrs[v].contains(tuple)){
                    allIn=true;
                }
            }
            if(!allIn){
                System.out.println("Tuple is out:"+tuple);
            }
            //System.out.println(tuple);
            /* print tuples in gnuplot format
            for(int v=0;v<tuple.getCols();v++){
                System.out.print(" "+tuple.getValue(v));
            }
            System.out.println();
             */
        }

        if(allIn){
            System.out.println("All tuples are inside in at least one MBR.");
        }else{
            System.out.println(" ERROR!");
        }
    }
}
