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

import xxl.core.io.raw.RawAccess;
import xxl.core.io.raw.RawAccessError;

/**
 * This is the implementation of RawAccess via JNI an C methods which implement the
 * unbuffered access to files and devices.
 */
public class NativeRawAccess implements RawAccess {

	/**
	 * HANDLE for the device
	 */
	protected long filep = 0;
	/**
	 * Amount of sectors
	 * The variable will be set by the native open method
	 */
	protected long sectors = 0;
	/**
	 * Size of a sector
	 */
	protected int sectorSize;
	/**
	 * Mode of access (bitwise encoded)
	 * Bit 0: flush buffers after each write
	 */
	protected int mode=0;
	
	/**
	 * Constructs a new raw access that uses JNI.
	 *
	 * @param filename of file or device (example: c: or /dev/hda4)
	 * @param sectorSize size of a sector
	 * @param mode mode of access (bitwise encoded). Bit 0: flush buffers after each write
	 * @exception RawAccessError a spezialized RuntimeException
	 */
	public NativeRawAccess(String filename, int sectorSize, int mode) throws RawAccessError {
		this.sectorSize = sectorSize;
		this.filep = 0;
		this.mode = mode;
		try {
			this.open(filename);
		}
		catch (RawAccessError e) {
			this.sectors = 0;
			throw new RawAccessError(e.toString());
		}
	}

	/**
	 * Constructs a new raw access that uses JNI.
	 *
	 * @param filename of file or device (example: c: or /dev/hda4)
	 * @param sectorSize size of a sector
	 * @exception RawAccessError a spezialized RuntimeException
	 */
	public NativeRawAccess(String filename, int sectorSize) throws RawAccessError {
		this (filename,sectorSize,0);
	}

	/**
	 * Constructs a new raw access that uses JNI
	 * (with sector size 512 bytes).
	 *
	 * @param filename of file or device (example: c: or /dev/hda4)
	 * @exception RawAccessError a specialized RuntimeException
	 */
	public NativeRawAccess(String filename) throws RawAccessError {
		this(filename,512);
	}

	/**
	 * Wrapped native open method.
	 *
	 * @param filename of file or device (example: c: or /dev/hda4)
	 * @throws RawAccessError
	 */
	public native void open(String filename) throws RawAccessError;

	/**
	 * Wrapped native close method
	 *
	 * @throws RawAccessError
	 */
	public native void close() throws RawAccessError;

	/**
	 * Wrapped native write method
	 *
	 * @param block array to be written
	 * @param sector number of the sector
	 * @throws RawAccessError
	 */
	public native void write(byte[] block, long sector) throws RawAccessError;

	/**
	 * Wrapped native read method
	 *
	 * @param block array to be written
	 * @param sector number of the sector
	 * @throws RawAccessError
	 */
	public native void read(byte[] block, long sector) throws RawAccessError;

	/**
	 * Returns the amount of blocks of the device
	 *
	 * @return amount of blocks
	 */
	public long getNumSectors() {
		return sectors;
	}

	/**
	 * Returns the size of a sector of the file/device.
	 *
	 * @return size of sectors
	 */
	public int getSectorSize() {
		return sectorSize;
	}

	/*
	 *  JNI methods call native methods of a library which has to be loaded.
	 *  This will be done once by the first use of this class
	 */
	static {
		System.loadLibrary("RawAccess");
	}
}
