/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.nra;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import util.ArraysUtils;

/**
 *
 * @author joao
 */
public class NRA<E extends NRAItem> implements Runnable {
    //Stores the topK results
    private final LinkedList<E> topK;

    private final HashMap<Integer, E> topkItems;

    //The source from where the data comes from
    private final Source<E>[] sources;

    //Maintaing the current upperBound
    protected final double[] upperBound;

    //Maintains the upperBound in very optimized way
    private Latice<E> latice;

    //Mark as irrelevant the sources that cannot contrubute with the result
    private final boolean[] irrelevantSource;

    private final int k;

    private int srcIndex=0;
    protected int nextSource=0;

    public NRA(Source<E>[] sources, int k){
        this.sources = sources;
        this.topK = new LinkedList<E>();
        this.latice = new Latice<E>(sources.length);
        this.upperBound = new double[sources.length];
        Arrays.fill(upperBound, Double.POSITIVE_INFINITY);
        this.irrelevantSource = new boolean[sources.length];
        this.k = k;
        topkItems = new HashMap<Integer, E>(k);
    }

    protected int selectSource(boolean[] irrelevantSource){
        int selectedSource=nextSource;
        nextSource = (nextSource+1) % irrelevantSource.length; //round robin
        //Selects only relevant sources.
        for(int i=0; i<irrelevantSource.length && irrelevantSource[selectedSource];i++){
            selectedSource = nextSource;
            nextSource = (nextSource+1) % irrelevantSource.length; //round robin
        }
        return selectedSource;
    }

    protected void markSourceIrrelevant(int srcIndex, int sourcesSize){
        irrelevantSource[srcIndex]=true;
        upperBound[srcIndex] = 0.0;
    }

    protected void updateUpperBound(double score, int srcIndex, int sourceSize){
        upperBound[srcIndex] = score;
    }

    private E nextObject(){
        srcIndex=selectSource(irrelevantSource);
        if(srcIndex>=0){
            E obj =sources[srcIndex].next();
            if(obj==null){
                markSourceIrrelevant(srcIndex, sources.length);
                boolean allAreIrrelevant = true;
                for(int i=0; i<irrelevantSource.length && allAreIrrelevant;i++){
                    if(!irrelevantSource[i]){
                        allAreIrrelevant = false;
                    }
                }
                if(!allAreIrrelevant){
                    obj = nextObject();
                }
            }else{             
                updateUpperBound(obj.getScore(), srcIndex, sources.length);
            }
            return obj;
        }
        return null;
    }

    public void run() {
        double kthScore=0;        

        E obj = null;
        boolean hasObjects = true;
        while(hasObjects && growingPhase(topK, upperBound, kthScore)){
            obj = nextObject();
            if(obj!=null){
                E candidate = latice.get(obj.getId());
                if(candidate==null){
                    candidate = obj;
                    candidate.updateSource(srcIndex, sources.length);
                    latice.insert(candidate);
                }else{
                    candidate.updateScore(obj.getScore(), srcIndex, sources.length);
                }

                if(candidate.getScore()>kthScore){
                    updateTopK(topK, candidate,k);
                    kthScore = topK.size()==k ? topK.getFirst().getScore() : kthScore;
                }
            }else{
                hasObjects = false;
            }
        }

        //remove from the latice the items that are in the topk set
        for(E item:topK){
            latice.remove(item);
        }

        latice.fullUpdate(upperBound, topK, kthScore, irrelevantSource);//update all items in the latice.

        //Shrinking phase
        while(hasObjects && latice.getMaxUpperBound() > kthScore){

            obj=nextObject();

            if(obj!=null){
                E candidate = latice.get(obj.getId());

                if(candidate==null){//check if it is in the topK
                    candidate = topkItems.get(obj.getId());
                }

                if(candidate!=null){
                    candidate.updateScore(obj.getScore(), srcIndex, sources.length);
                    if(candidate.getScore()>kthScore){
                        E removedItem = updateTopK(topK, candidate, k);
                        if(removedItem!=null){ //It means that candidate was not in topK, it was inserted now.
                            latice.remove(candidate);
                            latice.update(removedItem, upperBound);
                        }
                        kthScore = topK.size()==k ? topK.getFirst().getScore() : kthScore;
                    }else{
                        latice.update(candidate, upperBound);
                    }
                }

                if(latice.isTimeToPrune(upperBound, kthScore)){
                    latice.fullUpdate(upperBound, topK, kthScore, irrelevantSource);//update all items in the latice.
                }else{
                    latice.lightUpdate(upperBound, kthScore, irrelevantSource);
                }
            }else{
                hasObjects=false;
            }   
        }        
    }

    public Iterator<E> getResult(){

        return new Iterator<E>(){
            public boolean hasNext() {
                return topK.size()>0;
            }
            public E next() {
                return topK.removeLast();
            }
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }


    private boolean growingPhase(LinkedList<E> topK, double[] upperBound, double kthScore){
        return (topK.isEmpty() ||
                (ArraysUtils.sum(upperBound)>kthScore));
    }

    private E updateTopK(LinkedList<E> topK, E candidate, int k){
        ListIterator<E> iterator = topK.listIterator();
        E item=null;
        while(iterator.hasNext()){
            item = iterator.next();
            if(candidate.getId()==item.getId()){ //remove duplicates
                iterator.remove();
            }else if(candidate.getScore()==item.getScore()){
                if(candidate.getId()>item.getId()){ //take in acount the ids
                    iterator.previous();
                    break; //break in the correct position where the item must be inserted
                }
            }else if(candidate.getScore()<item.getScore()){
                iterator.previous();
                break; //break in the correct position where the item must be inserted
            }
        }

        topkItems.put(candidate.getId(), candidate);

        iterator.add(candidate);

        if(topK.size()> k){
            item = topK.removeFirst();
            topkItems.remove(item.getId());
        }else{
            item =null;
        }
        return item;
    }
}
