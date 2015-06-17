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

package xxl.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import xxl.core.collections.MapEntry;
import xxl.core.collections.containers.Container;
import xxl.core.collections.containers.io.BlockFileContainer;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.sources.io.InputStreamCursor;
import xxl.core.functions.Constant;
import xxl.core.functions.Function;
import xxl.core.io.converters.Converter;
import xxl.core.io.converters.IntegerConverter;
import xxl.core.io.converters.MultiConverter;
import xxl.core.io.converters.StringConverter;
import xxl.core.util.WrappingRuntimeException;
import xxl.core.util.XXLSystem;


/**
 * This class contains some static methods which can be used 
 * to store objects in a chain of linked Block-objects 
 * inside a container. This is useful, because the size of 
 * objects inside a container is usually bounded by the 
 * page size.
 * <p>
 * Each Block is supposed to have a block header. The first 5 
 * bytes of each Block are reserved for storing internally used
 * data. The next bytes are used to store a pointer to the
 * next Block (size is determined by the objectIdConverter of
 * the Container).
 * <p>
 * So, each method gets the page size and the size of the header.
 * The size of the header has to be larger than or equal to 
 * 5+size of the identifyers of the Container.
 */
public class LinkedContainerBlocks {

	/**
	 * No instances are allowed (only static methods).
	 */
	private LinkedContainerBlocks() {
	}

	/**
	 * Internally used class which returns a cursor of
	 * linked blocks.
	 */
	private static class LinkedContainerBlockCursor extends AbstractCursor {
		/** The page identifyer of the next page which is visited. */
		protected Object nextPageId;
		/** The container */
		protected Container container;
		/** The function which reads and returns the next page identifyer from a block object. */
		protected Function readNextPageId;
		
		/**
		 * Constructs a Cursor which delivers Blocks which are connected via links inside
		 * a container.
		 * @param container The container used.
		 * @param firstPageId The start identifyer of the chain of blocks.
		 * @param readNextPageId The function which reads and returns the next page identifyer from a block object.
		 */
		public LinkedContainerBlockCursor (Container container, Object firstPageId, Function readNextPageId) {
			this.container = container;
			this.nextPageId = firstPageId;
			this.readNextPageId = readNextPageId;
		}
		/**
		 * Determines iff there is another Block.
		 * @return true iff there is another Block.
		 */
		public boolean hasNextObject() {
			return nextPageId!=null;
		}
		/**
		 * Returns the next Block.
		 * @return the next Block.
		 */
		public Object nextObject() {
			Block b = (Block) container.get(nextPageId);
			if (b.get(4)==1)
				nextPageId = readNextPageId.invoke(b);
			else
				nextPageId=null;
			return b;
		}
	}

	/**
	 * Reads and returns objects from a container starting at the given page 
	 * identifyer.
	 * @param container The container used to store the data.
	 * @param converter The converter used to deserialize the objects of the iteration. 
	 * @param pageId First Identifyer of the Container where the chain of blocks starts.
	 * @param blockSize The size of each Block inside the Container.
	 * @param headerSize The size of the header inside each Block.
	 * @return The cursor containing the objects from the chained Blocks.
	 */
	public static Cursor readObjectsFromLinkedContainerBlocks(final Container container, Converter converter, Object pageId, int blockSize, int headerSize) { 
		
		Cursor cursor = new LinkedContainerBlockCursor(
			container,
			pageId,
			new Function() {
				public Object invoke(Object block) {
					try {
						return container.objectIdConverter().read(((Block) block).dataInputStream(5),null);
					}
					catch (IOException e) {
						throw new WrappingRuntimeException(e);
					}
				}
			}
		);
		
		InputStream is = new MultiBlockInputStream(
			cursor,
			headerSize,
			blockSize-headerSize,
			Block.GET_REAL_LENGTH
		);
		
		return new InputStreamCursor(
			new java.io.DataInputStream(is),
			converter
		);
	}

