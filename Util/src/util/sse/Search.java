/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import util.FileUtils;
import util.Util;
import util.statistics.StatisticCenter;

/**
 *
 * @author joao
 */
public class Search {
    private InvertedFile invertedFile;
    private Vocabulary termVocabulary;
    //the highest size(lenght) that a posting list has
    private double postingListMaxSize;
    private StatisticCenter statisticCenter;

    public Search(StatisticCenter statisticCenter, String invertedFile, Vocabulary termVocabulary){
        this.statisticCenter = statisticCenter;
        this.invertedFile = new InvertedFile(this.statisticCenter,invertedFile);
        this.termVocabulary = termVocabulary;
    }


    public void open() throws SSEExeption{
        termVocabulary.open();
        invertedFile.open();
        postingListMaxSize = invertedFile.getPostingListMaxSize();
    }
    /**
     * Returns an iterator over the elements in this list in proper sequence. In
     * descending order of ranking. From the most similar to the keywords to the
     * least similar.
     * @param query
     * @return
     */
    public Iterator<ResultEntry> query(String query) throws SSEExeption{
        HashMap<Integer, PartialScore> hash = new HashMap<Integer, PartialScore>();
        Vector queryVector = new Vector();
        Vector.vectorize(queryVector, query, termVocabulary);

        Iterator<Term> it = queryVector.iterator();
        Term keyword;
        PartialScore partialScore;
        double docWeight;
        double termWeight;
        while(it.hasNext()){
            keyword = it.next();

            Iterator<PostingListEntry> list = invertedFile.getPostingListIterator(keyword.getTermId());
            if(list!=null){ //there is document that contains the term
                termWeight =  Math.log(1+ postingListMaxSize/invertedFile.getOcurrences(keyword.getTermId()));

                PostingListEntry postingListEntry;
                while(list.hasNext()){
                    
                    postingListEntry = list.next();

                    partialScore = hash.get(postingListEntry.getDocId());
                    if(partialScore==null){
                        partialScore = new PartialScore();
                        hash.put(postingListEntry.getDocId(), partialScore);
                    }

                    //Weight of the document according to the given term.
                    docWeight = 1 + Math.log(postingListEntry.getWeight());
                    partialScore.incWeight(termWeight , docWeight);
                }               
            }
        }

        ArrayList<ResultEntry> result = new ArrayList<ResultEntry>(hash.size());
        /*
        for(Entry<Integer, PartialScore> entry: hash.entrySet()){
            result.add(new ResultEntry(entry.getKey(),
                    entry.getValue().getPartialScore()/invertedFile.getNumTerms(entry.getKey())));
        }
*/
        Collections.sort(result);
        return result.iterator();
    }

    public void close() throws SSEExeption{
        termVocabulary.close();
        invertedFile.close();
    }

    public static void main(String[] args) throws SSEExeption{
        //args = new String[] {"sif", "amenity"};
        
        System.out.println("Usage Search inverted_index_prefix query");
        Search search = new Search(null, args[0], new Vocabulary("termVocabulary.txt"));
        search.open();

        String query="";
        
        for(int i=1;i<args.length;i++){
            query+=args[i]+" ";
        }
        query= query.trim();

        long time = System.currentTimeMillis();
        Iterator<ResultEntry> queryResult = search.query(query);

        System.out.println("Results for the query '"+query+"' processed in"+Util.time(System.currentTimeMillis()-time)+":");
        for(int i=1;queryResult.hasNext();i++){
            System.out.println(i+". "+queryResult.next());
        }
        search.close();
    }
}

class PartialScore{
    private double score;

    public double getPartialScore(){
        return score;
    }

    /**
     * docWeight= ln(1+/NumDocumentsThatContainsTerm) and
     * termWeight=(1 + ln frequenceOfTermInDocument)
     * @param docWeight
     * @param termWeight
     */
    public void incWeight(double docWeight, double termWeight){
        this.score+=docWeight*termWeight;
    }

}