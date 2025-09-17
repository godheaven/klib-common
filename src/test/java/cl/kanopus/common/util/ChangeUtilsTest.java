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

import cl.kanopus.common.change.ChangeAction;
import cl.kanopus.common.change.Comparator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class ChangeUtilsTest {

    @Test
    void checkChangeOnList() {

        List<ExampleTO> original = Arrays.asList(
                new ExampleTO(1, "example1"), //NONE
                new ExampleTO(2, "example2"), //DELETE
                new ExampleTO(3, "example3")); //UPDATE

        List<ExampleTO> target = Arrays.asList(
                new ExampleTO(1, "example1"), //NONE
                //new ExampleTO(2, "example2"), //DELETE
                new ExampleTO(3, "example3-updated"), //UPDATE
                new ExampleTO(4, "example4")); //CREATE

        List<Comparator<ExampleTO>> merges = ChangeUtils.checkChangeOnList(original, target);
        Assertions.assertEquals(4, merges.size());
        Assertions.assertEquals(ChangeAction.NONE, merges.get(0).getAction());
        Assertions.assertEquals(ChangeAction.DELETE, merges.get(1).getAction());
        Assertions.assertEquals(ChangeAction.UPDATE, merges.get(2).getAction());
        Assertions.assertEquals(ChangeAction.CREATE, merges.get(3).getAction());

    }

    @Test
    void checkChangeOnListString() {

        List<String> original = Arrays.asList("example1", "example2", "example3");
        List<String> target = Arrays.asList("example3", "example4");

        List<Comparator<String>> merges = ChangeUtils.checkChangeOnList(original, target);
        Assertions.assertEquals(4, merges.size());

        HashMap<String, ChangeAction> map = new HashMap();
        map.put(merges.get(0).getValue(), merges.get(0).getAction());
        map.put(merges.get(1).getValue(), merges.get(1).getAction());
        map.put(merges.get(2).getValue(), merges.get(2).getAction());
        map.put(merges.get(3).getValue(), merges.get(3).getAction());

        Assertions.assertEquals(ChangeAction.DELETE, map.get("example1"));
        Assertions.assertEquals(ChangeAction.DELETE, map.get("example2"));
        Assertions.assertEquals(ChangeAction.NONE, map.get("example3"));
        Assertions.assertEquals(ChangeAction.CREATE, map.get("example4"));

    }

    @Test
    void checkChangeUpdateWithList() {

        ExampleTO original = new ExampleTO(1, "example1");
        original.setValues(Arrays.asList("eleven", "one", "two"));

        ExampleTO updated = new ExampleTO(1, "example2");

        ExampleTO updated2 = new ExampleTO(1, "example2");
        updated2.setValues(Arrays.asList("one", "three", "two"));

        ExampleTO updated3 = new ExampleTO(1, "example1");
        updated3.setValues(Arrays.asList("one", "three", "two"));

        ExampleTO nullable = null;

        Assertions.assertEquals(ChangeAction.DELETE, ChangeUtils.checkChange(original, nullable));
        Assertions.assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(original, updated));
        Assertions.assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(original, updated2));
        Assertions.assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(original, updated3));

    }

    @Test
    void checkChangeUpdate() throws IllegalArgumentException {

        ExampleTO original = new ExampleTO(1, "example1");
        original.setValues(Arrays.asList("eleven", "one", "two"));

        ExampleTO updated = new ExampleTO(1, "example2");
        ExampleTO nullable = null;

        Assertions.assertEquals(ChangeAction.DELETE, ChangeUtils.checkChange(original, nullable));
        Assertions.assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(original, updated));
    }

    @Test
    void checkChangeUpdateWithChild() throws IllegalArgumentException {

        ExampleTO original = new ExampleTO(1, "example1");
        original.setValues(Arrays.asList("eleven", "one", "two"));
        original.setOther(new ExampleTO.OtherTO(1, "role1"));

        ExampleTO updated1 = new ExampleTO(1, "example1");
        updated1.setValues(Arrays.asList("eleven", "one", "two"));
        updated1.setOther(new ExampleTO.OtherTO(1, "role1-updated"));

        ExampleTO updated2 = new ExampleTO(1, "example1");
        updated2.setValues(Arrays.asList("eleven", "one", "two"));
        updated2.setOther(null);

        Assertions.assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(original, updated1));
        Assertions.assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(original, updated2));
    }

    @Test
    void checkChangeCreate() throws IllegalArgumentException {

        ExampleTO original = new ExampleTO(1, "example1");
        original.setValues(Arrays.asList("one", "two"));

        ExampleTO nullable = null;

        Assertions.assertEquals(ChangeAction.NONE, ChangeUtils.checkChange(original, original));
        Assertions.assertEquals(ChangeAction.CREATE, ChangeUtils.checkChange(nullable, original));

    }

    @Test
    void checkChangeDelete() throws IllegalArgumentException {

        ExampleTO original = new ExampleTO(1, "example1");
        original.setValues(Arrays.asList("one", "two"));

        ExampleTO nullable = null;

        Assertions.assertEquals(ChangeAction.DELETE, ChangeUtils.checkChange(original, nullable));

    }

    public static class ExampleTO {

        private long id;
        private String name;
        private List<String> values;
        private OtherTO other;
        private List<OtherTO> complex;

        public ExampleTO() {
        }

        public ExampleTO(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

        public OtherTO getOther() {
            return other;
        }

        public void setOther(OtherTO other) {
            this.other = other;
        }

        public List<OtherTO> getComplex() {
            return complex;
        }

        public void setComplex(List<OtherTO> complex) {
            this.complex = complex;
        }

        public static class OtherTO {

            private long id;
            private String data;

            public OtherTO(long id, String data) {
                this.id = id;
                this.data = data;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getData() {
                return data;
            }

            public void setData(String data) {
                this.data = data;
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 19 * hash + (int) (getId() ^ (getId() >>> 32));
                return hash;
            }

        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 19 * hash + (int) (getId() ^ (getId() >>> 32));
            return hash;
        }

    }

}
