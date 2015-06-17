/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.HashSet;

/**
 *
 * @author joao
 */
public class Debug {
    private static HashSet<String> debugMsgs=null;
    /**
     * Print a given message only once
     * @param msg
     */
    public static void singlePrint(String msg){
        if(debugMsgs==null){
            debugMsgs = new HashSet<String>();
        }
        if(!debugMsgs.contains(msg)){
            System.out.println(msg);
            debugMsgs.add(msg);
        }
    }
}
