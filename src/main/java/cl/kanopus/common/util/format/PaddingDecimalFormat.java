package cl.kanopus.common.util.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 *
 */
public class PaddingDecimalFormat extends DecimalFormat {

    public PaddingDecimalFormat(String pattern, DecimalFormatSymbols symbols) {
        super(pattern, symbols);
    }

    public String format(double number, int padding) {
        StringBuilder sb = new StringBuilder();
        String num = super.format(number);
        if (padding > 0 && padding > num.length()) {
            int addSpace = padding - num.length();
            for (int i = 0; i < addSpace; i++) {
                sb.append(' ');
            }
        }
        sb.append(num);
        return sb.toString();
    }
}
