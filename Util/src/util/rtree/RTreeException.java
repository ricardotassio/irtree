/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.rtree;

/**
 *
 * @author joao
 */
public class RTreeException extends Exception{
    public RTreeException() {
        super();
    }

    public RTreeException(String message) {
        super(message);
    }

    public RTreeException(Throwable detail) {
        super(detail);
    }

    public RTreeException(String message, Throwable detail) {
        super(message, detail);
    }

}
