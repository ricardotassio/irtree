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


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import xxl.core.io.FilesystemOperations;
import xxl.core.util.Arrays;
import xxl.core.util.WrappingRuntimeException;

/**
 * This class provides a simple filesystem based on an array of RawAccesses. 
 * The file system manages a fixed number of files (maximum). The check for equality is
 * case sensitive (like under Linux).
 * Directories are not supported.
 */
public class RawAccessArrayFilesystemOperations implements FilesystemOperations {
	
	/**
	 * Array of RawAccesses used for the content of the files.
	 */
	protected RawAccess ras[];
	
	/**
	 * Filenames inside the file system.
	 */
	protected String filenames[];
	
	/**
	 * Determines if the files have been created so far.
	 */
	protected boolean filesCreated[];
	
	/** 
	 * The implementation needs a file that exists inside the file system. The file is never really used,
	 * just opened as read only and immediately closed again. This parameter only exists because of the inflexible 
	 * implementation of RandomAccessFile. 
	 */
	protected File dummyFile;

	/** Maximum number of files (number of RawAccesses). */
	protected int maxNumberOfFiles;
	
	/** Current number of files (filenames used). */
	protected int currentNumberOfFiles;
	
	/**
	 * Creates a simple file system based on a number of RawAccesses.
	 * Initially, the given filenames are mapped to the corresponding RawAccesses.
	 * If the corresponding value of filesCreated is true, then it is assumed
	 * that the RawAccess already contains data which is the content of the 
	 * file. If filesCreated is false, then the RawAccess is overwritten.
	 *
	 * @param ras an array of RawAccesses (@see PartitionRawAccess and @see RawAccessUtils.rawAccessPartitioner)
	 * @param filenames names of the files (initially). The length of the array must 
	 * 	lower or equal compared to the RawAccess array.
	 * @param filesCreated determines if the files have content at the beginning.
	 * @param dummyFile The implementation needs a file
	 *	that exists inside the file system. The file is never really used,
	 *	just opened as read only and immediately closed again. This parameter
	 *	only exists because of the inflexible implementation of RandomAccessFile.
	 */
	public RawAccessArrayFilesystemOperations(RawAccess ras[], String filenames[], boolean filesCreated[], File dummyFile) {
		this.maxNumberOfFiles = ras.length;
		
		if (filenames.length > maxNumberOfFiles )
			throw new RuntimeException("Number of filenames is bigger than the number of RawAccesses");
		if (filenames.length != filesCreated.length)
			throw new RuntimeException("Number of filenames and number of entries in filesCreated is not the same");

		this.currentNumberOfFiles = filenames.length;

		// Rebuild the arrays
		this.filenames = new String[maxNumberOfFiles];
		this.filesCreated = new boolean[maxNumberOfFiles];
		for (int i=0; i<filenames.length; i++) {
			this.filenames[i] = new String(filenames[i]);
			this.filesCreated[i] = filesCreated[i];
		}
		
		this.ras = ras;
		this.dummyFile = dummyFile;
	}
	
	/**
	 * Creates a simple file system based on a number of RawAccesses.
	 * Initially, the given filenames are mapped to the corresponding RawAccesses.
	 * If the value of filesCreated is true, then it is assumed
	 * that all RawAccesses already contain data which is the content of the 
	 * file. If filesCreated is false, then the RawAccesses become overwritten.
	 *
	 * @param ras an array of RawAccesses (@see PartitionRawAccess and @see RawAccessUtils.rawAccessPartitioner)
	 * @param filenames names of the files (initially). The length of the array must 
	 * 	lower or equal compared to the RawAccess array.
	 * @param filesCreated determines if the files have content at the beginning.
	 * @param dummyFile The implementation needs a file
	 *	that exists inside the file system. The file is never really used,
	 *	just opened as read only and immediately closed again. This parameter
	 *	only exists because of the inflexible implementation of RandomAccessFile.
	 */
	public RawAccessArrayFilesystemOperations(RawAccess ras[], String filenames[], boolean filesCreated, File dummyFile) {
		this(ras,filenames,Arrays.newBooleanArray(filenames.length,filesCreated),dummyFile);
	}

