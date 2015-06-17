/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.StringTokenizer;
import util.file.ColumnFileException;
import util.file.EntryStorage;
import util.file.IntegerEntry;

/**
 *
 * @author joao
 */
public class Vector {
    private static final String FILTER = " \n\\\",;.:_-'<>*|/+?={}()[]$%&#!@";
    private final HashMap<Integer, Term> map;
    private final int id;

    public Vector(){
        this(-1);
    }

    public Vector(int id){
        this.id=id;
        this.map = new HashMap<Integer, Term>();
    }

    public int getId(){
        return id;
    }

    public Term getTerm(int termId){
        return map.get(termId);
    }

    public boolean contains(int termId){
        return map.containsKey(termId);
    }

    /**
     * Returns the number of individual terms of the document.
     * @return
     */
    public int size(){
        return map.size();
    }

    public Collection<Term> getTerms(){
        return map.values();
    }

    public Iterator<Term> iterator(){
        return map.values().iterator();
    }

    public void add(Term term){
        this.put(term.getTermId(), term);
    }
    /**
     * This vector will contains all its terms plus other terms. For terms in
     * both the maximum score will be stored.
     *
     * @param other
     */
    public void agg(Vector other) {
        Iterator<Term> it = other.iterator();
        Term thisTerm;
        Term otherTerm;
        while(it.hasNext()){
            otherTerm = it.next();
            thisTerm = this.getTerm(otherTerm.getTermId());
            if(thisTerm==null){
                this.put(otherTerm.getTermId(), otherTerm.clone());
            }else if(otherTerm.getWeight()>thisTerm.getWeight()){
                thisTerm.setWeight(otherTerm.getWeight());
            }
        }
    }

    @Override
    public boolean equals(Object obj){
        return map.equals(((Vector)obj).map);
    }

    public boolean contains(Vector other) {
        if(other.size()>this.size()){
            return false;
        }
        Iterator<Term> it = other.iterator();
        Term otherTerm;
        Term thisTerm;
        while(it.hasNext()){
            otherTerm = it.next();
            thisTerm = this.getTerm(otherTerm.getTermId());
            if(thisTerm==null || thisTerm.getWeight() <otherTerm.getWeight()){
                return false;
            }
        }

        return true;
    }

    /**
     * Vectorize the given txt using the termVocabulry. As result the vector is filled.
     * @param termVocabulary
     * @param queryVector
     * @param txt
     * @return the number of terms in the vector
     * @throws SSEExeption
     */
    public static int vectorize(Vector vector, String txt, Vocabulary termVocabulary) throws SSEExeption{
        StringTokenizer tokens = new StringTokenizer(txt, FILTER);
        
        int numTerms =0;
        String token;
        Term term;
        int termId;
        while(tokens.hasMoreTokens()){
            token = tokens.nextToken().toLowerCase();
            if(isValid(token)){
                termId = termVocabulary.getId(token);
                term = vector.getTerm(termId);
                if(term==null){
                    term = new Term(termId);
                    vector.put(termId, term);
                }
                term.incNumOccurrences();
                numTerms++;
            }
        }
        return numTerms;
    }

    /**
     * Set the weight of the terms in the document vector
     * @param vecturm
     * @return the root of the sum of the square of the weights (document length)
     */
    public static double computeDocWeight(Vector vector){
        Iterator<Term> terms = vector.iterator();
        double sumeOfWeightSquare=0;
        double weight;
        Term term;
        while(terms.hasNext()){
            term = terms.next();
            //The weight is implemented as proposed by zobel.
            weight = 1+ Math.log(term.getNumOccurrences());
            term.setWeight(weight);
            sumeOfWeightSquare+=weight*weight;
        }
        return Math.sqrt(sumeOfWeightSquare);
    }

    public static double computeQueryWeight(Vector queryVector, int datasetSize,
            EntryStorage<IntegerEntry> termDocFrequency) throws SSEExeption, IOException,
            ColumnFileException{
        Iterator<Term> terms = queryVector.iterator();
        double sumeOfWeightSquare=0;
        double weight;
        IntegerEntry entry;
        Term term;
        while(terms.hasNext()){
            term = terms.next();
            
            //The weight is implemented as proposed by zobel.
            entry = termDocFrequency.getEntry(term.getTermId());
            if(entry!=null){
                weight = Math.log(1 + datasetSize/(double)entry.getValue());
                term.setWeight(weight);
            }else{
                System.out.println("--> term.id="+term.getTermId()+" was not found int the collection!!!");
                weight =0; //because the term does not exist in the collection
            }

            sumeOfWeightSquare+=weight*weight;
        }
        return Math.sqrt(sumeOfWeightSquare);
    }

    public static boolean hasTokens(String txt){
        StringTokenizer tokens = new StringTokenizer(txt, FILTER);
        String token;
        while(tokens.hasMoreTokens()){
            token = tokens.nextToken();
            if(isValid(token)){
                return true;
            }
        }
        return false;
    }

    private static boolean isValid(String token){
        if(token.length()<=1){
            return false;
        }
        for(int i=0;i<token.length();i++){
            if(!Character.isLetter(token.charAt(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * http://en.wikipedia.org/wiki/Vector_space_model
     * http://www.miislita.com/term-vector/term-vector-3.html
     */
    public static double cosine(Vector v1, Vector v2){
        double v1Length=0;
        double v2Length = 0;
        double dotProduct=0;

        Term term;
        Iterator<Term> it = v1.iterator();
        while(it.hasNext()){
            term = it.next();
            v1Length+=Math.pow(term.getWeight(), 2);
        }
        v1Length = Math.sqrt(v1Length);

        it = v2.iterator();
        while(it.hasNext()){
            term = it.next();
            if(v1.contains(term.getTermId())){
                dotProduct+=term.getWeight()*v1.getTerm(term.getTermId()).getWeight() ;
            }
            v2Length+=Math.pow(term.getWeight(), 2);
        }
        v2Length = Math.sqrt(v2Length);

        return dotProduct/(v1Length*v2Length);
    }

    /**
     * http://en.wikipedia.org/wiki/Vector_space_model
     * http://www.miislita.com/term-vector/term-vector-3.html
     */
    public static double fastCosine(Vector v1, Vector v2){        
        Vector small = v1.size()<v2.size() ? v1 : v2;
        Vector big = v1.size()<v2.size() ? v2 : v1;

        if(small.size()<=0){
            return 0;
        }else{
            double dotProduct=0;
            Term tSmall;
            Term tBig;
            Iterator<Term> it = small.iterator();
            while(it.hasNext()){
                tSmall = it.next();
                tBig = big.getTerm(tSmall.getTermId());

                if(tBig!=null){
                    dotProduct+=tSmall.getWeight()*tBig.getWeight();
                }
            }

            return dotProduct/(v1.size()*v2.size());
        }
    }

    @Override
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append("[");

        Iterator<Term> it = this.iterator();
        while(it.hasNext()){
            buf.append(it.next());
            buf.append(' ');
        }
        buf.deleteCharAt(buf.length()-1);
        buf.append(']');
        return buf.toString();
    }

    private void put(int termId, Term term) {
        map.put(termId, term);
    }
}
