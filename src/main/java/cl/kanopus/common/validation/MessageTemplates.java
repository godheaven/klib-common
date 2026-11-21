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

/**
 * Helper to build constraint violation templates that include user supplied values without allowing message/EL injection.
 */
final class MessageTemplates {

    private MessageTemplates() {
    }

    /**
     * Formats {@code message} (a {@link String#format(String, Object...)} pattern) with the given value, escaping the characters that have a special meaning for Jakarta Validation message interpolation ({@code \ { } $}) so the value is always rendered literally.
     *
     * @param message
     *            message pattern, e.g. {@code "El rut [%s] no es válido"}
     * @param value
     *            user supplied value
     * @return a safe template
     */
    static String format(String message, Object value) {
        return String.format(message, escape(value));
    }

    static String escape(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        StringBuilder sb = new StringBuilder(text.length() + 8);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' || c == '{' || c == '}' || c == '$') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
