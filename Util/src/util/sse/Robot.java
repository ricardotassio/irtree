/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;



/**
 *
 * @author joao
 */
public abstract class Robot implements Runnable{
    protected Vocabulary termVocabulary;
    protected Vocabulary documentVocabulary;
    protected String seed;

    public Robot(String seedFile, String termVocabularyFile,
            String documentVocabularyFile){        
        this.seed = seedFile;
        this.termVocabulary = new Vocabulary(termVocabularyFile);
        this.documentVocabulary = new Vocabulary(documentVocabularyFile);
    }

    public String getSeed(){
        return seed;
    }

    public void open() throws SSEExeption {
        termVocabulary.open();
        documentVocabulary.open();
    }

    public abstract Vector parse(String line) throws SSEExeption;

    public void close() throws SSEExeption {
        termVocabulary.close();
        documentVocabulary.close();
    }
}
