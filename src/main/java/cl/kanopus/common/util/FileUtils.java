/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 *
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 Pablo Díaz Saavedra
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --!
 */
package cl.kanopus.common.util;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.stream.Collectors;

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

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // close streams, but don't mask original exception, if any
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
            URL resource = FileUtils.class.getResource(filename);
            file = (resource != null) ? new File(resource.getFile()) : null;
        } catch (Exception ex) {
            messages.append(ex.getMessage());
        }
        return file;
    }

    private static File getFileAttemp2(String filename, StringBuilder messages) {
        File file = null;
        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource(filename);
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
