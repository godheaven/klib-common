package cl.kanopus.common.util;

import java.time.LocalDate;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class GsonUtilsTest {

    public GsonUtilsTest() {
    }

    @Test
    public void testSomeMethod() throws Exception {
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
