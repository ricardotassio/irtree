/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author JOAO
 */
public class RandomUtil {
    private static final long DEFAULT_SEED = 17399225432l;
    private static Random random = new Random(DEFAULT_SEED);
    
    /*
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Queue;
     public static boolean isInsideQueue(Queue queue, Entity object){
        Entity next = queue.first();
        
        while(next!=null){
            if(next.equals(object)){
                return true;
            }
            next = queue.succ(next);
        }
        return false;        
    }
     */

    /**    
     * @return rnd the source of randomness
     */
    public static Random getRandom(){
        return random;
    }

    public static void setNewSeed(long seed){        
        random = new Random(seed);
    }

    public static double random(){
        return random.nextDouble();
    }

    public static int nextInt(int value){
        return random.nextInt(value);
    }

    public static double nextDouble(double maxValue){
        return random.nextDouble()*maxValue;
    }

    public static double nextGaussian(double m, double s2) {
        return nextGaussian()*Math.sqrt(s2)+m;
    }

    public static double nextGaussian(){
        return random.nextGaussian();
    }

    /**
     * Expnential distribution. Returns a double value between [0, 1]. The more
     * skewed, the more the values returned are near 0.
     * @param skew
     * @return
     */
     public static double nextExponential(double skew) {
        return -Math.log(1 - random.nextDouble())/skew;
     }


     ////////////////// REAL DISTRIBUTION //////////////////
     private static ArrayList<Item> realDistribution;
     private static String signature;
     /**
      * @param fileName The file used to collect the data distribution.
      * @param column The column that identifies the value, 0 for the first column
      * @throws FileNotFoundException
      * @throws IOException
      */
     public static void proccessRealDistribution(String fileName, int column) throws FileNotFoundException, IOException{
         if(!(fileName+column).equals(signature)){
            double minValue = Double.POSITIVE_INFINITY;
            double maxValue = Double.NEGATIVE_INFINITY;
            signature = fileName+column;
            HashMap<Double, Item>  temp = new HashMap<Double, Item>();
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            StringTokenizer tokens;
            int count=0;
            for(String line=input.readLine(); line!=null; line=input.readLine()){
                if(!line.startsWith("#")){
                    tokens = new StringTokenizer(line);
                    for(int i=0;i<column;i++){
                        tokens.nextToken();
                    }
                    double value = Double.parseDouble(tokens.nextToken());
                    minValue = Math.min(minValue, value);
                    maxValue = Math.max(maxValue, value);
                    Item item = temp.get(value);
                    if(item==null){
                        item = new Item();
                        item.value = value;
                        temp.put(value, item);
                    }
                    item.occurr++;
                    count++;
                }
            }
            input.close();

            double lastRange=0;
            realDistribution = new ArrayList<Item>(temp.values());

            Collections.sort(realDistribution, new Comparator<Item>(){
                public int compare(Item o1, Item o2) {
                    return o2.occurr-o1.occurr;
                }
            });

            Item item;
            for(int i=0;i<realDistribution.size();i++){
                item = realDistribution.get(i);
                item.value = (item.value-minValue)/(maxValue-minValue); //Normalize to range [0, 1]
                item.startRange = lastRange;
                item.endRange = item.startRange + ((double)item.occurr)/((double)count);
                lastRange = item.endRange;                
            }
        }
     }

     public static double nextReal(double maxValue){
        double dice = RandomUtil.nextDouble(1);
        Item item;
        for(int i=0;i<realDistribution.size();i++){
            item = realDistribution.get(i);
            if(dice>=item.startRange && dice <item.endRange){
                return item.value;
            }
        }
        return realDistribution.get(0).value*maxValue;
     }
///////////////////////////////////////////////////////////////////////////////
    private static int zipfSize;
    private static double zipfSkew;
    private static double zipfBottom;
    
    /**
     * Returns a zipf value between 0 and 1.
     * @return
     */
    public static int nextZipf(double skew, int size) {
        int rank;
        double frequency = 0;
        double dice;

        if(size!=zipfSize || skew!=zipfSkew){
            zipfSize = size;
            zipfSkew = skew;
            zipfBottom = 0;
            for(int i=1;i<size; i++) {
                zipfBottom += (1/Math.pow(i, skew));
            }
        }

        rank = random.nextInt(zipfSize);

        frequency = (1.0d / Math.pow(rank, zipfSkew))/zipfBottom;

        dice = random.nextDouble();

        while(!(dice < frequency)) {
            rank = random.nextInt(zipfSize);
            frequency = (1.0d / Math.pow(rank, zipfSkew))/zipfBottom;
            dice = random.nextDouble();
        }
        return rank;
    }
///////////////////////////////////////////////////////////////////////



    public static void main(String[] args) throws FileNotFoundException, IOException{
        double value=0;
        int size = 100;
        int[] histogram = new int[10];

        RandomUtil.proccessRealDistribution("../Spatial/realDataSet/features_RL_F2.txt", 2);
        for(int i=0;i<size;i++){
            //value = Math.abs(RandomUtil.nextGaussian(0,0.2)*domain);
            //value = RandomUtil.nextZipf(1, domain);
            value = RandomUtil.nextReal(1);
            for(int v=0;v<histogram.length;v++){
                if(value<((v+1)/(double)10)){
                    histogram[v]++;
                    break;
                }
            }
            System.out.println("value["+(i+1)+"]="+value/10);
        }
        for(int v=0;v<histogram.length;v++){
            System.out.println("Percentage of values between "+(v/(double)10)+" and "+((v+1)/(double)10)+" is "+((histogram[v]/(double)size)*100)+"%");
        }
    }
}
class Item{
    double value;
    double startRange;
    double endRange;
    int occurr;

    @Override
    public String toString(){
        return "[value="+value+", range["+startRange+", "+endRange+"], occurr="+occurr+"]";
    }
}