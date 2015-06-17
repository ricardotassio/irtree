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

package xxl.core.util.timers;

/**
 * This is a timer which uses a native library to implement the Timer 
 * interface. The precision is usually very good. The library has to 
 * be inside the path.
 */
public class JNITimer implements Timer {

	/**
	 * The last starting time of this timer.
	 */
	protected long starttime;
	
	/**
	 * Constructs a new timer that is implemented in the
	 * platform dependant library "JNITimer". This code
	 * only works when the appropriate JNI-library is availlable
	 * for your platform. JNI-Code is implemented in the
	 * supplemental package connectivity.jni.
	 */
	public JNITimer() {
	}
	
	/**
	 * Starts a JNITimer.
	 */
	public native void start();
	
	/**
	 * Returns time in ms since JNITimer was started.
	 * @return returns time in ms since JNITimer was started
	 */
	public native long getDuration();
	
	/**
	 * Returns number of ticks per second
	 * @return returns number of ticks per second 
	 */	
	public native long getTicksPerSecond();

	/**
	 * Returns string "Native Timer"
	 * @return returns string "Native Timer"
	 */
	public String timerInfo() {
		return "Native Timer";
	}

	/**
	 * Performs a timer test.
	 * @param args can be used to submit parameters when the main method is called
	 */
	public static void main(String args[]) {
		TimerUtils.timerTest(new JNITimer());
	}

	/**
	 * Loads the library.
	 */
	static {
		try {
			System.loadLibrary("JNITimer");
		}
		catch (Throwable t) {
			throw new RuntimeException(
				"Library JNITimer could not be loaded from java.library.path: "+
				System.getProperty("java.library.path"));
		}
	}
}
