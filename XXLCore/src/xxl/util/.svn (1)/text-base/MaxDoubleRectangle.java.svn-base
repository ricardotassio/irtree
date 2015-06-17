/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xxl.util;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import xxl.core.indexStructures.Descriptor;
import xxl.core.spatial.points.DoublePoint;
import xxl.core.spatial.rectangles.DoublePointRectangle;
import xxl.core.spatial.rectangles.Rectangle;

public class MaxDoubleRectangle extends DoublePointRectangle{

    private double score;

    public MaxDoubleRectangle(DoublePoint leftCorner, DoublePoint rightCorner,double score) {
        super(leftCorner,  rightCorner);
        this.score = score;

    }

    public MaxDoubleRectangle(double[] leftCorner, double[] rightCorner,double score) {
        super(leftCorner,  rightCorner);
        this.score = score;
    }

    /** Creates a new DoublePointRectangle as a copy of the given rectangle
     *
     * @param rectangle rectangle which should be copied
    */
    public MaxDoubleRectangle(Rectangle rectangle, double score) {
        super(rectangle);
        this.score = score;
    }

    public MaxDoubleRectangle(int dim) {
        super(dim);
        this.score = -1;
    }

    @Override
    public void read(DataInput dataInput) throws IOException {
        super.read(dataInput);
        score = dataInput.readDouble();
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        super.write(dataOutput);
        dataOutput.writeDouble(score);
    }

    public double getScore() {
        return score;
    }

    //THIS IS USED BY RTREE
    @Override
    public void union(Descriptor descriptor) {
        super.union(descriptor);

        score = Math.max(score, ((MaxDoubleRectangle)descriptor).getScore());
    }

    public boolean contains(Descriptor descriptor) {
        //This descriptor contains another, if the other is enclosed in this descriptor
        //and the score of this descriptor is greater or equal the score of the new descriptor
        return super.contains(descriptor) &&
                this.getScore()>=((MaxDoubleRectangle)descriptor).getScore();
    }

    public Object clone() {
        return new MaxDoubleRectangle(this, score);
    }
}
