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

import java.util.List;

class TreeHashTest {

    @Test
    void addAndGetNodeHash_and_remove() {
        TreeHash tree = new TreeHash();
        NodeHash parent = new NodeHash("p1", null, TreeHash.KEY_ROOT, "t");
        NodeHash child = new NodeHash("c1", null, "p1", "t");

        tree.add(parent);
        tree.add(child);

        Assertions.assertNotNull(tree.getNodeHash("p1"));
        Assertions.assertNotNull(tree.getNodeHash("c1"));
        Assertions.assertTrue(tree.getNodeHash("p1").getKeyChildren().contains("c1"));

        tree.remove("c1");
        Assertions.assertNull(tree.getNodeHash("c1"));
        Assertions.assertFalse(tree.getNodeHash("p1").getKeyChildren().contains("c1"));
    }

    @Test
    void removeChildren_clearsDescendants() {
        TreeHash tree = new TreeHash();
        NodeHash p = new NodeHash("p2", null, TreeHash.KEY_ROOT, "t");
        NodeHash c1 = new NodeHash("c2_1", null, "p2", "t");
        NodeHash c2 = new NodeHash("c2_2", null, "p2", "t");
        tree.add(p);
        tree.add(c1);
        tree.add(c2);

        tree.removeChildren("p2");

        Assertions.assertNull(tree.getNodeHash("c2_1"));
        Assertions.assertNull(tree.getNodeHash("c2_2"));
        Assertions.assertTrue(tree.getNodeHash("p2").getKeyChildren().isEmpty());
    }
    
    @Test
    void getNodeList_and_visibleLists_and_depths() {
        TreeHash tree = new TreeHash();
        NodeHash p = new NodeHash("p3", null, TreeHash.KEY_ROOT, "t");
        NodeHash c = new NodeHash("c3", null, "p3", "t");
        tree.add(p);
        tree.add(c);

        // By default, parent is not expanded; getNodeList includes both nodes regardless
        tree.setRefresh(true);
        List<NodeHash> all = tree.getNodeList();
        // should contain p3 and c3 (order: p3 then c3) and depths set
        boolean foundP = false, foundC = false;
        for (NodeHash n : all) {
            if ("p3".equals(n.getKey())) {
                foundP = true;
                Assertions.assertEquals(0, n.getDepth());
            }
            if ("c3".equals(n.getKey())) {
                foundC = true;
                Assertions.assertEquals(1, n.getDepth());
            }
        }
        Assertions.assertTrue(foundP && foundC);

        // For visible list: if parent not expanded, children should not be returned
        tree.setRefresh(true);
        List<NodeHash> vis1 = tree.getNodeVisibleList();
        Assertions.assertTrue(vis1.stream().anyMatch(n -> "p3".equals(n.getKey())));
        Assertions.assertFalse(vis1.stream().anyMatch(n -> "c3".equals(n.getKey())));

        // expand parent and refresh
        tree.getNodeHash("p3").setExpanded(true);
        tree.setRefresh(true);
        List<NodeHash> vis2 = tree.getNodeVisibleList();
        Assertions.assertTrue(vis2.stream().anyMatch(n -> "c3".equals(n.getKey())));

        // visible-in-search filter
        tree.getNodeHash("c3").setVisibleInSearch(true);
        tree.setRefresh(true);
        List<NodeHash> search = tree.getNodeVisibleInSearchList();
        Assertions.assertEquals(1, search.size());
        Assertions.assertEquals("c3", search.get(0).getKey());
    }

    @Test
    void propagate_check_and_uncheck() {
        TreeHash tree = new TreeHash();
        NodeHash p = new NodeHash("p4", null, TreeHash.KEY_ROOT, "t");
        NodeHash c = new NodeHash("c4", null, "p4", "t");
        NodeHash g = new NodeHash("g4", null, "c4", "t");
        tree.add(p);
        tree.add(c);
        tree.add(g);

        // propagate check down
        tree.propagateCheckDown("p4");
        Assertions.assertTrue(tree.getNodeHash("p4").isSelected());
        Assertions.assertTrue(tree.getNodeHash("c4").isSelected());
        Assertions.assertTrue(tree.getNodeHash("g4").isSelected());

        // propagate uncheck down
        tree.propagateUncheckDown("p4");
        Assertions.assertFalse(tree.getNodeHash("p4").isSelected());
        Assertions.assertFalse(tree.getNodeHash("c4").isSelected());
        Assertions.assertFalse(tree.getNodeHash("g4").isSelected());

        // propagate check up from leaf
        tree.propagateCheckUp("g4");
        Assertions.assertTrue(tree.getNodeHash("g4").isSelected());
        Assertions.assertTrue(tree.getNodeHash("c4").isSelected());
        Assertions.assertTrue(tree.getNodeHash("p4").isSelected());
    }
}

