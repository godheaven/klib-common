package cl.kanopus.common.util;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class UtilsTest {

    public UtilsTest() {
    }

    @Test
    public void testIsNullOrEmpty() {

        Assertions.assertTrue(Utils.isNullOrEmpty(""));
        Assertions.assertTrue(Utils.isNullOrEmpty("   "));

        Assertions.assertFalse(Utils.isNullOrEmpty("   X"));
        Assertions.assertFalse(Utils.isNullOrEmpty("X"));
        Assertions.assertFalse(Utils.isNullOrEmpty("1"));
    }

    @Test
    public void testGetDateTimeFormat() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 1, 10, 2, 3);
        String result = Utils.getDateTimeFormat(localDateTime);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("01/02/2023 10:02:03", result);
    }

    @Test
    public void testElapsedTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(2).plusHours(5).plusMinutes(30);

        String result = Utils.getElapsedTime(now, future);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("2d 5h 30m", result);
    }

    @Test
    public void testIsEqualsOne() {
        String[] values = {"uno", "dos", "tres"};
        Assertions.assertTrue(Utils.isEqualsOne("uno", values));
        Assertions.assertFalse(Utils.isEqualsOne("cuatro", values));
    }

    @Test
    public void testGetLocalDate_String_String() {
        LocalDate localDate = LocalDate.of(2023, Month.JANUARY, 30);
        Assertions.assertEquals(localDate, Utils.getLocalDate("30-01-2023", "dd-MM-yyyy"));
    }

    @Test
    public void testGetLocalDate_Date() {
        Date date = Utils.getDate("30/01/2023", "dd/MM/yyyy");
        LocalDate expResult = LocalDate.of(2023, Month.JANUARY, 30);
        LocalDate result = Utils.getLocalDate(date);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testGetLocalDateTime() {
        Date date = Utils.getDate("30/01/2023 11:05:04", "dd/MM/yyyy HH:mm:ss");
        LocalDateTime expResult = LocalDateTime.of(2023, Month.JANUARY, 30, 11, 5, 4);
        LocalDateTime result = Utils.getLocalDateTime(date);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testParseDouble_long() {
        long value = 0L;
        double expResult = 0.0;
        double result = Utils.parseDouble(value);
        Assertions.assertEquals(expResult, result, 0);
    }

    @Test
    public void testParseDouble_Object() {
        double expResult = 2;
        double result = Utils.parseDouble("2");
        Assertions.assertEquals(expResult, result, 0);
    }

    @Test
    public void testDefaultValue_GenericType_GenericType() {
        Object value = null;
        Object defaultValue = null;
        Object expResult = null;
        Object result = Utils.defaultValue(value, defaultValue);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testDefaultValue_3args() {
        String expResult = "DEFAULT";
        String result = Utils.defaultValue(null, "DEFAULT");
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testSetTime() {
        Date date = Utils.getDate("30/01/2023 11:05:04", "dd/MM/yyyy HH:mm:ss");
        Date expResult = Utils.getDate("30/01/2023 10:54:00", "dd/MM/yyyy HH:mm:ss");
        Date result = Utils.setTime(date, 10, 54);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testGenerateRandomText_int() {
        int size = 0;
        String expResult = "";
        String result = Utils.generateRandomText(size);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testGenerateRandomText_0args() {
        String result = Utils.generateRandomText();
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void testGenerateRandomLong() {
        Long result = Utils.generateRandomLong();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result > 0);
    }

    @Test
    public void testGetNumberFormat_double() {
        double number = 123456.0;
        Assertions.assertEquals("123.456", Utils.getNumberFormat(number));
    }

    @Test
    public void testGetNumberFormat_double_boolean() {
        double number = 123456.0;
        Assertions.assertEquals("123456", Utils.getNumberFormat(number, false));
        Assertions.assertEquals("123.456", Utils.getNumberFormat(number, true));
    }

    @Test
    public void testGetNumberFormat_double_int() {
        double number = 123456.0;
        Assertions.assertEquals("   123.456", Utils.getNumberFormat(number, 10));
        Assertions.assertEquals("             123.456", Utils.getNumberFormat(number, 20));
    }

    @Test
    public void testGetNumberFormat_3args() {
        double number = 123456.0;
        Assertions.assertEquals("    123456", Utils.getNumberFormat(number, false, 10));
        Assertions.assertEquals("              123456", Utils.getNumberFormat(number, false, 20));

        Assertions.assertEquals("   123.456", Utils.getNumberFormat(number, true, 10));
        Assertions.assertEquals("             123.456", Utils.getNumberFormat(number, true, 20));
    }

    @Test
    public void testGetDecimalFormat_double() {
        Assertions.assertEquals("1.234,67", Utils.getDecimalFormat((double) 1234.67));
    }

    @Test
    public void testGetDateFormat_Date() {
        Date date = null;
        String expResult = "";
        String result = Utils.getDateFormat(date);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testGetDateFormat_LocalDate() {
        LocalDate localDate = LocalDate.of(2023, Month.JANUARY, 30);
        Assertions.assertEquals("30/01/2023", Utils.getDateFormat(localDate));
    }

    @Test
    public void testGetDateFormat_LocalDate_String() {
        LocalDate localDate = LocalDate.of(2023, Month.JANUARY, 30);
        Assertions.assertEquals("2023-01-30", Utils.getDateFormat(localDate, "yyyy-MM-dd"));
    }

    @Test
    public void testGetDateFormat_Date_String() {
        Date date = null;
        String pattern = "";
        String expResult = "";
        String result = Utils.getDateFormat(date, pattern);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testGetTimeFormat_Date() {
        Date date = null;
        String expResult = "";
        String result = Utils.getTimeFormat(date);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testGetTimeFormat_LocalDateTime() {
        LocalDateTime localDateTime = null;
        String expResult = "";
        String result = Utils.getTimeFormat(localDateTime);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testGetDateTimeFormat_Date() {
        Date date = null;
        String expResult = "";
        String result = Utils.getDateTimeFormat(date);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testGetDateTimeFormat_LocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 1, 10, 2, 3);
        String expResult = "01/02/2023 10:02:03";
        String result = Utils.getDateTimeFormat(localDateTime); //dd/MM/yyyy HH:mm:ss
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testGetDateTimeFormat_LocalDateTime_String() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 1, 10, 2, 3);
        String expResult = "01-02-2023 10:02:03";
        String pattern = "dd-MM-yyyy HH:mm:ss";
        String result = Utils.getDateTimeFormat(localDateTime, pattern);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testGetRutFormat() {
        String result = Utils.getRutFormat("12345678-9");
        Assertions.assertEquals("12.345.678-9", result);
    }

    @Test
    public void testGetDate_String() throws Exception {
        String text = "";
        Date expResult = null;
        Date result = Utils.getDate(text);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testGetDate_String_String() {
        String text = "";
        String pattern = "";
        Date expResult = null;
        Date result = Utils.getDate(text, pattern);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testGetElapsedTime_LocalDate_LocalDate() {
        LocalDate start = null;
        LocalDate end = null;
        String expResult = "";
        String result = Utils.getElapsedTime(start, end);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testGetElapsedTime_LocalDateTime_LocalDateTime() {
        LocalDateTime start = null;
        LocalDateTime end = null;
        String expResult = "";
        String result = Utils.getElapsedTime(start, end);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testGetDate_LocalDate() {
        LocalDate localdate = null;
        Date expResult = null;
        Date result = Utils.getDate(localdate);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testSubstring() {
        String text = "";
        int maxlength = 0;
        String expResult = "";
        String result = Utils.substring(text, maxlength);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testIsNumber() {
        String number = "";
        boolean expResult = false;
        boolean result = Utils.isNumber(number);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testIsNullOrEmpty_String() {
        String text = "";
        boolean expResult = false;
        boolean result = Utils.isNullOrEmpty(text);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testIsNullOrEmpty_StringArr() {
        String[] text = null;
        boolean expResult = false;
        boolean result = Utils.isNullOrEmpty(text);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testIsNullOrEmpty_List() {
        List list = null;
        boolean expResult = false;
        boolean result = Utils.isNullOrEmpty(list);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testIsRut_String() {
        Assertions.assertTrue(Utils.isRut("88888888-8"));
        Assertions.assertFalse(Utils.isRut("88.888.888-9"));
        Assertions.assertFalse(Utils.isRut("123-9"));

    }

    @Test
    public void testIsRut_int_char() {
        int rut = 0;
        char dv = ' ';
        boolean expResult = false;
        boolean result = Utils.isRut(rut, dv);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testIsGTIN() {
        String gtin = "";
        boolean expResult = false;
        boolean result = Utils.isGTIN(gtin);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testIsValidRegex() {
        String value = "";
        String regex = "";
        boolean expResult = false;
        boolean result = Utils.isValidRegex(value, regex);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testIsValidEmail() {
        String email = "";
        boolean expResult = false;
        boolean result = Utils.isValidEmail(email);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testRoundUp() {
        int dividend = 0;
        int divisor = 0;
        int expResult = 0;
        int result = Utils.roundUp(dividend, divisor);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testSplit() {
        List<String> expResult = Arrays.asList("uno", "dos", "tres");
        List<String> result = Utils.split("uno, dos, tres,");
        Assertions.assertEquals(expResult, result);
    }

    @Disabled
    @Test
    public void testSplitText() {
        int maxLineSize = 3;
        List<String> expResult = Arrays.asList("123", "456", "789");
        List<String> result = Utils.splitText("123 456 789", maxLineSize);
        Assertions.assertEquals(expResult, result);
    }

    @Disabled
    @Test
    public void testReplaceAll() {
        StringBuilder sb = null;
        String toReplace = "";
        String replacement = "";
        Utils.replaceAll(sb, toReplace, replacement);

    }

    @Disabled
    @Test
    public void testArrayToString_intArr() {
        int[] numbers = null;
        Object expResult = null;
        Object result = Utils.arrayToString(numbers);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testArrayToString_List() {
        List<String> text = null;
        String expResult = "";
        String result = Utils.arrayToString(text);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testArrayToString_List_String() {
        List<String> text = null;
        String separator = "";
        String expResult = "";
        String result = Utils.arrayToString(text, separator);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testToString_Long() {
        Long number = null;
        String expResult = "";
        String result = Utils.toString(number);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testToString_List() {
        List<? extends Number> numbers = null;
        String expResult = "";
        String result = Utils.toString(numbers);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testToArray() {
        List<Integer> integers = Arrays.asList(1, 2, 3);
        int[] expResult = {1, 2, 3};
        int[] result = Utils.toArray(integers);
        Assertions.assertArrayEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testToListInt() {
        String array = "";
        List<Integer> expResult = null;
        List<Integer> result = Utils.toListInt(array);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testToListLong() {
        String array = "";
        List<Long> expResult = null;
        List<Long> result = Utils.toListLong(array);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testIsDateBetween() {

        Date start = Utils.getDate("01-01-2000", "dd-MM-yyyy");
        Date end = Utils.getDate("31-01-2000", "dd-MM-yyyy");

        Date dateInRange = Utils.getDate("15-01-2000", "dd-MM-yyyy");
        Date dateOutRange = Utils.getDate("15-02-2000", "dd-MM-yyyy");

        Assertions.assertTrue(Utils.isDateBetween(dateInRange, start, end));
        Assertions.assertFalse(Utils.isDateBetween(dateOutRange, start, end));
    }

    @Test
    public void testIsDateEquals() {
        Date date1 = Utils.getDate("30-01-2000", "dd-MM-yyyy");
        Date date2 = Utils.getDate("30-01-2000", "dd-MM-yyyy");
        Assertions.assertTrue(Utils.isDateEquals(date1, date2));

        Date date3 = Utils.getDate("30-03-2000", "dd-MM-yyyy");
        Assertions.assertFalse(Utils.isDateEquals(date1, date3));
    }

    @Disabled
    @Test
    public void testFileToString() throws Exception {
        File textFile = null;
        StringBuilder expResult = null;
        StringBuilder result = Utils.fileToString(textFile);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testParseMoney2Long() {
        String text = "";
        long expResult = 0L;
        long result = Utils.parseMoney2Long(text);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testParseDouble2Integer() {
        String text = "";
        int expResult = 0;
        int result = Utils.parseDouble2Integer(text);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testParseDouble2Long() {

        String text = "";
        long expResult = 0L;
        long result = Utils.parseDouble2Long(text);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testParseLongDefault() {

        String text = "";
        long defaultValue = 0L;
        Long expResult = null;
        Long result = Utils.parseLongDefault(text, defaultValue);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testParseLong_String() {

        String text = "";
        Long expResult = null;
        Long result = Utils.parseLong(text);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testParseLong_String_boolean() {

        String text = "";
        boolean required = false;
        Long expResult = null;
        Long result = Utils.parseLong(text, required);
        Assertions.assertEquals(expResult, result);

    }

    @Test
    public void testParseIntDefault() {
        String text = "";
        int defaultValue = 5;
        Integer expResult = defaultValue;
        Integer result = Utils.parseIntDefault(text, defaultValue);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testParseInt_String() {
        String text = "";
        Integer expResult = null;
        Integer result = Utils.parseInt(text);
        Assertions.assertEquals(expResult, result);

    }

    @Disabled
    @Test
    public void testParseInt_String_boolean() {
        String text = "";
        boolean required = false;
        Integer expResult = null;
        Integer result = Utils.parseInt(text, required);
        Assertions.assertEquals(expResult, result);
    }

    @Test
    public void testParseBigInteger_String() {
        BigInteger result = Utils.parseBigInteger(1);
        Assertions.assertEquals(BigInteger.valueOf(1), result);
    }

    @Test
    public void testParseBigInteger_Long() {
        BigInteger result = Utils.parseBigInteger(1);
        Assertions.assertEquals(BigInteger.valueOf(1), result);
    }

    @Test
    public void testParseBigInteger_Double() {
        BigInteger result = Utils.parseBigInteger(1);
        Assertions.assertEquals(BigInteger.valueOf(1), result);
    }

    @Test
    public void testParseBigInteger_int() {
        BigInteger result = Utils.parseBigInteger(1);
        Assertions.assertEquals(BigInteger.valueOf(1), result);
    }

    @Test
    public void testParseBigDecimal_Double() {
        BigDecimal result = Utils.parseBigDecimal(1);
        Assertions.assertEquals(BigDecimal.valueOf(1), result);
    }

    @Disabled
    @Test
    public void testParseBigDecimal_Long() {

        Long num = null;
        BigDecimal expResult = null;
        BigDecimal result = Utils.parseBigDecimal(num);
        Assertions.assertEquals(expResult, result);
    }

    @Disabled
    @Test
    public void testParseBigDecimal_int() {
        int num = 0;
        BigDecimal expResult = null;
        BigDecimal result = Utils.parseBigDecimal(num);
        Assertions.assertEquals(expResult, result);
    }

    @Disabled
    @Test
    public void testParseStringWriter() {
        String text = "";
        StringWriter expResult = null;
        StringWriter result = Utils.parseStringWriter(text);
        Assertions.assertEquals(expResult, result);
    }

    @Disabled
    @Test
    public void testParseEnum() {
    }

    @Disabled
    @Test
    public void testValueOfEnum() {
    }

    @Disabled
    @Test
    public void testGetClienteN1A1A2() {
        String cliente = "";
        int maxlength = 0;
        String expResult = "";
        String result = Utils.getClienteN1A1A2(cliente, maxlength);
        Assertions.assertEquals(expResult, result);
    }

    @Disabled
    @Test
    public void testChunkList() {
    }

    @Test
    public void testPutIntoArrayList() {

        List<ExampleTO> items = new ArrayList<>();
        Assertions.assertTrue(items.isEmpty());
        
        List<ExampleTO> items2 = Utils.putIntoArrayList(new ExampleTO(1, "one"), items);
        Assertions.assertTrue(items2.size()==1);
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals("one", items.get(0).getName());
        
        List<ExampleTO> items3 = Utils.putIntoArrayList(new ExampleTO(10, "one"), items2);
        items3 = Utils.putIntoArrayList(new ExampleTO(2, "two"), items3);
        items3 = Utils.putIntoArrayList(new ExampleTO(3, "three"), items3);
        Assertions.assertTrue(items3.size()==3);
        Assertions.assertEquals(10, items.get(0).getId());
        Assertions.assertEquals("one", items.get(0).getName());
        Assertions.assertEquals(2, items.get(1).getId());
        Assertions.assertEquals("two", items.get(1).getName());
        Assertions.assertEquals(3, items.get(2).getId());
        Assertions.assertEquals("three", items.get(2).getName());
        
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
}
