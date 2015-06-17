/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.experiment;

/**
 *
 * @author joao
 */
public interface Experiment {
   
   /**
     * This method should be used to initialize the experiment. It gives the
     * properties file to be used and the round in which the experiment is running.
     * @param props
     * @param round
     * @throws ExperimentException
     */
    public void open() throws ExperimentException;

    /**
     * Executes the experiment
     * @throws ExperimentException
     */
    public void run() throws ExperimentException;

    /**
     * This method is called after run to close the experiment instances created
     * @throws ExperimentException
     */
    public void close() throws ExperimentException;

   /**
     * Return the result of the experiment.
     * @return
     */
    public ExperimentResult[] getResult();

}
