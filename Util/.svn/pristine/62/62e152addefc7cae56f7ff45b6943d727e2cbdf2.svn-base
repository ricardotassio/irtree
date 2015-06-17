/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import util.Util;

/**
 *
 * @author joao
 */
public class RobotHtml extends Robot {
    private Index index;

    public RobotHtml(Index index, String seed, String termVocabulary, String pageVocabulary){
        super(seed, termVocabulary, pageVocabulary);
        this.index = index;
    }

    @Override
    public void open() throws SSEExeption {
        super.open();
        index.open();
    }

    public Vector parse(String url) throws SSEExeption {
        try {
            Vector document = new Vector(documentVocabulary.getId(url));
            String text = RobotHtml.removeTags(RobotHtml.dowload(url)).trim();
            System.out.println("\n"+text);
            Vector.vectorize(document, text, termVocabulary);
            return document;
        } catch (MalformedURLException ex) {
            throw new SSEExeption(ex);
        } catch (IOException ex) {
            throw new SSEExeption(ex);
        }        
    }

    public void run(){
        try{

            BufferedReader reader = new BufferedReader(new FileReader(seed));
            String line;
            while((line=reader.readLine())!=null){
                index.index(this.parse(line));
            }
            reader.close();
        }catch (IOException ex) {
            throw new RuntimeException(ex);
        }catch (SSEExeption ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void close() throws SSEExeption {
        super.close();
        index.close();
    }
    
    /**
     * Returns the text (home page) of the given url.
     * @param stringUrl
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String dowload(String stringUrl) throws MalformedURLException, IOException{
        if(stringUrl!=null && stringUrl.length()>0){
            URL url;
            try{
                url= new URL(stringUrl.trim());
            }catch(MalformedURLException ex){
                url = new URL("http://"+stringUrl.trim());
            }
            URLConnection urlConnection = url.openConnection();
            InputStream input = urlConnection.getInputStream();
            int c;
            String result="";
            while((c = input.read())!=-1){
                result+= (char)c;
            }

            if(urlConnection instanceof HttpURLConnection){
                ((HttpURLConnection)urlConnection).disconnect();
            }else{
                input.close();
            }
            return result;
        }else{
            return null;
        }
    }

    /**
     * Remove as tags de um texto/html. Se for passado tag="" como
     * parâmetro, todas as tags são removidas
     */
    public static String removeTags(String text){
        StringBuilder result = new StringBuilder();
        int i;
        int l=0;

        while(l!=-1 && (i = text.indexOf("<",l)) != -1 ){
            result.append(text.substring(l,i));
            result.append(' ');            

            l = text.indexOf(">",i)+1;
        }
        
       return result.toString();
    }


    public static void main(String[] args) throws Exception{
        long time = System.currentTimeMillis();
        System.out.print("Indexing links in urls.txt...");

        Index index = new Index(null, "if");
        RobotHtml robot = new RobotHtml(index, "urls.txt", "termVocabulary.txt",
                "pageVocabulary.txt");        
        robot.open();
        robot.run();
        robot.close();
        index.close();
        System.out.println("done in "+Util.time(System.currentTimeMillis()-time));
    }
}
