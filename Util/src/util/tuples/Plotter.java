/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.tuples;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author joao
 */
public class Plotter {

    public static void plot(int datasetSize, DefaultTupleFactory tupleFactory,
            boolean renormalize) throws IOException{
        ArrayList<Tuple> dataset = new ArrayList<Tuple>(datasetSize);
        for(int i=0; i < datasetSize;i++){
            dataset.add(tupleFactory.produce());
        }

        if(renormalize){
            renormalize(dataset, tupleFactory.getMaxValue());
        }

        print(dataset, tupleFactory.getClass().getSimpleName());

        ArrayList<Tuple> skyline = new ArrayList<Tuple>();

        Tuple.skylineComparison(dataset, skyline);

        print(skyline, tupleFactory.getClass().getSimpleName()+"_sky");
    }
    private static void renormalize(ArrayList<Tuple> dataset, double maxValueUniverse) {
        double[] maxValue = new double[dataset.get(0).getNumDimensions()];
        double[] minValue = new double[dataset.get(0).getNumDimensions()];

        Arrays.fill(maxValue,Double.NEGATIVE_INFINITY);
        Arrays.fill(minValue,Double.POSITIVE_INFINITY);

        //compute min max
        for(Tuple tuple:dataset){
            for(int i=0;i<tuple.getNumDimensions();i++){
                maxValue[i] = Math.max(tuple.getValue(i), maxValue[i]);
                minValue[i] = Math.min(tuple.getValue(i), minValue[i]);
            }
        }

        //normalize
        for(Tuple tuple:dataset){
            for(int i=0;i<tuple.getNumDimensions();i++){
                tuple.setValue(i, maxValueUniverse*((tuple.getValue(i)-minValue[i])/(maxValue[i]-minValue[i])));
            }
        }
    }


    public static void plot(int datasetSize, DefaultTupleFactory tupleFactory) throws IOException{
        plot(datasetSize, tupleFactory, false);
    }

    private static void print(ArrayList<Tuple> dataset, String id) throws IOException{
        PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(id+".txt", true)), true);
        for(Tuple tuple:dataset){
            output.print(id);
            for(int i=0;i<tuple.getValues().length;i++){
                output.print(' ');
                output.print(tuple.getValue(i));
            }
            output.println();
        }        
        output.close();
    }
    
    public static void main(String[] args) throws IOException{
        int datasetSize = 1000;
        int numDimensions = 2;
        int precision = 8;
        int numClusters = 4;
        double sigma = 5;
        /*
        Plotter.plot(datasetSize, new UniformTupleFactory(numDimensions, 0,
                                       Tuple.UNIVERSE_MAX_VALUE, precision));
        Plotter.plot(datasetSize, new CorrelatedTupleFactory(numDimensions, 0,
                                       Tuple.UNIVERSE_MAX_VALUE, precision));
        Plotter.plot(datasetSize, new AntiCorrelatedTupleFactory(numDimensions, 0,
                                       Tuple.UNIVERSE_MAX_VALUE, precision));
        Plotter.plot(datasetSize, new ClusterTupleFactory(numDimensions,
                numClusters, Tuple.UNIVERSE_MAX_VALUE, precision, sigma));
         */
        Plotter.plot(datasetSize, new FileTupleFactory(numDimensions,
                numClusters, Tuple.UNIVERSE_MAX_VALUE, precision,
                "ZillowData.txt", true), true);
    }

}
