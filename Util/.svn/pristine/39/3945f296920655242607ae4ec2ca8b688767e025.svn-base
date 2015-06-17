/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

public class Mapping {
    private String fileName;
    //big id to small id
    private HashMap<Long, Integer> map;
    

    public Mapping(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns a small-id given a big-id. If the big-id does not exist in the mapping,
     * the big-id is stored and a small-id is returned.
     * @param str
     * @return
     * @throws SSEExeption
     */
    public Integer getId(Long bigId){
        Integer id=null;
        id = map.get(bigId);
        if(id==null){            
            id = map.size()+1;
            map.put(bigId, id);
        }
        return id;
    }

    public Long getBigId(Integer smallId){
        for(Entry<Long, Integer> entry : map.entrySet()){
            if(entry.getValue()==smallId){
                return entry.getKey();
            }
        }
        return null;
    }

    public Set<Entry<Long,Integer>> entrySet(){
        return map.entrySet();
    }


    public long getSizeInBytes(){
        return new File(fileName).length();
    }

    public void open() throws FileNotFoundException, IOException{
        map = new HashMap<Long, Integer>();
        File file = new File(fileName);
        if(file.exists()){
            LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(file)));
            String line;
            int separator;
            for(int i=1; (line=reader.readLine())!=null ;i++){
                separator = line.indexOf(' ');
                map.put(Long.parseLong(line.substring(0, separator)),
                        Integer.parseInt(line.substring(separator+1)));
            }
            reader.close();
        }else{
            FileUtils.createDirectories(fileName);
        }
        
    }

    public int size(){
        return map.size();
    }

    public void flush() throws IOException{
        PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)), true);
        
        for(Entry<Long,Integer> entry:entrySet()){
            output.println(entry.getKey()+" "+entry.getValue());
        }                
        output.close();
    }

    public void close() throws IOException{
        flush();
        map=null;
    }

    public boolean delete() throws IOException{
        close();
        return new File(fileName).delete();
    }

    @Override
    public String toString(){
        return map.toString();
    }

    public static void main(String[] args)throws Exception{
        Mapping map = new Mapping(args[0]);
        map.open();
        System.out.println("Mapping:"+args[0]+
                ", getTermId("+args[1]+")="+map.getId(Long.parseLong(args[1])));

        map.close();
    }
}
