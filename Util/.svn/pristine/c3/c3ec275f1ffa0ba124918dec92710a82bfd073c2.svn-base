/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.experiment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ArraysUtils;
import util.statistics.DefaultStatisticCenter;

/**
 *
 * @author joao
 */
public abstract class DefaultExperimentManager extends DefaultStatisticCenter implements ExperimentManager{
    
    private Properties properties;

    private int round;

    private long startTime;


    public DefaultExperimentManager(Properties props, int round){
        this.properties = props;
        this.round = round;
    }

    public void open() throws ExperimentException {
        this.startTime = System.currentTimeMillis();


        String output = getProperties().getProperty("experiment.output");
        if(output==null){
            throw new ExperimentException("Experiment properties should contains"+
                    " 'experiment.output' that is the directory where the experiment" +
                    " results are stored!");
        }
        new File(output+"/"+getNameID()).mkdirs();
    }


    public void close() throws ExperimentException {
        try {            
            saveReport();

            aggregateResults(getProperties().getProperty("experiment.output")+"/"+getNameID(),
                    Boolean.parseBoolean(getProperties().getProperty("experiment.discardMinMax")));

            FileOutputStream fileOutput = new FileOutputStream(
                    getProperties().getProperty("experiment.output")+
                    "/"+getNameID()+"/"+getNameID()+".properties");
            getProperties().store(fileOutput,
                    "Experiment (round "+round+") started at "+new Date(startTime)+
                    ", and finished at "+new Date(System.currentTimeMillis()));
            fileOutput.flush();
            fileOutput.close();
        } catch (IOException ex) {
            Logger.getLogger(DefaultExperimentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveReport() throws IOException{        
        DataOutputStream output = new DataOutputStream(
                    new FileOutputStream(getProperties().getProperty("experiment.output")+
                    "/"+getNameID()+"/"+getNameID()+"_round_"+getRound()+".txt"));

        output.writeBytes("#"+getOutputCaptions() +" item value min max observations\n");


        output.writeBytes(getStatus(getOutputVariables()));
        output.flush();
        output.close();
        
    }



    //The value is the item before observation, maximum, minimum, and stdDev starting from the end
    private static int findValuePosition(String line){
        char[] chars = line.toCharArray();
        int count=0;
        int pos = chars.length-1;
        for(;pos>0;pos--){
            if(chars[pos]==' '){
                count++;
                if(count==5){
                    break;
                }
            }
        }
        return pos;
    }

    public static void aggregateResults(String dirName, boolean discardMinMax) throws FileNotFoundException, IOException{
        //Remove the directory mark "/" at the end of the name
        dirName = dirName.endsWith("/")?dirName.substring(0, dirName.length()-1):dirName;

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.startsWith(".") && name.contains("round");
        }};

        HashMap<String, ArrayList<Double>> aggResult = new HashMap<String, ArrayList<Double>>();
        String caption =null;
        File dir = new File(dirName);
        String[] children =  dir.list(filter);
        String line;
        for (int i=0; i<children.length; i++) {
            String filename = children[i];
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(dirName+"/"+filename)));
            line = input.readLine();
            if(caption==null){
                caption=line.substring(1, findValuePosition(line));
            }
            for(line=input.readLine(); line!=null; line=input.readLine()){
                int pos = findValuePosition(line);
                String itemName=line.substring(0,pos).trim();
                double value =Double.parseDouble(line.substring(pos, line.indexOf(' ', pos+1)));
                ArrayList<Double> itemResult = aggResult.get(itemName);
                if(itemResult==null){
                    itemResult = new ArrayList<Double>();
                    aggResult.put(itemName, itemResult);
                }
                itemResult.add(value);
            }
            input.close();
        }

        StringBuffer str = new StringBuffer();
        for(String key:aggResult.keySet()){
            ArrayList<Double> itemResult = aggResult.get(key);
            if(discardMinMax && itemResult.size()>2){
                Collections.sort(itemResult);
                itemResult.remove(0); //remover first
                itemResult.remove(itemResult.size()-1); //remover last
            }

            double[] values = ArraysUtils.toArray(itemResult);
            str.append(key);
            str.append(' ');
            str.append(ArraysUtils.mean(values));
            str.append(' ');
            if(values.length>1){
                str.append(ArraysUtils.stdDev(values));
                str.append(' ');
            }else{
                str.append("- ");
            }
            str.append(values.length);
            str.append("\n");
        }
        DataOutputStream output = new DataOutputStream(
                    new FileOutputStream(dirName+"/"+
                    dirName.substring(dirName.lastIndexOf("/"))+"_agg.txt"));

        output.writeBytes("#"+caption +" mean stdev observations\n");


        output.writeBytes(str.toString());
        output.flush();
        output.close();
    }
    

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @return the round
     */
    public int getRound() {
        return round;
    }

    public static void main(String[] args)throws Exception{
        System.out.println("Usage util.experiment.DefaultExperimentManager"+
                "[directory with experiment results] [discardMinMax (boolean)]");

        DefaultExperimentManager.aggregateResults(args[0], Boolean.parseBoolean(args[1]));
    }
}
