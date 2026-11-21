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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

class UtilsRegressionTest {

    @Test
    void parseBigDecimalKeepsDecimals() {
        assertEquals(new BigDecimal("19.99"), Utils.parseBigDecimal(19.99));
        assertEquals(new BigDecimal("0.1"), Utils.parseBigDecimal(0.1));
        assertEquals(new BigDecimal("-2.5"), Utils.parseBigDecimal(-2.5));
        assertEquals("1000", Utils.parseBigDecimal(1000.0).toPlainString());
        assertEquals("1000", Utils.parseBigDecimal(1000.0).toString());
        assertNull(Utils.parseBigDecimal((Double) null));
        assertNull(Utils.parseBigDecimal((Long) null));
    }

    @Test
    void parseBigIntegerDoesNotOverflow() {
        assertEquals(new BigInteger("10000000000"), Utils.parseBigInteger(1.0E10));
        assertEquals(BigInteger.valueOf(-3), Utils.parseBigInteger(-3.9));
        assertNull(Utils.parseBigInteger((Double) null));
        assertNull(Utils.parseBigInteger((Long) null));
    }

    @Test
    void randomNumbersAreStrictlyPositive() {
        for (int i = 0; i < 10_000; i++) {
            assertTrue(Utils.generateRandomLong() > 0);
            assertTrue(Utils.generateRandomInt() > 0);
        }
    }

    @Test
    @Timeout(5)
    void replaceAllDoesNotLoopWhenReplacementContainsPlaceholder() {
        StringBuilder sb = new StringBuilder("Hello {0}, bye {0}");
        Utils.replaceAll(sb, "{0}", "x{0}y");
        assertEquals("Hello x{0}y, bye x{0}y", sb.toString());

        StringBuilder sb2 = new StringBuilder("aXbXc");
        Utils.replaceAll(sb2, "X", "");
        assertEquals("abc", sb2.toString());

        StringBuilder sb3 = new StringBuilder("abc");
        Utils.replaceAll(sb3, "", "z");
        assertEquals("abc", sb3.toString());
        Utils.replaceAll(sb3, null, "z");
        Utils.replaceAll(null, "a", "z");
        assertEquals("abc", sb3.toString());
    }

    @Test
    void nullSafety() {
        assertFalse(Utils.isRut(null));
        assertFalse(Utils.isRut("11111111-11"));
        assertTrue(Utils.isRut("11111111-1"));
        assertNull(Utils.getRutFormat(null));
        assertEquals("1", Utils.getRutFormat("1"));
        assertEquals("11.111.111-1", Utils.getRutFormat("111111111"));
        assertFalse(Utils.isDateEquals((Date) null, new Date()));
        assertFalse(Utils.isDateEquals(new Date(), (Date) null));
        assertTrue(Utils.isDateEquals(new Date(0), new Date(1)));
        assertEquals("", Utils.getDateFormat((java.time.LocalDate) null));
        assertEquals("", Utils.getDateFormat((java.time.LocalDate) null, "yyyy"));
        assertFalse(Utils.contains(new Integer[]{null, 2}, 1));
        assertTrue(Utils.contains(new Integer[]{null, 2}, 2));
        assertNull(Utils.getDate(null, "dd/MM/yyyy"));
    }

    @Test
    void spanishDateFormatUsesLocaleOf() {
        Date date = Utils.getDate("15/01/2024", "dd/MM/yyyy");
        assertEquals("15 ENERO 2024", Utils.getDateFormat(date, "dd MMMM yyyy"));
    }

    @Test
    void fileToStringReadsUtf8AndKeepsLines(@TempDir Path tmp) throws Exception {
        Path p = tmp.resolve("utf8.txt");
        Files.writeString(p, "ñandú\nlínea 2", StandardCharsets.UTF_8);
        File f = p.toFile();
        assertEquals("ñandú\nlínea 2\n", Utils.fileToString(f).toString());
    }
}
