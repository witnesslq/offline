package io;

import java.io.*;

/**
 * Created by fengwu on 15/5/21.
 */
public class FormattedMemoryInput {

    // buffer input file
    public static String read(final String filename) throws IOException {
        // Reading input by lines
        BufferedReader in = new BufferedReader(
                new FileReader(filename));
        String s;
        StringBuilder sb = new StringBuilder();
        while ((s = in.readLine()) != null) {
            sb.append(s + '\n');
        }
        in.close();
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        try {
            DataInputStream in = new DataInputStream(
                    new ByteArrayInputStream(read("/Users/fengwu/Downloads/myScalaApp/src/main/java/io/FormattedMemoryInput.java").getBytes()));

            while (in.available() != 0)
                System.out.print((char) in.readByte());
        } catch (EOFException e) {
            System.err.println(e);
        }
    }
}
