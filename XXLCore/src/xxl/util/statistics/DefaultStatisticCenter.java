/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xxl.util.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author joao
 */
public class DefaultStatisticCenter implements StatisticCenter{
    private final HashMap<String, Tally> tallies;
    private final HashMap<String, Count> counts;

    public DefaultStatisticCenter(){
        tallies = new HashMap<String, Tally>();
        counts = new HashMap<String, Count>();
    }

    public Tally getTally(String name) {
        Tally tally = tallies.get(name);

        if(tally==null){
            tally = new Tally(name);
            tallies.put(name, tally);
        }
        return tally;
    }

    public Count getCount(String name) {
        Count count = counts.get(name);

        if(count==null){
            count = new Count(name);
            counts.put(name, count);
        }
        return count;
    }

    public void resetTallies() {
        reset(tallies);
    }

    public void resetCounts() {
        reset(counts);
    }

    public void resetStatistics(){
        resetTallies();
        resetCounts();
    }

    private void reset(HashMap<String, ? extends StatisticObject> statistics){
        for(StatisticObject obj:statistics.values()){
            obj.reset();
        }
    }

    public String getStatus(){
        return getStatus("");
    }
    
    /**
     * The same as getStatus, however it appends a prefix at each result. It
     * is very useful for plotting data using Gnuplot
     * @param fixedName
     * @return
     */
    public String getStatus(String prefix){
        ArrayList<String> results = new ArrayList<String>();
        
        for(Tally tally:tallies.values()){
            StringBuilder result = new StringBuilder();

            result.append(prefix); result.append(' ');
            result.append(tally.getName()); result.append(' ');
            result.append(tally.getMean()); result.append(' ');
            if(tally.getObservations()<2){
                result.append("-"); result.append(' ');
            }else{
                result.append(tally.getStdDev()); result.append(' ');
            }
            
            result.append(tally.getMinimum()); result.append(' ');
            result.append(tally.getMaximum()); result.append(' ');            
            result.append(tally.getObservations());
            
            results.add(result.toString());
        }

        for(Count count:this.counts.values()){
            results.add(getResult(prefix, count, 1));
        }
        
        Collections.sort(results);

        return results.toString();
    }

    private String getResult(String fixedName, Count count, double divisor){
        StringBuffer result = new StringBuffer();
        result.append(fixedName); result.append(' ');
        result.append(count.getName()); result.append(' ');
        result.append(count.getValue()/divisor); result.append(' ');
        result.append('-'); result.append(' '); //To be compatible with Tallyes.
        result.append(count.getMinimum()); result.append(' ');
        result.append(count.getMaximum());result.append(' ');
        result.append(count.getObservations());
        return result.toString();
    }

}
