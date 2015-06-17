/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;

import java.util.Iterator;
import java.util.List;
import util.statistics.StatisticCenter;

/**
 *
 * @author joao
 */
public class Index {
    private InvertedFile invertedFile;
    private StatisticCenter statisticCenter;

    public Index(StatisticCenter statisticCenter, String invertedFile){
        this.invertedFile = new InvertedFile(statisticCenter, invertedFile);
        this.statisticCenter = statisticCenter;
    }

    public StatisticCenter getStatisticCenter(){
        return this.statisticCenter;
    }

    public void open() throws SSEExeption{
         invertedFile.open();
    }

    /**
     * This method requires that you explicitly open/close the index.
     * @param docId
     * @param vector
     */
     public void index(Vector vector) throws SSEExeption{
        Iterator<Term> terms = vector.iterator();
        Term term;
   
        while(terms.hasNext()){
            term = terms.next();

            List<PostingListEntry> list = invertedFile.getPostingList(term.getTermId());
            list.add(new PostingListEntry(vector.getId(), term.getWeight()));
            invertedFile.putPostingList(term.getTermId(), list);
        }
    }
    

    /**
     * This method calls flush and closes any file that was openned.
     * @throws SSEExeption
     */
    public void close() throws SSEExeption{
        invertedFile.close();
    }
}
