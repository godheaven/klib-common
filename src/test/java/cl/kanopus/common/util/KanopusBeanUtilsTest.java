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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

class KanopusBeanUtilsTest {

    @SuppressWarnings("unused")
    public static class A {
        private String name;
        private int value;
        private List<B> list = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public List<B> getList() { return list; }
        public void setList(List<B> list) { this.list = list; }
    }

    @SuppressWarnings("unused")
    public static class B {
        private String s;
        public String getS() { return s; }
        public void setS(String s) { this.s = s; }
    }

    @Test
    void copyPropertiesSimple() {
        A a = new A();
        a.setName("x");
        a.setValue(5);
        List<B> lb = new ArrayList<>();
        B b = new B(); b.setS("s"); lb.add(b);
        a.setList(lb);

        A out = KanopusBeanUtils.copyProperties(a, A.class);
        assertEquals("x", out.getName());
        assertEquals(5, out.getValue());
        assertNotNull(out.getList());
        assertEquals(1, out.getList().size());
    }

    @Test
    void copyListAndPaginator() {
        A a1 = new A(); a1.setName("one"); a1.setValue(1);
        A a2 = new A(); a2.setName("two"); a2.setValue(2);
        List<A> source = new ArrayList<>(); source.add(a1); source.add(a2);

        List<A> copied = KanopusBeanUtils.copyList(source, A.class);
        assertEquals(2, copied.size());
        assertEquals("one", copied.get(0).getName());

        // paginator
        cl.kanopus.common.data.Paginator<A> p = new cl.kanopus.common.data.Paginator<>();
        p.getRecords().addAll(source);
        p.setTotalRecords(2L);

        cl.kanopus.common.data.Paginator<A> out = KanopusBeanUtils.copyPaginator(p, A.class);
        assertEquals(2L, out.getTotalRecords());
        assertEquals(2, out.getRecords().size());
    }

    @Test
    void byteArrayToImageBase64Conversion() {
        // create proper JavaBean classes with getters/setters so BeanUtils can copy properties
        class Src {
            private byte[] data;
            public byte[] getData() { return data; }
            public void setData(byte[] data) { this.data = data; }
        }
        class Dst {
            private cl.kanopus.common.data.ImageBase64 data;
            public cl.kanopus.common.data.ImageBase64 getData() { return data; }
            public void setData(cl.kanopus.common.data.ImageBase64 data) { this.data = data; }
        }

        Src s = new Src(); s.setData(new byte[]{10,20,30});
        Dst d = new Dst(); d.setData(new cl.kanopus.common.data.ImageBase64());

        KanopusBeanUtils.copyProperties(s, d);
        assertNotNull(d.getData());
        assertNotNull(d.getData().getData());
        assertArrayEquals(s.getData(), java.util.Base64.getDecoder().decode(d.getData().getData()));
    }
}
