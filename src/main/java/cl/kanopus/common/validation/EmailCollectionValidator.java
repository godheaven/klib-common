package cl.kanopus.common.validation;

import cl.kanopus.common.util.Utils;
import cl.kanopus.common.validation.constraints.EmailCollection;
import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class EmailCollectionValidator implements ConstraintValidator<EmailCollection, Collection<String>> {

    private EmailCollection annotation;

    @Override
    public void initialize(EmailCollection annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(Collection<String> value, ConstraintValidatorContext constraintContext) {

        if (value == null || value.isEmpty()) {
            return true;
        }

        for (String email : value) {
            if (!Utils.isValidEmail(email)) {
                constraintContext.disableDefaultConstraintViolation();
                constraintContext.buildConstraintViolationWithTemplate(String.format(annotation.message(), email)).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
