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

import java.util.Iterator;
import java.io.InputStream;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;

/**
 * This class delivers an InputStream out of an Iterator which
 * contains Blocks (created with ObjectToBlockIterator for example).
 */
public class MultiBlockInputStream extends InputStream {
	/** Cursor of Blocks */
	private Cursor cursor;
	
	/** Start offset */
	private int startOffset;
	
	/** End offset */
	private int endOffset;
	
	/** The current block which is outputed */
	private Block currentBlock;
	
	/** Current offset inside the current Block */
	private int currentOffset;
	
	/** The end offset of the current Block */
	private int currentEndOffset;
	
	/**
	 * Function which is called to determine the end offset inside the current block.
	 * The function is called with 1 parameter which is the current block.
	 */
	Function getLengthUsedInsideBlock;

	/**
	 * Constructs a new MultiBlockInputStream. An object of this class outputs 
	 * parts of the Blocks of an input Iterator as a stream. Each block has an area
	 * which can contain bytes of interest (from startOffset until endOffset).
	 * In addition, for every Block, the Function getLengthUsedInsideBlock is called 
	 * (if the Function is not null) to determine the real end of interesting data
	 * inside the Block. For this purpose, the Function Block.GET_REAL_LENGTH can
	 * be useful.
	 *
	 * @param it Input Iterator which contains Block objects.
	 * @param startOffset Beginning offset inside each Block, where the interesting information
	 *	starts.
	 * @param endOffset End offset inside each Block, where the interesting information ends.
	 * @param getLengthUsedInsideBlock Function which is called for every Block (if not null)
	 *	to determine the real end offset of the current Block. The function gets the
	 *	current Block as the only parameter.
	 */
	public MultiBlockInputStream (Iterator it, int startOffset, int endOffset, Function getLengthUsedInsideBlock) {
		this.cursor = Cursors.wrap(it);
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.getLengthUsedInsideBlock = getLengthUsedInsideBlock;
		
		currentBlock = null;
		currentOffset = -1;
		currentEndOffset = -1;

		if (endOffset <= startOffset)
			throw new RuntimeException("endOffset has to be larger than startOffset");
	}

	/**
	 * Reads the next byte of the InputStream (-1 if the end of stream is reached).
	 * @return the next byte or -1 if the end of stream is reached.
	 */
	public int read() {
		int nb;
		
		if (currentBlock == null) {
			if (cursor.hasNext()) {
				currentBlock = (Block) cursor.next();
				currentOffset = startOffset;
				if (getLengthUsedInsideBlock!=null)
					currentEndOffset = 
						startOffset + 
						((Integer) getLengthUsedInsideBlock.invoke(currentBlock)).intValue();
				else
					currentEndOffset = endOffset;
			}
			else
				return -1;
		}
		
		nb = currentBlock.get(currentOffset++);
		if (currentOffset == currentEndOffset)
			currentBlock = null; // release it as soon as possible
		
		return nb;
	}

	/**
	 * Closes the InputStream.
	 */
	public void close() {
	}

	/**
	 * Use case which serializes Strings to Blocks and vice versa.
	 * @param args The command line options are ignored here.
	 */
	public static void main(String args[]) {
		String s = "Hello World";
		Cursor cursor;
		// Block b = null;
		
		System.out.println("Example");
		System.out.println("=======");
		
		cursor = new ObjectToBlockCursor(
			new xxl.core.cursors.sources.Repeater(s,10),
			xxl.core.io.converters.StringConverter.DEFAULT_INSTANCE,
			5,
			4,
			100,
			Block.SET_REAL_LENGTH
		);
		
		InputStream is = new MultiBlockInputStream(cursor,4,5,Block.GET_REAL_LENGTH);
		
		cursor = new xxl.core.cursors.sources.io.InputStreamCursor(
			new java.io.DataInputStream(is),
			xxl.core.io.converters.StringConverter.DEFAULT_INSTANCE
		);
		
		xxl.core.cursors.Cursors.println(cursor);
		
		cursor.close();
	}
}
