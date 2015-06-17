/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.tuples;

import java.util.ArrayList;
import java.util.Arrays;
import util.ArraysUtils;
import util.RandomUtil;

/**
 *
 * @author joao
 */
public abstract class DefaultTupleFactory implements TupleFactory{
    protected final int dimensions;
    protected final double maxValue;
    protected final int precision;
    protected final int clusters;

    protected Cell[] mbrs;
    protected double[][] centroids;
    protected ArrayList<Tuple> tuples;    
    
    public DefaultTupleFactory(int numDimensions, int clusters, double maxValue, int precision){
        this.dimensions = numDimensions;
        this.maxValue = maxValue;
        this.precision = precision;
        this.clusters = clusters;
        if(clusters>0){
            tuples = new ArrayList<Tuple>();
            mbrs =new Cell[clusters];
            centroids = new double[clusters][numDimensions];

            for (int i=0 ; i < clusters; i++){
               for (int j=0 ; j < numDimensions; j++) {
                   centroids[i][j] =RandomUtil.random()*maxValue;
               }
            }
        }
    }

    public double getMaxValue(){
        return maxValue;
    }

    public Cell[] getClustersMBRs() {

        int[] clusteredTuples = kMeans(tuples, centroids);
        for(int i=0;i<tuples.size();i++){
            updateMBRs(clusteredTuples[i], tuples.get(i));

        }
        //Some mbrs can be null indicating that no point were allocated to them.
        int countFilledMBRs=0;
        for(int i=0;i<this.mbrs.length;i++){
            if(mbrs[i]!=null) countFilledMBRs++;
        }

        Cell[] retMBrs = new Cell[countFilledMBRs];
        for(int i=0,v=0;i<this.mbrs.length;i++){
            if(mbrs[i]!=null){
                retMBrs[v++] = mbrs[i];
            }
        }
        return retMBrs;
    }

    protected void updateMBRs(int center, Tuple tuple){
        if(mbrs[center]==null){
            mbrs[center] = new Cell(tuple.clone(), tuple.clone());
        }else if (!mbrs[center].contains(tuple)){
            for(int i=0;i<this.dimensions;i++){
                if(tuple.getValue(i)<mbrs[center].getMin().getValue(i)){
                    mbrs[center].getMin().setValue(i, tuple.getValue(i));
                }
                if(tuple.getValue(i)>mbrs[center].getMax().getValue(i)){
                    mbrs[center].getMax().setValue(i, tuple.getValue(i));
                }
            }
        }
    }

    private static int[] kMeans(ArrayList<Tuple> objects, double[][] centroids){        
        int[] clusteredTuples = new int[objects.size()];
        int[] centroidsPopulation = new int[centroids.length];
        Arrays.fill(clusteredTuples, -1);
        
        //Select initial centers.
        if(Math.pow(2, centroids[0].length)>=centroids.length){
            //the centers will be positioned in the corner of space.
            int minCorner=0;
            int maxConer = (int)Math.pow(2, centroids[0].length)-1;
            for(int i=0;i<centroids.length;i++){
                int corner = (i%2)==0?minCorner++:maxConer--;
                for(int v=0;v<centroids[i].length;v++){
                    centroids[i][v]=((corner>>v)&1)==1?Tuple.UNIVERSE_MAX_VALUE:Tuple.UNIVERSE_MIN_VALUE;
                }
            }
        }else{
            //the centers are chosed randomly among the points
            for(int i=0;i<centroids.length;i++){
                int ichnos = RandomUtil.nextInt(objects.size());
                if(!ArraysUtils.contains(clusteredTuples, ichnos, 0,clusteredTuples.length)){
                    System.arraycopy(objects.get(ichnos).getValues(), 0, centroids[i], 0, centroids[i].length);
                    clusteredTuples[ichnos]= ichnos;
                }else{
                    i--;
                }
            }
        }

        //At the begining all centroids has the same population
        int mostPopulated = 0;
        int lessPopulated = 0;
        boolean changed=true;
        while(changed || centroidsPopulation[lessPopulated]==0){
            changed = false;            
            for(int i=0;i<objects.size();i++){                
                double min = Double.MAX_VALUE;
                int ichnos = -1;
                for(int j=0;j<centroids.length;j++){
                    double dist = distance(objects.get(i),centroids[j]);
                    if(dist <= min){
                        ichnos = j;
                        min = dist;
                    }
                }
                //change of cluster.
                if(clusteredTuples[i] != ichnos){
                    changed = true;
                    clusteredTuples[i] = ichnos;
                    centroidsPopulation[ichnos]++;
                }
            }
            for(int i=0;i<centroidsPopulation.length;i++){
                if(centroidsPopulation[i]>centroidsPopulation[mostPopulated]){
                    mostPopulated = i;
                }
                if(centroidsPopulation[i]<centroidsPopulation[lessPopulated]){
                    lessPopulated = i;
                }
            }            
            updateClustersMeans(objects, clusteredTuples, centroids, centroidsPopulation, lessPopulated, mostPopulated);
       }
   return clusteredTuples;
    }

    private static void updateClustersMeans(ArrayList<Tuple> objects, int[] clusteredObjects, 
            double[][] centroids, int[] centroidPopulation, int lessPopulated, int mostPopulated){
        for(int i=0;i<centroids.length;i++){
           double counter = 0;
           Tuple tuple = new Tuple(centroids[i].length);
            //Update all objects inside a cluster
           for(int j=0;j<clusteredObjects.length;j++){
                if(clusteredObjects[j]==i){
                    for (int k = 0; k < centroids[i].length; k++){
                        tuple.setValue(k, tuple.getValue(k)+objects.get(j).getValue(k));
                    }
                    counter++;
                 }
            }
            if(counter!=0){
                centroids[i] = tuple.getValues();
                for (int k = 0; k < centroids[i].length; k++){
                    centroids[i][k] = centroids[i][k]/counter;
                }
            }else{
                for (int k = 0; k < centroids[i].length; k++){
                    centroids[i][k] = (centroids[i][k]+centroids[mostPopulated][k])/2.0;
                }
            }
        }
    }

    private static double distance(Tuple tuple, double[] two){
		double sum=0;
		for (int i=0 ; i < two.length; i++) {
			sum += Math.pow((tuple.getValue(i) - two[i]),2);
		}
		return Math.sqrt(sum);
	}
}
