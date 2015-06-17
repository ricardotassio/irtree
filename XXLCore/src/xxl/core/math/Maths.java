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

package xxl.core.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import xxl.core.comparators.ComparableComparator;
import xxl.core.functions.Function;
import xxl.core.math.functions.FunctionRealFunction;
import xxl.core.math.functions.RealFunction;
import xxl.core.math.numerics.integration.TrapezoidalRuleRealFunctionArea;
import xxl.core.util.Strings;

/**
 * The class <code>Maths</code> contains methods that extend the basic
 * numeric operations provided in <tt>java.lang.Math</tt>.
 */
public class Maths {

	/**
	 *	The Integer that represents the primitive int 0.
	 */
	public static final Integer zero = new Integer(0);

	/**
	 * Don't let anyone instantiate this class.
	 */
	private Maths() {}

	/**
	 * Returns the smallest (closest to negative infinity)
	 * value that is not less than the first argument and that is
	 * equal to a mathematical integer. <br>This value is computed with
	 * the help of the second argument <tt>base</tt> as follows:<br>
	 * <code><pre>
	 * 	return i + i%base==0 ? 0 : base-i%base;
	 * </pre></code>
	 *
	 * @param i each element returned is greater than this value
	 * @param base rest added to the first argument
	 * @return an integer value that is not less than the first argument computed
	 * 		by <code>i + i%base==0 ? 0 : base-i%base</code>.
	 */
	public static final int ceil(int i, int base) {
		return i + i % base == 0 ? 0 : base - i % base;
	}

	/**
	 * Returns the largest (closest to positive infinity)
	 * value that is not greater than the first argument and
	 * that is equal to a mathematical integer. <br>This value is computed with
	 * the help of the second argument <tt>base</tt> as follows:<br>
	 * <code><pre>
	 * 	return i - i%base;
	 * </pre></code>
	 *
	 * @param i each element returned is not greater than this value
	 * @param base rest subtracted from the first argument
	 * @return an integer value that is not greater than the first argument computed
	 * 		by <code>i - i%base</code>.
	 */
	public static final int floor(int i, int base) {
		return i - i % base;
	}

	/**
	 * An implementation of the signum function for <tt>double</tt> values. <br>
	 * This function returns: <ul>
	 * <li>'1' if the given argument is greater than '0'
	 * <li>'0' if the given argument is equal to '0'
	 * <li>'-1' if the given argument is less than '0'
	 *</ul>
	 *
	 * @param d argument to be tested
	 * @return '1' if the given argument is greater than '0', '0' if it is equal
	 * 		to it, else '-1'
	 */
	public static final int signum(double d) {
		return d > 0 ? 1 : d < 0 ? -1 : 0;
	}

	/**
	 * An implementation of the signum function for <tt>long</tt> values.
	 * This function returns: <ul>
	 * <li>'1' if the given argument is greater than '0'
	 * <li>'0' if the given argument is equal to '0'
	 * <li>'-1' if the given argument is less than '0'
	 *</ul>
	 *
	 * @param i argument to be tested
	 * @return '1' if the given argument is greater than '0', '0' if it is equal
	 * 		to it, else '-1'
	 */
	public static final int signum(long i) {
		return i > 0 ? 1 : i < 0 ? -1 : 0;
	}

	/**
	 * Returns the smaller one of two objects according to a comparator.
	 *
	 * @param o1 first object
	 * @param o2 second object
	 * @param comparator comparator used to compare the two objects
	 * @return smaller object of <code>o1</code> and <code>o2</code>
	 */
	public static final Object min(Object o1, Object o2, Comparator comparator) {
		return comparator.compare(o1, o2) < 0 ? o1 : o2;
	}

	/**
	 * Returns the smaller one of two objects using a
	 * ComparableComparator.DEFAULT_INSTANCE.
	 *
	 * @param o1 the first object.
	 * @param o2 the second object.
	 * @return smaller object of <code>o1</code> and <code>o2</code>
	 */
	public static final Object min(Object o1, Object o2) {
		return min(o1, o2, ComparableComparator.DEFAULT_INSTANCE);
	}

	/**
	 * Returns the greater one of two objects according to a comparator.
	 *
	 * @param o1 first object
	 * @param o2 second object
	 * @param comparator comparator used to compare the two objects
	 * @return greater object of <code>o1</code> and <code>o2</code>
	 */
	public static final Object max(Object o1, Object o2, Comparator comparator) {
		return comparator.compare(o1, o2) >= 0 ? o1 : o2;
	}

