package cl.kanopus.common.annotation.impl;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 * @company Kanopus.cl
 */
public class ToLowerCaseConverter extends StdConverter<String, String> {

    @Override
    public String convert(String value) {
        return (value == null) ? null : value.toLowerCase().trim();
    }
}
