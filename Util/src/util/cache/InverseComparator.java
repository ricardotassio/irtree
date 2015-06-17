/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.cache;

import java.util.Comparator;


class InverseComparator implements Comparator{
    public int compare(Object o1, Object o2) {
        return -((Comparable)o1).compareTo(o2);
    }
}