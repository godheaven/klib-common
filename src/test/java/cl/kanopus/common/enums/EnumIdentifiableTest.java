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
package cl.kanopus.common.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumIdentifiableTest {

    private enum TestEnum implements EnumIdentifiable<Integer> {
        A(1), B(2);
        private final Integer id;

        TestEnum(Integer id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }

    @Test
    void enumIdentifiableReturnsId() {
        assertEquals(1, TestEnum.A.getId());
        assertEquals(2, TestEnum.B.getId());
    }
}
