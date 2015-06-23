package io.inbot.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Opening files with pre java 8 io apis is kind of sucky due to the nested constructor calls. These static methods make
 * it less painful to do stuff like read a file or a resource or convert a resource to a string. Be sure to use try with
 * resources or a finally block of course.
 */
public class IOUtils {

    /**
     * Read a resource from the classpath or fallback to treating it as a file.
     * @param resourcePath resource path inside your jar, or a path to an actual file
     * @return BufferedReader
     * @throws IOException
     */
    public static BufferedReader resource(String resourcePath) throws IOException {
        InputStream is = IOUtils.class.getClassLoader().getResourceAsStream(resourcePath);
        if(is != null) {
            return new BufferedReader(new InputStreamReader(is, UTF_8));
        } else {
            File file = new File(resourcePath);
            if(file.exists()) {
                return fileReader(file);
            } else {
                throw new IllegalArgumentException("resource does not exist " + resourcePath);
            }
        }
    }

    public static BufferedWriter gzipFileWriter(String file) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)), UTF_8));
    }

    public static BufferedWriter gzipFileWriter(File file) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)), UTF_8), 64 * 1024);
    }

    public static BufferedWriter fileWriter(String file) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), UTF_8));
    }

    public static BufferedWriter fileWriter(File file) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), UTF_8));
    }

    public static BufferedReader gzipFileReader(String file) throws IOException {
        return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), UTF_8));
    }

    public static BufferedReader gzipFileReader(File file) throws IOException {
        return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), UTF_8));
    }

    public static BufferedReader fileReader(String file) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
    }

    public static BufferedReader fileReader(File file) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
    }

    public static String readString(BufferedReader r) {
        try {
            try {
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    buf.append(line + '\n');
                }
                return buf.toString();
            } finally {
                r.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String readString(InputStream is) {
        return readString(new BufferedReader(new InputStreamReader(is, UTF_8)));
    }
}
