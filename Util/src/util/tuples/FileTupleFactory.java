/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.tuples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import util.ArraysUtils;
import util.Util;

/**
 *
 * @author joao
 */
public class FileTupleFactory extends DefaultTupleFactory{
    private static ReaderShutdownHook readerShutdownHook = null;
    private final boolean ignoreTuplesWithZeros;

    /**
     * The first line of the file must contains the maxValue for each dimension.
     * Marking one dimenions with 0 (zero) means that this value shoud be ignored.
     * The maximum number of dimensions is the number of maxValues different of 0 (zero).
     * If one wants to produce more tuples than the amount of tuples given in the
     * files, the file is read again and starts producing tuples from the begining.
     * We assume that the values are separated using space.
     * @param numDimensions
     * @param clusters
     * @param maxValue
     * @param precision
     * @param fileName
     */
    public FileTupleFactory(int numDimensions, int clusters, double maxValue,
            int precision, String fileName, boolean ignoreTuplesWithZeros){
        super(numDimensions, clusters, maxValue, precision);
        this.ignoreTuplesWithZeros = ignoreTuplesWithZeros;
        if(readerShutdownHook==null){
            readerShutdownHook = new ReaderShutdownHook(fileName);
            Runtime.getRuntime().addShutdownHook(readerShutdownHook);
        }
    }

    public Tuple produce() {
        try {                        
            double[] values;
            do{
                String line = readerShutdownHook.nextLine();
                values= getValues(line, this.dimensions,
                    readerShutdownHook.getMaxValues());
            }while(ignoreTuplesWithZeros && ArraysUtils.min(values)==0);

            //the values are in a range from 0 and 1
            for(int i=0;i<values.length;i++){
                values[i] = Util.cast(values[i]*this.maxValue, this.precision);
            }
            Tuple tuple = new Tuple(values);

            if(clusters>0){
                tuples.add(tuple);
            }
            return tuple;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    
    static double[] getValues(String line, int dims, double[] maxValues){
        StringTokenizer tokens = new StringTokenizer(line);

        double[] values = new double[dims];
        int d=0;
        String token=null;
        for(int i=0;d<dims && tokens.hasMoreTokens();i++){
            token = tokens.nextToken();
            if(maxValues[i]!=0){ //should get this value
                try{
                    values[d] =Double.parseDouble(token)/maxValues[i];
                }catch(NumberFormatException e){
                    values[d] = 0;
                }
                d++;
            }
        }

        return values;
    }

    public static void main(String[] args) throws Exception{
        boolean printMaxValues = true;
        if(printMaxValues){
            BufferedReader reader = new BufferedReader(new FileReader("ZillowData/ZillowData_orig.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("ZillowData/ZillowData2.txt"));
            double[] values;
            double[] map = new double[]{1,1,1,1,1,1,1};
            double[] maxValues = new double[map.length];

            String line = reader.readLine();
            int countTotalLines = 0;
            int countValidLines = 0;
            while(line!=null){
                countTotalLines++;
                values = getValues(line, map.length, map);
                int equalZero=0;
                for(int i=0;i<values.length;i++){
                   if(values[i]==0){
                      equalZero++;
                   }
                }
                //Keep only the lines which has 1 zero in the maximum.
                if(equalZero==0){
                    countValidLines++;
                    for(int i=0;i<maxValues.length;i++){
                        if(values[i]>maxValues[i]){
                            maxValues[i] = values[i];
                        }
                    }
                    writer.write(line+"\n");
                }
                line = reader.readLine();
            }
            System.out.println("Total lines="+countTotalLines+", where "+countValidLines+" are valid.");
            System.out.println("The line bellow contains the maxValues for each dimension.");
            for(int i=0;i<maxValues.length;i++){
                System.out.print(maxValues[i]+" ");
            }
            System.out.println();
            writer.flush();
            writer.close();
            reader.close();
        }else{
            int size = 100000;
            int NumDimensions=2;
            int numClusters = 5;
            int precision=2;            
            FileTupleFactory tupleFactory = new FileTupleFactory(NumDimensions,
                    numClusters, Tuple.UNIVERSE_MAX_VALUE, precision,
                    "ZillowData/ZillowData.txt", false);

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
}
 class ReaderShutdownHook extends Thread {
    private static BufferedReader reader;
    private static double[] maxValues=null;

    private String fileName;
    
    public ReaderShutdownHook(String filename){
        this.fileName = filename;
    }

    private BufferedReader createReader() throws FileNotFoundException, IOException{
        reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        StringTokenizer tokens = new StringTokenizer(line);
        double[] map = new double[tokens.countTokens()];
        Arrays.fill(map, 1);
        maxValues = FileTupleFactory.getValues(line, tokens.countTokens(), map);
        return reader;
    }

    public double[] getMaxValues() throws FileNotFoundException, IOException{
        if(reader==null){
            reader = createReader();
        }
        return maxValues;
    }

    public String nextLine() throws FileNotFoundException, IOException{
        if(reader==null){
            reader = createReader();
        }

        String line = reader.readLine();
        if(line==null){
            reader.close();
            reader = createReader();
            line = reader.readLine();
        }
        return line;
    }


    @Override
    public void run() {
        try {
            if(reader!=null) reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
