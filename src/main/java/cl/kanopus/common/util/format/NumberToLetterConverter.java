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
package cl.kanopus.common.util.format;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public abstract class NumberToLetterConverter {

    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ",
            "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE ", "DIEZ ",
            "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS ",
            "DIECISIETE ", "DIECIOCHO ", "DIECINUEVE ", "VEINTE "};

    private static final String[] DECENAS = {"VENTI", "TREINTA ", "CUARENTA ",
            "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA ",
            "CIEN "};

    private static final String[] CENTENAS = {"CIENTO ", "DOSCIENTOS ",
            "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ",
            "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    private NumberToLetterConverter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Convert a numeric string into words (e.g. "$123,456.32").
     *
     * @param number numeric string representation
     * @return number spelled out in words (uppercase Spanish terms)
     * @throws NumberFormatException if the number is invalid or out of range
     */
    public static String convertNumberToLetter(String number) throws NumberFormatException {
        return convertNumberToLetter(Double.parseDouble(number));
    }

    /**
     * Convert a numeric value to its textual representation. The supported
     * range is 0 to 999,999,999 (inclusive).
     *
     * @param doubleNumber numeric value to convert
     * @return number converted to words (uppercase Spanish terms)
     * @throws NumberFormatException if the number is out of supported range
     */
    public static String convertNumberToLetter(double doubleNumber)
            throws NumberFormatException {

        StringBuilder converted = new StringBuilder();

        String patternThreeDecimalPoints = "#.###";

        DecimalFormat format = new DecimalFormat(patternThreeDecimalPoints);
        format.setRoundingMode(RoundingMode.DOWN);

        // Format the number to the pattern with three decimal places
        doubleNumber = Math.round(doubleNumber);
        String formatedDouble = format.format(doubleNumber);
        doubleNumber = Double.parseDouble(formatedDouble);

        // Check bounds
        if (doubleNumber > 999999999) {
            throw new NumberFormatException("Number is greater than 999,999,999 and cannot be converted");
        }

        if (doubleNumber < 0) {
            throw new NumberFormatException("Number must be non-negative");
        }

        String[] splitNumber = String.valueOf(formatedDouble).replace('.', '#').split("#");

        // Decompose the millions triplet
        int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
                8))
                + String.valueOf(getDigitAt(splitNumber[0], 7))
                + String.valueOf(getDigitAt(splitNumber[0], 6)));
        if (millon == 1) {
            converted.append("UN MILLON ");
        } else if (millon > 1) {
            converted.append(convertNumber(String.valueOf(millon))).append("MILLONES ");
        }

        // Decompose the thousands triplet
        int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
                5))
                + String.valueOf(getDigitAt(splitNumber[0], 4))
                + String.valueOf(getDigitAt(splitNumber[0], 3)));
        if (miles == 1) {
            converted.append("MIL ");
        } else if (miles > 1) {
            converted.append(convertNumber(String.valueOf(miles))).append("MIL ");
        }

        // Decompose the last units triplet
        int cientos = Integer.parseInt(String.valueOf(getDigitAt(
                splitNumber[0], 2))
                + String.valueOf(getDigitAt(splitNumber[0], 1))
                + String.valueOf(getDigitAt(splitNumber[0], 0)));
        if (cientos == 1) {
            converted.append("UN ");
        }

        if (millon + miles + cientos == 0) {
            converted.append("CERO ");
        }
        if (cientos > 1) {
            converted.append(convertNumber(String.valueOf(cientos)));
        }

        converted.append("PESOS");

        return converted.toString();
    }

    /**
     * Convierte los trios de numeros que componen las unidades, las decenas y
     * las centenas del numero.
     *
     * @param number Numero a convetir en digitos
     * @return Numero convertido en letras
     */
    private static String convertNumber(String number) {

        if (number.length() > 3) {
            throw new NumberFormatException(
                    "Maximum length is 3 digits");
        }

        // Special case for 100
        if (number.equals("100")) {
            return "CIEN ";
        }

        StringBuilder output = new StringBuilder();
        if (getDigitAt(number, 2) != 0) {
            output.append(CENTENAS[getDigitAt(number, 2) - 1]);
        }

        int k = Integer.parseInt(String.valueOf(getDigitAt(number, 1))
                + String.valueOf(getDigitAt(number, 0)));

        if (k <= 20) {
            output.append(UNIDADES[k]);
        } else if (k > 30 && getDigitAt(number, 0) != 0) {
            output.append(DECENAS[getDigitAt(number, 1) - 2]).append("Y ").append(UNIDADES[getDigitAt(number, 0)]);
        } else {
            output.append(DECENAS[getDigitAt(number, 1) - 2]).append(UNIDADES[getDigitAt(number, 0)]);
        }

        return output.toString();
    }

    /**
     * Return the numeric digit at the requested position counting from right to left.
     *
     * @param origin   the source string
     * @param position position from the right (0-based)
     * @return digit at the specified position, or 0 if out of range
     */
    private static int getDigitAt(String origin, int position) {
        if (origin.length() > position && position >= 0) {
            return origin.charAt(origin.length() - position - 1) - 48;
        }
        return 0;
    }

}
