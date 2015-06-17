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

package xxl.core.cursors.identities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import xxl.core.collections.queues.Queue;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.wrappers.QueueCursor;
import xxl.core.io.Buffer;
import xxl.core.io.BufferedRandomAccessFile;
import xxl.core.io.LRUBuffer;
import xxl.core.io.converters.Converter;
import xxl.core.util.WrappingRuntimeException;
import xxl.core.util.XXLSystem;

/**
 * A tee-cursor provides a method to duplicate a given iteration. That means the
 * iteration given to a constructor of this class can be consumed in the standard
 * way but all delivered
 * elements will also be inserted in the specified storage area, that is given to
 * this tee-cursor, too. Then the given storage area can be traversed calling
 * the <tt>cursor</tt> method on a tee-cursor.
 * 
 * <p>If you understand this class as an operator in an operator tree, this
 * operator has one input iterations, namley the given iteration in the
 * constructor, and is able to deliver several output iterations. The tee-cursor
 * itself is one of this output iterations. By calling the method {@link #cursor()} one can
 * get a further output iteration. So a lot of output iterations can be
 * produced, if the storage area can be traversed more than one time, and for
 * that reason the input iteration's elements are duplicated.</p>
 * The tee-cursor is closed when, at a certain time, itself and all cursors
 * returned by {@link #cursor()} are closed. This can be changed by setting the
 * <tt>explicitClose</tt> parameter to <tt>true</tt> in the constructor 
 * {@link #TeeCursor(Iterator, TeeCursor.StorageArea, boolean)}. Then, only a call to
 * {@link #closeAll()} closes the input and the StorageArea.
 * 
 * <p><b>Note:</b> All output iterators are cursors that do not support the 
 * <tt>remove</tt> and  <tt>update</tt> operation. The duplication of the input 
 * iteration is fulfilled by lazy evaluation.
 * 
 * <p>Depending on the implementation of the inner interface <i>StorageArea</i>,
 * a tee-cursor can be based on queues, lists or files. 
 * This class provides standard
 * implementations for the these kinds of storage areas:
 * <ul>
 *     <li>
 *         {@link ListStorageArea}
 *     </li>
 *     <li>
 *         {@link QueueStorageArea}
 *     </li>
 *     <li>
 *         {@link FileStorageArea}
 *     </li>
 * </ul></p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.SecureDecoratorCursor
 */
public class TeeCursor extends AbstractCursor {

	/**
	 * This interface is used to store the objects delivered by the iteration
	 * specified in a tee-cursor's constructor and to return them, if the method
	 * <tt>cursor</tt> of the class implementing this interface is called.
	 * Furthermore the behaviour of a storage area can be determined implementing
	 * the method <tt>isSingleton</tt> with the intention to show if the storage
	 * area can be traversed more than one time.
	 */
	public interface StorageArea {

		/** Inserts the given object into the storage area.
		 *
		 * @param o the object to be inserted in the storage area.
		 */
		public abstract void insert(final Object o);

		/** Returns an iteration containing all objects that are currently
		 * belonging to this storage area.
		 *
		 * @return an iteration that contains all objects of the storage area.
		 */
		public abstract Cursor cursor();

		/** If a storage area can be traversed exactly one time this method
		 * should return <tt>true</tt>, otherwise <tt>false</tt>.
		 * 
		 * @return <tt>true</tt> if the storage area can be traversed exaclty one
		 *         time, <tt>false</tt> otherwise .
		 */
		public abstract boolean isSingleton();

		/** Closes the storage area. Signals the storage area to clean up
		 * resources, close files, etc. After a call to <tt>close</tt> a call to
		 * <tt>iterator</tt> is not guarantied to yield proper results. Multiple
		 * calls to <tt>close</tt> do not have any effect, i.e., if
		 * <tt>close</tt> was called the storage area remains in the state
		 * "closed".
		 */
		public abstract void close();
		
	}

	
	/** A {@link TeeCursor.StorageArea} that stores elemenst in a list. 
	 */
	public static class ListStorageArea implements StorageArea {
		
		/** The list used to store elements. 
		 */
		protected List list;
		
		/** Creates a new ListStorageArea.
		 * 
		 * @param list a list to store elements in
		 */
		public ListStorageArea(List list) {
			this.list = list;
		}
		
