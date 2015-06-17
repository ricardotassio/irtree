/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import util.FileUtils;
import util.file.ListIterator;
import util.file.ListStorage;
import util.statistics.StatisticCenter;

/**
 *
 * @author joao
 */
public class InvertedFile {
    private ListStorage<PostingListEntry> postingLists;
    private Vocabulary vocabulary;     //maps big term_ids to small ids

    private String prefixFile;
    private StatisticCenter statistiCenter;

    /**
     *
     * @param statistiCenter
     * @param prefixFile
     * @param blockSize The default size of each block usted to store the inverted files
     */
    public InvertedFile(StatisticCenter statistiCenter, String prefixFile){
        this.prefixFile = prefixFile;
        this.statistiCenter = statistiCenter;
    }

    public void open()throws SSEExeption{
        try {
            FileUtils.createDirectories(prefixFile);

            vocabulary = new Vocabulary(prefixFile+".txt");
            vocabulary.open();

            postingLists = new ListStorage<PostingListEntry>(statistiCenter,
                    "inverted_files", prefixFile, PostingListEntry.SIZE, PostingListEntry.FACTORY);
            postingLists.open();
    
        } catch (Exception ex) {
            throw new SSEExeption(ex);
        }
    }

    public String getPrefixFile(){
        return this.prefixFile;
    }

    /**
     * Returns a PostingListIterator for the given the term (termId).
     * @param termId
     * @return PostingList
     * @throws SSEExeption
     */
    public ListIterator<PostingListEntry> getPostingListIterator(int termId)
            throws SSEExeption{
        try {
            return postingLists.getEntries(getSmallId(termId));
        } catch (Exception ex) {
            throw new SSEExeption(ex);
        }
    }

    private int getSmallId(int bigId) throws SSEExeption{
        return vocabulary.getId(""+bigId);
    }

    public List<PostingListEntry> getPostingList(int term_id) throws SSEExeption {
        ArrayList<PostingListEntry> list = new ArrayList<PostingListEntry>();
        ListIterator<PostingListEntry> it = this.getPostingListIterator(term_id);
        while(it!=null && it.hasNext()){
            list.add(it.next());
        }
        return list;
    }

    /**
     * Stores the posting list of a given term. This method must by synchronized.
     * @param termId
     * @param list, the posting list to be inserted
     * @throws SSEExeption
     */
    public void putPostingList(int termId, List<PostingListEntry> list) throws SSEExeption{
        this.putPostingList(termId, list.iterator(), list.size());
    }

    public void putPostingList(int termId, Iterator<PostingListEntry> iterator, int size) throws SSEExeption{
        try {
            postingLists.putList(getSmallId(termId), iterator, size);
        } catch (Exception ex) {
            throw new SSEExeption(ex);
        }
    }

    public void close()throws SSEExeption{
        try {
            vocabulary.close();
            postingLists.close();
        } catch (Exception ex) {
            throw new SSEExeption(ex);
        }
    }

    /**
     * Returns the number of documents in which the given term occurs.
     * @param termId
     * @return
     * @throws SSEExeption
     */
    public int getOcurrences(int termId) throws SSEExeption{
        try {
            return postingLists.getListSize(getSmallId(termId));
        } catch (Exception ex) {
            throw new SSEExeption(ex);
        }
    }

    public long getSizeInBytes() throws SSEExeption{
        try{
            return postingLists.getSizeInBytes() + vocabulary.getSizeInBytes();
        } catch (Exception ex) {
            throw new SSEExeption(ex);
        }
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        try {
            ArrayList<Entry<String, Integer>> terms = new ArrayList<Entry<String, Integer>>(vocabulary.entrySet());
            str.append('[');
            int term_id;
            for(Entry<String,Integer> term:terms){
                term_id = Integer.parseInt(term.getKey().toString());
                str.append("(").append(term_id).append(")");
                str.append(getPostingList(term_id).toString());
                str.append(',');
            }
            if(str.charAt(str.length()-1)==',') str.delete(str.length()-1, str.length());
            str.append(']');
        } catch (SSEExeption ex) {
            throw new RuntimeException(ex);
        }
        return str.toString();
    }

    /**
     * This methods is expensive. Use
     * @return
     * @throws SSEExeption
     */
    double getPostingListMaxSize() throws SSEExeption{
        int maxValue = Integer.MIN_VALUE;
        try {            
            for(int smallId : postingLists.getIds()){
                maxValue = Math.max(maxValue, postingLists.getListSize(smallId));
            }
        } catch (Exception ex) {
            throw new SSEExeption(ex);
        }
        return maxValue;
    }

    public static void delete(String fileName) {
        ListStorage.delete(fileName);
        new File(fileName + ".txt").delete();//delete the vocabulary
    }

    public static void main(String[] args) throws SSEExeption{
        InvertedFile index = new InvertedFile(null, args[0]);
        Vocabulary vocabulary = new Vocabulary("termVocabulary.txt");

        vocabulary.open();
        index.open();
        
        System.out.println("list["+args[1]+"]="+index.getPostingList(
                vocabulary.getId(args[1])));

        index.close();
        vocabulary.close();
    }

}
