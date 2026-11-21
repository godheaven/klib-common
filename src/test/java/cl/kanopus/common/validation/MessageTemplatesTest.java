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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import cl.kanopus.common.validation.constraints.EmailCollection;
import cl.kanopus.common.validation.constraints.EnumValue;
import cl.kanopus.common.validation.constraints.Gtin;
import cl.kanopus.common.validation.constraints.Rut;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class MessageTemplatesTest {

    private static final String MALICIOUS = "${''.getClass()}{javax.validation.constraints.NotNull.message}\\";
    private static final String ESCAPED = "\\$\\{''.getClass()\\}\\{javax.validation.constraints.NotNull.message\\}\\\\";

    @Test
    void escapesInterpolationCharacters() {
        assertEquals(ESCAPED, MessageTemplates.escape(MALICIOUS));
        assertEquals("plain 100%", MessageTemplates.escape("plain 100%"));
        assertNull(MessageTemplates.escape(null));
        assertEquals("v [a\\{b\\}]", MessageTemplates.format("v [%s]", "a{b}"));
        assertEquals("100%", MessageTemplates.format("%s", "100%"));
    }

    private static ConstraintValidatorContext context() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);
        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));
        return ctx;
    }

    private static String capturedTemplate(ConstraintValidatorContext ctx) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).buildConstraintViolationWithTemplate(captor.capture());
        return captor.getValue();
    }

    @Test
    void rutValidatorDoesNotInterpolateUserValue() {
        Rut ann = mock(Rut.class);
        when(ann.message()).thenReturn("El rut [%s] no es válido");
        RutValidator v = new RutValidator();
        v.initialize(ann);
        ConstraintValidatorContext ctx = context();
        assertFalse(v.isValid(MALICIOUS, ctx));
        assertEquals("El rut [" + ESCAPED + "] no es válido", capturedTemplate(ctx));
    }

    @Test
    void gtinValidatorDoesNotInterpolateUserValue() {
        Gtin ann = mock(Gtin.class);
        when(ann.message()).thenReturn("GTIN [%s]");
        GtinValidator v = new GtinValidator();
        v.initialize(ann);
        ConstraintValidatorContext ctx = context();
        assertFalse(v.isValid(MALICIOUS, ctx));
        assertEquals("GTIN [" + ESCAPED + "]", capturedTemplate(ctx));
    }

    @Test
    void emailCollectionValidatorDoesNotInterpolateUserValue() {
        EmailCollection ann = mock(EmailCollection.class);
        when(ann.message()).thenReturn("El email [%s] no es válido");
        EmailCollectionValidator v = new EmailCollectionValidator();
        v.initialize(ann);
        ConstraintValidatorContext ctx = context();
        assertFalse(v.isValid(List.of("ok@example.com", MALICIOUS), ctx));
        assertEquals("El email [" + ESCAPED + "] no es válido", capturedTemplate(ctx));
    }

    enum Color implements cl.kanopus.common.enums.EnumIdentifiable<String> {
        RED;

        @Override
        public String getId() {
            return "R";
        }
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void enumValueValidatorDoesNotInterpolateUserValue() {
        EnumValue ann = mock(EnumValue.class);
        when(ann.message()).thenReturn("El valor [%s] no es válido");
        when(ann.type()).thenReturn((Class) Color.class);
        EnumValueValidator v = new EnumValueValidator();
        v.initialize(ann);
        ConstraintValidatorContext ctx = context();
        assertFalse(v.isValid(MALICIOUS, ctx));
        assertEquals("El valor [" + ESCAPED + "] no es válido", capturedTemplate(ctx));
        assertTrue(v.isValid("R", context()));
    }
}
