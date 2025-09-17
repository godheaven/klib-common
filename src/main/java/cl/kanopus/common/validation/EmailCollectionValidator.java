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
package cl.kanopus.common.validation;

import cl.kanopus.common.util.Utils;
import cl.kanopus.common.validation.constraints.EmailCollection;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

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
