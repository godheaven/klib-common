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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

class FileUtilsTest {
    static final long KB = 1024;
    static final long MB = KB * 1024;
    static final long GB = MB * 1024;
    static final long TB = GB * 1024;
    static final long PB = TB * 1024;

    @Test
    void testPrettyFileSize() {
        Assertions.assertEquals("0 B", FileUtils.prettyFileSize(0));
        Assertions.assertEquals("1,0 KB", FileUtils.prettyFileSize(KB));
        Assertions.assertEquals("2,0 KB", FileUtils.prettyFileSize(KB * 2));

        Assertions.assertEquals("1,0 MB", FileUtils.prettyFileSize(MB));
        Assertions.assertEquals("2,0 MB", FileUtils.prettyFileSize(MB * 2));

        Assertions.assertEquals("1,0 GB", FileUtils.prettyFileSize(GB));
        Assertions.assertEquals("2,0 GB", FileUtils.prettyFileSize(GB * 2));

        Assertions.assertEquals("1,0 TB", FileUtils.prettyFileSize(TB));
        Assertions.assertEquals("2,0 TB", FileUtils.prettyFileSize(TB * 2));

        Assertions.assertEquals("1,0 PB", FileUtils.prettyFileSize(PB));
        Assertions.assertEquals("2,0 PB", FileUtils.prettyFileSize(PB * 2));
    }

    @Test
    void testGetFile() throws FileNotFoundException {

        Assertions.assertThrows(FileNotFoundException.class,
                () -> FileUtils.getFile("not-exist1.txt", "not-exist2.txt", "test-notfound.txt"));

        File f = FileUtils.getFile("not-exist1.txt", "not-exist2.txt", "test.txt");
        Assertions.assertTrue(f.exists());
        Assertions.assertEquals("test.txt", f.getName());
    }

    @Test
    void testCreateFileFromBytesAndToByteArray() throws Exception {
        // use a temporary file path inside temp directory
        File tmp = File.createTempFile("fu-test", ".bin");
        tmp.deleteOnExit();

        byte[] data = "payload-data".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        // Ensure FileUtils writes into the temp directory by setting temporalFolder
        FileUtils.setTemporalFolder(tmp.getParent());
        File out = FileUtils.createFile(data, tmp.getName());
        Assertions.assertTrue(out.exists());

        // read file using FileUtils.fileToString(String)
        StringBuilder sb = FileUtils.fileToString(out.getAbsolutePath());
        Assertions.assertTrue(sb.toString().contains("payload-data"));

        // test toByteArray using an InputStream
        try (java.io.InputStream in = new java.io.ByteArrayInputStream(data)) {
            byte[] arr = FileUtils.toByteArray(in);
            Assertions.assertArrayEquals(data, arr);
        }
    }

    @Test
    void testTemporalFolderPathResolution() throws Exception {
        FileUtils.setTemporalFolder(System.getProperty("java.io.tmpdir"));
        String path = FileUtils.createFile("x", "fu-test-tmp.txt", java.nio.charset.StandardCharsets.UTF_8).getAbsolutePath();
        Assertions.assertTrue(path.contains(System.getProperty("java.io.tmpdir")));
    }
}
