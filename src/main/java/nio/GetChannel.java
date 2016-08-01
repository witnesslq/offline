package nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by fengwu on 15/5/21.
 */
public class GetChannel {

    private static final int BSIZE = 1024;

    public static final String IFILENAME = "/Users/fengwu/Downloads/myScalaApp/src/main/Resources/ALS/test.data";
    
    public static void main(String[] args) throws Exception {
        // Write a file by channel
        // rewrite
        FileChannel fc = new FileOutputStream(IFILENAME)
                .getChannel();
        fc.write(ByteBuffer.wrap("4,6,5.1".getBytes()));
        fc.close();

        // Add to the end of file:
        fc = new RandomAccessFile(IFILENAME, "rw").getChannel();
        fc.position(fc.size()); // Move to the end
        fc.write(ByteBuffer.wrap("4,6,5.2".getBytes()));
        fc.close();

        // Read the file
        fc = new FileInputStream(IFILENAME).getChannel();
        ByteBuffer buff = ByteBuffer.allocate(BSIZE);
        int bytesRead = fc.read(buff);
        while (bytesRead != -1){

            buff.flip();

            while (buff.hasRemaining())
                System.out.print((char)buff.get());

            buff.clear(); // make buffer ready for writing
            bytesRead = fc.read(buff);
        }

    }
}
