package cl.kanopus.common.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.stream.Collectors;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class FileUtils {

    private static String temporalFolder;

    private FileUtils() {
    }

    public static void setTemporalFolder(String temporalFolder) {
        FileUtils.temporalFolder = temporalFolder;
    }

    public static StringBuilder fileToString(String filename) throws IOException {
        StringBuilder text = new StringBuilder();
        try (FileReader f = new FileReader(getFile(filename)); BufferedReader b = new BufferedReader(f)) {
            text.append(b.lines().collect(Collectors.joining()));
        }
        return text;
    }

    public static boolean renameFile(File fileEnvioDTE, String filename) {
        String newFilename = checkAndGetFullTemporalPath(filename);
        return fileEnvioDTE.renameTo(new File(newFilename));
    }

    public static File createFile(String text, String filename, Charset charset) throws IOException {
        String path = checkAndGetFullTemporalPath(filename);
        File fileDir = new File(path);
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileDir), charset))) {
            out.write(text);
            out.flush();
        }
        return fileDir;
    }

    public static File createFile(byte[] bytes, String filename) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        baos.writeBytes(bytes);
        return createFile(baos, filename);
    }

    public static File createFile(ByteArrayOutputStream outputStream, String filename) throws IOException {
        String path = checkAndGetFullTemporalPath(filename);
        File fileDir = new File(path);
        try (FileOutputStream output = new FileOutputStream(fileDir)) {
            output.write(outputStream.toByteArray());
        }
        return fileDir;
    }

    public static File createFile(InputStream inputStream, String filename) throws IOException {
        String path = checkAndGetFullTemporalPath(filename);
        File fileDir = new File(path);

        InputStream in = null;
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(fileDir))) {

            in = inputStream;
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            // close streams, but don't mask original exception, if any
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }

        }
        return fileDir;
    }

    private static String checkAndGetFullTemporalPath(String filename) {
        return (temporalFolder != null) ? (temporalFolder + File.separator + filename) : (filename);
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        // read bytes from the input stream and store them in buffer
        while ((len = in.read(buffer)) != -1) {
            // write bytes from the buffer into output stream
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }

    public static File getFile(String... files) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        File file = null;
        for (String f : files) {
            //first attemtp
            file = getFileAttemp1(f, sb);

            //second attemtp
            if (file == null || !file.exists()) {
                file = getFileAttemp2(f, sb);
            }

            //third attemtp
            if (file == null || !file.exists()) {
                file = getFileAttemp3(f, sb);
            }

            if (file != null && file.exists()) {
                break;
            }
        }

        if (file == null || !file.exists()) {
            throw new FileNotFoundException(sb.toString());
        }
        return file;
    }

    private static File getFileAttemp1(String filename, StringBuilder messages) {
        File file = null;
        try {
            URL resource = FileUtils.class.getClassLoader().getResource(filename);
            file = (resource != null) ? new File(resource.getFile()) : null;
        } catch (Exception ex) {
            messages.append(ex.getMessage());
        }
        return file;
    }

    private static File getFileAttemp2(String filename, StringBuilder messages) {
        File file = null;
        try {
            URL resource = Thread.currentThread().getClass().getClassLoader().getResource(filename);
            file = (resource != null) ? new File(resource.getFile()) : null;
        } catch (Exception ex2) {
            messages.append(ex2.getMessage());
        }
        return file;
    }

    private static File getFileAttemp3(String filename, StringBuilder messages) {
        File file = null;
        try {
            file = new File(filename);
        } catch (Exception ex2) {
            messages.append(ex2.getMessage());
        }
        return file;
    }

    public static String prettyFileSize(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

}
