/* XXL: The eXtensible and fleXible Library for data processing

Copyright (C) 2000-2004 Prof. Dr. Bernhard Seeger
                        Head of the Database Research Group
                        Department of Mathematics and Computer Science
                        University of Marburg
                        Germany

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307,
USA

	http://www.xxl-library.de

bugs, requests for enhancements: request@xxl-library.de

If you want to be informed on new versions of XXL you can
subscribe to our mailing-list. Send an email to

	xxl-request@lists.uni-marburg.de

without subject and the word "subscribe" in the message body.
*/

package xxl.core.io.raw;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import xxl.core.util.timers.Timer;
import xxl.core.util.timers.TimerUtils;

/**
 * This class writes a logfile for all accesses of a RawDevice.
 */
public class RawAccessLogger implements RawAccess {
	/** Timer which is used for the entries inside the logfile. */
	Timer t;
	
	/** Close for Outputstream? */
	boolean closeFOS;
	
	/** OutputStream, where logfile will be written to. */
	OutputStream myLogger = null;
	
	/** RawAccess which is decorated */
	protected RawAccess r;

	/**
 	* Constructs RawAccessLogger 
 	* @param os name of outputstream 
 	* @param r rawaccess,to which logfile wii be written. 
 	* @exception RawAccessError a spezialized RuntimeException
 	*/
 	public RawAccessLogger(OutputStream os, RawAccess r) throws RawAccessError{
		myLogger=os;
		this.r=r;
		t = (Timer) TimerUtils.FACTORY_METHOD.invoke();
		t.start();
	}

	/**
 	 * Constructs a new RawAccessLogger 
 	 * @param logfilename name of logfile for a given rawaccess.
 	 * @param r rawaccess ,of which logfile will be made.
 	 * @throws RawAccessError
 	 */
 	public RawAccessLogger(String logfilename, RawAccess r)throws RawAccessError{
		super();
		
		try{
			this.r = r;
			myLogger = new FileOutputStream(logfilename);
			t= (Timer) TimerUtils.FACTORY_METHOD.invoke();
   		    t.start();
		}
		catch(IOException e){
			throw new RawAccessError("RawAccessLogger: device/file not found");
		}
   		closeFOS=true;
  	}

	/**
	* Write the result to the log file.
	*
	* @param s the log entry to be written.
	* @exception RawAccessError a spezialized RuntimeException
	*/
	private void writeLogEntry(String s) throws RawAccessError{
		try{
		   byte[] a=s.getBytes();
   		   myLogger.write(a);
   		   myLogger.write(10);
   		   myLogger.flush();
		}
		catch(IOException e){
		   throw new RawAccessError("RawAccessLogger.writeLogEntry"+e.toString());
		}
	}

	/**
	 * Opens a device or file.
	 *
	 * @param filename the name of the file.
	 * @exception RawAccessError a spezialized RuntimeException
	 */
	public void open (String filename)throws RawAccessError{
		r.open(filename);
  		writeLogEntry("open called ");
	}

	/**
	 * Closes the device or file.
	 *
	 * @exception RawAccessError a spezialized RuntimeException
	 */
	public void close(){ 
		try{
			r.close();
			if(closeFOS)
				myLogger.close(); 
  		}
  		catch(IOException e){
  			throw new RawAccessError("RawAccessLogger: " + e.toString());
		}
  	} 

	/**
	 * Writes block to file/device and a logfile.
	 *
	 * @param block array to be written
	 * @param sector number of the sector
	 * @exception RawAccessError a spezialized RuntimeException
	 */
   	public void write(byte[] block, long sector)throws RawAccessError{
		r.write(block,sector);
  		writeLogEntry( (float)t.getDuration()/t.getTicksPerSecond()*1000+
            	"\t"+0+"\t"+ sector+"\t" + 512+"\t"+ 0 );
	}     

	/**
	 * Reads block from file/device and write a logfile.
	 * 
	 * @param block byte array of 512 which will be written to the sector
	 * @param sector number of the sector
 	 * @throws RawAccessError
	 */     
	public void read(byte[] block,long sector) throws RawAccessError{
		r.read(block,sector);
  		writeLogEntry(
      		(float)t.getDuration()/t.getTicksPerSecond()*1000+ 
      		"\t"+0+"\t"+sector+"\t"+512+"\t"+1);
	}

	/**
	 *  Returns the amount of sectors in the file/device.
	 *
	 * @return amount of sectors
 	 * @throws RawAccessError
	 */
	public long getNumSectors()throws RawAccessError{
  			return r.getNumSectors();
	}

	/**
	 * Returns the size of sectors of the file/device.
	 *
	 * @return amount of sectors
 	 * @throws RawAccessError
	 */
	public int getSectorSize()throws RawAccessError{
  		return r.getSectorSize();
	}
}
