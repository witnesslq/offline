package nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Created by fengwu on 15/5/22.
 */
public class BufferToText {
    private static final int BSIZE = 1024;

    public static final String OFILENAME = "/Users/fengwu/Downloads/myScalaApp/src/main/Resources/ALS/otest.data";

    public static void main(String[] args) throws IOException {
        // in
        FileChannel inChannel = new FileOutputStream(GetChannel.IFILENAME).getChannel();
        inChannel.write(ByteBuffer.wrap("吴峰".getBytes("UTF-16BE")));
        inChannel.close();

        // out
        FileChannel outChannel = new FileInputStream(GetChannel.IFILENAME).getChannel();
        ByteBuffer buff  =  ByteBuffer.allocate(BSIZE);

        outChannel.read(buff);
        buff.flip();  // tell other readers ,i have finished read

        // doesn't work: cause encode and decode not specified
        System.out.println(buff.asCharBuffer());

        // decode using system's default charset
        buff.rewind();
        String encoding = System.getProperty("file.encoding");
        System.out.println("decoded using " + encoding + ":" +
                Charset.forName("UTF-16BE").decode(buff));



        // Scattering Reads
        ByteBuffer header = ByteBuffer.allocate(128);
        ByteBuffer body   = ByteBuffer.allocate(1024);

        ByteBuffer[] bufferArray = { header, body };

        outChannel.read(bufferArray);

    }

}
