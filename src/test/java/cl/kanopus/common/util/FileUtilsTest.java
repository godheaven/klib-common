package cl.kanopus.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class FileUtilsTest {

    @Test
    public void testPrettyFileSize() {

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

}
