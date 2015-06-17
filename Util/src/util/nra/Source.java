/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.nra;

/**
 *
 * @author joao
 */
public interface Source<E extends NRAItem> {
    public E next();
}
