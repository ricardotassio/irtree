/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import util.FileUtils;
import util.RandomUtil;
import util.Util;
import util.config.Settings;

/**
 *
 * @author joao
 */
public class ExperimentRunner implements Runnable{
    private ExperimentFactory factory;
    private String[] args;
    
    public ExperimentRunner(ExperimentFactory factory, String[] args){
        this.factory = factory;
        this.args = args;
    }

    public void run() {
        try{
            System.out.println(System.getProperty("user.dir"));

            //Read properties file.
            Properties properties = null;
            if(args.length==1){
                properties = Settings.loadProperties(args[0]);
            }else{
                System.out.println("Wrong parameters!!! Usage:");
                System.out.println("java -Xms256M -Xmx1024M -jar Spatial.jar  [experiment.properties]");
                System.exit(1);
            }

            //Used to collect the experiments results for validity check. This is used only in test mode experiment.test=true
            HashMap<String, ResultTest> probeResults = new HashMap<String, ResultTest>();
            ArrayList<String> erros = new ArrayList<String>();

            ArrayList<Properties> allProperties = Settings.getAllProperties(properties);

            int startRound = Integer.parseInt(properties.getProperty("experiment.startRound"));
            int endRound = Integer.parseInt(properties.getProperty("experiment.endRound"));
            long lastTurnExecutionTime=-1;
            long globalTime = System.currentTimeMillis();

            for(int round=startRound;round<=endRound;round++){                
                long turnExecutionTime = System.currentTimeMillis();

                RandomUtil.setNewSeed(round); //reset the random for each round

                for(int i=0;i<allProperties.size();i++){
                    Properties cfg = allProperties.get(i);

                    ExperimentManager experiment = factory.produce(cfg, round);
                    
                    System.out.println("\n\n\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    System.out.println("Running experiment "+(i+1)+"/"+allProperties.size()+", round "+round+"/"+(endRound)+"...");
                    System.out.println("Experiment started at: "+ new Date(System.currentTimeMillis()));
                    System.out.println("Experiment's category: "+cfg.getProperty("experiment.categoryName"));
                    System.out.println("Experiment's id: "+experiment.getNameID());

                    

                    System.out.println("\nInitializing experiment...");
                    long execTime = System.currentTimeMillis();
                    experiment.open();
                    System.out.println("Initialization concluded in "+ Util.time(System.currentTimeMillis()-execTime)+".");

                    System.out.println("Running Experiment ["+new Date(System.currentTimeMillis())+"]...");
                    execTime = System.currentTimeMillis();
                    // create experiment
                    experiment.run();
                    System.out.println("=>Execution concluded in "+ Util.time(System.currentTimeMillis()-execTime)+".\n");


                    experiment.close();

                    if(Boolean.parseBoolean(cfg.getProperty("experiment.print"))){
                        printResult(cfg, experiment);
                    }

                    if(Boolean.parseBoolean(cfg.getProperty("experiment.test"))){
                        executeTest(erros, probeResults, experiment);
                    }


                    if(properties.getProperty("experiment.deleteDataSet").equals("afterExperiment")){
                        deleteDatasetFolder(properties);
                    }

                    if(lastTurnExecutionTime!=-1 && (i+1)!=allProperties.size()){
                        System.out.println("\n-------------------------------------------------");
                        if(lastTurnExecutionTime>(System.currentTimeMillis()-turnExecutionTime)){
                            System.out.println("*This round will finish in approximately "+
                                    Util.time(lastTurnExecutionTime-(System.currentTimeMillis()-turnExecutionTime))+".");
                        }else{
                            System.out.println("*This round should have finished "+
                                    Util.time((System.currentTimeMillis()-turnExecutionTime)-lastTurnExecutionTime)+" ago.");
                        }
                    }
                }                

                lastTurnExecutionTime = (System.currentTimeMillis()-turnExecutionTime);

                System.out.println("\n\n\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                System.out.println("Round "+(round)+"/"+(endRound)+" successfully executed in "+ Util.time(lastTurnExecutionTime));

                if(properties.getProperty("experiment.deleteDataSet").equals("afterRound")){
                    deleteDatasetFolder(properties);
                }
                //System.out.println("Ivoking garbage collection for cleaning memory for the next execution...");
                //System.gc();

                System.out.println("\n\n\n");
            }
            System.out.println("\n\n\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            if(erros.isEmpty()){
                System.out.println("All experiments SUCCESSFULLY executed in "+ Util.time(System.currentTimeMillis()-globalTime));
            }else{
                System.out.println("All experiments executed in "+ Util.time(System.currentTimeMillis()-globalTime));
                System.out.println(erros.size()+" ERROS were found!!!");
                for(int i=0; i< erros.size(); i++){
                    System.out.println("["+(i+1)+"] "+erros.get(i));
                }
            }
            System.out.println("\n\n\n");
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void executeTest(ArrayList<String> erros, HashMap<String, ResultTest> probeResults, ExperimentManager experiment) {
        System.out.println("\n*Executing on test mode.");

        if(probeResults.containsKey(experiment.getTestId())){
            ResultTest storedResultTest = probeResults.get(experiment.getTestId());
            String msg = "Comparing '"+storedResultTest.getExperimentId()+"' X '"+
                experiment.getNameID();

            System.out.println(msg+"...");

            ResultTest curResultTest = new ResultTest(experiment.getNameID(), experiment.getResult());

            if(storedResultTest.equals(curResultTest)){
                System.out.println(">>>> CONGRATULATIONS!!! The results are the same!");
            }else{
                System.out.println(">>>>> E R R O R ! ! !  The results are NOT the same!");
                erros.add(msg);
            }
            System.out.println();
        }else{
            probeResults.put(experiment.getTestId(),new ResultTest(experiment.getNameID(), experiment.getResult()));
        }
    }


    private static void printResult(Properties cfg, ExperimentManager experiment) {
        ExperimentResult[] result = experiment.getResult();
        int printSize = Integer.parseInt(cfg.getProperty("experiment.printSize"));
        printSize = Math.min(printSize, result.length);
        System.out.println("Printing "+printSize+" result items out of "+result.length+".");

        for(int i=0;i<printSize;i++){
            System.out.println("["+(i+1)+"] -> "+result[i].getMessage());
        }
    }

    private static void deleteDatasetFolder(Properties properties) {
        String dataFolder = properties.getProperty("experiment.dataSetFolder");
        dataFolder = (dataFolder.indexOf("/")==-1)? dataFolder:dataFolder.substring(0,dataFolder.indexOf("/"));
        System.out.print("experiment.deleteDataSet="+properties.getProperty("experiment.deleteDataSet")+
                ". Deleting dataset folder '"+dataFolder+"'...");

        if(FileUtils.deleteDirectory(new File(dataFolder))){
            System.out.println(" DELETED!");
        }else{
            System.out.println(" ERROR!");
        }
    }
}
class ResultTest{
    private String experimentId;
    private ExperimentResult[] experimentResult;

    public ResultTest(String expId, ExperimentResult[] expResult){
        this.experimentId = expId;
        this.experimentResult = expResult;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof ResultTest){
            for(int i=0;i<experimentResult.length;i++){
                if(i>=((ResultTest)o).getExperimentResult().length){
                     System.out.println("["+(i+1)+"]--->>> "+
                            experimentResult[i].getMessage()+" IS DIFFERENT FROM 'null'");
                    return false;
                }else if(!experimentResult[i].equals(((ResultTest)o).getExperimentResult()[i])){
                    System.out.println("["+(i+1)+"]--->>> "+ 
                            experimentResult[i].getMessage()+" IS DIFFERENT FROM "+
                            ((ResultTest)o).getExperimentResult()[i].getMessage());
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @return the experimentId
     */
    public String getExperimentId() {
        return experimentId;
    }

    /**
     * @return the experimentResult
     */
    public ExperimentResult[] getExperimentResult() {
        return experimentResult;
    }

}