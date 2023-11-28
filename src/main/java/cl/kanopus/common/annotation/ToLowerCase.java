package cl.kanopus.common.annotation;

import cl.kanopus.common.annotation.impl.ToLowerCaseConverter;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 * @company Kanopus.cl
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(converter = ToLowerCaseConverter.class)
@JsonDeserialize(converter = ToLowerCaseConverter.class)
public @interface ToLowerCase {
}
