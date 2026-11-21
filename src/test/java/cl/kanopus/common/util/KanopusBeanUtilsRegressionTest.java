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

import static org.junit.jupiter.api.Assertions.*;

import cl.kanopus.common.enums.EnumIdentifiable;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class KanopusBeanUtilsRegressionTest {

    public enum SourceStatus implements EnumIdentifiable<String> {
        ACTIVE("A"),
        INACTIVE("I");

        private final String id;

        SourceStatus(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    public enum TargetStatus implements EnumIdentifiable<String> {
        ENABLED("A"),
        DISABLED("I");

        private final String id;

        TargetStatus(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    public static class Item {
        private String name;

        public Item() {
        }

        public Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ItemCopy {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Base {
        private List<Item> items = new ArrayList<>();

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }
    }

    public static class Source extends Base {
        private SourceStatus status;
        private SourceStatus sameStatus;
        private Item[] array;
        private int[] numbers;
        private List<Integer> numberList;
        private List<Item> listForArray;

        public SourceStatus getStatus() {
            return status;
        }

        public void setStatus(SourceStatus status) {
            this.status = status;
        }

        public SourceStatus getSameStatus() {
            return sameStatus;
        }

        public void setSameStatus(SourceStatus sameStatus) {
            this.sameStatus = sameStatus;
        }

        public Item[] getArray() {
            return array;
        }

        public void setArray(Item[] array) {
            this.array = array;
        }

        public int[] getNumbers() {
            return numbers;
        }

        public void setNumbers(int[] numbers) {
            this.numbers = numbers;
        }

        public List<Integer> getNumberList() {
            return numberList;
        }

        public void setNumberList(List<Integer> numberList) {
            this.numberList = numberList;
        }

        public List<Item> getListForArray() {
            return listForArray;
        }

        public void setListForArray(List<Item> listForArray) {
            this.listForArray = listForArray;
        }
    }

    public static class TargetBase {
        private List<ItemCopy> items;

        public List<ItemCopy> getItems() {
            return items;
        }

        public void setItems(List<ItemCopy> items) {
            this.items = items;
        }
    }

    public static class Target extends TargetBase {
        private TargetStatus status;
        private SourceStatus sameStatus;
        private ItemCopy[] array;
        private int[] numbers;
        private int[] numberList;
        private ItemCopy[] listForArray;

        public TargetStatus getStatus() {
            return status;
        }

        public void setStatus(TargetStatus status) {
            this.status = status;
        }

        public SourceStatus getSameStatus() {
            return sameStatus;
        }

        public void setSameStatus(SourceStatus sameStatus) {
            this.sameStatus = sameStatus;
        }

        public ItemCopy[] getArray() {
            return array;
        }

        public void setArray(ItemCopy[] array) {
            this.array = array;
        }

        public int[] getNumbers() {
            return numbers;
        }

        public void setNumbers(int[] numbers) {
            this.numbers = numbers;
        }

        public int[] getNumberList() {
            return numberList;
        }

        public void setNumberList(int[] numberList) {
            this.numberList = numberList;
        }

        public ItemCopy[] getListForArray() {
            return listForArray;
        }

        public void setListForArray(ItemCopy[] listForArray) {
            this.listForArray = listForArray;
        }
    }

    public static class NoDefaultConstructor {
        public NoDefaultConstructor(String x) {
            // no-args constructor intentionally missing
        }
    }

    private static Source source() {
        Source s = new Source();
        s.setStatus(SourceStatus.INACTIVE);
        s.setSameStatus(SourceStatus.ACTIVE);
        s.setArray(new Item[]{new Item("a1"), new Item("a2")});
        s.setNumbers(new int[]{1, 2, 3});
        s.setNumberList(List.of(7, 8));
        s.setListForArray(List.of(new Item("l1")));
        s.getItems().add(new Item("inherited"));
        return s;
    }

    @Test
    void copiesEnumsBetweenDifferentAndSameTypes() {
        Target t = KanopusBeanUtils.copyProperties(source(), Target.class);
        assertEquals(TargetStatus.DISABLED, t.getStatus());
        assertEquals(SourceStatus.ACTIVE, t.getSameStatus());
    }

    @Test
    void copiesArraysListsAndPrimitiveArrays() {
        Target t = KanopusBeanUtils.copyProperties(source(), Target.class);
        assertNotNull(t.getArray());
        assertEquals(2, t.getArray().length);
        assertEquals("a1", t.getArray()[0].getName());
        assertEquals("a2", t.getArray()[1].getName());
        assertArrayEquals(new int[]{1, 2, 3}, t.getNumbers());
        assertArrayEquals(new int[]{7, 8}, t.getNumberList());
        assertEquals(1, t.getListForArray().length);
        assertEquals("l1", t.getListForArray()[0].getName());
    }

    @Test
    void usesGenericTypeOfInheritedFields() {
        Target t = KanopusBeanUtils.copyProperties(source(), Target.class);
        assertNotNull(t.getItems());
        assertEquals(1, t.getItems().size());
        assertInstanceOf(ItemCopy.class, t.getItems().get(0));
        assertEquals("inherited", t.getItems().get(0).getName());
    }

    @Test
    void topLevelArrayToArrayCopy() {
        Item[] src = {new Item("x"), new Item("y"), new Item("z")};
        ItemCopy[] dst = new ItemCopy[2];
        KanopusBeanUtils.copyProperties(src, dst);
        assertEquals("x", dst[0].getName());
        assertEquals("y", dst[1].getName());

        ItemCopy[] bigger = new ItemCopy[5];
        KanopusBeanUtils.copyProperties(src, bigger);
        assertEquals("z", bigger[2].getName());
        assertNull(bigger[3]);

        int[] ints = new int[3];
        KanopusBeanUtils.copyProperties(new int[]{4, 5, 6}, ints);
        assertArrayEquals(new int[]{4, 5, 6}, ints);

        long[] longs = new long[2];
        KanopusBeanUtils.copyProperties(new Integer[]{1, null}, longs);
        assertArrayEquals(new long[]{1, 0}, longs);
    }

    @Test
    void topLevelListToArrayCopy() {
        ItemCopy[] dst = new ItemCopy[1];
        KanopusBeanUtils.copyProperties(List.of(new Item("q"), new Item("r")), dst);
        assertEquals("q", dst[0].getName());

        int[] ints = new int[2];
        KanopusBeanUtils.copyProperties(List.of(9, 10), ints);
        assertArrayEquals(new int[]{9, 10}, ints);
    }

    @Test
    void copyListHandlesNullElements() {
        List<Item> list = new ArrayList<>();
        list.add(null);
        list.add(new Item("n"));
        List<ItemCopy> out = KanopusBeanUtils.copyList(list, ItemCopy.class);
        assertEquals(2, out.size());
        assertNull(out.get(0));
        assertEquals("n", out.get(1).getName());
    }

    @Test
    void copyPropertiesWithoutNoArgsConstructorGivesClearError() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> KanopusBeanUtils.copyProperties(new Item("x"), NoDefaultConstructor.class));
        assertTrue(ex.getMessage().contains(NoDefaultConstructor.class.getName()));
    }

    @Test
    void mergeListAndNewValues() {
        List<String> merged = KanopusBeanUtils.mergeList(null, List.of("a", "b"));
        assertEquals(List.of("a", "b"), merged);
        assertEquals(List.of("a", "b", "c"), KanopusBeanUtils.mergeList(merged, List.of("b", "c")));

        Item src = new Item("old");
        ItemCopy dst = new ItemCopy();
        KanopusBeanUtils.copyProperties(src, dst, new KanopusBeanUtils.BeanProperty("name", String.class, "new"));
        assertEquals("new", dst.getName());
    }
}
