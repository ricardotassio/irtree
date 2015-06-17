/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.rtree;

import java.util.HashMap;

/**
 *
 * @author joao
 */
public class MemoryStorageManager implements StorageManager{
    private HashMap<Integer, Node> data;

    public MemoryStorageManager(){
        super();
        data = new HashMap<Integer, Node>();
    }

    @Override
    public void store(int id, Node node) throws RTreeException {
        data.put(id, node);
    }

    @Override
    public Node getNode(int index) throws RTreeException {
        return data.get(index);
    }

    public void close() throws RTreeException {
        data.clear();
        data = null;
    }

    public String info() throws RTreeException{
        StringBuffer str = new StringBuffer();
        str.append("MemoryStorageManager");
        str.append(", size= "+data.size()+".");
        return str.toString();
    }

}
