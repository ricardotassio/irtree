/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.cache;

/**
 *
 * @author joao
 */
public class CacheItem<I> {
    public static enum Type {WRITE, READ}
    private I item;
    private Type type;

    public CacheItem(I item, Type type){
        this.item = item;
        this.type = type;
    }

    public I getItem(){
        return item;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isWrite(){
        return type == Type.WRITE;
    }

    public boolean isRead(){
        return type == Type.READ;
    }
}
