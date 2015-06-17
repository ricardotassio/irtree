/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 *
 * @author joao
 */
public class FileUtils {
    //kilobyte, 2^10

    public static final int kB = 1024;
    //Megabyte, 2^20
    public static final int MB = 1024 * kB;
    //Gigabyte, 2^30
    public static final int GB = 1024 * MB;
    public static final int DISK_PAGE_SIZE = 4 * kB;

    public static boolean deleteDirectory(File dir) {
        boolean result = true;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                result &= deleteDirectory(file);
            }
            result &= file.delete();
        }
        return result;
    }

    public static synchronized void createDirectories(String filePath) {
        if (filePath.lastIndexOf("/") > 0) {
            String dir = filePath.substring(0, filePath.lastIndexOf("/"));
            if (dir.length() > 0) {
                File fileDir = new File(dir);
                if (!fileDir.exists()) {
                    if (!fileDir.mkdirs()) {
                        throw new RuntimeException("Could not create the directory '" + dir + "'!");
                    }
                }
            }
        }
    }

    /**
     * The file c
     * @param file can be a directory or a file.
     * @return If it is a file, the size of the file is returned.
     * If it is a directory, the size of the directory is returned.*
     */
    public static long getSizeInBytes(String fileName) {
        long size = 0;
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isDirectory()) {
                Stack<File> stack = new Stack<File>();
                stack.push(file);
                while (!stack.isEmpty()) {
                    file = stack.pop();
                    if (file.isDirectory()) {
                        for (File entry : file.listFiles()) {
                            stack.push(entry);
                        }
                    }
                    size += file.length();
                }
            } else {
                size = file.length();
            }
        }
        return size;
    }

    /**
     * Load the minimum and maximum values of each column of a file, where each line of a file
     * represents a record, where the field are separated by black space.
     * @param min
     * @param max
     * @param fileName
     * @throws FileNotFoundException
     * @throws IOException
     * @return the number of lines of the file evaluated.
     */
    public static int loadMinMax(double[] min, double[] max, String fileName) throws FileNotFoundException, IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        for (int i = 0; i < min.length; i++) {
            min[i] = Double.MAX_VALUE;
            max[i] = -Double.MAX_VALUE;
        }

        double value;
        StringTokenizer tokens;
        int count = 0;
        //Parse the file first time just to get min and max.
        for (String line = input.readLine(); line != null; line = input.readLine()) {
            if (!line.startsWith("#")) {
                count++;
                tokens = new StringTokenizer(line);
                for (int i = 0; i < min.length; i++) {
                    value = Double.parseDouble(tokens.nextToken());
                    min[i] = Math.min(min[i], value);
                    max[i] = Math.max(max[i], value);
                }
            }
        }
        input.close();
        return count;
    }

    /**
     * 
     * @param fileName
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void incrementalId(String separator, String inputFile, String outputFile) throws FileNotFoundException, IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));

        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
        int id = 1;
        //Parse the file first time just to get min and max.
        for (String line = input.readLine(); line != null; line = input.readLine()) {
            if (!line.startsWith("#")) {
                output.write((id++) + line.substring(line.indexOf(separator)));
                output.newLine();
            }
        }
        input.close();
        output.close();
    }

    public static void copyFile(String sourceFile, String destFile) throws IOException {
        if(new File(sourceFile).exists()){
            createDirectories(destFile);
            if (!new File(destFile).exists()) {
                new File(destFile).createNewFile();
            }

            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        }
    }

    static void main(String[] args) throws Exception {
        String separator = " ";
        incrementalId(separator, args[0], args[1]);
    }
}
