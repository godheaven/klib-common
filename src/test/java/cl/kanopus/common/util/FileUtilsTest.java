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

    @Test
    void testPrettyFileSize() {

        long KB = 1024;
        long MB = KB * 1024;
        long GB = MB * 1024;
        long TB = GB * 1024;
        long PB = TB * 1024;

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

        Assertions.assertThrows(FileNotFoundException.class, () -> {
            FileUtils.getFile("not-exist1.txt", "not-exist2.txt", "test-notfound.txt");
        });

        File f = FileUtils.getFile("not-exist1.txt", "not-exist2.txt", "test.txt");
        Assertions.assertTrue(f.exists());
        Assertions.assertEquals("test.txt", f.getName());
    }
}