	/**
	 * Returns the greater one of two objects using a
	 * ComparableComparator.DEFAULT_INSTANCE.
	 *
	 * @param o1 first object
	 * @param o2 second object
	 * @return greater object of <code>o1</code> and <code>o2</code>
	 */
	public static final Object max(Object o1, Object o2) {
		return max(o1, o2, ComparableComparator.DEFAULT_INSTANCE);
	}

	/**
	 * Returns the median of three objects using the comparator. 
	 * These objects don't need to be pairwise different.
	 * The implementation is as follows: <br>
	 * <pre>
	 * <code>
	 * 	if (comparator.compare(a,b) <= 0)
	 * 		if (comparator.compare(b,c) <= 0)
	 * 			return b;
	 * 		else					//i.e. c<tt><</tt>b
	 * 			return Comparators.max(a,c,comparator);
	 * 	else						//i.e. b<tt><</tt>a
	 * 		if (comparator.compare(a,c) <= 0)
	 * 			return a;
	 * 		else					//i.e. c<tt><</tt>a
	 * 			return Comparators.max(b,c,comparator);
	 * </pre></code>
	 *
	 * @param a first object
	 * @param b second object
	 * @param c third object
	 * @param comparator comparator used to compare two objects
	 * @return median of three given objects according to a given comparator
	 */
	public static final Object medianOfThree(Object a, Object b, Object c, Comparator comparator) {
		if (comparator.compare(a, b) <= 0)
			if (comparator.compare(b, c) <= 0)
				return b;
			else //i.e. c<b
				return Maths.max(a, c, comparator);
		else //i.e. b<a
			if (comparator.compare(a, c) <= 0)
				return a;
			else //i.e. c<a
				return Maths.max(b, c, comparator);
	}

	/**
	 * The 3-median-strategy used to compute the pivot element in the Quicksort-algorithm. 
	 * Three objects are taken from a given object-array, namely the left one, the right one
	 * and one in the middle. For these three elements the median is computed using the
	 * given comparator with the intention that the median
	 * delivers a separation in nearly equal parts.
	 *
	 * @param array object which delivers the left, middle and right element
	 * @param comparator comparator used to compare each tuple of the objects
	 * @return median of three given objects concerning the given comparator
	 */
	public static final Object medianOfThree(Object[] array, Comparator comparator) {
		return medianOfThree(array[0], array[array.length / 2], array[array.length - 1], comparator);
	}

	/**
	 * Extracts the mantisse-bits of a double-value d. <br>
	 * Precondition: d in [0;1) (i.e. 1.0 excluded).
	 * <p>
	 * We use full precision: i.e. bit 10^-1 is at position 62, 10^-2 at position 61 and so on.
	 * (We do not use bit 63 because bit 63 is interpreted as the sign of a
	 * long, i.e. the result of long-comparisons would not be correct.) <br>
	 * There is no need to store the exponent (=10^-1) or sign (+).
	 * <p>
	 * <b>Note</b>, that the maximum precision of doubles is then
	 * restricted to 2^-63=1.08*10^-19. <br>
	 * This method is extremely useful to create z-codes and to compute replicate-rectangles.
	 * The bit-fields can be easily merged to a zCode.
	 *
	 * @param d double value where the mantisse-bits should be extracted of
	 * @return a normalized long bit representation of the given double value
	 */
	public static long doubleToNormalizedLongBits(double d) {
		final long l = Double.doubleToLongBits(d);
		final long exponent = 0x7ff0000000000000L & l; //get exponent
		final int shift = - ((int) (exponent >>> 52) - 1022); //compute shift
		return (((0x000fffffffffffffL & l) | 0x0010000000000000L) << 10) >>> shift;
		//the implicit bit is shifted left: to position 63 (first bit)
		//then shift "shift" bits right again if exponent was != -1
	}

