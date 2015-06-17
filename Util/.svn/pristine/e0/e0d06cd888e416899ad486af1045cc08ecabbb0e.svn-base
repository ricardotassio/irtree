/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.tuples;

import util.RandomUtil;
import util.Util;

/**
 *
 * @author joao
 */
public class ClusterTupleFactory extends DefaultTupleFactory{
    private double sigma;


    public ClusterTupleFactory(int numDimensions, int clusters,
            double maxValue, int precision, double sigma){
        super(numDimensions, clusters, maxValue, precision);
        this.sigma = sigma;
        for (int i=0 ; i < clusters; i++){
           for (int j=0 ; j < numDimensions; j++) {
               centroids[i][j] =RandomUtil.random()*maxValue;
           }
        }
    }

    public Tuple produce() {
        int center = RandomUtil.nextInt(centroids.length);

        Tuple tuple = new Tuple(this.dimensions);
        double value;
        for (int i=0;i<this.dimensions;i++){
            double gausian= RandomUtil.nextGaussian();
            value = gausian*sigma+centroids[center][i];
            if((value<Tuple.UNIVERSE_MIN_VALUE)||(value>maxValue)){
               i--;
            }else{
                tuple.setValue(i, Util.cast(value, precision));
            }
        }
        //Update the MBRs dinamically while receives tuples.
        updateMBRs(center, tuple);
        return tuple;
    }

    @Override
    public Cell[] getClustersMBRs() {
        return this.mbrs;
    }

    public static void main(String[] args){
        int size = 10000;
        int NumDimensions=2;
        int numClusters = 5;
        int precision=2;
        double sigma = 5;

        ClusterTupleFactory tupleFactory = new ClusterTupleFactory(NumDimensions, numClusters, Tuple.UNIVERSE_MAX_VALUE, precision, sigma);

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