		/** Adds the object o to the end of the list.
		 * 
		 * @param o the element to insert
		 */ 
		public void insert(final Object o) {
			list.add(o);
		}

		/** Returns a cursor that delivers the list's elements from its beginning.
		 * This has to be implemented separately because otherwise a 
		 * ConcurrentModificationException would be thrown, if several 
		 * iterators work on the same list.
		 * 
		 * @return a cursor delivering the list's elements
		 */
		public Cursor cursor() {
			return new AbstractCursor() {

				int index = 0;

				public boolean hasNextObject() {
					return list.size() > index;
				}

				public Object nextObject() {
					return list.get(index++);
				}
			};
		}

		/** As reading from a list is not destroying, it can be traversed by 
		 * more than one iterator and this StorageArea is not singleton.
		 * 
		 * @return <code>false</code>
		 */ 
		public boolean isSingleton () {
			return false;
		}

		/** Clears the list to release resources.
		 */ 
		public void close () {
			list.clear();
		}
	}
	
	/** A {@link TeeCursor.StorageArea} that stores elemenst in a queue. 
	 * If you uses this class, it is highly recommended that you use
	 * an order preserving queue. If not, the ordering of the elements
	 * in the resulting cursor cannot be predicted.
	 */
	public static class QueueStorageArea implements StorageArea {

		/** The queue used to store elements. The queue 
		 * should be order preserving.
		 */
		protected Queue queue;

		/** Creates a new QueueStorageArea based on a
		 * queue (should be order preserving). 
		 * 
		 * @param queue a queue to store elements in
		 */		
		public QueueStorageArea(Queue queue) {
			this.queue = queue;
		}

		/** Adds the object o to the queue, i.e.
		 * calls <code>queue.enqueue(o);</code>.
		 * 
		 * @param o the element to insert
		 */ 
		public void insert(final Object o) {
			queue.enqueue(o);
		}

		/** Returns a cursor that delivers the queue's elements from its beginning.
		 * As this deletes them from the queue, this StorageArea is singleton.
		 * 
		 * @return a cursor delivering the queue's elements
		 */
		public Cursor cursor() {
			return new QueueCursor(queue);
		}

		/** As reading from a queue is destroying, it cannot be traversed by 
		 * more than one iterator and this StorageArea is singleton.
		 * 
		 * @return <code>true</code>
		 */ 
		public boolean isSingleton() {
			return true;
		}

		/** Clears the queue to release resources.
		 */ 
		public void close() {
			queue.clear();
			queue.close();
		}
	}

	/** A {@link TeeCursor.StorageArea} that stores elemenst in a file. 
	 */
	public static class FileStorageArea implements StorageArea {
		
		/** The {@link BufferedRandomAccessFile} used to store the objects
		 * in this StorageArea.
		 */
		protected BufferedRandomAccessFile braf;

		/** The File used for {@link #braf}; 
		 */
		protected File file;
		
		
		/** A suitable converter for the objects to store. 
		 */
		protected Converter converter;
		
		
		/** A pointer indicating where the next write operation takes place.
		 */
		private long writePointer = 0;
			
		/** Creates a new FileStorageArea.
		 * 
		 * @param file an instance of the class {@link java.io.File} representing the
		 *        storage area that will be used in the method {@link #cursor()} to
		 *        deliver a further output-iteration of this operator.
		 * @param converter the converter used to serialize the input iterations
		 *        elements.
		 * @param buffer the buffer that is used for buffering the file.
		 * @param blockSize the size of a block of buffered bytes.
		 */
		public FileStorageArea(final File file, final Converter converter, Buffer buffer, int blockSize) {
			this.file = file;
			this.converter = converter;
			try {
				braf = new BufferedRandomAccessFile(file, "rw", buffer, blockSize);
			}
			catch (IOException ioe) {
				throw new WrappingRuntimeException(ioe);
			}
		}		

