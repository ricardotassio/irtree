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

package xxl.core.util.concurrency;

/**
 * Implements a channel by using semaphores.
 * If the channel does not contain an object, the caller thread 
 * of the take method has to wait. If the channel contains an object,
 * the caller thread of the put method has to wait.
 *
 * The concept of channels is well described in Doug Lea: "Concurrent
 * Programming in Java", Second Edition, Sun Microsystems. 
 * Online: http://java.sun.com/Series/docs/books/cp/
 * Be aware of the trickiness of concurrent programming!
 */
public class AsynchronousChannel implements Channel {
	/**
	 * Semaphore which has to be acquired before writing the Object.
	 */
	protected Semaphore putPermit;
	/**
	 * Semaphore which has to be acquired before reading the Object.
	 */
	protected Semaphore takePermit;
	
	/** Buffered object */
	protected Object object=null;
	
	/** 
	 * Constructs an asynchronous channel using the semaphores specified.
	 * Usually, the user does not use this constructor but the parameterless
	 * constructor.
	 *
	 * @param putPermit Semaphore which has to be acquired before writing the Object.
	 * @param takePermit Semaphore which has to be acquired before reading the Object.
	 */
	public AsynchronousChannel(Semaphore putPermit, Semaphore takePermit) {	
		this.putPermit = putPermit;
		this.takePermit = takePermit;
		takePermit.acquire();
	}

	/** 
	 * Constructs an asynchronous channel.
	 */
	public AsynchronousChannel() {	
		this(new Semaphore(), new Semaphore());
	}
	
	/**
	 * Waits for an object that is sent from a different thread 
	 * via the put method.
	 *
	 * @return object from the channel
	 */
	public Object take() {
		takePermit.acquire();
		Object ret = object;
		object = null;
		putPermit.release();
		return ret;
	}

	/**
	 * Tries to read an object from the channel. 
	 * If an object is not availlable, this method returns false.
	 * The object is returned inside the first element
	 * of the object array.
	 *
	 * @param o Object array. The first element is used for
	 *	returning the desired object
	 * @return true - iff there was an object in the channel
	 */
	public boolean attemptTake(Object o[]) {
		if (takePermit.attempt()) {
			o[0] = object;
			object = null;
			putPermit.release();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Puts an object into the channel.
	 *
	 * @param object object that is put into the channel
	 */
	public void put(Object object) {
		putPermit.acquire();
		this.object = object;
		takePermit.release();
	}
	
	/**
	 * Determines if an object is currently in the channel (no blocking!).
	 *
	 * @return true if the channel currently does not contain any element
	 */
	public boolean isEmpty() {
		if (takePermit.attempt()) {
			takePermit.release();
			return false;
		}
		else
			return true;
	}

	/**
	 * Determines if the channel currently is full (no blocking!).
	 *
	 * @return true if the channel is currently full
	 */
	public boolean isFull() {
		if (putPermit.attempt()) {
			putPermit.release();
			return false;
		}
		else
			return true;
	}

	/**
	 * Demonstrates the use of an asynchronous channel.
	 * 
	 * @param args the input arguments of the main method
	 */
	public static void main(String args[]) {
		System.out.println("AsynchronousChannel");

		final Channel channel = new AsynchronousChannel();

		// Construction of two demo threads
		Thread t = new Thread () {
			public void run() {
				int number=100;
				
				while (number>0) {
					if (!channel.isEmpty()) {
						System.out.println("Received: "+((Integer) channel.take()).intValue());
						number--;
					}
					else
						System.out.println("Channel does not contain an object");
					try { Thread.sleep(1); } catch (Exception e) {}
				}
			}
		};
		t.start();
	
		int count = 0;
		while (t.isAlive()) {
			if (!channel.isFull())
				channel.put(new Integer(count++));
			// try { Thread.sleep(2); } catch (Exception e) {}
		}
	}
}
