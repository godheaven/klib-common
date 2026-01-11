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

import cl.kanopus.common.enums.EnumIdentifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class UtilsTest {

    @Test
    void testIsNullOrEmpty() {

        Assertions.assertTrue(Utils.isNullOrEmpty(""));
        Assertions.assertTrue(Utils.isNullOrEmpty("   "));

        Assertions.assertFalse(Utils.isNullOrEmpty("   X"));
        Assertions.assertFalse(Utils.isNullOrEmpty("X"));
        Assertions.assertFalse(Utils.isNullOrEmpty("1"));
    }

    @Test
    void testGetDateTimeFormat() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 1, 10, 2, 3);
        String result = Utils.getDateTimeFormat(localDateTime);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("01/02/2023 10:02:03", result);
    }

    @Test
    void testElapsedTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(2).plusHours(5).plusMinutes(30);

        String result = Utils.getElapsedTime(now, future);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("2d 5h 30m", result);
    }


    @Test
    void testIsEqualsOne_Enum() {
        Assertions.assertTrue(Utils.isEqualsOne(new EnumIdentifiableExample(1L), new EnumIdentifiableExample(1L), new EnumIdentifiableExample(2L)));
        Assertions.assertFalse(Utils.isEqualsOne(new EnumIdentifiableExample(1L), new EnumIdentifiableExample(2L), new EnumIdentifiableExample(3L)));
    }

    @Test
    void testIsEqualsOne_Text() {
        Assertions.assertTrue(Utils.isEqualsOne("uno", "uno", "dos"));
        Assertions.assertFalse(Utils.isEqualsOne("uno", "dos", "tres"));
    }

    @Test
    void testGetLocalDate_String_String() {
        LocalDate localDate = LocalDate.of(2023, Month.JANUARY, 30);
        Assertions.assertEquals(localDate, Utils.getLocalDate("30-01-2023", "dd-MM-yyyy"));
    }

    @Test
    void testGetLocalDate_Date() {
        Date date = Utils.getDate("30/01/2023", "dd/MM/yyyy");
        LocalDate expResult = LocalDate.of(2023, Month.JANUARY, 30);
        LocalDate result = Utils.getLocalDate(date);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    void testGetLocalDateTime() {
        Date date = Utils.getDate("30/01/2023 11:05:04", "dd/MM/yyyy HH:mm:ss");
        LocalDateTime expResult = LocalDateTime.of(2023, Month.JANUARY, 30, 11, 5, 4);
        LocalDateTime result = Utils.getLocalDateTime(date);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    void testParseDouble_long() {
        long value = 0L;
        double expResult = 0.0;
        double result = Utils.parseDouble(value);
        Assertions.assertEquals(expResult, result, 0);
    }

    @Test
    void testParseDouble_Object() {
        double expResult = 2;
        double result = Utils.parseDouble("2");
        Assertions.assertEquals(expResult, result, 0);
    }

    @Test
    void testDefaultValue_GenericType_GenericType() {
        Object value = null;
        Object defaultValue = null;
        Object expResult = null;
        Object result = Utils.defaultValue(value, defaultValue);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    void testDefaultValue_3args() {
        String expResult = "DEFAULT";
        String result = Utils.defaultValue(null, "DEFAULT");
        Assertions.assertEquals(expResult, result);
    }

    @Test
    void testSetTime() {
        Date date = Utils.getDate("30/01/2023 11:05:04", "dd/MM/yyyy HH:mm:ss");
        Date expResult = Utils.getDate("30/01/2023 10:54:00", "dd/MM/yyyy HH:mm:ss");
        Date result = Utils.setTime(date, 10, 54);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    void testGenerateRandomText_int() {
        int size = 3;
        String result = Utils.generateRandomText(size);
        Assertions.assertEquals(3, result.length());
    }

    @Test
    void testGenerateRandomText_0args() {
        String result = Utils.generateRandomText();
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void testGenerateRandomLong() {
        Long result = Utils.generateRandomLong();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result > 0);
    }

    @Test
    void testGetNumberFormat_double() {
        double number = 123456.0;
        Assertions.assertEquals("123.456", Utils.getNumberFormat(number));
    }

    @Test
    void testGetNumberFormat_double_boolean() {
        double number = 123456.0;
        Assertions.assertEquals("123456", Utils.getNumberFormat(number, false));
        Assertions.assertEquals("123.456", Utils.getNumberFormat(number, true));
    }

    @Test
    void testGetNumberFormat_double_int() {
        double number = 123456.0;
        Assertions.assertEquals("   123.456", Utils.getNumberFormat(number, 10));
        Assertions.assertEquals("             123.456", Utils.getNumberFormat(number, 20));
    }

    @Test
    void testGetNumberFormat_3args() {
        double number = 123456.0;
        Assertions.assertEquals("    123456", Utils.getNumberFormat(number, false, 10));
        Assertions.assertEquals("              123456", Utils.getNumberFormat(number, false, 20));

        Assertions.assertEquals("   123.456", Utils.getNumberFormat(number, true, 10));
        Assertions.assertEquals("             123.456", Utils.getNumberFormat(number, true, 20));
    }

    @Test
    void testGetDecimalFormat_double() {
        Assertions.assertEquals("1.234,67", Utils.getDecimalFormat((double) 1234.67));
    }

    @Test
    void testGetDateFormat_Date() {
        Date date = null;
        String expResult = "";
        String result = Utils.getDateFormat(date);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    void testGetDateFormat_LocalDate() {
        LocalDate localDate = LocalDate.of(2023, Month.JANUARY, 30);
        Assertions.assertEquals("30/01/2023", Utils.getDateFormat(localDate));
    }

    @Test
    void testGetDateFormat_LocalDate_String() {
        LocalDate localDate = LocalDate.of(2023, Month.JANUARY, 30);
        Assertions.assertEquals("2023-01-30", Utils.getDateFormat(localDate, "yyyy-MM-dd"));
    }

    @Test
    void testGetDateFormat_Date_String() {
        Date date = null;
        String pattern = "";
        String expResult = "";
        String result = Utils.getDateFormat(date, pattern);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    void testGetTimeFormat_Date() {
        Date date = null;
        String expResult = "";
        String result = Utils.getTimeFormat(date);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    void testGetTimeFormat_LocalDateTime() {
        LocalDateTime localDateTime = null;
        String expResult = "";
        String result = Utils.getTimeFormat(localDateTime);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    void testGetDateTimeFormat_Date() {
        Date date = null;
        String expResult = "";
        String result = Utils.getDateTimeFormat(date);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    void testGetDateTimeFormat_LocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 1, 10, 2, 3);
        String expResult = "01/02/2023 10:02:03";
        String result = Utils.getDateTimeFormat(localDateTime); //dd/MM/yyyy HH:mm:ss
        Assertions.assertEquals(expResult, result);
    }

    @Test
    void testGetDateTimeFormat_LocalDateTime_String() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 1, 10, 2, 3);
        String expResult = "01-02-2023 10:02:03";
        String pattern = "dd-MM-yyyy HH:mm:ss";
        String result = Utils.getDateTimeFormat(localDateTime, pattern);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    void testGetRutFormat() {
        String result = Utils.getRutFormat("12345678-9");
        Assertions.assertEquals("12.345.678-9", result);
    }

    @Test
    void testGetDate_String() throws Exception {
        String text = "";
        Date expResult = null;
        Date result = Utils.getDate(text);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    void testGetDate_String_String() {
        String text = "";
        String pattern = "";
        Date expResult = null;
        Date result = Utils.getDate(text, pattern);
        Assertions.assertEquals(expResult, result);

    }


    @Test
    void testGetElapsedTime_LocalDate_LocalDate() {
        LocalDate start = LocalDate.of(2023, Month.JANUARY, 1);
        LocalDate end = LocalDate.of(2023, Month.JANUARY, 30);
        Assertions.assertEquals("29d", Utils.getElapsedTime(start, end));
    }


    @Test
    void testGetElapsedTime_LocalDateTime_LocalDateTime() {
        LocalDateTime start = LocalDateTime.of(2023, Month.JANUARY, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, Month.JANUARY, 30, 10, 0);
        Assertions.assertEquals("29d", Utils.getElapsedTime(start, end));

    }


    @Test
    void testGetDate_LocalDate() {
        LocalDate localdate = LocalDate.of(2023, Month.JANUARY, 30);

        Date result = Utils.getDate(localdate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("30/01/2023", Utils.getDateFormat(result));

    }


    @Test
    void testSubstring() {
        String text = "";
        int maxlength = 0;
        String expResult = "";
        String result = Utils.substring(text, maxlength);
        Assertions.assertEquals(expResult, result);
    }


    @Test
    void testIsNumber() {
        Assertions.assertTrue(Utils.isNumber("8"));
        Assertions.assertFalse(Utils.isNumber("eight"));
    }


    @Test
    void testIsNullOrEmpty_String() {
        Assertions.assertTrue(Utils.isNullOrEmpty(""));
        Assertions.assertTrue(Utils.isNullOrEmpty(" "));
        Assertions.assertTrue(Utils.isNullOrEmpty((String) null));
        Assertions.assertFalse(Utils.isNullOrEmpty("Test"));
    }


    @Test
    void testIsNullOrEmpty_StringArr() {
        String[] text = new String[]{"", null};
        Assertions.assertTrue(Utils.isNullOrEmpty(text));
    }


    @Test
    void testIsNullOrEmpty_List() {
        Assertions.assertFalse(Utils.isNullOrEmpty(Arrays.asList("item1", "item2")));
        Assertions.assertTrue(Utils.isNullOrEmpty(new ArrayList<>()));
    }

    @Test
    void testIsRut_String() {
        Assertions.assertTrue(Utils.isRut("88888888-8"));
        Assertions.assertFalse(Utils.isRut("88.888.888-9"));
        Assertions.assertFalse(Utils.isRut("123-9"));
    }

    @Test
    void testIsRut_int_char() {
        int rut = 0;
        char dv = ' ';
        boolean expResult = false;
        boolean result = Utils.isRut(rut, dv);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    void testIsGTIN() {

        Assertions.assertFalse(Utils.isGTIN(""));
        Assertions.assertFalse(Utils.isGTIN("123"));
        Assertions.assertFalse(Utils.isGTIN("xxxx"));
        Assertions.assertFalse(Utils.isGTIN("123xxx"));

        Assertions.assertTrue(Utils.isGTIN("0000341643843"));
        Assertions.assertTrue(Utils.isGTIN("0000337787551"));
        Assertions.assertTrue(Utils.isGTIN("8880000000017"));
        Assertions.assertTrue(Utils.isGTIN("7793640000143"));
        Assertions.assertTrue(Utils.isGTIN("7809591400113"));
    }


    @Test
    void testIsValidRegex() {
        Assertions.assertTrue(Utils.isValidRegex("abc123", "^[a-z]+[0-9]+$"));
        Assertions.assertFalse(Utils.isValidRegex("123abc", "^[a-z]+[0-9]+$"));

    }

    @Test
    void testIsValidEmail() {
        Assertions.assertTrue(Utils.isValidEmail("test@gmail.com"));
        Assertions.assertFalse(Utils.isValidEmail("test@gmail"));
        Assertions.assertFalse(Utils.isValidEmail("test.com"));
        Assertions.assertFalse(Utils.isValidEmail("test@.com"));
        Assertions.assertFalse(Utils.isValidEmail("test@com"));
        Assertions.assertFalse(Utils.isValidEmail("test@com."));
        Assertions.assertFalse(Utils.isValidEmail("test@.com."));
        Assertions.assertFalse(Utils.isValidEmail("test@com..com"));
        Assertions.assertFalse(Utils.isValidEmail("test@@gmail.com"));
        Assertions.assertFalse(Utils.isValidEmail("test gmail.com"));
        Assertions.assertFalse(Utils.isValidEmail("test@gmail .com"));
    }


    @Test
    void testRoundUp() {
        Assertions.assertEquals(2, Utils.roundUp(8, 5));
        Assertions.assertEquals(3, Utils.roundUp(11, 5));
        Assertions.assertEquals(0, Utils.roundUp(0, 5));
        Assertions.assertEquals(1, Utils.roundUp(5, 5));
    }

    @Test
    void testSplit() {
        List<String> expResult = Arrays.asList("uno", "dos", "tres");
        List<String> result = Utils.split("uno, dos, tres,");
        Assertions.assertEquals(expResult, result);
    }


    @Test
    void testSplitText() {
        Assertions.assertLinesMatch(Arrays.asList("123 456", "789 0 123", "ABC DEF", "GHI"), Utils.splitText("123 456 789 0 123 ABC DEF GHI", 10));
        Assertions.assertLinesMatch(Arrays.asList("123456789"), Utils.splitText("123456789", 5));
        Assertions.assertLinesMatch(Arrays.asList("123", "456", "789"), Utils.splitText("123 456 789", 3));
    }


    @Test
    void testReplaceAll() {
        StringBuilder sb = new StringBuilder("This is a test. This test is only a test.");
        String toReplace = "test";
        String replacement = "kanopus";
        Utils.replaceAll(sb, toReplace, replacement);
        Assertions.assertEquals("This is a kanopus. This kanopus is only a kanopus.", sb.toString());
    }


    @Test
    void testArrayToString_intArr() {
        int[] numbers = {1, 2, 3, 4, 5};
        Assertions.assertEquals("1,2,3,4,5", Utils.arrayToString(numbers));
    }


    @Test
    void testArrayToString_List() {
        List<String> text = Arrays.asList("1", "2", "3");
        Assertions.assertEquals("1,2,3", Utils.arrayToString(text));
    }


    @Test
    void testArrayToString_List_String() {
        List<String> text = Arrays.asList("1", "2", "3");
        String separator = "|";
        Assertions.assertEquals("1|2|3", Utils.arrayToString(text, separator));
    }


    @Test
    void testToString_Long() {
        Long number = 9L;
        Assertions.assertEquals("9", Utils.toString(number));
    }


    @Test
    void testToString_List() {
        List<? extends Number> numbers = null;
        String expResult = "";
        String result = Utils.toString(numbers);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    void testToArrayInt() {
        List<Integer> values = Arrays.asList(1, 2, 3);
        int[] expResult = {1, 2, 3};
        int[] result = Utils.toArrayInt(values);
        Assertions.assertArrayEquals(expResult, result);
    }

    @Test
    void testToArrayLong() {
        List<String> values = Arrays.asList("1", "2", "3x");
        long[] expResult = {1, 2};
        long[] result = Utils.toArrayLong(values);
        Assertions.assertArrayEquals(expResult, result);
    }


    @Test
    void testToListInt() {
        Assertions.assertNotNull(Utils.toListInt("1,2,3"));
        Assertions.assertArrayEquals(Arrays.asList(1, 2, 3).toArray(), Utils.toListInt("1,2,3").toArray());

    }


    @Test
    void testToListLong() {
        Assertions.assertNotNull(Utils.toListLong("1,2,3"));
        Assertions.assertArrayEquals(Arrays.asList(1L, 2L, 3L).toArray(), Utils.toListLong("1,2,3").toArray());

    }

    @Test
    void testIsDateBetween() {

        Date start = Utils.getDate("01-01-2000", "dd-MM-yyyy");
        Date end = Utils.getDate("31-01-2000", "dd-MM-yyyy");

        Date dateInRange = Utils.getDate("15-01-2000", "dd-MM-yyyy");
        Date dateOutRange = Utils.getDate("15-02-2000", "dd-MM-yyyy");

        Assertions.assertTrue(Utils.isDateBetween(dateInRange, start, end));
        Assertions.assertFalse(Utils.isDateBetween(dateOutRange, start, end));
    }

    @Test
    void testIsDateEquals() {
        Date date1 = Utils.getDate("30-01-2000", "dd-MM-yyyy");
        Date date2 = Utils.getDate("30-01-2000", "dd-MM-yyyy");
        Assertions.assertTrue(Utils.isDateEquals(date1, date2));

        Date date3 = Utils.getDate("30-03-2000", "dd-MM-yyyy");
        Assertions.assertFalse(Utils.isDateEquals(date1, date3));
    }


    @Test
    void testFileToString() throws Exception {
        Assertions.assertThrows(Exception.class, () -> {
            Utils.fileToString(new File("not_found.txt"));
        });
        Assertions.assertEquals("test1\n", Utils.fileToString(new File("src/test/resources/test.txt")).toString());

    }


    @Test
    void testParseMoney2Long() {
        String text = "";
        long expResult = 0L;
        long result = Utils.parseMoney2Long(text);
        Assertions.assertEquals(expResult, result);

    }


    @Test
    void testParseDouble2Integer() {

        Assertions.assertEquals(0, Utils.parseDouble2Integer(""));
        Assertions.assertEquals(1, Utils.parseDouble2Integer("1.2"));
        Assertions.assertEquals(9, Utils.parseDouble2Integer("9.9"));

    }


    @Test
    void testParseDouble2Long() {
        Assertions.assertEquals(0L, Utils.parseDouble2Long(""));
        Assertions.assertEquals(1L, Utils.parseDouble2Long("1.2"));
        Assertions.assertEquals(9L, Utils.parseDouble2Long("9.9"));
    }


    @Test
    void testParseLongDefault() {
        Assertions.assertEquals(0, Utils.parseLongDefault("", 0));
        Assertions.assertEquals(1, Utils.parseLongDefault("no-num", 1));
        Assertions.assertEquals(9, Utils.parseLongDefault("9", 2));
    }


    @Test
    void testParseLong_String() {

        String text = "";
        Long expResult = null;
        Long result = Utils.parseLong(text);
        Assertions.assertEquals(expResult, result);

    }


    @Test
    void testParseLong_String_boolean() {

        String text = "";
        boolean required = false;
        Long expResult = null;
        Long result = Utils.parseLong(text, required);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    void testParseIntDefault() {
        String text = "";
        int defaultValue = 5;
        Integer expResult = defaultValue;
        Integer result = Utils.parseIntDefault(text, defaultValue);
        Assertions.assertEquals(expResult, result);

    }


    @Test
    void testParseInt_String() {
        Assertions.assertEquals((Integer) 1, Utils.parseInt("1"));
    }


    @Test
    void testParseInt_String_boolean() {
        String text = "12000";
        boolean required = false;
        Integer expResult = 12000;
        Integer result = Utils.parseInt(text, required);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    void testParseBigInteger() {
        Assertions.assertEquals(BigInteger.valueOf(1), Utils.parseBigInteger((int) 1));
        Assertions.assertEquals(BigInteger.valueOf(1), Utils.parseBigInteger((long) 1));
        Assertions.assertEquals(BigInteger.valueOf(1), Utils.parseBigInteger("1"));
        Assertions.assertEquals(BigInteger.valueOf(1), Utils.parseBigInteger((double) 1));
    }


    @Test
    void testParseBigDecimal_Double() {
        BigDecimal result = Utils.parseBigDecimal(1);
        Assertions.assertEquals(BigDecimal.valueOf(1), result);

        double price = 1999.0;
        BigDecimal result2 = Utils.parseBigDecimal(price);
        Assertions.assertEquals("1999", result2 + "");
    }


    @Test
    void testParseBigDecimal_Long() {
        Assertions.assertEquals(new BigDecimal(1), Utils.parseBigDecimal(1L));
    }


    @Test
    void testParseBigDecimal_int() {
        Assertions.assertEquals(new BigDecimal(1), Utils.parseBigDecimal(1));
    }


    @Test
    void testParseStringWriter() {
        StringWriter expResult = new StringWriter();
        expResult.append("text");
        Assertions.assertEquals(expResult.toString(), Utils.parseStringWriter("text").toString());
    }


    @Test
    void testGetCustomerN1A1A2() {
        Assertions.assertEquals("Juan", Utils.getCustomerN1A1A2("Juan", 12));
        Assertions.assertEquals("Juan Andres", Utils.getCustomerN1A1A2("Juan Andres", 12));
        Assertions.assertEquals("Juan Perez", Utils.getCustomerN1A1A2("Juan Perez Coloma", 12));
        Assertions.assertEquals("Juan Perez Coloma", Utils.getCustomerN1A1A2("Juan Andres Perez Coloma", 17));
        Assertions.assertEquals("", Utils.getCustomerN1A1A2("", 12));
    }


    @Test
    void testPutIntoArrayList() {

        List<ExampleTO> items = new ArrayList<>();
        Assertions.assertTrue(items.isEmpty());

        List<ExampleTO> items2 = Utils.putIntoArrayList(new ExampleTO(1, "one"), items);
        Assertions.assertEquals(1, items2.size());
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals("one", items.get(0).getName());

        List<ExampleTO> items3 = Utils.putIntoArrayList(new ExampleTO(10, "one"), items2);
        items3 = Utils.putIntoArrayList(new ExampleTO(2, "two"), items3);
        items3 = Utils.putIntoArrayList(new ExampleTO(3, "three"), items3);
        Assertions.assertEquals(3, items3.size());
        Assertions.assertEquals(10, items.get(0).getId());
        Assertions.assertEquals("one", items.get(0).getName());
        Assertions.assertEquals(2, items.get(1).getId());
        Assertions.assertEquals("two", items.get(1).getName());
        Assertions.assertEquals(3, items.get(2).getId());
        Assertions.assertEquals("three", items.get(2).getName());

    }


    static class EnumIdentifiableExample implements EnumIdentifiable<Long> {
        private final Long id;

        public EnumIdentifiableExample(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }


    }

    public static class ExampleTO {

        private final int id;
        private final String name;

        public ExampleTO(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            ExampleTO other = (ExampleTO) obj;
            return (this.getName() != null && other != null && this.getName().equals(other.getName()));
        }

    }

    @Test
    void arrayToString_and_toArray_conversions() {
        Assertions.assertEquals("1,2,3", Utils.arrayToString(new int[]{1, 2, 3}));
        Assertions.assertEquals("1,2,3", Utils.arrayToString(new long[]{1, 2, 3}));

        List<Integer> ints = Arrays.asList(4, 5, 6);
        Assertions.assertArrayEquals(new int[]{4, 5, 6}, Utils.toArrayInt(ints));
        Assertions.assertArrayEquals(new Integer[]{4, 5, 6}, Utils.toArrayInteger(ints));
    }

    @Test
    void mergeTextCustomSeparator_handlesEmptyAndNulls() {
        Assertions.assertEquals("a b", Utils.mergeTextCustomSeparator(" ", "a", "", null, "b"));
        Assertions.assertEquals("", Utils.mergeTextCustomSeparator(","));
    }
}