		/** Stored the object o in the file.
		 * 
		 * @param o the element to insert
		 */ 
		public void insert(final Object o) {
			try {
				braf.seek(writePointer); // setting writePointer to correct position
				converter.write(braf, o); // writing object
				writePointer = braf.getFilePointer(); // saving current position
			}
			catch (IOException ioe) {
				throw new WrappingRuntimeException(ioe);
			}
		}

		
		/** Returns a cursor that delivers the file's elements from its beginning.
		 * As this does not delete them from the file, this StorageArea is not singleton.
		 * 
		 * @return a cursor delivering the file's elements
		 */
		public Cursor cursor() {
			return new AbstractCursor () {

				private long readPointer = 0;
				private Object next;

				public boolean hasNextObject() {
					try {
						braf.seek(readPointer); // setting readPointer to correct position
						next = converter.read(braf); // reading object
						readPointer = braf.getFilePointer(); // storing readPointer
						return true;
					}
					catch (IOException ioe) {
						return  false;
					}
				}

				public Object nextObject() {
					return next;
				}
			};
		}

		/** As reading from the file is not destroying, it can be traversed by 
		 * more than one iterator and this StorageArea is not singleton.
		 * 
		 * @return <code>false</code>
		 */
		public boolean isSingleton() {
			return false;
		}

		/** Closes and deletes the file to release resources.
		 */ 
		public void close() {
			try {
				braf.close(); // closing BufferedRandomAccess file
			}
			catch (IOException ioe) {
				throw new WrappingRuntimeException(ioe);
			}
			file.delete(); // deleting file
		}
	}

	/** The input cursor.
	 */
	protected Cursor inCursor;
			
	/** Counter determining how often next has been invoked on the underlying 
	 * cursor {@link #inCursor}.
	 */
	long inCursorCounter=0;

	/** The storage area where the input iteration's elements are buffered.
	 */
	protected StorageArea storageArea;
		
	/** Counter determining how many output cursors have been generated.
	 */
	int numberOfCursors=0;
		
	/** List of cursors over the {@link storageArea}, one for each output exept for
	 * the {@link #leader}, whos cursor is <code>null</code>.
	 */
	List storageAreaCursors = new ArrayList();
	
	/** The index of the output cursor whichs next method has mostly been called, i.e. 
	 * the cursor currently consuming the input {@link inCursor}. 
	 */
	int leader=0;

	/** The output cursor used for returning elements.
	 */
	protected Cursor outCursor;

	/** List of cursors returned by {@link #cursor()}.
	 */
	protected ArrayList outCursors = new ArrayList();
	
	/** Counter determining how many output-cursors have been closed.
	 */
	protected int numberOfClosedCursors;
	
	/** Flag determining if the tee-cursor should return new 
	 * cursors if all outputs were closed.
	 */
	protected boolean explicitClose;
	

	/**
	 * Creates a new tee-cursor and duplicates the input using the specified storage area.
	 *
	 * @param iterator the iteration representing the input of this operator.
	 * @param storageArea the storage area that will be used to buffer the input iterator
	 * @param explicitClose flag determining if the tee-cursor should return new 
	 * cursors if all outputs were closed. 
	 */
	public TeeCursor(Iterator iterator, StorageArea storageArea, boolean explicitClose) {
		inCursor = Cursors.wrap(iterator);
		this.storageArea = storageArea;
		this.explicitClose = explicitClose;
		numberOfClosedCursors = 0;
	}

	/**
	 * Creates a new tee-cursor and duplicates the input using a 
	 * {@link ListStorageArea} to store the elements.
	 *
	 * @param iterator the iteration representing the input of this operator.
	 * @param list the list to store elements in
	 */
	public TeeCursor(Iterator iterator, List list) {
		this (iterator, new ListStorageArea(list), false);
	}
		
	/**
	 * Creates a new tee-cursor and duplicates the input using a 
	 * {@link ListStorageArea} with an ArrayList to store the elements.
	 *
	 * @param iterator the iteration representing the input of this operator.
	 */
	public TeeCursor(Iterator iterator) {
		this (iterator, new ArrayList());
	}

	/**
	 * Creates a new tee-cursor and duplicates the input using the given queue. 
	 *
	 * @param iterator the iteration representing the input of this operator.
	 * @param queue the queue used for storing elements. The queue should be order 
	 * 	preserving. 
	 */
	public TeeCursor(Iterator iterator, Queue queue) {
		this (iterator, new QueueStorageArea(queue), false);
	}
	
