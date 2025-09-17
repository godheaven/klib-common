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
package cl.kanopus.common.util.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class PaddingDecimalFormat extends DecimalFormat {

    public PaddingDecimalFormat(String pattern, DecimalFormatSymbols symbols) {
        super(pattern, symbols);
    }

    public String format(double number, int padding) {
        StringBuilder sb = new StringBuilder();
        String num = super.format(number);
        if (padding > 0 && padding > num.length()) {
            int addSpace = padding - num.length();
            for (int i = 0; i < addSpace; i++) {
                sb.append(' ');
            }
        }
        sb.append(num);
        return sb.toString();
    }
}
