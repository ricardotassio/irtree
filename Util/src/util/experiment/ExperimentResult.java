/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.experiment;

/**
 *
 * @author joao
 */
public interface ExperimentResult {

    /**
     * Identifies uniquely the result
     * @return
     */
    public abstract int getId();
    
    /**
     * Text to be print about the result
     * @return
     */
    public abstract String getMessage();
}
