package xxl.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import xxl.core.indexStructures.Descriptor;
import xxl.core.spatial.points.DoublePoint;
import xxl.core.spatial.rectangles.DoublePointRectangle;
import xxl.core.spatial.rectangles.Rectangle;

public class BooleanRectangle extends DoublePointRectangle{

    private boolean agg;

    public BooleanRectangle(DoublePoint leftCorner, DoublePoint rightCorner, boolean agg) {
        super(leftCorner,  rightCorner);
        this.agg = agg;

    }

    public BooleanRectangle(double[] leftCorner, double[] rightCorner,boolean agg) {
        super(leftCorner,  rightCorner);
        this.agg = agg;
    }

    /** Creates a new DoublePointRectangle as a copy of the given rectangle
     *
     * @param rectangle rectangle which should be copied
    */
    public BooleanRectangle(Rectangle rectangle, boolean agg) {
        super(rectangle);
        this.agg = agg;
    }

    public BooleanRectangle(int dim) {
        super(dim);
        this.agg = false;
    }

    @Override
    public void read(DataInput dataInput) throws IOException {
        super.read(dataInput);
        agg = dataInput.readBoolean();
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        super.write(dataOutput);
        dataOutput.writeBoolean(agg);
    }

    //THIS IS USED BY RTREE
    @Override
    public void union(Descriptor descriptor) {
        super.union(descriptor);

        agg = agg || ((BooleanRectangle)descriptor).getBoolean();
    }

    public boolean contains(Descriptor descriptor) {
        //This descriptor contains another, if the other is enclosed in this descriptor
        //and the aggregated value of this descriptor is true or the new descriptor is false
        return super.contains(descriptor) &&
                (this.getBoolean() || !((BooleanRectangle)descriptor).getBoolean());
    }

    public boolean getBoolean() {
        return agg;
    }


    public Object clone() {
        return new BooleanRectangle(this,agg);
    }
}