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

package xxl.core.math.statistics.nonparametric.histograms;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import xxl.core.cursors.Cursors;
import xxl.core.cursors.groupers.AggregateGrouper;
import xxl.core.cursors.groupers.ReplacementSelection;
import xxl.core.cursors.sources.RandomIntegers;

/**
 * This class realizes a histogram based on a logarithm scale.
 * As input data 2-dimensional data is considered. The first
 * dimension is used to map the vector to a certain histogram bucket.
 * The second value represents an aggregate (e.g. sum or average).
 */
public class LogScaleHistogram {

	/** element counter for the buckets */
	private int[] buckets;

	/** sum inside each bucket */
	private double[] sum;

	/** borders of the bucket. border[0]<border[1]< ... < border[bucketCount] */
	private double[] borders;

	/** left - right */
	private double size;

	/** number of buckets */
	private int bucketCount;

	/**
	 * Constructs a log scale histogram with a predefined number of buckets. The interval is [left,right].
	 * logFactor means that interval 0 ( [border[0],border[1]) ) is logFactor smaller than
	 * interval 1 ( [border[1],border[2]) ).
	 * 
	 * @param left left border
	 * @param right right border
	 * @param bucketCount number of buckets
	 * @param logFactor size factor between two consecutive intervals
	 */
	public LogScaleHistogram(double left, double right, int bucketCount, double logFactor) {
		this.bucketCount = bucketCount;
		size = right - left;

		borders = new double[bucketCount + 1];
		borders[0] = left;
		borders[bucketCount] = right;

		double partitions = Math.pow(logFactor, bucketCount - 1);
		double momPart = 1;

		for (int i = 1; i < bucketCount; i++) {
			borders[i] = left + (momPart / partitions) * size;
			momPart *= logFactor;
		}

		buckets = new int[bucketCount];
		sum = new double[bucketCount];
	}

	/**
	 * Inserts a tuple into the histogram, i.e., the according bucket is computed and
	 * the frequency is updated.
	 * 
	 * @param d x-value (decides into which bucket the
	 *	value falls)
	 * @param value y-value
	 */
	public void process(double d, double value) {
		for (int i = 1; i < bucketCount; i++) {
			if (d < borders[i]) {
				buckets[i - 1]++;
				sum[i - 1] += value;
				break;
			}
		}
		if (d > borders[bucketCount - 1]) {
			buckets[bucketCount - 1]++;
			sum[bucketCount - 1] += value;
		}
	}

	/**
	 * Returns the gathered statistic.
	 * 
	 * @return String with the data of the histogram
	 */
	public String toString() {
		StringBuffer b = new StringBuffer("Nr\tleft\tright\tcount\taverage\n");
		for (int i = 1; i <= bucketCount; i++)
			if (buckets[i - 1] > 0)
				b.append(
					i
						+ "\t"
						+ borders[i
						- 1]
						+ "\t"
						+ borders[i]
						+ "\t"
						+ buckets[i
						- 1]
						+ "\t"
						+ sum[i
						- 1] / buckets[i
						- 1]
						+ "\n");
		return b.toString();
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {
		// used data for examples
		List data = Cursors.toList(new RandomIntegers(20, 500));
		System.out.println("frequency of data");
		Iterator debug =
			new ReplacementSelection(
				new AggregateGrouper.CFDCursor(data.iterator()), 10, 
				new Comparator() {
					public int compare(Object o1, Object o2) {
						return ((Comparable) ((Object[]) o2)[1]).compareTo(((Object[]) o1)[1]);
					}
				}
			);
		while (debug.hasNext()) {
			Object[] tuple = (Object[]) debug.next();
			System.out.println("data >" + tuple[0] + "< occurred " + tuple[1] + " times");
		}

		LogScaleHistogram hist = new LogScaleHistogram(0, 20, 5, 10);
		Iterator it = data.iterator();
		while (it.hasNext())
			hist.process(((Number) it.next()).doubleValue(), new Random().nextDouble());
		System.out.println("\nResulting histogram:" + '\n' + hist);
	}
}