	/**
	 * Complementary function to {@link #doubleToNormalizedLongBits(double d)}.
	 *
	 * @param l a long value that will be interpreted as a normalized long bit representation
	 * 		of a double value
	 * @return double value belonging to a given normalized long bit representation
	 */
	public static double normalizedLongBitsToDouble(long l) {
		long exponent = 0x3FEL; // exponent = -1
		//be careful: we have to shift back the mantisse because of the implicit bit!
		if (l != 0x0L) { //check for this case
			long mask = 0x1L << 62;
			int pos = 62;
			for (; pos >= 0; pos--) {
				if ((l & mask) == mask) { //i.e. the bit at position <pos> is set
					break;
				}
				mask >>>= 1;
			}
			exponent -= (62 - pos);
			l = ((l >>>= 9) & 0x000fffffffffffffL) | (exponent << 52); //create double adding exponent and mantisse
		}
		return Double.longBitsToDouble(l);
	}

	/**
	 * Computes the distance in the p-norm of double values treated as vectors
	 * in a real space with dimension 1.
	 * 
	 * @param x argument to evaluate. That is (x,y) |--> d(x,y)
	 * @param y argument to evaluate. That is (x,y) |--> d(x,y)
	 * @param p norm parameter
	 * @throws IllegalArgumentException if the dimensions of x,y don't match or p
	 * < 1.
	 * @return p-distance of x and y
	 */
	public static double pDistance(double x, double y, int p) throws IllegalArgumentException {
		return pDistance(new double[] { x }, new double[] { y }, p);
	}

	/**
	 * Computes the distance in p-norm of the double[] treated as vectors
	 * in a real space with dimension of x.length.
	 * 
	 * @param x argument to evaluate. That is (x,y) |--> d(x,y)
	 * @param y argument to evaluate. That is (x,y) |--> d(x,y)
	 * @param p norm parameter
	 * @throws IllegalArgumentException if the dimensions of x,y don't match or p
	 * < 1.
	 * @return p-distance of x and y
	 */
	public static double pDistance(double[] x, double[] y, int p) throws IllegalArgumentException {
		if (x.length != y.length)
			throw new IllegalArgumentException("dimensions must match for computing the distance of two vectors!");
		if (p < 1)
			throw new IllegalArgumentException("p-distance only supported for p >= 1!");
		double r = 0.0;
		for (int i = 0; i < x.length; i++)
			r = r + Math.pow(Math.abs(x[i] - y[i]), p);
		r = Math.pow(r, 1.0 / p);
		return r;
	}

	/** Computes the factorial of the given integer value.
	 * The computation of the factorial of a given integer with this method is
	 * based upon recursion.
	 * 
	 * @param n given argument
	 * @throws IllegalArgumentException if the given integer is less than zero (n<0)
	 * @return factorial (n!) of the given argument
	 */
	public static double fac(int n) throws IllegalArgumentException {
		if (n < 0)
			throw new IllegalArgumentException("The computation of the factorial of a negative value is not supported!");
		if (n == 0)
			return 1.0;
		if (n == 1)
			return 1.0;
		else
			return n * fac(n - 1);
	}

	/** Computes the odd factorial OF(n) = n!/2^(n/2)*(n/2)! = (n-1) (n-3) ... 1 of the even integer value n.
	 * The computation of the factorial of any given even integer with this method is
	 * not based upon recursion.
	 * 
	 * @param n even number of type int
	 * @throws IllegalArgumentException if the given integer is less than zero (n<0) or odd
	 * @return odd factorial (n!/2(n/2)!) of the given argument
	 */
	public static double oddFactorial(int n) throws IllegalArgumentException {
		if (n < 0)
			throw new IllegalArgumentException("The computation of the factorial of a negative value is not supported!");
		if (isOdd(n))
			throw new IllegalArgumentException("The computation of the odd factorial of an odd number is not supported!");
		if (n == 0)
			return 1.0;
		return fac2(n) / (Math.pow(2.0, n / 2) * fac2((n / 2)));
	}

	/** Computes the factorial of x, i.e., the product of all positive integers less or equal x.
	* This version of computing a factorial of any given integer does not make use
	* of recursion!
	* 
	* @param x a integer value
	* @throws IllegalArgumentException if the given integer is less than zero (x<0)
	* @return x!
	*/
	public static double fac2(int x) throws IllegalArgumentException {
		if (x == 0)
			return 1.0;
		if (x < 0)
			throw new IllegalArgumentException("The computation of the faculty of a negative value is not supported!");
		double r = 1;
		for (int i = 1; i <= x; i++)
			r = r * i;
		return r;
	}

