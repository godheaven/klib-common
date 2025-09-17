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
package cl.kanopus.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;

class GsonUtilsTest {

    public GsonUtilsTest() {
    }

    @Test
    void testSomeMethod() throws Exception {
        LocalDate localdate = Utils.getLocalDate("16/03/2021 00:00:00", "dd/MM/yyyy HH:mm:ss");
        java.sql.Date sql = java.sql.Date.valueOf(localdate);

        TestDate test = new TestDate();
        test.setFecha1(Utils.getDate("16/03/2021 0:00:00"));
        test.setFecha2(Utils.getDate("16/03/2021"));
        test.setFecha3(Utils.getDate("16/03/2021 11:43:00"));
        test.setFechaSql(sql);

        String result = GsonUtils.custom.toJson(test);
        String expected = "{\"fecha1\":\"2021-03-16T00:00:00\",\"fecha2\":\"2021-03-16T00:00:00\",\"fecha3\":\"2021-03-16T00:00:00\",\"fechaSql\":\"2021-03-16T00:00:00\"}";

        Assertions.assertEquals(expected, result);
    }

    public static class TestDate {

        private Date fecha1;
        private Date fecha2;
        private Date fecha3;
        private java.sql.Date fechaSql;

        public Date getFecha1() {
            return fecha1;
        }

        public void setFecha1(Date fecha1) {
            this.fecha1 = fecha1;
        }

        public Date getFecha2() {
            return fecha2;
        }

        public void setFecha2(Date fecha2) {
            this.fecha2 = fecha2;
        }

        public Date getFecha3() {
            return fecha3;
        }

        public void setFecha3(Date fecha3) {
            this.fecha3 = fecha3;
        }

        public java.sql.Date getFechaSql() {
            return fechaSql;
        }

        public void setFechaSql(java.sql.Date fechaSql) {
            this.fechaSql = fechaSql;
        }

    }
}