	/**
	 * Removes a chain of linked blocks from the container.
	 * @param container The container used to store the data.
	 * @param pageId The page identifyer of the first Block to be
	 * 	removed. This does not have to be the first Block of the chain.
	 * @param removeFirstBlock True iff the Block with the identifyer 
	 * 	pageId also has to be removed. 
	 */
	public static void removeLinkedBlocks(Container container, Object pageId, boolean removeFirstBlock) {
		Object newId;
		boolean remove = removeFirstBlock;
		
		while (true) {
			Block b = (Block) container.get(pageId);
			if (b.get(4)==1) {
				try {
					newId = container.objectIdConverter().read(b.dataInputStream(5),null);
				} 
				catch (IOException e) {
					throw new WrappingRuntimeException(e);
				}
				if (remove)
					container.remove(pageId);
				else
					remove = true;
				pageId = newId;
			}
			else {
				if (remove)
					container.remove(pageId);
				return;
			}
		}
	}

	/**
	 * Writes the objects from the Iterator into the Container. If multiple
	 * Blocks are used, they are linked. 
	 * @param container The container used to store the data.
	 * @param converter The converter used to serialize the objects of the iteration. 
	 * @param it The objects to be stored.
	 * @param firstPageId First Identifyer of the Container which has to be
	 * 	overwritten. If a chain of Blocks is detected, then all of them are
	 * 	overwritten or removed. If firstPageId is null then only a new chain is
	 * 	written.
	 * @param blockSize The size of each Block inside the Container.
	 * @param headerSize The size of the header inside each Block.
	 * @return The page identifyer used for the first Block inside the Container.
	 */
	public static Object writeObjectsToLinkedContainerBlocks(Container container, Converter converter, Iterator it, Object firstPageId, int blockSize, int headerSize) {
		Object pageId=null;
		
		Cursor cursor = new ObjectToBlockCursor(
			it,
			converter,
			blockSize-headerSize,
			headerSize,
			container.objectIdConverter().getSerializedSize(),
			// max EntrySize (container id plus tuple id)
			Block.SET_REAL_LENGTH
		);
		
		// store Blocks inside container and link them
		if (cursor.hasNext()) {
			Object lastId=firstPageId;
			Object currentId;
			boolean readNextId = firstPageId!=null;
			Block lastBlock = (Block) cursor.next();
			Block currentBlock;
			
			if (lastId==null)
				lastId = container.reserve(new Constant(lastBlock));
			
			pageId = lastId;
			
			while (cursor.hasNext()) {
				currentId = null;
				
				currentBlock = (Block) cursor.next();
				currentBlock.set(4,(byte) 0);

				if (readNextId) {
					Block b = (Block) container.get(lastId);
					if (b.get(4)==1) {
						try {
							currentId = container.objectIdConverter().read(b.dataInputStream(5),null);
						}
						catch (IOException e) {
							throw new WrappingRuntimeException(e);
						}
					}
					else
						readNextId = false;
				}
				
				if (currentId==null)
					currentId = container.reserve(new Constant(currentBlock));
				
				// link the Blocks
				try {
					lastBlock.set(4,(byte) 1);
					container.objectIdConverter().write(lastBlock.dataOutputStream(5),currentId);
				}
				catch (IOException e) {
					throw new WrappingRuntimeException(e);
				}
				container.update(lastId, lastBlock);
				lastBlock = currentBlock;
				lastId = currentId;
			}
			
			if (readNextId)
				removeLinkedBlocks(container, lastId, false);

			// last link is already 0
			container.update(lastId,lastBlock);
		}
		
		return pageId;
	}

	/**
	 * Outputs the map to System.out.
	 * @param map the map to be outputed.
	 */
	private static void outputMap(Map map) {
		Iterator it = map.entrySet().iterator();
		
		while (it.hasNext()) {
			java.util.Map.Entry me = (java.util.Map.Entry) it.next();
			System.out.println(me.getKey()+"\t"+me.getValue());
		}
	}