	/** Computes the binomial coefficient (n choose k).
	 * 
	 * @param n first coefficient
	 * @param k second coefficient
	 * @return binomial coefficient (n choose k)
	 */
	public static double binomialCoeff(int n, int k) {
		return fac2(n) / (fac2(k) * fac2(n - k));
	}

	/** Computes the binomial coefficient (n choose k). This method is applicable
	 * for bigger numbers than allowed in <code>binomialCoeff</code>.
	 * 
	 * @param n first coefficient
	 * @param k second coefficient
	 * @return binomial coefficient (n choose k)
	 */
	public static double binomialCoeff2(int n, int k) {
		double r = 1;
		for (int i = n; i > n - k; i--)
			r = r * i;
		return r / fac2(k);
	}

	/** Solves a linear system of equations Ax=b for the special case of
	 * having a tri-diagonal matrix with coefficients a_ij of the matrix A.
	 * <br>
	 * There, a1 represents the upper diagonal axis of the matrix, a2 the 
	 * diagonal axis of the matrix, a3 the lower diagonal axis of the matrix
	 * and b1 the solution of the linear equation system.
	 * 
	 * @param a1 upper diagonal axis of the matrix
	 * @param a2 diagonal axis of the matrix
	 * @param a3 lower diagonal axis of the matrix
	 * @param b1 right side of the equation
	 * @return solution of the linear equation system
	 * @throws IllegalArgumentException if the dimensions of the given double[]
	 * do not match or the given system is not solvable
	 */
	public static double[] triDiagonalGaussianLGS(double[] a1, double[] a2, double[] a3, double[] b1)
		throws IllegalArgumentException {

		if ((a1.length != a3.length) || (b1.length != a2.length)) {
			throw new IllegalArgumentException("argument dimensions don't match");
		}

		int n = b1.length;
		double[] bb1 = new double[n];
		double[] x = new double[n];
		double[] aa3 = new double[n - 1];
		for (int i = 0; i < n; i++) {
			bb1[i] = b1[i];
			x[i] = a2[i];
			if (i < n - 1)
				aa3[i] = a3[i];
		}

		for (int i = 0; i < n; i++) {
			if (a2[i] == 0)
				throw new IllegalArgumentException("linear equation system is not solvable");

			bb1[i] = bb1[i] / x[i];
			if (i < (n - 1))
				aa3[i] = aa3[i] / x[i];
			x[i] = 1.0;
			if (i < n - 1) {
				x[i + 1] -= aa3[i] * a1[i];
				bb1[i + 1] -= bb1[i] * a1[i];
			}
		}
		x[n - 1] = bb1[n - 1];
		for (int i = n - 2; i >= 0; i--) {
			x[i] = bb1[i] - aa3[i] * x[i + 1];
		}
		return x;
	}

	/** Returns a root computed with the modified Newton method
	 * (a special root-finding technique ) using
	 * a given starting value and a given error bound.
	 *
	 * @param start starting value for the modified Newton method (special root-finding technique)
	 * @param epsilon error threshold
	 * @param f real-valued {@link xxl.core.functions.Function function}
	 *
	 * @throws ArithmeticException if the numerical method is not contracting, i.e., the
	 * starting value isn't approximately zero
	 *
	 * @return root computed with the modified Newton method
	 */
	public static double qNewton(double start, double epsilon, Function f) throws ArithmeticException {
		return qNewton(start, epsilon, new FunctionRealFunction(f));
	}

	/** Returns a root computed with the modified Newton method
	 * (a special root-finding technique ) using
	 * a given starting value and a given error bound.
	 *
	 * @param start starting value for the modified Newton method (special root-finding technique)
	 * @param epsilon error threshold
	 * @param f {@link xxl.core.math.functions.RealFunction real-valued function}
	 *
	 * @throws ArithmeticException if the numerical method is not contracting, i.e., the
	 * starting value isn't approximately zero.
	 *
	 * @return root computed with the modified Newton method
	 */
	public static double qNewton(double start, double epsilon, RealFunction f) throws ArithmeticException {
		double h = 1e-8d;
		double x = start;
		//
		double d = Double.MAX_VALUE;
		double dd = 0.0;
		double fx = 0.0;
		double xx = 0.0;
		do {
			fx = f.eval(x);
			xx = x;
			x = x - ((fx * h) / (f.eval(x - h) - fx));
			dd = d;
			d = Math.abs(xx - x);
		} while ((d <= epsilon) && (d < dd));
		if (d >= epsilon)
			throw new ArithmeticException(
				"Modified Newton method failed for approximate zero "
					+ start
					+ " with epsilon "
					+ epsilon
					+ " in "
					+ f
					+ "!");
		//
		return x;
	}