	/**
	 * Creates a new tee-cursor and duplicates the input using the given file. 
	 *
	 * @param iterator the iteration representing the input of this operator.
	 * @param file an instance of the class {@link java.io.File} representing the
	 *        storage area that will be used in the method {@link #cursor()} to
	 *        deliver a further output-iteration of this operator.
	 * @param converter the converter used to serialize the input iterations
	 *        elements.
	 * @param buffer the buffer that is used for buffering the file.
	 * @param blockSize the size of a block of buffered bytes.
	 */
	public TeeCursor(Iterator iterator, final File file, final Converter converter, Buffer buffer, int blockSize) {
		this (iterator, new FileStorageArea(file,converter,buffer,blockSize), false);
	}
	
	/**
	 * Opens the tee-cursor, i.e., signals the cursor to reserve resources.
	 * Before a cursor has been opened calls to methods like
	 * <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Therefore <tt>open</tt> must be called before a cursor's data
	 * can be processed. Multiple calls to <tt>open</tt> do not have any effect,
	 * i.e., if <tt>open</tt> was called the cursor remains in the state
	 * <i>opened</i> until its <tt>close</tt> method is called.
	 * 
	 * <p>Note, that a call to the <tt>open</tt> method of a closed cursor
	 * usually does not open it again because of the fact that its state
	 * generally cannot be restored when resources are released respectively
	 * files are closed.</p>
	 */
	public void open() {
		super.open();
		outCursor = cursor();
		outCursor.open();
	}

	/**
	 * Returns a new {@link xxl.core.cursors.Cursor cursor} delivering exactly
	 * the same elements as the decorated input iteration returned by a call to
	 * the <tt>next</tt> method, because these elements have been inserted in the
	 * user defined storage area. Only the order, the elements may be returned
	 * in, can be different, because calls to <tt>next</tt> and <tt>peek</tt> are
	 * delegated from the cursor to the underlying storage area that may realize
	 * a special strategy. The <tt>remove</tt> and <tt>update</tt> operation of
	 * the returned cursor throws an
	 * {@link java.lang.UnsupportedOperationException} due to the fact that a
	 * deletion of an element contained in a duplicated output iteration is not
	 * well defined and would produce a
	 * {@link java.util.ConcurrentModificationException}, because all other
	 * iterations get inconsistent.
	 * 
	 * <p><b>Note:</b>
	 * This method checks if the underlying storage area realizes a
	 * Singleton-Design-Pattern, so only one instance of a cursor may be returned
	 * by this method. Every further call throws an
	 * {@link java.lang.IllegalStateException}.
	 *
	 * @return a cursor contaning the elements of the input iteration
	 * @throws IllegalStateException if the storage area can only be traversed
	 *         for one time, i.e., the <tt>isSingleton</tt> method returns
	 *         <tt>true</tt>, but it is traversed for a second time.
	 */
	public Cursor cursor() throws IllegalStateException {
		if (storageArea.isSingleton() && numberOfCursors==2)
			throw new IllegalStateException("The storage area is not allowed to be a singleton storage area.");
		if (numberOfClosedCursors>0 && !explicitClose && numberOfCursors==numberOfClosedCursors)
			throw new IllegalStateException("No more output cursors can be returned after all others have been closed.");
		numberOfCursors++;	
		storageAreaCursors.add(null);

		Cursor result = new AbstractCursor() {
			int id = numberOfCursors-1;
			long counter=0;

			public void open() {
				super.open();
				if (id>0)
					storageAreaCursors.set(id,storageArea.cursor()); 
			}

			public boolean hasNextObject() {
				if (counter==inCursorCounter) {
					return inCursor.hasNext();
				}
				else
					return ((Cursor) storageAreaCursors.get(id)).hasNext();
			}

			public Object nextObject() {
				if (counter==inCursorCounter) {
					if (leader!=id) {
						Cursor myCursor = (Cursor) storageAreaCursors.get(id);
						if (leader==-1)
							myCursor.close();
						else
							storageAreaCursors.set(leader, myCursor);
						storageAreaCursors.set(id, null);
						leader = id;
					}
					if (inCursor.hasNext()) {
						Object next = inCursor.next();
						storageArea.insert(next);
						inCursorCounter++;
						counter++;
						return next;
					}
					else
						throw new NoSuchElementException();
				}
				counter++;
				return ((Cursor) storageAreaCursors.get(id)).next();
			}

			public void close() {
				numberOfClosedCursors++;
				if (numberOfClosedCursors==numberOfCursors && !explicitClose) {
					closeAll();
				}					
				if (leader!=id)
					((Cursor) storageAreaCursors.get(id)).close();
				else
					leader=-1;
			}
		};
		outCursors.add(result);
		return result;
	}

