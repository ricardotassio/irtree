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

package xxl.core.cursors.sources.io;

import xxl.core.cursors.AbstractCursor;
import xxl.core.util.concurrency.Channel;

/**
 * This class provides a cursor backed on a
 * {@link xxl.core.util.concurrency.Channel channel}, i.e., the cursor listens to
 * the given channel and waits for objects send through it. When a certain
 * termination object is received (for example <tt>null</tt>), the cursor stops
 * listening and does not provide any more data.
 */
public class ChannelCursor extends AbstractCursor {
	
	/**
	 * The communication channel that is used for receiving the data of this
	 * iteration.
	 */
	protected Channel channel;

	/**
	 * The termination object that is used to signal the end of the iteration.
	 */
	protected Object termObject;
	
	/**
	 * Creates a new channel-cursor that returns the data received through the
	 * given channel.
	 * 
	 * @param channel the channel to which the channel-cursor is listening for
	 *        its data.
	 * @param termObject the object that is used to signal the end of the
	 *        iteration. When the channel is exhausted, i.e., no more data will
	 *        be sent through it, this object will be passed to the
	 *        channel-cursor via its channel.
	 */
	public ChannelCursor(Channel channel, Object termObject) {
		this.channel = channel;
		this.termObject = termObject;
	}
	
	/**
	 * Creates a new channel-cursor that returns the data received through the
	 * given channel. Until the channel-cursor receives a <tt>null</tt> object
	 * it listens to its channel.
	 * 
	 * @param channel the channel to which the channel-cursor is listening for
	 *        its data.
	 */
	public ChannelCursor(Channel channel) {
		this(channel, null);
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the channel-cursor has more elements.
	 */
	protected boolean hasNextObject() {
		return (next = channel.take()) != termObject;
	}
	
	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the channel-cursor's methods, e.g., <tt>update</tt>
	 * or <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return next;
	}

	/**
	 * The main method contains some examples to demonstrate the usage and the
	 * functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to submit
	 *        parameters when the main method is called.
	 */
	public static void main(String[] args) {
		final Channel channel = new xxl.core.util.concurrency.AsynchronousChannel();
		
		(
			new Thread() {
				public void run() {
					channel.put(new Integer(17));
					channel.put(new Integer(42));
					channel.put(new Integer(1));
					channel.put(new Integer(4));
					channel.put(new Integer(13));
					channel.put(null);
				}
			}
		).start();
		
		ChannelCursor cursor = new ChannelCursor(channel);
		
		cursor.open();
		
		while (cursor.hasNext())
			System.out.println(cursor.next());
			
		cursor.close();
	}
}
