/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.rtree;

import java.util.Arrays;

/**
 *
 * @author joao
 */
public class LinearRTree extends RTree{

    public LinearRTree(int dimensions, StorageManager storageManager, String outputPath) {
        super(dimensions, storageManager, outputPath);
    }


    /**
    * Pick the seeds used to split a node.
    * Select two entries to be the first elements of the groups
    */
    @Override
    protected void pickSeeds(Node n, Entry newEntry, Node newNode) throws RTreeException{
        // Find extreme rectangles along all dimension. Along each dimension,
        // find the entry whose rectangle has the highest low side, and the one
        // with the lowest high side. Record the separation.
        double maxNormalizedSeparation = 0;
        int highestLowIndex = 0;
        int lowestHighIndex = 0;

        // for the purposes of picking seeds, take the MBR of the node to include
        // the new rectangle aswell.
        n.mbr.add(newEntry.getMBR());

        for (int d = 0; d < newEntry.getMBR().getDimension(); d++) {
            double tempHighestLow = newEntry.getMBR().min[d];
            int tempHighestLowIndex = -1; // -1 indicates the new rectangle is the seed

            double tempLowestHigh = newEntry.getMBR().max[d];
            int tempLowestHighIndex = -1;

            for (int i = 0; i < n.entryCount; i++) {
                double tempLow = n.entries[i].getMBR().min[d];
                if (tempLow >= tempHighestLow) {
                    tempHighestLow = tempLow;
                    tempHighestLowIndex = i;
                } else {  // ensure that the same index cannot be both lowestHigh and highestLow
                    double tempHigh = n.entries[i].getMBR().max[d];
                    if (tempHigh <= tempLowestHigh) {
                        tempLowestHigh = tempHigh;
                        tempLowestHighIndex = i;
                    }
                }

                // PS2 [Adjust for shape of the rectangle cluster] Normalize the separations
                // by dividing by the widths of the entire set along the corresponding
                // dimension
                double normalizedSeparation = (tempHighestLow - tempLowestHigh) / (n.mbr.max[d] - n.mbr.min[d]);

                if (normalizedSeparation > 1 || normalizedSeparation < -1) {
                    throw new RTreeException("Invalid normalized separation");
                }

                // PS3 [Select the most extreme pair] Choose the pair with the greatest
                // normalized separation along any dimension.
                if (normalizedSeparation > maxNormalizedSeparation) {
                    maxNormalizedSeparation = normalizedSeparation;
                    highestLowIndex = tempHighestLowIndex;
                    lowestHighIndex = tempLowestHighIndex;
                }
            }
        }

        // highestLowIndex is the seed for the new node.
        if (highestLowIndex == -1) {
            newNode.addEntry(newEntry);
        } else {
            newNode.addEntry(n.entries[highestLowIndex]);
            n.entries[highestLowIndex] = null;

            // move the new rectangle into the space vacated by the seed for the new node
            //The new newEntry will be picked next because it slot is UNSSIGNED.
            n.entries[highestLowIndex] = newEntry.copy();
        }

        // lowestHighIndex is the seed for the original node.
        if (lowestHighIndex == -1) {
            lowestHighIndex = highestLowIndex;
        }

        entryStatus[lowestHighIndex] = ENTRY_STATUS_ASSIGNED;
        n.entryCount = 1;
        n.mbr.set(n.entries[lowestHighIndex].getMBR().min, n.entries[lowestHighIndex].getMBR().max);
    }

}