	/**
	 * Closes the tee-cursor. Signals it to clean up resources. This does not
	 * always close the input cursor, which is closed by {@link #closeAll()}
	 * or, if {#explicitClose} is <code>false</code>, when this and all cursors
	 * returned by {@link #cursor()} have been closed. 
	 * After a call to <tt>close</tt> calls
	 * to methods like <tt>next</tt> or <tt>peek</tt> are not guarantied to yield
	 * proper results. Multiple calls to <tt>close</tt> do not have any effect,
	 * i.e., if <tt>close</tt> was called the tee cursor remains in the state
	 * "closed".
	 */
	public void close() {
		super.close();
		outCursor.close();
	}
	
	/**
	 * Closes this tee-cursor, the input cursor, the storagearea and all
	 * cursors returned by {@link #cursor()}.
	 */
	public void closeAll() {
		if (numberOfClosedCursors!=numberOfCursors) {
			Iterator it = outCursors.iterator();		
			while (it.hasNext()) {
				((Cursor)it.next()).close();
			}
			numberOfClosedCursors=numberOfCursors;
		}
		inCursor.close();
		storageArea.close();
		explicitClose = false;
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the iteration has more elements, <tt>false</tt> otherwise
	 */
	protected boolean hasNextObject() {
		return outCursor.hasNext();
	}

	/**
	 * Returns the next element in the iteration.	protected Object
	 * 
	 * @return next element in the iteration
	 */ 
	protected Object nextObject() { 
		return outCursor.next();
	}

	/**
	 * Example which shows the usage of this class.
	 *  
	 * @param args Command line arguments are ignored here.
	 */
	public static void main(String[] args) {

		/******************************************************************************/
		/*         Example 1                                                          */
		/******************************************************************************/
		System.out.println("Example 1");
		System.out.println("---------");
		
		int numberOfCursors = 5;
		int numberOfElementsPerCursor = 100;
		boolean verbose = true;
		StorageArea storageArea = new ListStorageArea(new java.util.ArrayList()); 
		// StorageArea storageArea = new QueueStorageArea((Queue)ArrayQueue.FACTORY_METHOD.invoke()); 
		
		Cursor cursor = new xxl.core.cursors.sources.Enumerator(numberOfElementsPerCursor);
		
		TeeCursor tee = new TeeCursor(cursor, storageArea, false); 
		
		Cursor cursors[] = new Cursor[numberOfCursors];
		boolean fullyConsumed[] = new boolean[numberOfCursors];
		
		cursors[0] = tee;
		cursors[0].open();
		fullyConsumed[0]=false;
		
		for (int i=1; i<numberOfCursors; i++) {
			cursors[i] = tee.cursor();
			cursors[i].open();
			fullyConsumed[i]=false;
		}
		
		java.util.Random random = new java.util.Random();
		
		int totalNumberOfElements = 0;
		int numberOfFullyConsumedCursors = 0;
		int cursorNumber;
		while (numberOfFullyConsumedCursors<numberOfCursors) {
			cursorNumber = (int) (random.nextDouble()*numberOfCursors);
			if (!fullyConsumed[cursorNumber]) {
				if (cursors[cursorNumber].hasNext()) {
					Object element = cursors[cursorNumber].next();
					if (verbose)
						System.out.println("Cursor number: "+cursorNumber+", Element: "+element);
					totalNumberOfElements++;
				}
				else {
					fullyConsumed[cursorNumber] = true;
					System.out.println("Cursor "+cursorNumber+" has finished its work");
					numberOfFullyConsumedCursors++;
				}
			}
		}
		
		Cursor c2 = tee.cursor();
		if (xxl.core.cursors.Cursors.count(c2)!=numberOfElementsPerCursor)
			throw new RuntimeException("Last cursor was not succesful");
		
		for (int i=0; i<numberOfCursors; i++)
			cursors[i].close();
		
		cursor.close();
		
		if (totalNumberOfElements!=numberOfElementsPerCursor*numberOfCursors)
			throw new RuntimeException("Not all elements were delivered");

		/******************************************************************************/
		/*         Example 2                                                          */
		/******************************************************************************/
		System.out.println("Example 2");
		System.out.println("---------");
		
		cursor = new xxl.core.cursors.sources.Enumerator(numberOfElementsPerCursor);
		cursor.open();
		
		tee = new TeeCursor(cursor, storageArea, false); 
		
		cursors = new Cursor[numberOfCursors];
		fullyConsumed = new boolean[numberOfCursors];
		
		cursors[0] = tee;
		cursors[0].open();
		fullyConsumed[0]=false;
		
		for (int i=1; i<numberOfCursors; i++) {
			cursors[i] = tee.cursor();
			cursors[i].open();
			fullyConsumed[i]=false;
		}
		
		random = new java.util.Random();
		
		numberOfFullyConsumedCursors=0;
		totalNumberOfElements = 0;
		cursorNumber = 0;
		int step = 1;

		while (numberOfFullyConsumedCursors<numberOfCursors) {
			/*if (step==5) {
				c2 = tee.cursor();
				if (xxl.core.cursors.Cursors.count(c2)!=numberOfElementsPerCursor)
					throw new RuntimeException("Intermediate cursor was not succesful");
			}*/
			for (int i=0; i<step; i++) {
				if (!fullyConsumed[cursorNumber]) {
					if (cursors[cursorNumber].hasNext()) {
						Object element = cursors[cursorNumber].next();
						if (verbose)
							System.out.println("Cursor number: "+cursorNumber+", Element: "+element);
						totalNumberOfElements++;
					}
					else {
						fullyConsumed[cursorNumber] = true;
						System.out.println("Cursor "+cursorNumber+" has finished its work");
						numberOfFullyConsumedCursors++;
					}
				}
			}
			step++;
			cursorNumber = (cursorNumber+1)%numberOfCursors;
		}
		
		for (int i=0; i<numberOfCursors; i++)
			cursors[i].close();
		
		cursor.close();
		
		if (totalNumberOfElements!=numberOfElementsPerCursor*numberOfCursors)
			throw new RuntimeException("Not all elements were delivered");
		
		/*********************************************************************/
		/*                            Example 3                              */
		/*********************************************************************/
		String filename = 
			XXLSystem.getOutPath(new String[]{"output","core"})+File.separator+"TeeCursor.dat";
		
		TeeCursor teeCursor = new TeeCursor(
			new xxl.core.cursors.sources.Enumerator(100),
			new File(filename),
			xxl.core.io.converters.IntegerConverter.DEFAULT_INSTANCE,
			new LRUBuffer(128), 
			1024
		);
		
		teeCursor.open();
		Cursor tempCursor1 = teeCursor.cursor();
		tempCursor1.open();

		System.out.println("Consuming input iteration: ");
		while (teeCursor.hasNext())
			System.out.println(teeCursor.next());

		System.out.println("Consuming the first temporal generated cursor: ");
		while (tempCursor1.hasNext())
			System.out.println(tempCursor1.next());

		tempCursor1.close();
		Cursor tempCursor2 = teeCursor.cursor();
		tempCursor2.open();
		
		System.out.println("Consuming the second temporal generated cursor: ");
		while (tempCursor2.hasNext())
			System.out.println(tempCursor2.next());
			
		tempCursor2.close();
		teeCursor.close();

		/*********************************************************************/
		/*                            Example 4                              */
		/*********************************************************************/
		
		teeCursor = new TeeCursor(
			new xxl.core.cursors.sources.Enumerator(100),
			new File(filename),
			xxl.core.io.converters.IntegerConverter.DEFAULT_INSTANCE,
			new LRUBuffer(128),
			1024
		);
		
		teeCursor.open();
		Cursor tempCursor = teeCursor.cursor();
		tempCursor.open();

		System.out.println("An alternating consumption of one element of the input iteration and one element of the temporal generated cursor: ");
		while (teeCursor.hasNext() || tempCursor.hasNext()) {
			if (teeCursor.hasNext())
				System.out.println("RIGHT : " + teeCursor.next());
			if (tempCursor.hasNext())
				System.out.println("LEFT  : " + tempCursor.next());
		}

		tempCursor.close();
		teeCursor.close();

		System.out.println("Test finished sucessfully");
	}
}
