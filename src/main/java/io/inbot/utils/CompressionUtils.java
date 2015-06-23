package io.inbot.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Helper class to compress/expand byte arrays.
 */
public class CompressionUtils {
    public static byte[] compress(byte[] uncompressed) {
        Deflater d = new Deflater();
        d.setLevel(Deflater.BEST_COMPRESSION);
        d.setInput(uncompressed);
        d.finish();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(uncompressed.length);

        // Compress the data
        byte[] buf = new byte[1024];
        while (!d.finished()) {
            int count = d.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            throw new IllegalStateException("could not compress");
        }
        return bos.toByteArray();
    }

    public static byte[] decompress(byte[] compressed) {
        Inflater inflater = new Inflater();
        inflater.setInput(compressed);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);

        // Compress the data
        byte[] buf = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buf);
                bos.write(buf, 0, count);
            }
            bos.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("could not decompress");
        } catch (DataFormatException e) {
            throw new IllegalArgumentException("could not decompress");
        }
        return bos.toByteArray();
    }
}
