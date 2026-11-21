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

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUtilsSecurityTest {

    @TempDir
    Path tmp;

    @AfterEach
    void reset() {
        FileUtils.setTemporalFolder(null);
    }

    @Test
    void rejectsPathTraversal() {
        FileUtils.setTemporalFolder(tmp.resolve("base").toString());
        assertThrows(IllegalArgumentException.class, () -> FileUtils.createFile("x", "../evil.txt", StandardCharsets.UTF_8));
        assertThrows(IllegalArgumentException.class, () -> FileUtils.createFile("x", "a/../../evil.txt", StandardCharsets.UTF_8));
        assertThrows(IllegalArgumentException.class, () -> FileUtils.createFile(new byte[]{1}, tmp.resolve("abs.bin").toAbsolutePath().toString()));
        assertThrows(IllegalArgumentException.class, () -> FileUtils.createFile(new byte[]{1}, ""));
        assertThrows(IllegalArgumentException.class, () -> FileUtils.renameFile(new File("whatever"), "../x"));
        assertFalse(Files.exists(tmp.resolve("evil.txt")));
    }

    @Test
    void allowsFilesInsideTemporalFolder() throws Exception {
        Path base = Files.createDirectories(tmp.resolve("base"));
        Files.createDirectories(base.resolve("sub"));
        FileUtils.setTemporalFolder(base.toString());
        File f = FileUtils.createFile("x", "sub/../ok.txt", StandardCharsets.UTF_8);
        assertEquals(base.resolve("ok.txt").toAbsolutePath().normalize().toFile(), f);
        assertTrue(f.exists());
    }

    @Test
    void temporalFolderIsVolatile() throws Exception {
        Field field = FileUtils.class.getDeclaredField("temporalFolder");
        assertTrue(Modifier.isVolatile(field.getModifiers()));
    }

    @Test
    void fileToStringPreservesNewlinesAndUtf8() throws Exception {
        Path p = tmp.resolve("text.txt");
        String content = "línea 1\nlínea 2\r\nñ\n";
        Files.write(p, content.getBytes(StandardCharsets.UTF_8));
        assertEquals(content, FileUtils.fileToString(p.toString()).toString());

        Path latin = tmp.resolve("latin.txt");
        Files.write(latin, "año".getBytes(StandardCharsets.ISO_8859_1));
        assertEquals("año", FileUtils.fileToString(latin.toString(), StandardCharsets.ISO_8859_1).toString());
    }

    @Test
    void createFileFromInputStreamPropagatesIOException() {
        FileUtils.setTemporalFolder(tmp.toString());
        InputStream failing = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };
        IOException ex = assertThrows(IOException.class, () -> FileUtils.createFile(failing, "fail.bin"));
        assertEquals("boom", ex.getMessage());
    }

    @Test
    void createFileFromStreams() throws Exception {
        FileUtils.setTemporalFolder(tmp.toString());
        File f = FileUtils.createFile(new ByteArrayInputStream("abc".getBytes(StandardCharsets.UTF_8)), "in.bin");
        assertEquals("abc", Files.readString(f.toPath()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes("def".getBytes(StandardCharsets.UTF_8));
        File g = FileUtils.createFile(baos, "out.bin");
        assertEquals("def", Files.readString(g.toPath()));
        assertTrue(FileUtils.renameFile(g, "renamed.bin"));
        assertTrue(Files.exists(tmp.resolve("renamed.bin")));
    }
}
