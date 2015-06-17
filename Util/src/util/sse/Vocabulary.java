/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import util.FileUtils;

/**
 *
 * @author joao
 */
public class Vocabulary {
    private String fileName;
    private HashMap<String, Integer> map;
    private PrintWriter output;


    public Vocabulary(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the id that identify the term. If the term does not exist in the vocabulary,
     * the term is stored and a unique id is returned.
     * @param str
     * @return
     * @throws SSEExeption
     */
    public int getId(String str) throws SSEExeption{
        Integer id=null;
        id = map.get(str);
        if(id==null){
            output.println(str);
            id = map.size()+1;
            map.put(str, id);
        }
        return id;
    }

    public String getTerm(int id) throws SSEExeption{
        for(Entry<String, Integer> entry : map.entrySet()){
            if(entry.getValue()==id){
                return entry.getKey();
            }
        }
        return null;
    }

    public Set<Entry<String,Integer>> entrySet(){
        return map.entrySet();
    }


    public long getSizeInBytes(){
        return new File(fileName).length();
    }
    
    public void open() throws SSEExeption{
        try{
            map = new HashMap<String, Integer>();
            File file = new File(fileName);
            if(file.exists()){
                LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(file)));
                String term;
                for(int i=1; (term=reader.readLine())!=null ;i++){
                    map.put(term, i);
                }
                reader.close();                
            }else{                
                FileUtils.createDirectories(fileName);               
            }
            output = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)), true);
        } catch (IOException ex) {
            throw new SSEExeption(ex);
        }
    }

    public int size(){
        return map.size();
    }
    
    public void close() throws SSEExeption{
        output.close();
        output=null;
        map=null;
    }

    @Override
    public String toString(){
        return map.toString();
    }

    public static void main(String[] args)throws Exception{
        Vocabulary vocabulary = new Vocabulary(args[0]);
        vocabulary.open();        
        System.out.println("vocabulary:"+args[0]+", getTermId("+args[1]+")="+vocabulary.getId(args[1]));
        
        vocabulary.close();
    }
}
