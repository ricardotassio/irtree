/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xxl.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import xxl.core.spatial.points.DoublePoint;
import xxl.core.spatial.rectangles.DoublePointRectangle;
import xxl.core.spatial.rectangles.Rectangle;

public class TextRectangle extends DoublePointRectangle{

    private int id;
    private String text;

    public TextRectangle(DoublePoint leftCorner, DoublePoint rightCorner, int id, String text) {
        super(leftCorner,  rightCorner);
        this.id = id;
        this.text = text;
    }

    public TextRectangle(double[] leftCorner, double[] rightCorner, int id, String text) {
        super(leftCorner,  rightCorner);
        this.id = id;
        this.text = text;
    }

    /** Creates a new DoublePointRectangle as a copy of the given rectangle
     *
     * @param rectangle rectangle which should be copied
    */
    public TextRectangle(Rectangle rectangle, int id, String text) {
        super(rectangle);
        this.id = id;
        this.text = text;
    }

    public TextRectangle(int dim) {
        super(dim);
        this.id = -1;
        this.text = null;
    }

    public void setId(int id){
        this.id = id;
    }

    @Override
    public void read(DataInput dataInput) throws IOException {
        super.read(dataInput);
        id = dataInput.readInt();
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        super.write(dataOutput);
        dataOutput.writeInt(id);
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    /**
     * The clone is called by  XXL trees to create node rectangles.
     * @return
     */
    @Override
    public Object clone() {
        return new NodeTextRectangle(this, -1, null);
    }

    void cleanText() {
        this.text = null;
    }
}
