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

import java.io.*;

/**
 * Implements RawAcces with java.io.RandomAccessFile (->RAF).
 */
public class RAFRawAccess implements RawAccess {

	/**
	 * java.io.RandomAccessFile-Handle for the device/file.
	 */
	private RandomAccessFile myRAF = null;
	
	/** Indicates if there are synchronization calls */
	private boolean useSync;
	/**
	 * Size of a sector
	 */
	private int sectorSize;

	/**
	 * Returns a new instance of a raw access that uses a RandomAccessFile.
	 *
	 * @param filename name of device or file
	 * @param useSync make a synchronization call after each read/write
	 * @param sectorSize size of a sector
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public RAFRawAccess(String filename, boolean useSync, int sectorSize) throws RawAccessError {
		this.useSync = useSync;
		this.sectorSize = sectorSize;
		open(filename);
	}

	/**
	 * Returns a new instance of a raw access that uses a RandomAccessFile
	 * (with sector size 512 bytes).
	 *
	 * @param filename name of device or file
	 * @param useSync make a synchronization call after each read/write
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public RAFRawAccess(String filename, boolean useSync) throws RawAccessError {
		this(filename, useSync, 512);
	}

	/**
	 * Returns a new instance of a raw access that uses a RandomAccessFile.
	 *
	 * @param filename name of device or file
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public RAFRawAccess(String filename) throws RawAccessError {
		this(filename,false);
	}

	/**
	 * Opens a device or file
	 * See super class for detailed description
	 *
	 * @param filename name of device or file
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public void open(String filename) throws RawAccessError {
		try {
			if (!(new File(filename)).exists())
				throw new RawAccessError("RAFRawAccess: open() cannot access device");
			myRAF = new RandomAccessFile(filename, "rw");
		}
		catch (FileNotFoundException e) {
			throw new RawAccessError("RAFRawAccess: open() device/file not found");
		}
	}

	/**
	 * Closes device or file
	 * See super class for detailed description
	 *
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public void close() throws RawAccessError {
		// Anything there instanciated to close?
		if (myRAF == null)
			throw new RawAccessError("RAFRawAccess: close() no device open");
		try {
			// lets try it
			myRAF.close();
		}
		catch (IOException e) {
			throw new RawAccessError("RAFRawAccess: " + e.toString());
		}
	}

	/**
	 * Writes block to file/device
	 * See super class for detailed description
	 *
	 * @param block array to be written
	 * @param sector number of the sector
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public void write(byte[] block, long sector) throws RawAccessError {
		if (block.length != sectorSize)
			throw new RawAccessError("RAFRawAccess: write() wrong block length");
		if (myRAF == null)
			throw new RawAccessError("RAFRawAccess: write() no device/file open");
		if (sector >= getNumSectors())
			throw new RawAccessError("RAFRawAccess: write() sector out of bounds");
		try {
			myRAF.seek(sector * sectorSize);
			myRAF.write(block);
			if(useSync) myRAF.getFD().sync();
		}
		catch (IOException e) {
			throw new RawAccessError("RAFRawAccess: write() " + e.toString());
		}
	}

	/**
	 * Reads block from file/device
	 * See super class for detailed description
	 *
	 * @param block byte array of sectorSize bytes for the sector
	 * @param sector number of the sector
	 */
	public void read(byte[] block, long sector) {
		if (myRAF == null)
			throw new RawAccessError("RAFRawAccess: read() no device/file open");
		try {
			myRAF.seek(sector * sectorSize);
			if (myRAF.read(block) != sectorSize)
				throw new RawAccessError("RAFRawAccess: read() failed");
			if(useSync) myRAF.getFD().sync();
		}
		catch (IOException e) {
			throw new RawAccessError("RAFRawAccess: open() " + e.toString());
		}
	}

	/**
	 * Returns the amount of sectors in the file/device.
	 *
	 * @return amount of sectors
	 */
	public long getNumSectors() {
		if (myRAF == null)
			return -1;
		try {
			return  (myRAF.length() / sectorSize);
		}
		catch (java.io.IOException e) {
		}
		return -1;
	}

	/**
	 * Returns the size of a sector of the file/device.
	 *
	 * @return size of sectors
	 */
	public int getSectorSize() {
		return sectorSize;
	}
}
