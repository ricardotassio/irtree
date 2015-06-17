/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.nra;


public class NRAItem implements Comparable{
    protected int id;
    protected int sourceSize;
    protected int bitmap;
    protected double upperBoundScore;
    protected double score;

    public int getId(){
        return id;
    }

    /**
     * @param value
     * @param sourceIndex the source that was affected
     * @param sourceSize is the total number of sources that is maybe required for some applications
     */
    public void updateScore(double value, int sourceIndex, int sourceSize){
        updateSource(sourceIndex, sourceSize);
        score+=value;
    }

    /**
     * Mark the source as visited
     * @param sourceIndex
     * @param sourceSize
     */
    protected void updateSource(int sourceIndex, int sourceSize){
        bitmap = bitmap | (1<<sourceIndex); //Update the source of value
    }

    /**
     * Bitmap indicating the sources where the item was found.
     * @return
     */
    public int getSourceMap(){
        return bitmap;
    }

    public double updateUpperBoundScore(double[] upperBound) {
        upperBoundScore = 0;
        for(int i=0;i<upperBound.length;i++){
            if((bitmap & (1<<i))==0){ //The object was not found in the i source
                upperBoundScore+=upperBound[i];
            }
        }
        upperBoundScore+=score;
        return upperBoundScore;
    }

    public double getScore() {
        return score;
    }

    public double getUpperBoundScore() {
        return upperBoundScore;
    }

    @Override
    public String toString(){
        return String.format("[id=%d, lb=%.2f, ub=%.2f, brm=%d]", id,
                this.getScore(), this.getUpperBoundScore(), bitmap);
    }

    public int compareTo(Object o) {
        NRAItem other = (NRAItem) o;

        if(this.getScore()>other.getScore()){
            return 1;
        }else if(this.getScore()<other.getScore()){
            return -1;
        }else{
            return (int) (this.getId() - other.getId());
        }
    }
}