	/** Returns all roots of a given (continuous) real-valued function as <TT>double[]</TT> using
	 * a combination of the modified Newton method and a Bisection method.
	 *
	 * @param a left border of the interval where to find the roots
	 * @param b right border of the interval where to find the roots
	 * @param h step width of the step n; Bisection part of the algorithm
	 * @param function function from which to find the roots
	 * @return all found roots of the given function to the given parameters
	 */
	public static double[] rootFinding(double a, double b, double h, Function function) {
		return rootFinding(a, b, h, new FunctionRealFunction(function));
	}

	/** Returns all roots of a given (continuous) real-valued function as <TT>double[]</TT> using
	 * a combination of the modified Newton method and a Bisection method.
	 *
	 * @param a left border of the interval where to find the roots
	 * @param b right border of the interval where to find the roots
	 * @param h step width of the step n the bisection part of the algorithm
	 * @param f real function from which to find the roots
	 * @return all found roots of the given function to the given parameters
	 */
	public static double[] rootFinding(double a, double b, double h, RealFunction f) {
		List l = new ArrayList();
		double epsilon = 1e-8d;
		//
		double d = 0.0;
		double c = a;
		double x = 0.0;
		double x0 = 0.0;
		int i = 0;
		//
		do {
			d = c + h;
			if (f.eval(c) * f.eval(d) <= 0.0) { // one root in this sub interval [c,d]
				boolean rootFound = false;
				do {
					x = (c + d) / 2.0;
					try {
						x0 = qNewton(x, epsilon, f);
						rootFound = true;
					} catch (ArithmeticException ae) { // root not found, doing bisection
						x = (c + d) / 2.0;
						if (f.eval(c) * f.eval(x) < 0)
							d = x;
						else
							c = x;
					}
				} while (!rootFound);
				i++;
				l.add(new Double(x0));
			}
			c = d; // processing next sub interval
		}
		while (c < b);
		//
		double[] r = new double[l.size()];
		for (int j = 0; j < r.length; j++)
			r[j] = ((Double) l.get(j)).doubleValue();
		return r;
	}

	/**
	 * Determines whether the argument x is even or odd!
	 * 
	 * @param x integer value
	 * @return true if x is even, false otherwise
	 */
	public static boolean isEven(int x) {
		return (x & 1) == 0;
	}

	/**
	 * Determines whether the argument x is even or odd!
	 * 
	 * @param x integer value
	 * @return true if x is odd, false otherwise
	 */
	public static boolean isOdd(int x) {
		return !isEven(x);
	}

	/** Determines whether the argument x is even or odd!
	 * 
	 * @param x double value, must be element of Z
	 * @throws IllegalArgumentException  if x could not be used as an integer without loss of information!
	 * @return true if x is even, false otherwise
	 */
	public static boolean isEven(double x) throws IllegalArgumentException {
		if (Math.floor(x) != x)
			throw new IllegalArgumentException("Can't determine whether a decimal fractal is even or odd respectively");
		return isEven((int) x);
	}

	/** Determines whether the argument x is even or odd!
	 * 
	 * @param x double value, must be element of Z
	 * @throws IllegalArgumentException if x could not be used as an integer without loss of information!
	 * @return true if x is odd, false otherwise.
	 */
	public static boolean isOdd(double x) throws IllegalArgumentException {
		return !isEven(x);
	}

	/** Computes the hermite Polynomial of degree r at x.
	 * The hermite polynomial is defined by<BR>
	 * H(0,x) = 1,<BR>
	 * H(1,x) = x and <BR>
	 * H(r,x) = xH(r-1,x) - (r-1) H(r-2,x) <BR>
	 *
	 * @param r degree of the hermite polynomial to compute
	 * @param x function argument
	 * @return hermite polynomial of degree r evaluated at x
	 *
	 */
	public static double hermitePolynomial(int r, double x) {
		if (r < 0)
			throw new IllegalArgumentException("Hermite Polynomials of degrees less than zero are not possible to compute!");
		return (r >= 2) ? x * hermitePolynomial(r - 1, x) - (r - 1) * hermitePolynomial(r - 2, x) : (r == 1) ? x : 1.0;
	}

