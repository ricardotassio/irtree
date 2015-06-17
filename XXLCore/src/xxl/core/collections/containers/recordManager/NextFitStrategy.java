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

package xxl.core.collections.containers.recordManager;

import java.util.Iterator;
import java.util.Map;

import xxl.core.collections.containers.recordManager.RecordManager.PageInformation;

/** 
 * This class provides the next-fit stratey for the record manager.
 * Next fit is a very simple strategy that returns the first block with 
 * enough memory to hold the record. Different from first-fit it starts
 * each search where the last search ended.
 */
public class NextFitStrategy extends AbstractStrategy {
	/**
	 * Stores the last entry where a reservation has been made.
	 */
	private Map.Entry lastEntry;
	
	/**
	 * Creates a new NextFitStrategy Object.
	 */
	public NextFitStrategy() {
		lastEntry = null;
	}

	/**
	 * Tests an entry if there is enough space for a reservation.
	 * @param entry The entry to be tested.
	 * @param bytesRequired bytes needed for the Record.
	 * @return the entry if a reservation is possible else null.
	 */
	public Map.Entry testEntry(Map.Entry entry, int bytesRequired) {
		PageInformation pi = (PageInformation) entry.getValue();
		int bytesFree = pi.bytesFreeAfterPossibleReservation(bytesRequired);
		if (bytesFree>=0)
			return entry;
		else
			return null;
	}

	/**
	 * Tests a whole iterator of entries if there is enough space for a 
	 * reservation.
	 * @param it Iterator with entries.
	 * @param bytesRequired bytes needed for the Record.
	 * @return the entry if a reservation is possible else null.
	 */
	private Map.Entry findPlaceInIterator(Iterator it, int bytesRequired) {
		Map.Entry searchEntry;
		while (it.hasNext()) {
			searchEntry = testEntry((Map.Entry) it.next(),bytesRequired);
			if (searchEntry!=null)
				return searchEntry;
		}
		return null;
	}

	/**
	 * Finds a block with enough free space to hold the given number
	 * of bytes.
	 * @param bytesRequired The free space needed, in bytes.
	 * @return Id of the Page or null, if no such page exists.
	 */
	public Object getPageForRecord(int bytesRequired) {
		Map.Entry searchEntry=null;

		if (lastEntry!=null)
			searchEntry = testEntry(lastEntry, bytesRequired);

		if (searchEntry==null) {
			if (lastEntry==null)
				searchEntry = findPlaceInIterator(pages.entrySet().iterator(),bytesRequired);
			else {
				searchEntry = findPlaceInIterator(
					pages.tailMap(lastEntry.getKey()).entrySet().iterator(),
					bytesRequired);
				if (searchEntry==null) {
					searchEntry = findPlaceInIterator(
						pages.headMap(lastEntry.getKey()).entrySet().iterator(),
						bytesRequired);
				}
			}
		}
		
		lastEntry = searchEntry;
		if (searchEntry==null)
			return null;
		else
			return searchEntry.getKey();
	}

	/**
	 * Informs the strategy, that a page has been deleted by the RecordManager.
	 * @param pageId identifyer of the page which has been removed.
	 * @param pi PageInformation for the page.
	 */
	public void pageRemoved(Object pageId, PageInformation pi) {
		if (lastEntry!=null && pageId == lastEntry.getKey())
			lastEntry=null;
	}
	
	/**
	 * Outputs the state of the Strategy.
	 * @return The String representation of the state of the Strategy.
	 */
	public String toString() {
		if (lastEntry==null)
			return "NextFitStrategy: null";
		else
			return "NextFitStrategy: current page: "+lastEntry.getKey();
	}
}
