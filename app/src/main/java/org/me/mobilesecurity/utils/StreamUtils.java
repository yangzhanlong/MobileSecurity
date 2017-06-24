package org.me.mobilesecurity.utils;


import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

    public static String parseStream(InputStream inputStream) {
        ByteArrayOutputStream baos = null;

        try {
            int len = -1;
            byte buffer[] = new byte[1024];
            baos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
                return baos.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            closeIO(inputStream);
            closeIO(baos);
        }

        return null;
    }

    public static void closeIO(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            io = null;
        }
    }
}
