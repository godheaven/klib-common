package cl.kanopus.common.validation.constraints;

import cl.kanopus.common.validation.RutValidator;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = RutValidator.class)
@Documented
public @interface Rut {

    String message() default "El rut [%s] no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
