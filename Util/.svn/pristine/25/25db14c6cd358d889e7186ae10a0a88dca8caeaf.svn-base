/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.nra;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import util.ArraysUtils;

class Latice<E extends NRAItem>{
    private final LinkedHashMap<Integer, E> itemSet;
    private final NRAItem[] leaders;
    private double lastUpperBound=-1;
    private double maxUpperBound=Double.POSITIVE_INFINITY;
    private int topKBitmap=0;

    Latice(int length) {
        itemSet=new LinkedHashMap<Integer, E>();
        leaders = new NRAItem[(int)Math.pow(2, length)];
    }

    E get(int id) {
        return itemSet.get(id);
    }

    void insert(E candidate) {
        itemSet.put(candidate.getId(), candidate);
    }

    void remove(E item) {
        itemSet.remove(item.getId());
    }

    void update(E item, double[] upperBound) {
        if(item.updateUpperBoundScore(upperBound)>item.getScore()){
            if(! itemSet.containsKey(item.getId())){
                insert(item);
            }

            if(leaders[item.getSourceMap()]==null ||
                    item.getUpperBoundScore()>leaders[item.getSourceMap()].getUpperBoundScore()){

                leaders[item.getSourceMap()] = item;

            }
        }
    }

    boolean isTimeToPrune(double[] upperBound, double kthScore) {
        boolean result=true;
        if(lastUpperBound!=-1){
            result = (getMaxUpperBound()-(lastUpperBound-ArraysUtils.sum(upperBound)))<=kthScore;
        }
        return result;
    }

    double getMaxUpperBound() {
        return maxUpperBound;
    }

    /**
     * Update the upper bound of the leaders.
     */
    void lightUpdate(double[] upperBound, double kthScore, boolean[] irrelevantSource){
        maxUpperBound=0;
        for(int i=1;i<leaders.length;i++){
            if(leaders[i]!=null){
                maxUpperBound = Math.max(maxUpperBound, leaders[i].updateUpperBoundScore(upperBound));
                if(leaders[i].getUpperBoundScore()<=kthScore){
                    leaders[i] =null;
                    updateIrrelevantSources(irrelevantSource, i);
                }
            }
        }
    }

    private void updateIrrelevantSources(boolean[] irrelevantSource, int driedSourceBitmap) {
        //All items in top-k have been seen in the same sources of the dried source
        if( (topKBitmap & driedSourceBitmap)==driedSourceBitmap ){
            int countZeros=0;
            int index=0;
            for(int i=0;i<irrelevantSource.length && countZeros<2;i++){
                if((((driedSourceBitmap>>i)&1)==0)){
                    countZeros++;
                    index = i;
                }
            }

            if(countZeros==1 && !irrelevantSource[index]){
                if(driedSourceBitmap>=irrelevantSource.length){
                    //Checks if its imediate substes are all marked 'dead', equals to null
                    for(int i=0;i<irrelevantSource.length;i++){
                        if((((driedSourceBitmap>>i)&1)==1)  && leaders[driedSourceBitmap^(1<<i)]!=null){
                            return;
                        }
                    }
                }
                irrelevantSource[index] = true;
            }
        }
    }

    void fullUpdate(double[] upperBound, LinkedList<E> topK, double kthScore, boolean[] irrelevantSource) {
        lastUpperBound = ArraysUtils.sum(upperBound);
        maxUpperBound = 0;
        Arrays.fill(leaders, null);

        //Update topKBitMap
        for(E item:topK){
            topKBitmap |= item.getSourceMap();
        }

        //update upper bound score for all candidates
        E item=null;
        for(Iterator<E> it=itemSet.values().iterator();it.hasNext();){
            item = it.next();
            
            item.updateUpperBoundScore(upperBound);
            //Remove item if its upperBound cannot be better than kthScore
            if(item.getUpperBoundScore()<=kthScore){
                it.remove();
            }else{
                maxUpperBound = Math.max(item.getUpperBoundScore(), maxUpperBound);
                if(leaders[item.getSourceMap()]==null ||
                        leaders[item.getSourceMap()].getUpperBoundScore()<item.getUpperBoundScore()){
                    leaders[item.getSourceMap()] = item;
                }
            }
        }

        for(int i=1;i<leaders.length;i++){
            if(leaders[i]==null){
                updateIrrelevantSources(irrelevantSource, i);
            }
        }
    }
}

