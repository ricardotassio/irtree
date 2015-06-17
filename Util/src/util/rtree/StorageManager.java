/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.rtree;

/**
 *
 * @author joao
 */
public interface StorageManager {

    public void store(int id, Node node) throws RTreeException;

    /**
     * Get a node object, given the ID of the node.
     */
    public abstract Node getNode(int index) throws RTreeException;

    public void close() throws RTreeException;

    /**
     * Return some information about the StorageManager
     * @return
     * @throws RTreeException
     */
    public String info() throws RTreeException;
}
