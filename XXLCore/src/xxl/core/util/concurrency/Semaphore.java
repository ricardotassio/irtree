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
 * Implements a Semaphore by using synchronized blocks.
 * The concept of Semaphores is well described in Doug Lea: "Concurrent
 * Programming in Java", Second Edition, Sun Microsystems. 
 * Online: http://java.sun.com/Series/docs/books/cp/
 */
public class Semaphore {
	
	/**
	 * The number of thread that still can enter the critical section at this
	 * moment. 
	 */
	private int count;
	
	/**
	 * The total number of threads that can enter the critical section. 
	 */
	private int initialCount;
	
	/** 
	 * Constructs a Semaphore that allows count threads to enter the critical section
	 * (section between acquire and release) at once.
	 *
	 * @param count maximal number of threads that can enter the critical section at once.
	 * @param initialValue number of threads that can enter the critical section after 
	 * 		construction (useful for several algorithms).
	 */
	public Semaphore(int count,int initialValue) {
		this.count = initialValue;
		this.initialCount = count;
		if (count<=0)
			throw new RuntimeException("Semaphore construction exception: count has to be greater than 0.");
	}
	
	/** 
	 * Constructs a Semaphore that allows only thread to be in the critical section
	 * at once.
	 */
	public Semaphore() {
		this(1,1);
	}
	
	/** 
	 * Acquires the Semaphore. This Method stops the current thread if
	 * the internal counter of the semaphore
	 */
	public synchronized void acquire() {
		while (count==0) {
			try  {
				wait();
			}
			catch (InterruptedException e) {}
		}
		count--;
	}
	
	/** 
	 * Releases the Semaphore and wakes up a waiting thread.
	 */
	public synchronized void release() {
		count++;
		if (count>initialCount)
			throw new RuntimeException("Semaphore exception: release is called too often");
		// let the next waiting thread continue execution
		notify();
	}
	
	/** 
	 * Tries to acquire the Semaphore but does not wait. If false is
	 * returned no lock has been acquired.
	 *
	 * Example:
	 * <code>
	 *		if (s.attempt()) {
	 *			... do something ...
	 *			s.release();
	 *		}
	 * </code>
	 * @return true if acquired
	 */
	public synchronized boolean attempt() {
		if (count>0) {
			count--;
			return true;
		}
		else
			return false;
	}

	/** 
	 * Tries to acquire the Semaphore and waits msecs at most. If false is
	 * returned no lock has been acquired.
	 *
	 * Example:
	 * <code>
	 *		if (s.attempt(1000)) {
	 *			... do something ...
	 *			s.release();
	 *		}
	 * </code>
	 * @param msecs maximum timeout to wait
	 * @return true if acquired
	 * 
	 */
	public boolean attempt(int msecs) {
		boolean state = attempt();
		
		if (state)
			return true;
		else {
			synchronized (this) {
				try  {
					wait(msecs);
				}
				catch (InterruptedException e) {}
				// must be in a synchronized statement because if the 
				// waiting thread receives a notification, he should 
				// be the one that gets the next lock.
				return attempt();
			}
		}
	}

	/**
	 * Writes the current state into a string (for debugging purposes). 
	 * Do not use something like that:
	 *
	 * 		if (sem.toString().equals("1") {
	 * 			critical section
	 *		}
	 * 
	 * @return returns current state as a string
	 */
	public String toString() {
		synchronized (this) {
			return String.valueOf(count);
		}
	}

	/**
	 * For demonstration purpose.
	 */
	private static class DemoThread extends Thread {
		
		/**
		 * An array of integer objects manipulated during the demonstration run.
		 */
		Integer count[];
		
		/**
		 * The used semaphore.
		 */
		Semaphore sem;
		
		/**
		 * Creates a new demonastration thread.
		 * 
		 * @param name the name of the thread.
		 * @param count an array of integer objects manipulated during the
		 *        demonstration run.
		 * @param sem the used semaphore.
		 */
		DemoThread(String name,Integer count[],Semaphore sem) {
			this.count = count;
			this.sem = sem;
			// Call Thread.setName
			setName(name);
		}

		/**
		 * run demonstration
		 */
		public void run() {
			for (int i=0 ; i<50 ; i++) {
				sem.acquire();
				try { Thread.sleep(20); } catch (InterruptedException e) {}
				System.out.println(getName() + " Iteration " + i);
				count[0] = new Integer(count[0].intValue()+1);
				sem.release();
			}
		}
	}

	/** 
	 * Demonstrates the use of a Semaphore to control two competing 
	 * threads.
	 * 
	 * @param args the input arguments of the main method
	 */
	public static void main(String args[]) {
		System.out.println("Controlling threads with a semaphore");
		final Semaphore sem = new Semaphore();

		// No thread starts working before I tell him.
		sem.acquire();

		// Resource: count[]
		Integer count[] = new Integer[1];
		count[0] = new Integer(0);
		
		// Construction of two demo threads
		Thread t1 = new DemoThread("Thread 1",count,sem);
		Thread t2 = new DemoThread("Thread 2",count,sem);
		// running...
		t1.start();
		t2.start();
		
		while (t1.isAlive() || t2.isAlive()) {
			// Controlling both threads
			System.out.println("Controller - Count: " + count[0]);
			try { Thread.sleep(200); } catch (InterruptedException e) {}
			System.out.println("Controller - Let threads work for a while...");
			sem.release();
			try { Thread.sleep(500); } catch (InterruptedException e) {}
			sem.acquire();
		}
		
		// Exception for main maker!
		if (count[0].intValue()!=100)
			throw new RuntimeException("Semaphores do not work correctly!");

		System.out.println("Test attempt(msec)");
		// The semaphore is locked at this time
		final long millis = System.currentTimeMillis();
		
		new Thread() {
			public void run() {
				if (sem.attempt(1000)) {
					long timediff = System.currentTimeMillis() - millis;
					System.out.println("Got the semaphore after " + timediff + "ms");
					sem.release();
				}
			}
		}.start();
		
		try { Thread.sleep(500); } catch (InterruptedException e) {}
		// Let the thread continue
		sem.release();
	}
}