	/**
	 * Creates a simple file system based on a number of RawAccesses.
	 * Initially, no files exist on the file system.
	 *
	 * @param ras an array of RawAccesses (@see PartitionRawAccess and @see RawAccessUtils.rawAccessPartitioner)
	 * @param dummyFile The implementation needs a file
	 *	that exists inside the file system. The file is never really used,
	 *	just opened as read only and immediately closed again. This parameter
	 *	only exists because of the inflexible implementation of RandomAccessFile.
	 */
	public RawAccessArrayFilesystemOperations(RawAccess ras[], File dummyFile) {
		this(ras,new String[0],new boolean[0],dummyFile);
	}
	
	/**
	 * Determines the position of the file inside the array.
	 * @param filename Name of the file to be searched.
	 * @return the position of the file inside the array.
	 */
	private int getIndex(String filename) {
		if (currentNumberOfFiles>0)
			for (int i=0; i<maxNumberOfFiles; i++)
				if (filename.equals(filenames[i]))
					return i;
		return -1;
	}

	/**
	 * Creates the file on the file system without accessing it.
	 * @param filename the name of the file.
	 * @return the index inside the filenames array.
	 */
	private int createFileInternal(String filename) {
		if (currentNumberOfFiles < maxNumberOfFiles) {
			for (int i=0; i<maxNumberOfFiles; i++) {
				if (filenames[i]==null) {
					filenames[i] = new String(filename);
					filesCreated[i] = false;
					currentNumberOfFiles++;
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Creates the file on the file system without accessing it.
	 * @param filename the name of the file.
	 * @return true iff the operation completed successfully.
	 */
	public boolean createFile(String filename) {
		return (createFileInternal(filename)>=0);
	}
	
	/**
	 * Opens the file and returns a RandomAccessFile.
	 * @param fileName the name of the file.
	 * @param mode the access mode ("r" or "rw").
	 * @return the new RandomAccessFile or null, if the operation was not successful.
	 */
	public RandomAccessFile openFile(String fileName, String mode) {
		try {
			int index = getIndex(fileName);
			
			if (index==-1) {
				index = createFileInternal(fileName); // create new file
				if (index==-1) 
					return null;
			}

			if (index>=0) {
				// File found (exists)
				RandomAccessFile ra = new RawAccessRAF(ras[index],filesCreated[index],dummyFile);
				filesCreated[index] = true;
				return ra;
			}
			else
				return null;
		}
		catch (IOException ie) {
			throw new WrappingRuntimeException(ie);
		}
	}

	/**
	 * Determines if the file exists or not.
	 * @param fileName the name of the file to be checked.
	 * @return true iff the file exists.
	 */
	public boolean fileExists(String fileName) {
		return getIndex(fileName)>=0;
	}

	/**
	 * Renames the name of the file to a new name.
	 * @param oldName the old name of the file.
	 * @param newName the new name of the file.
	 * @return true iff the operation completed successfully.
	 */
	public boolean renameFile(String oldName, String newName) {
		int index = getIndex(oldName);
		if (index>=0) {
			int indexNew = getIndex(newName);
			if (indexNew==-1)	// no duplicate file names
				filenames[index] = newName;
			return true;
		}
		else 
			return false;
	}

	/**
	 * Deletes the file.
	 * @param fileName the name of the file.
	 * @return true iff the operation completed successfully.
	 */
	public boolean deleteFile(String fileName) {
		int index = getIndex(fileName);
		
		if (index>=0) {
			filenames[index] = null;
			filesCreated[index] = false;
			currentNumberOfFiles--;
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Tests the file system.
	 * 
     * @param args the arguments
	 */
	public static void main(String args[]) {
		RawAccessArrayFilesystemOperations fs = 
			new RawAccessArrayFilesystemOperations(
				// 4+1 Partitions, Last partition has 17-3-2-3-4 = 5 sectors
				RawAccessUtils.rawAccessPartitioner(new RAMRawAccess(17),new int[]{3,2,3,4}), 
				// here, we do not work with RandomAccessFiles, so it works.
				null
			);
			
		for (int runs=1; runs<=3; runs++) {
			System.out.println("Run number: "+runs);
			for (int i=1; i<=5; i++) 
				if (!fs.createFile(String.valueOf(i)))
					throw new RuntimeException("Files could not be created");
			if (fs.createFile("6"))
				throw new RuntimeException("Files creation should not have been possible");
			for (int i=1; i<=5; i++) 
				if (!fs.fileExists(String.valueOf(i)))
					throw new RuntimeException("Files does not exist");
			for (int i=5; i>=1; i--) 
				if (!fs.deleteFile(String.valueOf(i)))
					throw new RuntimeException("Files could not become deleted");
		}
		System.out.println("Test completed successfully");
	}
}
