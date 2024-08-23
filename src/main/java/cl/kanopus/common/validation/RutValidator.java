package cl.kanopus.common.validation;

import cl.kanopus.common.util.Utils;
import cl.kanopus.common.validation.constraints.Rut;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class RutValidator implements ConstraintValidator<Rut, String> {

    private Rut annotation;

    @Override
    public void initialize(Rut annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if (Utils.isNullOrEmpty(object)) {
            return true;
        }

        boolean isValid = Utils.isRut(object);
        if (!isValid) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(String.format(annotation.message(), object)).addConstraintViolation();
        }
        return isValid;
    }

}
