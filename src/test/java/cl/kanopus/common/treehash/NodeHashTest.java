/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 * 
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 - 2026 Pablo Díaz Saavedra
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
package cl.kanopus.common.treehash;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NodeHashTest {

    @Test
    void gettersAndSetters_work() {
        NodeHash n = new NodeHash("k1", "data", "type");
        Assertions.assertEquals("k1", n.getKey());
        Assertions.assertEquals("type", n.getType());

        n.setKey("k2");
        n.setKeyParent("parent");
        n.setData("d2");
        n.setDepth(5);
        n.setExpanded(true);
        n.setSelected(true);
        n.setOriginalSelected(true);
        n.setSelectable(false);
        n.setHighLight(true);
        n.setVisibleInSearch(true);

        Assertions.assertEquals("k2", n.getKey());
        Assertions.assertEquals("parent", n.getKeyParent());
        Assertions.assertEquals("d2", n.getData());
        Assertions.assertEquals(5, n.getDepth());
        Assertions.assertTrue(n.isExpanded());
        Assertions.assertTrue(n.isSelected());
        Assertions.assertTrue(n.isOriginalSelected());
        Assertions.assertFalse(n.isSelectable());
        Assertions.assertTrue(n.isHighLight());
        Assertions.assertTrue(n.isVisibleInSearch());
    }

    @Test
    void hasChild_reflectsChildrenList() {
        NodeHash n = new NodeHash("root", null, "t");
        Assertions.assertFalse(n.isHasChild());
        n.getKeyChildren().add("c1");
        Assertions.assertTrue(n.isHasChild());
    }
}