	/**
	 * Tests this class with a map, which is stored inside blocks with 64 bytes each.
	 * @param args Command line arguments are ignored here.
	 */
	public static void main(String args[]) {
		
		Map map = new HashMap();
		Map map2 = new HashMap();
		
		map.put(new Integer(0), "00ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(1), "01ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(2), "02ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(3), "03ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(4), "04ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(5), "05ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(6), "06ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(7), "07ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(8), "08ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(9), "09ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(10),"10ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		
		int blockSize = 64;
		
		Container container; //  = new MapContainer();
		container = new BlockFileContainer(
			XXLSystem.getOutPath(new String[]{"output","core"})+File.separator+"maptest",
			blockSize
		);
		
		int headerSize = 5 + container.getIdSize();
		// 4 Bytes: real length of the block (int), 1 Byte: has link 
		// rest: for linking Blocks
		
		Converter mapEntryConverter = new MultiConverter(
			new Converter[] {
				IntegerConverter.DEFAULT_INSTANCE,
				StringConverter.DEFAULT_INSTANCE
			},
			xxl.core.collections.MapEntry.FACTORY_METHOD,
			xxl.core.collections.MapEntry.TO_OBJECT_ARRAY_FUNCTION
		);
		
		Object pageId;
		Cursor cursor;
		Iterator it;
		int count1, count2, count3;
		
		System.out.println("Map before");
		outputMap(map);
		
		System.out.println("Write to container");
		it = map.entrySet().iterator();
		pageId = writeObjectsToLinkedContainerBlocks(container, mapEntryConverter, it, null, blockSize, headerSize);
		
		count1 = container.size();
		System.out.println("Number of pages (inside the container): "+count1);
		
		cursor = readObjectsFromLinkedContainerBlocks(container, mapEntryConverter, pageId, blockSize, headerSize);
		
		// now, it is a cursor of MapEntry objects
		while (cursor.hasNext()) {
			MapEntry me = (MapEntry) cursor.next();
			map2.put(me.getKey(),me.getValue());
		}
		
		System.out.println("Number of pages (inside the container): "+container.size());
		
		System.out.println("Reconstructed map");
		outputMap(map2);
		
		if (!map.equals(map2))
			throw new RuntimeException("Maps are not identical");
		
		System.out.println("Append some elements");
		map.put(new Integer(11),"11ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(12),"12ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(13),"13ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		map.put(new Integer(14),"14ABCDEDGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
		
		System.out.println("Write to container");
		it = map.entrySet().iterator();
		pageId = writeObjectsToLinkedContainerBlocks(container, mapEntryConverter, it, pageId, blockSize, headerSize);
		count2 = container.size();
		System.out.println("Number of pages (inside the container): "+count2);
		
		System.out.println("Read from container");
		cursor = readObjectsFromLinkedContainerBlocks(container, mapEntryConverter, pageId, blockSize, headerSize);
		Cursors.last(cursor);
		
		System.out.println("Delete some elements");
		map.remove(new Integer(1));
		map.remove(new Integer(3));
		map.remove(new Integer(5));
		map.remove(new Integer(7));
		map.remove(new Integer(9));
		map.remove(new Integer(11));
		map.remove(new Integer(13));
		
		System.out.println("Write to container");
		it = map.entrySet().iterator();
		pageId = writeObjectsToLinkedContainerBlocks(container, mapEntryConverter, it, pageId, blockSize, headerSize);
		count3 = container.size();
		System.out.println("Number of pages (inside the container): "+count3);

		System.out.println("Read from container");
		cursor = readObjectsFromLinkedContainerBlocks(container, mapEntryConverter, pageId, blockSize, headerSize);
		Cursors.last(cursor);
		
		System.out.println("Remove all pages from container");
		removeLinkedBlocks(container, pageId, true);
		
		if (container.size()!=0)
			throw new RuntimeException("Test failed!");
		
		if (count2<=count1)
			throw new RuntimeException("Not enough pages inside the container after the second write command!");
		
		if (count3>=count2)
			throw new RuntimeException("To much pages inside the container after the third write command!");
		
		if (container instanceof BlockFileContainer)
			((BlockFileContainer)container).delete();
		
		System.out.println("Test finished successfully");
	}
}
