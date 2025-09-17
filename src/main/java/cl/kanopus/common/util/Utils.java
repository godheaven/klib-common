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
import cl.kanopus.common.util.format.PaddingDecimalFormat;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final PaddingDecimalFormat NUMBER_FORMAT;
    private static final PaddingDecimalFormat NUMBER_FORMAT_WITH_MILES;
    private static final DecimalFormat DECIMAL_FORMAT;

    private static final SimpleDateFormat DATE_FORMAT;
    private static final SimpleDateFormat TIME_FORMAT;
    private static final SimpleDateFormat DATETIME_FORMAT;
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();


    static {
        DecimalFormatSymbols symbolComma = new DecimalFormatSymbols();
        symbolComma.setDecimalSeparator(',');

        NUMBER_FORMAT = new PaddingDecimalFormat("#,###;(-#,###)", symbolComma);
        NUMBER_FORMAT_WITH_MILES = new PaddingDecimalFormat("###.##;(-###.##)", symbolComma);

        DECIMAL_FORMAT = new DecimalFormat("#,###.##;(-#,###.##)", symbolComma);

        DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
        DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    }

    private Utils() {
    }

    public static StringBuilder printInfoKtools(String component, String version) {
        StringBuilder info = new StringBuilder();

        info.append("\n");
        info.append("██   ██ ████████  ██████   ██████  ██      ███████ \n");
        info.append("██  ██     ██    ██    ██ ██    ██ ██      ██     \n");
        info.append("█████      ██    ██    ██ ██    ██ ██      ███████\n");
        info.append("██  ██     ██    ██    ██ ██    ██ ██           ██\n");
        info.append("██   ██    ██     ██████   ██████  ███████ ███████\n");
        info.append("\n");
        info.append(" :: ").append(String.format("%-31s", component)).append("::  (").append(version).append(") \n");
        info.append(" :: Developed By Kanopus\n");
        info.append(" :: https://github.com/godheaven/\n");

        return info;
    }

    public static <T extends Object> List<T> putIntoArrayList(T item, List<T> items) {
        List<T> result = items == null ? new ArrayList<>() : items;
        if (result.contains(item)) {
            result.set(result.indexOf(item), item);
        } else {
            result.add(item);
        }
        return result;
    }

    public static boolean isEqualsOne(String text, String... values) {
        boolean equals = false;
        for (String v : values) {
            equals = v.equals(text);
            if (equals) {
                break;
            }
        }

        return equals;
    }

    public static boolean isEqualsOne(EnumIdentifiable source, EnumIdentifiable... values) {
        boolean equals = false;
        for (EnumIdentifiable v : values) {
            equals = v.getId().equals(source.getId());
            if (equals) {
                break;
            }
        }

        return equals;
    }

    public static LocalDate getLocalDate(String date, String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate getLocalDate(Date date) {
        return (date != null) ? Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate() : null;
    }

    public static LocalDateTime getLocalDateTime(Date date) {
        return (date != null) ? Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime() : null;
    }

    public static double parseDouble(long value) {
        return Double.parseDouble(value + "");
    }

    public static double parseDouble(Object value) {
        return Double.parseDouble(value + "");
    }

    public static <T extends Object> T defaultValue(T value, T defaultValue) {
        if (value instanceof String string) {
            return (!isNullOrEmpty(string) ? value : defaultValue);
        } else {
            return (value != null) ? value : defaultValue;
        }

    }

    public static <T extends Object> T defaultValue(Map<Integer, T> array, int index, T defaultValue) {
        T result = defaultValue;
        if (array != null) {
            result = defaultValue(array.get(index), defaultValue);
        }
        return result;
    }

    public static Date setTime(Date date, int hour, int minute) {
        Calendar result = Calendar.getInstance();
        result.setTime(date);
        result.set(Calendar.HOUR_OF_DAY, hour);
        result.set(Calendar.MINUTE, minute);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result.getTime();
    }

    public static String generateRandomText(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Token size must be greater than 0");
        }

        StringBuilder token = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            token.append(ALPHABET[RNG.nextInt(ALPHABET.length)]);
        }

        return token.toString();
    }

    public static String generateRandomText() {
        return generateRandomText(12);
    }

    public static Long generateRandomLong() {
        BigInteger bi = new BigInteger(130, RNG);
        return bi.longValue() > 0 ? bi.longValue() : bi.longValue() * -1;
    }

    public static Integer generateRandomInt() {
        BigInteger bi = new BigInteger(130, RNG);
        return bi.intValue() > 0 ? bi.intValue() : bi.intValue() * -1;
    }

    public static String getNumberFormat(double number) {
        return getNumberFormat(number, true, 0);
    }

    public static String getNumberFormat(double number, boolean includeSeparatorMiles) {
        return getNumberFormat(number, includeSeparatorMiles, 0);
    }

    public static String getNumberFormat(double number, int padding) {
        return getNumberFormat(number, true, padding);
    }

    public static String getNumberFormat(double number, boolean includeSeparatorMiles, int padding) {
        return (includeSeparatorMiles) ? NUMBER_FORMAT.format(number, padding) : NUMBER_FORMAT_WITH_MILES.format(number, padding);
    }

    public static String getDecimalFormat(double number) {
        return DECIMAL_FORMAT.format(number);
    }

    public static String getDateFormat(Date date) {
        return (date != null) ? DATE_FORMAT.format(date) : "";
    }

    public static String getDateFormat(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return localDate.format(formatter);
    }

    public static String getDateFormat(LocalDate localDate, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(formatter);
    }

    public static String getDateFormat(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("es", "ES"));
        return (date != null) ? sdf.format(date).toUpperCase() : "";
    }

    public static String getTimeFormat(Date date) {
        return (date != null) ? TIME_FORMAT.format(date) : "";
    }

    public static String getTimeFormat(LocalDateTime localDateTime) {
        return (localDateTime != null) ? localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "";
    }

    public static String getDateTimeFormat(Date date) {
        return (date != null) ? DATETIME_FORMAT.format(date) : "";
    }

    public static String getDateTimeFormat(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return (localDateTime != null) ? localDateTime.format(formatter) : "";
    }

    public static String getDateTimeFormat(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return (localDateTime != null) ? localDateTime.format(formatter) : "";
    }

    public static String getRutFormat(String rut) {
        return getRutFormat(rut, true);
    }

    public static String getRutFormat(String rut, boolean includeDots) {
        String plainText = rut.replaceAll("\\.", "").replace("-", "").toUpperCase();

        String part1 = plainText.substring(0, plainText.length() - 1);
        String part2 = plainText.substring(plainText.length() - 1, plainText.length());
        return includeDots
                ? Utils.getNumberFormat(Double.parseDouble(part1)) + "-" + part2
                : part1 + "-" + part2;

    }

    public static Date getDate(String text) throws ParseException {
        return (text != null && !text.isEmpty()) ? DATE_FORMAT.parse(text) : null;
    }

    public static Date getDate(String text, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(text);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static String getElapsedTime(LocalDate start, LocalDate end) {
        try {
            long milliseconds = ChronoUnit.MILLIS.between(start, end);
            StringBuilder sb = new StringBuilder();
            if (milliseconds < 60000) {
                sb.append("0 dias");
            } else {
                long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
                if (days > 0) {
                    sb.append(days);
                    sb.append("dias");
                }
            }
            return sb.toString().trim();
        } catch (Exception ex) {
            return null;
        }

    }

    public static String getElapsedTime(LocalDateTime start, LocalDateTime end) {
        try {
            long milliseconds = ChronoUnit.MILLIS.between(start, end);
            StringBuilder sb = new StringBuilder();
            if (milliseconds < 60000) {
                sb.append("0m");
            } else {
                long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
                milliseconds -= TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
                milliseconds -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
                if (days > 0) {
                    sb.append(days);
                    sb.append("d ");
                }
                if (hours > 0) {
                    sb.append(hours);
                    sb.append("h ");
                }
                if (minutes > 0) {
                    sb.append(minutes);
                    sb.append("m ");
                }
            }
            return sb.toString().trim();
        } catch (Exception ex) {
            return null;
        }

    }

    public static Date getDate(LocalDate localdate) {
        return localdate != null ? java.sql.Date.valueOf(localdate) : null;
    }

    public static String substring(String text, int maxlength) {
        if (text != null) {
            return (text.length() > maxlength) ? text.substring(0, maxlength) : text;
        } else {
            return null;
        }
    }

    public static boolean isNumber(String number) {
        boolean isNumber = false;
        try {
            if (number != null && !"".equals(number)) {
                Integer.valueOf(number);
                isNumber = true;
            }
        } catch (NumberFormatException e) {
            //Is not a number
        }
        return isNumber;
    }

    public static boolean isNullOrEmpty(String text) {
        return (text == null || text.trim().isEmpty());
    }

    public static boolean isNullOrEmpty(String... text) {
        boolean is = false;
        for (String t : text) {
            if ((t == null || t.trim().isEmpty())) {
                is = true;
                break;
            }
        }

        return is;
    }

    public static boolean isNullOrEmpty(List<? extends Object> list) {
        return (list == null || list.isEmpty());
    }

    public static boolean isNullOrEmpty(Object[] list) {
        return (list == null || list.length == 0);
    }

    public static boolean isEquals(String text1, String text2) {
        return ((text1 == null && text2 == null) || (text1 != null && text1.equals(text2)));
    }

    public static boolean isRut(String rut) {
        boolean valid = false;
        if (!rut.isEmpty()) {
            // Creamos un arreglo con el rut y el digito verificador
            String[] rutDv = rut.split("-");
            // Las partes del rut (numero y dv) deben tener una longitud positiva
            if (rutDv.length == 2) {
                // Capturamos error (al convertir un string a entero)
                try {
                    int ru = Integer.parseInt(rutDv[0]);
                    char dv = rutDv[1].toUpperCase().charAt(0);
                    // Validamos que sea un rut valid según la norma
                    valid = Utils.isRut(ru, dv);
                } catch (Exception ex) {
                    valid = false;
                }
            }
        }
        return valid;
    }

    public static boolean isRut(int rut, char dv) {
        int m = 0;
        int s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return dv == (char) (s != 0 ? s + 47 : 75);
    }

    public static boolean isGTIN(String gtin) {
        boolean valid = false;
        if (!isNullOrEmpty(gtin) && gtin.matches("^[0-9]{8,18}$")) {

            int[] checkDigitArray = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            int[] gtinMaths = {3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3};
            String[] barcodeArray = gtin.split("(?!^)");
            int gtinLength = gtin.length();
            int modifier = (17 - (gtinLength - 1));
            int gtinCheckDigit = Integer.parseInt(gtin.substring(gtinLength - 1));
            int tmpCheckDigit;
            int tmpCheckSum = 0;
            int i;
            int ii;

            // Run through and put digits into multiplication table
            for (i = 0; i < (gtinLength - 1); i++) {
                checkDigitArray[modifier + i] = Integer.parseInt(barcodeArray[i]);  // Add barcode digits to Multiplication Table
            }

            // Calculate "Sum" of barcode digits
            for (ii = modifier; ii < 17; ii++) {
                tmpCheckSum += (checkDigitArray[ii] * gtinMaths[ii]);
            }

            // Difference from Rounded-Up-To-Nearest-10 - Fianl Check Digit Calculation
            tmpCheckDigit = (int) ((Math.ceil((float) tmpCheckSum / (float) 10) * 10) - tmpCheckSum);

            // Check if last digit is same as calculated check digit
            valid = (gtinCheckDigit == tmpCheckDigit);

        }
        return valid;
    }

    public static boolean isValidRegex(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static int roundUp(int dividend, int divisor) {
        return (dividend + divisor - 1) / divisor;
    }

    public static List<String> split(String text) {
        List<String> array = new ArrayList<>();
        if (text != null && !text.trim().isEmpty()) {
            String[] result = text.split(",");
            for (int i = 0; i < result.length; i++) {
                array.add(result[i].trim());
            }
        }
        return array;
    }

    public static List<String> splitText(String text, int maxLineSize) {
        List<String> lines = new ArrayList<>();
        if (text != null && !text.trim().isEmpty()) {
            String[] lineasSeparadas = text.trim().split("\n");
            for (String l : lineasSeparadas) {
                //12345678901
                //mi texto largo

                StringBuilder linea = new StringBuilder();
                String[] lineasSeparadaNivel2 = l.split(" ");
                for (String s : lineasSeparadaNivel2) {
                    if ((linea.length() + s.length()) < maxLineSize) {
                        linea.append(s).append(" ");
                    } else {
                        lines.add(linea.toString().trim());
                        linea.setLength(0);
                        linea.append(s).append(" ");
                    }
                }
                lines.add(linea.toString().trim());

            }
        }
        return lines;
    }

    public static String mergeText(String... values) {
        return mergeTextCustomSeparator(" ", values);
    }

    public static String mergeTextCustomSeparator(String separator, String... values) {
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            if (!Utils.isNullOrEmpty(v)) {
                if (!sb.isEmpty()) {
                    sb.append(separator);
                }
                sb.append(v);
            }
        }

        return sb.toString();
    }

    public static void replaceAll(StringBuilder sb, String toReplace, String replacement) {
        int index;
        while ((index = sb.lastIndexOf(toReplace)) != -1) {
            sb.replace(index, index + toReplace.length(), replacement);
        }
    }

    public static String arrayToString(int[] numbers) {
        StringBuilder sb = new StringBuilder();
        if (numbers != null) {
            for (Object n : numbers) {
                if (!sb.isEmpty()) {
                    sb.append(",");
                }
                sb.append(n);
            }
        }
        return sb.toString();
    }

    public static String arrayToString(long[] numbers) {
        StringBuilder sb = new StringBuilder();
        if (numbers != null) {
            for (Object n : numbers) {
                if (!sb.isEmpty()) {
                    sb.append(",");
                }
                sb.append(n);
            }
        }
        return sb.toString();
    }

    public static String arrayToString(Integer[] numbers) {
        return arrayGenericToString(numbers);
    }

    public static String arrayToString(Long[] numbers) {
        return arrayGenericToString(numbers);
    }

    private static String arrayGenericToString(Object[] numbers) {
        StringBuilder sb = new StringBuilder();
        if (numbers != null) {
            for (Object n : numbers) {
                if (!sb.isEmpty()) {
                    sb.append(",");
                }
                sb.append(n);
            }
        }
        return sb.toString();
    }

    public static String arrayToString(List<String> text) {
        return arrayToString(text, ",");
    }

    public static String arrayToString(List<String> text, String separator) {
        StringBuilder sb = new StringBuilder();
        if (text != null) {
            for (String n : text) {
                if (!sb.isEmpty()) {
                    sb.append(separator);
                }
                sb.append(n);
            }
        }
        return sb.toString();
    }

    public static String toString(Long number) {
        StringBuilder sb = new StringBuilder();
        if (number != null) {
            sb.append(number);
        }
        return sb.toString();
    }

    public static String toString(List<? extends Number> numbers) {
        StringBuilder sb = new StringBuilder();
        if (numbers != null) {
            for (Number n : numbers) {
                if (!sb.isEmpty()) {
                    sb.append(",");
                }
                sb.append(n);
            }
        }
        return sb.toString();
    }

    public static int[] toArrayInt(List<Integer> numbers) {
        int[] ret = new int[numbers.size()];
        Iterator<Integer> iterator = numbers.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next();
        }
        return ret;
    }

    public static Integer[] toArrayInteger(List<Integer> numbers) {
        Integer[] ret = new Integer[numbers.size()];
        Iterator<Integer> iterator = numbers.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next();
        }
        return ret;
    }

    public static String[] toArrayString(List<? extends EnumIdentifiable> values) {
        String[] list = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            list[i] = values.get(i).getId() + "";
        }
        return list;
    }

    public static long[] toArrayLong(List<String> values) {
        List<Long> list = new ArrayList<>();
        for (String v : values) {
            Long parsed = parseLong(v);
            if (parsed != null) {
                list.add(parsed);
            }
        }
        return list.stream().mapToLong(i -> i).toArray();
    }

    public static List<Integer> toListInt(String array) {
        List<Integer> integers = new ArrayList<>();
        if (array != null) {
            String[] split = array.split(",");
            for (String s : split) {
                try {
                    integers.add(Integer.valueOf(s.trim()));
                } catch (NumberFormatException ex) {
                    //No se agrega nada
                }
            }
        }
        return integers;
    }

    public static List<Long> toListLong(String array) {
        List<Long> integers = new ArrayList<>();
        if (array != null) {
            String[] split = array.split(",");
            for (String s : split) {
                try {
                    integers.add(Long.valueOf(s.trim()));
                } catch (NumberFormatException ex) {
                    //No se agrega nada
                }
            }
        }
        return integers;
    }

    public static boolean isDateBetween(Date fecha, Date fechaInicio, Date fechaFin) {
        if (fecha != null && fechaInicio != null && fechaFin != null) {
            //fechaActual debe ser mayor o igual a fechaInicio y menor o igual a fechaFin
            boolean fechaActualMayorFechaInicio = (fecha.after(fechaInicio) || Utils.getDateFormat(fecha).equals(Utils.getDateFormat(fechaInicio)));
            boolean fechaActualMenorFechaFin = (fecha.before(fechaFin) || Utils.getDateFormat(fecha).equals(Utils.getDateFormat(fechaFin)));
            return fechaActualMenorFechaFin || (fechaActualMayorFechaInicio && fechaActualMenorFechaFin);
        } else {
            return false;
        }
    }

    public static boolean isDateEquals(LocalDate date1, LocalDate date2) {
        boolean equals = false;
        if (date1 != null && date2 != null) {
            equals = date1.equals(date2);

        }
        return equals;
    }

    public static boolean isDateEquals(Date date1, Date date2) {
        String date1Text = DATE_FORMAT.format(date1);
        String date2Text = DATE_FORMAT.format(date2);
        return date1Text.equals(date2Text);
    }

    public static StringBuilder fileToString(File textFile) throws IOException {
        if (!textFile.exists()) {
            throw new FileNotFoundException("File does not exist: " + textFile);
        }
        StringBuilder contents = new StringBuilder();
        try (BufferedReader input = new BufferedReader(new FileReader(textFile))) {
            String line;
            while ((line = input.readLine()) != null) {
                contents.append(line);
                contents.append("\n");
            }
        }
        return contents;
    }

    public static long parseMoney2Long(String text) {
        long number = 0;
        if (text != null && !text.isEmpty()) {
            String numberText = text.trim().replaceAll("\\.", "");
            number = Long.parseLong(numberText);
        }
        return number;
    }

    public static int parseDouble2Integer(String text) {
        int number = 0;
        if (text != null && !text.isEmpty()) {
            double mValue = Double.parseDouble(text);
            number = Double.valueOf(mValue).intValue();
        }
        return number;
    }

    public static long parseDouble2Long(String text) {
        long number = 0;
        if (text != null && !text.isEmpty()) {
            double mValue = Double.parseDouble(text);
            number = Double.valueOf(mValue).longValue();
        }
        return number;
    }

    public static Long parseLongDefault(String text, long defaultValue) {
        Long num = null;
        try {
            if (text != null) {
                num = Long.valueOf(text.trim());
            }
        } catch (NumberFormatException ex) {
            //Is not a number
        }

        return num != null ? num : defaultValue;
    }

    public static Long parseLong(String text) {
        return parseLong(text, false);
    }

    public static Long parseLong(String text, boolean required) {
        Long num = null;
        if (required) {
            num = Long.valueOf(text.trim());
        } else {
            try {
                if (text != null) {
                    num = Long.valueOf(text.trim());
                }
            } catch (NumberFormatException ex) {
                //Is not a number
            }
        }
        return num;
    }

    public static Integer parseIntDefault(String text, int defaultValue) {
        Integer num = null;
        try {
            if (text != null) {
                num = Integer.valueOf(text.trim());
            }
        } catch (NumberFormatException ex) {
            //Is not a number
        }

        return num != null ? num : defaultValue;
    }

    public static Integer parseInt(String text) {
        return parseInt(text, true);
    }

    public static Integer parseInt(String text, boolean required) {
        Integer num = null;
        if (required) {
            num = Integer.valueOf(text);
        } else {
            try {
                if (text != null) {
                    num = Integer.valueOf(text);
                }
            } catch (NumberFormatException ex) {
                //Is not a number
            }
        }
        return num;
    }

    public static BigInteger parseBigInteger(String text) {
        BigInteger num = null;
        try {
            if (text != null) {
                num = BigInteger.valueOf(parseLong(text.trim()));
            }
        } catch (Exception ex) {
            //Is not a number
        }
        return num;
    }

    public static BigInteger parseBigInteger(Long num) {
        return BigInteger.valueOf(num);
    }

    public static BigInteger parseBigInteger(Double num) {
        return num == null ? null : BigInteger.valueOf(num.intValue());
    }

    public static BigInteger parseBigInteger(int num) {
        return BigInteger.valueOf(num);
    }

    public static BigDecimal parseBigDecimal(Double num) {
        return BigDecimal.valueOf(num.longValue());
    }

    public static BigDecimal parseBigDecimal(Long num) {
        return BigDecimal.valueOf(num);
    }

    public static BigDecimal parseBigDecimal(int num) {
        return BigDecimal.valueOf(num);
    }

    public static StringWriter parseStringWriter(String text) {
        StringWriter sw = new StringWriter();
        sw.append(text);
        return sw;
    }

    public static <T extends Enum<T> & EnumIdentifiable<S>, S> T parseEnum(Class<T> type, S id) {
        for (T t : type.getEnumConstants()) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public static <T extends Enum<T> & EnumIdentifiable<S>, S> T valueOfEnum(Class<T> type, String name) {
        for (T t : type.getEnumConstants()) {
            if (t.name().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public static String getClienteN1A1A2(String cliente, int maxlength) {
        StringBuilder sb = new StringBuilder();
        if (cliente != null && cliente.length() > maxlength) {
            String[] names = cliente.split(" ");
            if (names.length >= 1) {
                sb.append(names[0]);
            }

            for (int i = 2; i < names.length; i++) {
                sb.append(" ").append(names[i]);
            }
        } else {
            sb.append(cliente);
        }

        return sb.toString();
    }

    /**
     * Returns List of the List argument passed to this function with size =
     * chunkSize
     *
     * @param list      Generic type of the List
     * @param chunkSize maximum size of each partition
     * @return A list of Lists which is portioned from the original list
     */
    public static <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Invalid chunk size: " + chunkSize);
        }
        List<List<T>> chunkList = new ArrayList<>(list.size() / chunkSize);
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunkList.add(list.subList(i, i + chunkSize >= list.size() ? list.size() : i + chunkSize));
        }
        return chunkList;
    }

    public static String toCamelCase(String text) {
        //TODO: Se debe implementar
        if (!Utils.isNullOrEmpty(text)) {
            text = text.replace("_", " ");
        }
        return text;
    }

    public static boolean contains(Integer[] array, int id) {
        boolean exist = false;
        if (array != null) {
            for (Integer a : array) {
                if (a == id) {
                    exist = true;
                    break;
                }
            }
        }

        return exist;
    }


    public static byte[] sha256(String value) {
        try {
            // Uso de JCA estándar
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            return md.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // No debería ocurrir
            return new byte[0];
        }
    }
}
