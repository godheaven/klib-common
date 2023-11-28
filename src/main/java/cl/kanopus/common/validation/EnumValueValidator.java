package cl.kanopus.common.validation;

import cl.kanopus.common.util.Utils;
import cl.kanopus.common.validation.constraints.EnumValue;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private EnumValue annotation;

    @Override
    public void initialize(EnumValue annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }

        Object parseValue = Utils.parseEnum(annotation.type(), object);
        boolean isValid = parseValue != null;
        if (!isValid) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(String.format(annotation.message(), object)).addConstraintViolation();
        }
        return isValid;
    }

}
