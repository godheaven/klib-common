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

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintValidatorContext;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnumValueValidatorTest {

    @SuppressWarnings("unused")
    private enum TestEnum implements cl.kanopus.common.enums.EnumIdentifiable<Integer> {
        A(1), B(2);
        private final Integer id;
        TestEnum(Integer id) { this.id = id; }
        @Override public Integer getId() { return id; }
    }

    @Test
    void nullIsValid() {
        EnumValueValidator v = new EnumValueValidator();
        v.initialize(null);
        assertTrue(v.isValid(null, mock(ConstraintValidatorContext.class)));
    }

    @Test
    void validIdReturnsTrue() {
        EnumValueValidator v = new EnumValueValidator();
        cl.kanopus.common.validation.constraints.EnumValue ann = mock(cl.kanopus.common.validation.constraints.EnumValue.class);
        when(ann.type()).thenReturn((Class<?>) TestEnum.class);
        v.initialize(ann);

        // reference enum constant to silence unused-var warnings
        Integer id = TestEnum.A.getId();
        assertTrue(v.isValid(id, mock(ConstraintValidatorContext.class)));
    }

    @Test
    void invalidBuildsViolation() {
        EnumValueValidator v = new EnumValueValidator();
        cl.kanopus.common.validation.constraints.EnumValue ann = mock(cl.kanopus.common.validation.constraints.EnumValue.class);
        when(ann.type()).thenReturn((Class<?>) TestEnum.class);
        when(ann.message()).thenReturn("El valor [%s] no es vlido");
        v.initialize(ann);

        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);

        boolean ok = v.isValid("nope", ctx);
        assertFalse(ok);
        verify(ctx).disableDefaultConstraintViolation();
        verify(ctx).buildConstraintViolationWithTemplate(anyString());
    }
}
