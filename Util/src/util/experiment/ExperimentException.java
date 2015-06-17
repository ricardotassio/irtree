/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.experiment;


public class ExperimentException extends Exception {

    public ExperimentException() {
        super();
    }

    public ExperimentException(String message) {
        super(message);
    }

    public ExperimentException(Throwable detail) {
        super(detail);
    }

    public ExperimentException(String message, Throwable detail) {
        super(message, detail);
    }
}
