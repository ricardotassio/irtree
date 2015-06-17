
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.FileUtils;
import util.Util;
import xxl.core.indexStructures.ORTree;
import xxl.core.io.converters.IntegerConverter;
import xxl.core.spatial.KPE;
import xxl.util.statistics.DefaultStatisticCenter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Robos
 */
public class IRTreeTeste {
    
    public static Item parse(String line) throws ParseException {

        String[] split = line.split(" ");

        int id = Integer.parseInt(split[0]);
        String user = split[1];
        double lat = Double.parseDouble(split[2]);
        double lng = Double.parseDouble(split[3]);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd" + "-" + "HH:mm:ss");
        Date timestamp = formatter.parse(split[4] + "-" + split[5]);

        String text = "";

        for (int i = 7; i < split.length; i++) {
            text += split[i] + " ";
        }



        return new Item(id, line, lat, lng, timestamp, text);
    }
    
    public static void main(String[] args) throws Exception {
        java.util.Properties properties = new java.util.Properties();
        properties.load(new java.io.FileInputStream("spatial.properties"));

        int bufferSize = 0;
        int vectorCacheSize = 100;

        // internal variables
        // Leafnodes are 32+4 Bytes of size.
        // Indexnodes are 32+8 Bytes of size.
        // so take the maximum for the block size!
        //int blockSize = 4+2+(32+8)*maxcap;
        IRTree.INVERTED_FILES_DIRECTORY = properties.getProperty("irTree.folder") + "/ifDir";
        DefaultStatisticCenter statisticCenter = new DefaultStatisticCenter();
        IRTree irTree = new IRTree(statisticCenter, "ir_tree_id_",
                properties.getProperty("irTree.folder") + "/rtree",
                Integer.parseInt(properties.getProperty("irTree.dimensions")),
                bufferSize,
                Integer.parseInt(properties.getProperty("diskStorage.blockSize")),
                Integer.parseInt(properties.getProperty("irTree.minNodeEntries")),
                Integer.parseInt(properties.getProperty("irTree.maxNodeEntries")),
                FileUtils.DISK_PAGE_SIZE, FileUtils.DISK_PAGE_SIZE, vectorCacheSize, true);


        irTree.open();
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("small.txt")));

        long time = System.currentTimeMillis();
        String line = input.readLine();
        if (irTree.getSizeInBytes() == 0) {
            while (line != null) {
                Item item = parse(line);
                //System.out.println(item);
                TextRectangle mbr = new TextRectangle(new double[]{item.lat, item.lgt},
                        new double[]{item.lat, item.lgt}, item.id, item.txt);
                //DoublePointRectangleMax mbr = new DoublePointRectangleMax(point, point, RandomUtil.nextDouble(1));
                irTree.insert(new KPE(mbr, item.id, IntegerConverter.DEFAULT_INSTANCE));
                line = input.readLine();
            }

            irTree.buildInvertedFiles((ORTree.IndexEntry) irTree.rootEntry());
            irTree.flush();
        }


        System.out.println("\n\n\nTree:\n" + irTree.toString());

        System.out.println("IRTree constructed in " + Util.time(System.currentTimeMillis() - time));
        System.out.println("Statistics results...");
        System.out.println(statisticCenter.getStatus());

        System.out.print("Checking the tree...");
        irTree.checkTree((ORTree.IndexEntry) irTree.rootEntry());
        System.out.println("OK!");

        input.close();
        irTree.close();
    }

}
