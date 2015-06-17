/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.sse;

/**
 *
 * @author joao
 */
public class SSEExeption extends Exception{

    public SSEExeption() {
        super();
    }

    public SSEExeption(String message) {
        super(message);
    }

    public SSEExeption(Throwable detail) {
        super(detail);
    }

    public SSEExeption(String message, Throwable detail) {
        super(message, detail);
    }
}
