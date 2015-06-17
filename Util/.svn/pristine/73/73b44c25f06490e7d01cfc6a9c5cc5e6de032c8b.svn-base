/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author joao
 */
public interface PersistentEntryFactory<E extends PersistentEntry> {

    /**
     * Implements how to read an entry.
     * @param input
     * @throws IOException
     */
    public E produce(DataInputStream input) throws IOException;
}
