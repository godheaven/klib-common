package cl.kanopus.common.util.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 *
 */
public class RoundNumberFormat {

    private final DecimalFormat formater;

    public RoundNumberFormat(String pattern, DecimalFormatSymbols symbols) {
        this.formater = new DecimalFormat(pattern, symbols);
    }

    public String format(double number) {
        return formater.format(Math.round(number));
    }
}
