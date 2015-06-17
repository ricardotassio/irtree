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

package xxl.core.util.random;

import xxl.core.math.functions.RealFunction;
import xxl.core.math.functions.RealFunctions;

/** The class provides a random number generator delivering random numbers distributed
 * like a user-defined function F. <BR>
 * 
 * If f(x) is the pdf of the distribution F, a function g(x) with f(x) <= c * g(x) for some constant c
 * must be given with g(x) the pdf of a distribution G.
 * Random numbers with distribution F could be obtained by the following algorithm:
 * (1) Generrate a random number x with distribution G <BR>
 * (2) calculate r = c * g(x) / f(x) <BR>
 * (3) Generrate an unifomly distribution random number u ( ~ U[0,1]) <BR>
 * (4) IF u*r < 1 return x, otherwise start again <BR>
 * To obtain random numbers x with x ~ G the {@link InversionDistributionBasedPRNG} could be used. <BR>
 * For producing random numbers use an instance of this class with the 
 * {@link xxl.core.cursors.sources.ContinuousRandomNumber ContinuousRandomNumber cursor}.
 */

public class RejectionDistributionBasedPRNG implements ContinuousRandomWrapper{

	/** pdf of the distribution F */
	RealFunction f;

	/** pdf of the distribution G */
	RealFunction g;

	/** some constant with f(x) <= c * g(x) f.a. x */
	double c;

	/** random wrapper delivering random numbers x with x ~ G */
	ContinuousRandomWrapper crwG;

	/** random wrapper delivering random numbers u with u ~ U[0,1] */
	ContinuousRandomWrapper crw;

	/** Constructs a new object of the class.
	 *
	 *
	 * @param crw random wrapper delivering random numbers u with u ~ U[0,1]
	 * @param f pdf of the distribution F the produced random numbers should belong to
	 * @param g pdf of the distribution G with f(x) <= c * g(x) f.a. x
	 * @param c the constant c in f(x) <= c * g(x) f.a. x
	 * @param crwG random wrapper delivering random numbers x with x ~ G
	 *
	 * @see InversionDistributionBasedPRNG
	 * @see ContinuousRandomWrapper
	 * @see xxl.core.math.functions.RealFunction
	 */	 
	public RejectionDistributionBasedPRNG( ContinuousRandomWrapper crw, RealFunction f, RealFunction g, double c, ContinuousRandomWrapper crwG){
		this.crw = crw;
		this.f = f;
		this.g = g;
		this.c = c;
		this.crwG = crwG;
	}

	/** Return the next computed pseudo random number x with x ~ F.
	 * @return the next computed pseudo random number x with x ~ F.
	 */
	public double nextDouble(){
		double x = -1.0;
		boolean found = false;
		do{
			x = crwG.nextDouble();
			double r = c * g.eval( x) / f.eval( x);
			double u = crw.nextDouble();
			if ( u*r < 1.0)
				found = true;
		}
		while (! found);
		return x;
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main( String[] args) {
		// prob. density function of the distribution F with x ~ F
		// pdf f(x) = x^4 + I(x)_[.3,1], x in [0,1]
		RealFunction f = RealFunctions.pdfCP00();
		/* probability density function to G(x): g(x) = 2x * I(x)[0,1]*/
		RealFunction g = RealFunctions.pdfCont01();
		/* Inversal function of G:  G^{-1}(y) = \sqrt(y) * I(y)[0,1] */
		RealFunction invG = RealFunctions.invDistCont01();
		// ---
		double c = 2.65;
		// ---
		ContinuousRandomWrapper jcrw = new JavaContinuousRandomWrapper();
		ContinuousRandomWrapper gRnd = new InversionDistributionBasedPRNG( jcrw, invG);
		RejectionDistributionBasedPRNG rb = new RejectionDistributionBasedPRNG( jcrw, f, g, c, gRnd);
		java.util.Iterator iterator = new xxl.core.cursors.sources.ContinuousRandomNumber( rb, 10000);
		System.out.println("# Random numbers distributed with the pdf f(x) = x^4 + I(x)_[.3,1], x in [0,1]");
		while( iterator.hasNext())
			System.out.println( iterator.next());
	}
}