	/** Returns the 'characteristic value' of x regarding to an interval [a,b], i.e.,
	 * 1 if and only if x \in [a,b], 0 otherwise.
	 *
	 * @param x value to evaluate
	 * @param a left interval border
	 * @param b right interval border
	 *
	 * @return 1.0 if x \in [a,b], 0 otherwise
	 */
	public static double characteristicalFunction(double x, double a, double b) {
		return ((x <= b) & (x >= a)) ? 1.0 : 0.0;
	}

	/**
	 * Computes the Levenshtein distance for two given strings.
	 * That means the number of transformations to convert one string into the other is
	 * determined. Transformations are the one-step operations of insertion, deletion
	 * and substitution. <br>
	 * For details see: [Lev66] Levenshtein, V.I. (1966) "Binary codes capable of
	 * correcting insertions and reversals" Sov. Phys. Dokl. 10:707-10
	 * Algorithm for distance computation: [NeWu70] Needleman, S.B., Wunsch, C.D. (1970)
	 * "A general method applicable to the search for similarities in the amino acid
	 * sequence of two proteins" J. Mol. Biol. 48:443-453
	 *
	 * @param s first string.
	 * @param t second string.
	 * @return Levenshtein distance value
	 */
	public static double levenshteinDistance(String s, String t) {
		int n = s.length(), m = t.length();
		if (n == 0)
			return m;
		if (m == 0)
			return n;
		double[][] d = new double[n + 1][m + 1];
		char s_i, t_j; // i'th character of s; j'th character of t
		char[] s_char = s.toCharArray(), t_char = t.toCharArray();
		for (int i = 0; i <= n; i++)
			d[i][0] = i;
		for (int j = 0; j <= m; j++)
			d[0][j] = j;
		for (int i = 1; i <= n; i++) {
			s_i = s_char[i - 1]; //s.charAt (i - 1);
			for (int j = 1, cost = 0; j <= m; j++) {
				t_j = t_char[j - 1]; //t.charAt (j - 1);
				cost = s_i == t_j ? 0 : 1;
				d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);
			}
		}
		return d[n][m];
	}

	/** Compares two real functions f and g on a grid and computes the local errors.
	 * The functions are evaluated on a given grid that contains n grid knots.
	 * Computing these criteria is particularly interesting for the comparison
	 * between an original function and an approximation.
	 * Four different error criteria are provided:
	 * <br>
	 * L1=(\sum_{x_i in the grid} |f(x_i)-g(x_i)|)/n
	 * <br>
	 * MSE=(\sum_{x_i in the grid} |f(x_i)-g(x_i)|^2)/n
	 * <br>
	 * MXDV=max{|f(x_i)-g(x_i)|,x_i in the grid}
	 * <br>
	 * RMSE=MSE^0.5
	 * <br>
	 * <b>Note:</b> The evaluation of the functions runs unattended, i.e., it is not 
	 * checked if the function is defined on the grid knots!
	 * 
	 * @param grid given grid
	 * @param orig function to compare
	 * @param est function to compare
	 * @return a double array with four values: L1, MSE, MXDV, RMSE
	 */
	public static double[] errorEstimation(double[] grid, RealFunction orig, RealFunction est) {
		double L1 = 0;
		double MSE = 0;
		double RMSE = 0;
		double MXDV = 0;
		double diff = 0;
		int size = grid.length;

		for (int i = 0; i < grid.length; i++) {
			diff = Math.abs(orig.eval(grid[i]) - est.eval(grid[i]));
			MSE += diff * diff;
			L1 += diff;
			if (diff > MXDV)
				MXDV = diff;
		}

		L1 /= size;
		MSE /= size;
		RMSE = Math.sqrt(MSE);
		return new double[] { L1, MSE, MXDV, RMSE };
	}

	/** Compares two real functions f and g on a grid and computes the local errors.
	 * The functions are evaluated on an equidistant grid that contains n grid knots.
	 * Computing these criteria is particularly interesting for the comparison
	 * between an original function and an approximation.
	 * Four different error criteria are provided:
	 * <br>
	 * L1=(\sum_{x_i in the grid} |f(x_i)-g(x_i)|)/n
	 * <br>
	 * MSE=(\sum_{x_i in the grid} |f(x_i)-g(x_i)|^2)/n
	 * <br>
	 * MXDV=max{|f(x_i)-g(x_i)|,x_i in the grid}
	 * <br>
	 * RMSE=MSE^0.5
	 * <br>
	 * <b>Note:</b> The evaluation of the functions runs unattended, i.e., it is not 
	 * checked if the function is defined on the grid knots.
	 * 
	 * @param a left border of the grid
	 * @param b right border of the grid
	 * @param n number of grid knots
	 * @param orig function to compare
	 * @param est function to compare
	 * @return a double array with four values: L1, MSE, MXDV, RMSE
	 */
	public static double[] errorEstimation(double a, double b, int n, RealFunction orig, RealFunction est) {
		return errorEstimation(xxl.core.util.DoubleArrays.equiGrid(a, b, n), orig, est);
	}
	
	/**
	 * This method provides a multidimensional aggregation function, that is initialized with an arry of 
	 * aggregation functions.
	 * 
	 * @param functions array of aggregation functions
	 * @return multidimensional aggregation function
	 */
	public static Function multiDimAggregateFunction(final Function[] functions) {
		return new Function() {

			protected Object[] store = null;

			public Object invoke(Object aggregates, Object next) {
				if (aggregates == null) {
					if (store == null) {
						store = new Object[functions.length];
						Arrays.fill(store, null);
					}
					int numberOfInits = 0;
					for (int i = 0; i < functions.length; i++) {
						store[i] = functions[i].invoke(store[i], next);
						if (store[i] != null)
							numberOfInits++;
					}
					if (numberOfInits == functions.length) {
						Object[] res = new Object[store.length];
						for (int i = 0; i < res.length; i++) {
							res[i] = store[i];
						}
						return res;
					} else
						return null;
				} else
					for (int i = 0; i < functions.length; i++)
						((Object[])aggregates)[i] =
							functions[i].invoke(
								((Object[])aggregates)[i],
								next);
				return aggregates;
			}
		};
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		// testing triDiag Gauss
		int n = 4;
		double[] a1 = new double[n - 1];
		double[] a2 = new double[n];
		double[] a3 = new double[n - 1];
		double[] a4 = new double[n];
		//
		a1[0] = 1.0;
		a1[1] = 1.0;
		a1[2] = 1.0; // 1 1           3         2
		a2[0] = 1.0;
		a2[1] = 2.0;
		a2[2] = 2.0;
		;
		a2[3] = 2.0; // 1 2 1   * x = 5 => x =  1
		a3[0] = 1.0;
		a3[1] = 1.0;
		a3[2] = 1.0; //   1 2 1       5         1
		a4[0] = 3.0;
		a4[1] = 5.0;
		a4[2] = 5.0;
		;
		a4[3] = 5.0; //     2 2       5         2
		//
		System.out.println("testing triDiagGauss:\n");
		System.out.println("previous data:");
		System.out.println("a1=" + Strings.toString(a1));
		System.out.println("a2=" + Strings.toString(a2));
		System.out.println("a3=" + Strings.toString(a3));
		System.out.println("a4=" + Strings.toString(a4));

		System.out.println("Solved=" + Strings.toString(triDiagonalGaussianLGS(a1, a2, a3, a4)));

		System.out.println("later ...");
		System.out.println("a1=" + Strings.toString(a1));
		System.out.println("a2=" + Strings.toString(a2));
		System.out.println("a3=" + Strings.toString(a3));
		System.out.println("a4=" + Strings.toString(a4));

		System.out.println("-----------------------------------------\n");

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		// testing trapez
		final double m = 2.0;
		final double b = 8.0;
		Function f1 = new Function() {
			public Object invoke(Object o) {
				double x = ((Number) o).doubleValue();
				return new Double(m * x + b);
			}
		};
		int nt = 10;
		System.out.println("trapez integration for y = " + m + "x+" + b);
		System.out.println("n=" + nt + " steps for computation");
		System.out.println("[" + 2 + ", " + 5 + "]=" + TrapezoidalRuleRealFunctionArea.trapez(2.0, 5.0, f1, nt));
		System.out.println("[" + 0 + ", " + 10 + "]=" + TrapezoidalRuleRealFunctionArea.trapez(0.0, 10.0, f1, nt));
		System.out.println("[" + -3 + ", " + 3 + "]=" + TrapezoidalRuleRealFunctionArea.trapez(-3.0, 3.0, f1, nt));
		System.out.println("-----------------------------------------\n");

		/*********************************************************************/
		/*                            Example 3                              */
		/*********************************************************************/
		// testing simple functions
		System.out.println("isEven(3) = " + isEven(3));
		System.out.println("isEven(2) = " + isEven(2));
		System.out.println("isOdd(0) = " + isOdd(0));
		System.out.println("isOdd(-1) = " + isOdd(-1));
		System.out.println("isEven(-3) = " + isEven(-3));
		System.out.println("-----------------------------------------\n");

		/*********************************************************************/
		/*                            Example 4                              */
		/*********************************************************************/
		// testing binomialCoeff and binomialCoeff2
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j <= i; j++) {
				System.out.print(binomialCoeff(i, j) + " ");
				if (binomialCoeff(i, j) != binomialCoeff2(i, j))
					System.out.println("Error computing binomialCoeff");
			}
			System.out.println();
		}
		System.out.println("-----------------------------------------\n");

		/*********************************************************************/
		/*                            Example 5 (a)                          */
		/*********************************************************************/
		// testing qNewton (modified Newton method) I, with f(x) e^x + x^2 - 2
		System.out.println("\n\ntesting modified newton method (qNewton( double, double, Function)):\n");
		// ca.!! x0 = -1.31597377779629, x1 = 0.5372744491738566
		RealFunction f = new RealFunction() {
			public double eval(double x) {
				return Math.pow(Math.E, x) + x * x - 2.0;
			}
			public String toString() {
				return "f(x) = exp(x) + x^2 -2.0";
			}
		};

		double epsilon = 0.4;
		double start = 0.0;
		double x0 = 0.0;
		System.out.println(f + "\n------------------------------");
		for (int i = 0; i <= 12; i++) {
			start = i * .25 - 2.0;
			System.out.print("start value x=" + start);
			System.out.print("\tfor error bound epsilon=" + epsilon);
			try {
				x0 = qNewton(start, epsilon, f);
				System.out.println("\tx0 = " + x0);
			} catch (ArithmeticException ae) {
				System.out.println("\tfailed for this parameters!");
			}
		}

		/*********************************************************************/
		/*                            Example 5 (b)                          */
		/*********************************************************************/
		// testing rootFinding
		System.out.println("\n\ntesting root finding:\n");
		double a5 = -3.0;
		double b5 = 3.0;
		double h5 = 0.00001;
		System.out.println("Finding all roots of " + f + " between " + a5 + " and " + b5 + " with h=" + h5 + ":");
		System.out.println(Strings.toString(rootFinding(a5, b5, h5, f)));
		System.out.println("-----------------------------------------\n");

		/*********************************************************************/
		/*                            Example 6                              */
		/*********************************************************************/
		// testing fac, fac2, oddFactorial
		System.out.println("testing factorial methods");
		for (int i = 0; i < 21; i++) {
			System.out.println("computing " + i + "!:");
			System.out.println("\tfac(" + i + ")=" + fac(i));
			System.out.println("\tfac2(" + i + ")=" + fac2(i));
			if (isEven(i))
				System.out.println("\toddFactorial(" + i + ")=" + oddFactorial(i));
		}
		System.out.println("-----------------------------------------\n");

		/*********************************************************************/
		/*                            Example 7                              */
		/*********************************************************************/
		// testing hermite polynomial
		System.out.println("testing the hermite polynomial");
		double x7 = 0.0;
		for (int i = 0; i < 9; i++) {
			x7 = -3.0;
			System.out.println("H(" + i + ", " + x7 + ")=" + hermitePolynomial(i, x7));
			x7 = -1.0;
			System.out.println("H(" + i + ", " + x7 + ")=" + hermitePolynomial(i, x7));
			x7 = 0.0;
			System.out.println("H(" + i + ", " + x7 + ")=" + hermitePolynomial(i, x7));
			x7 = 1.0;
			System.out.println("H(" + i + ", " + x7 + ")=" + hermitePolynomial(i, x7));
			x7 = 3.0;
			System.out.println("H(" + i + ", " + x7 + ")=" + hermitePolynomial(i, x7));
		}
		System.out.println("-----------------------------------------\n");
	}
}