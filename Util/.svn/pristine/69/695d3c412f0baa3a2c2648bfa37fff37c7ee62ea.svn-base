/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author joao
 */
public class Settings {

    
    /**
     * Load the properties recursively, putting the new proverties over those that
     * it extends.
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Properties loadProperties(String file) throws FileNotFoundException, IOException{
        String directory = file.contains("/")?file.substring(0,file.lastIndexOf("/")+1):"";
        FileInputStream input = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(input);
        System.out.println("Loading properties from "+file);
        if(properties.getProperty("extends")!=null){
            input.close();
            properties = loadProperties(directory+properties.getProperty("extends"));
            input = new FileInputStream(file);
            properties.load(input);
        }
        input.close();
        return properties;
    }

    public static ArrayList<Properties> getAllProperties(Properties cfg){
        ArrayList<Properties> allProperties = new ArrayList<Properties>();
        Set keys = cfg.keySet();
        Iterator itKeys = keys.iterator();
        while(itKeys.hasNext()){
            String key = (String)itKeys.next();
            String value = cfg.getProperty(key);            
            if(value.indexOf(';')!=-1){
                StringTokenizer tokens = new StringTokenizer(value);
                while(tokens.hasMoreTokens()){
                    String newValue = tokens.nextToken(";").trim();
                    Properties newCfg = (Properties) cfg.clone();
                    newCfg.setProperty(key, newValue);
                    for(Object obj:cfg.keySet()){
                        String str = (String) obj;
                        //Exists another property whose value is equal to this curren key value
                        if(newCfg.getProperty(str).equals(key)){
                            newCfg.setProperty(str, newValue);
                        }
                    }
                    allProperties.addAll(getAllProperties(newCfg));
                }
                return allProperties;
            }else{
                //Existis another property key with the current value.
                if(cfg.getProperty(value)!=null && cfg.getProperty(value).indexOf(';')==-1){
                    cfg.setProperty(key, cfg.getProperty(value));
                }
            }
        }
        allProperties.add(cfg);
        return allProperties;
    }
}
