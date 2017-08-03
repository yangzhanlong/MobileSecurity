package org.me.mobilesecurity.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {
    /**
     * 压缩
     *
     * @param srcPath
     * @param destPath
     * @throws IOException
     */
    public static void zip(String srcPath, String destPath) throws IOException {
        zip(new File(srcPath), new File(destPath));
    }

    /**
     * 压缩
     *
     * @param srcFile
     * @param destFile
     * @throws IOException
     */
    public static void zip(File srcFile, File destFile) throws IOException {
        zip(new FileInputStream(srcFile), new FileOutputStream(destFile));
    }

    /**
     * 压缩
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void zip(InputStream in, OutputStream out) throws IOException {
        // 流的操作
        // 标准的输入流---> (Gzip输出流 --> 标准的输出流)
        // 包装

        byte[] buffer = new byte[1024];
        int len = -1;

        GZIPOutputStream gos = new GZIPOutputStream(out);
        try {
            while ((len = in.read(buffer)) != -1) {
                gos.write(buffer, 0, len);
            }
        } finally {
            closeIO(in);
            closeIO(gos);
        }
    }

    /**
     * 解压
     *
     * @param srcPath
     * @param destPath
     * @throws IOException
     */
    public static void unzip(String srcPath, String destPath)
            throws IOException {
        unzip(new File(srcPath), new File(destPath));
    }

    /**
     * 解压
     *
     * @param srcFile
     * @param destFile
     * @throws IOException
     */
    public static void unzip(File srcFile, File destFile) throws IOException {
        unzip(new FileInputStream(srcFile), new FileOutputStream(destFile));
    }

    /**
     * gzip的解压
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void unzip(InputStream in, OutputStream out)
            throws IOException {

        // 流的操作
        // (标准的输入流--GZip的输入流)-->标准流的方式输出

        GZIPInputStream gis = new GZIPInputStream(in);

        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = gis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } finally {
            closeIO(gis);
            closeIO(out);
        }
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
