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


/**
 * Implements RawAccess via usage of main memory.
 * It is fast and limited to 4GB.
 */
public class RAMRawAccess implements RawAccess {

	/**
	 * The array in main memory: [amount of sector][sectorSize]
	 */
	byte[][] array = null;
	/**
	 * Size of a sector
	 */
	private int sectorSize;

	/**
	 * Returns a new instance of a raw access that uses main memory.
	 *
	 * @param numblocks the amount of sectors
	 * @param sectorSize size of a sector
	 * @exception RawAccessError Description of the Exception
	 */
	public RAMRawAccess(long numblocks, int sectorSize) throws RawAccessError {
		this.sectorSize = sectorSize;
		open(numblocks);
	}

	/**
	 * Returns a new instance of a raw access that uses main memory
	 * (with sector size 512 bytes).
	 *
	 * @param numblocks the amount of sectors
	 * @exception RawAccessError Description of the Exception
	 */
	public RAMRawAccess(long numblocks) throws RawAccessError {
		this(numblocks,512);
	}

	/**
	 * Creates a standard raw access with 20480 blocks (1MB).
	 *
	 * @param filename normal devices have names, here it will be ignored (use NULL for example).
	 */
	public void open(String filename) {
		open(20480);
	}

	/**
	 * Creates a standard raw access with "numblocks" sectors.
	 *
	 * @param numblocks the amount of sectors
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public void open(long numblocks) throws RawAccessError {
		int nb = (int) numblocks;
		// Casting alright?
		if (numblocks > nb)
			throw new RawAccessError("RAMRawAccess: wrong amount of blocks");
			
		try {
			array = new byte[(int) numblocks][sectorSize];
		} catch (OutOfMemoryError e) {
			throw new RawAccessError("RAMRawAccess: Out of memory");
		}
	}

	/**
	 * Closes the raw access.
	 *
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public void close() throws RawAccessError {
		if (array == null)
			throw new RawAccessError("RAMRawAccess: close() failed");
	}

	/**
	 * Writes a block to the raw access.
	 *
	 * @param block array to be written
	 * @param sector number of the sector
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public void write(byte[] block, long sector) throws RawAccessError {
		if (block.length != sectorSize)
			throw new RawAccessError("RAMRawAccess: write() wrong block length");
		if (array == null)
			throw new RawAccessError("RAMRawAccess: write() no device open");
		array[(int) sector] = block;
		System.arraycopy(block, 0, array[(int) sector], 0, block.length);
	}

	/**
	 * Reads a block from the raw access.
	 *
	 * @param block byte array of sectorSize bytes for the sector
	 * @param sector number of the sector
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public void read(byte[] block, long sector) throws RawAccessError {
		if (array == null)
			throw new RawAccessError("RAMRawAccess: read() no device open");

		// copy the block into the object we will return
		// Beware of returning references which can be overwritten!
		System.arraycopy(array[(int) sector], 0, block, 0, array[(int) sector].length);
	}

	/**
	 * The amount of blocks
	 *
	 * @return the amount of blocks. If anything fails the value will be -1
	 */
	public long getNumSectors() {
		// Do we have a "RAM" disk?
		if (array == null)
			return -1;
		return array.length;
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
