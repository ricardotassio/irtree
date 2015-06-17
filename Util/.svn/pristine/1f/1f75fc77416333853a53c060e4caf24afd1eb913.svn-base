/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;

import util.Util;

/**
 *
 * @author joao
 */
public class ResultEntry implements Comparable<Object>{
    private final int docId;
    private final double score;

    /**
     *
     * @param docId
     * @param docSize number of distinct terms in the document
     */
    public ResultEntry(int docId, double score){
        this.docId = docId;
        this.score = score;
    }
    /**
     * @return the docId
     */
    public int getDocId() {
        return docId;
    }

    /**
     * @return the weight
     */
    public double getScore() {
        return score;
    }


    @Override
    public String toString(){
        return "["+docId+", "+Util.cast(getScore(),3)+"]";
    }

    public int compareTo(Object o) {
        ResultEntry other = (ResultEntry)o;
        if(this.getScore()<other.getScore()){
            return 1;
        }else if(this.getScore()>other.getScore()){
            return -1;
        }
        return 0;
    }
}
