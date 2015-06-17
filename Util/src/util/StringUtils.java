/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author joao
 */
public class StringUtils {

     public static ArrayList<String> parseLine(String line, char separator){
        char[] chars = line.toCharArray();
        ArrayList<String> tokens = new ArrayList<String>();
        StringBuilder token = new StringBuilder();
        
        for(int i=0; i < chars.length; i++){
            if(line.charAt(i)==separator){
                tokens.add(token.toString());
                token.delete(0, token.length()); //reset token
            }else{
                token.append(chars[i]);
            }
        }
        tokens.add(token.toString());
        return tokens;
    }

    public static String toString(Collection c){
        StringBuilder result = new StringBuilder();
        for(Object item:c){
            result.append(item.toString());
            result.append("\n");
        }
        return result.toString();
    }

    public static void main(String[] args){
        String before = "Hi!\\n How are \n you?\r\n I'm \n   good!";
        System.out.println(before);
        String after = before.replace('\n', '#').replace('\r', '#');
        System.out.println(after);
        after = after.replaceAll("\\s+", " ");
        System.out.println(after);
    }

}
