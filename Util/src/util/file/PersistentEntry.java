/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public interface PersistentEntry{

    public void write(DataOutputStream out) throws IOException;


    public void read(DataInputStream in) throws IOException;

}
