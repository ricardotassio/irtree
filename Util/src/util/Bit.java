/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author JOAO
 */
public class Bit {
    private static int[] bitsSetTable256;
    
    public static int getNumSetBits(int v){    
        // Initially generate the table algorithmically
        if(bitsSetTable256==null){
            bitsSetTable256= new int[256];
            for (int i = 0; i < 256; i++){
                bitsSetTable256[i] = (i & 1) + bitsSetTable256[i / 2];
            }
        }
        return bitsSetTable256[v & 0xff] + 
               bitsSetTable256[(v >> 8) & 0xff] + 
               bitsSetTable256[(v >> 16) & 0xff] + 
               bitsSetTable256[v >> 24];
    }
    
    public static void main(String[] args){
        //for(int i=0;i<32;i++){
        //    System.out.println(i+"("+Integer.toBinaryString(i)+") has "+Bit.getNumSetBits(i)+" bits");    
        //}        
        
        int relBitmap = 0b0_010_010_001_010_101;
        int frequencyBitmap = 0b0_001_010_001_000_001;
        
        int occurrences=0;
        int mask = 0b111;
        int shift=0;
        for(int i=0;i<5;i++){
            occurrences+= ((relBitmap>>shift)&mask)*((frequencyBitmap>>shift)&mask);            
            shift+=3;
        }        
        double frequency = Math.log(occurrences);
   
        System.out.println(((relBitmap>>shift)&mask)+"*"+((frequencyBitmap>>shift)&mask));                
        System.out.println("occurrences="+occurrences+", frequency="+frequency);                
    }

}
