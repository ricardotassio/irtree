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

package xxl.core.spatial.predicates;

import java.io.File;
import java.util.Iterator;

import xxl.core.predicates.Predicate;
import xxl.core.spatial.cursors.PointInputCursor;
import xxl.core.spatial.points.Point;

/**
 *	The UnitCubeConstraint-predicate checks if the argument
 *	(which is assumed to be a Point) is inside the unit-cube [0;1)^dim.
 *
 */
public class UnitCubeConstraint extends Predicate {
    
    /** A default instance of this class.
     */
    public static final UnitCubeConstraint DEFAULT_INSTANCE = new UnitCubeConstraint();
    
    /** Returns the result of the predicate as a primitive boolean.
     <br>
     <pre>
     implementation:
     <code><pre>
     *               return invoke(new Object [] {argument});
     </code></pre>
     @param argument the argument to the predicate
     @return the predicate value is returned
     */
    public boolean invoke (Object argument) {
        Point p = (Point) argument;
        for(int i=0; i< p.dimensions(); i++)
            if(p.getValue(i) < 0 || p.getValue(i) >= 1)
                return false;
        return true;
    }
    
    
    /**
     use-case:
     checks whether all Objects delivered by an Iterator fulfill
     the given constraint
     
     @param args args[0] file-name, args[1] dimensionality of the data
     */
    public static void main(String[] args){
        if(args.length != 2){
            System.out.println("usage: java xxl.core.spatial.UnitCubeConstraint <file-name> <dim>");
            return;	
        }
        
        Iterator it = 
            new PointInputCursor(new File(args[0]), PointInputCursor.FLOAT_POINT, Integer.parseInt(args[1]));
        
        System.out.println("# file-name        : "+args[0]);
        System.out.println("# dimensionality   : "+args[1]);
        
        boolean constraint = true;
        
        for(int count = 0; it.hasNext(); count++){
            Object next = it.next();
            if( !UnitCubeConstraint.DEFAULT_INSTANCE.invoke(next) ){
                if(constraint){
                    constraint = false;
                    System.out.println("# The constraint is FALSE for the following elements.");
                    System.out.println("# format: <n-th element of input-iterator>: <next().toString()>");
                }
                System.out.println(count+":\t"+next);
            }
        }
        if(constraint)
            System.out.println("# The constraint is TRUE for all elements.");
    }
}
