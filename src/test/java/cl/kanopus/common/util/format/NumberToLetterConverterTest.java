package cl.kanopus.common.util.format;

import cl.kanopus.common.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class NumberToLetterConverterTest {

    @Test
    public void testPrettyFileSize() {

  
        Assertions.assertEquals("MIL PESOS", NumberToLetterConverter.convertNumberToLetter((double) 1000));
        Assertions.assertEquals("CUARENTA Y CINCO MIL PESOS", NumberToLetterConverter.convertNumberToLetter((double) 45000));
        Assertions.assertEquals("TRESCIENTOS MIL OCHOCIENTOS NOVENTA Y SIETE PESOS", NumberToLetterConverter.convertNumberToLetter((double) 300897));
        Assertions.assertEquals("UN MILLON QUINIENTOS PESOS", NumberToLetterConverter.convertNumberToLetter((double) 1000500));
        Assertions.assertEquals("CINCUENTA PESOS", NumberToLetterConverter.convertNumberToLetter((double) 50));
    }
 
}
