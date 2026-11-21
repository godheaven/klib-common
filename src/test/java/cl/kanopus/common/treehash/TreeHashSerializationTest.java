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
package cl.kanopus.common.treehash;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class TreeHashSerializationTest {

    private static TreeHash roundTrip(TreeHash tree) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(tree);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            return (TreeHash) in.readObject();
        }
    }

    @Test
    void treeIsSerializableAndListIsRebuilt() throws Exception {
        TreeHash tree = new TreeHash();
        tree.add(new NodeHash("p", "data-p", TreeHash.KEY_ROOT, "t"));
        tree.add(new NodeHash("c", "data-c", "p", "t"));
        assertEquals(2, tree.getNodeList().size());

        TreeHash copy = roundTrip(tree);
        List<NodeHash> nodes = copy.getNodeList();
        assertNotNull(nodes);
        assertEquals(2, nodes.size());
        assertEquals("data-c", copy.getNodeHash("c").getData());
        assertNotNull(copy.getNodeVisibleList());
    }

    @Test
    void removeNodeBranchWhenOnlyChildOfRoot() {
        TreeHash tree = new TreeHash();
        tree.add(new NodeHash("p", null, TreeHash.KEY_ROOT, "t"));
        tree.add(new NodeHash("c", null, "p", "t"));

        tree.removeNodeBranch("c");
        assertNull(tree.getNodeHash("c"));
        assertNull(tree.getNodeHash("p"));
        assertNotNull(tree.getNodeHash(TreeHash.KEY_ROOT));
        assertTrue(tree.getNodeHash(TreeHash.KEY_ROOT).getKeyChildren().isEmpty());
        assertTrue(tree.getNodeList().isEmpty());

        // removing the root or an unknown key does nothing
        tree.removeNodeBranch(TreeHash.KEY_ROOT);
        tree.removeNodeBranch("unknown");
        assertNotNull(tree.getNodeHash(TreeHash.KEY_ROOT));
    }

    @Test
    void removeNodeBranchKeepsParentWithSiblings() {
        TreeHash tree = new TreeHash();
        tree.add(new NodeHash("p", null, TreeHash.KEY_ROOT, "t"));
        tree.add(new NodeHash("c1", null, "p", "t"));
        tree.add(new NodeHash("c2", null, "p", "t"));
        tree.removeNodeBranch("c1");
        assertNull(tree.getNodeHash("c1"));
        assertNotNull(tree.getNodeHash("p"));
        assertEquals(List.of("c2"), tree.getNodeHash("p").getKeyChildren());
    }

    @Test
    void visibleInSearchListIsNotConfusedWithVisibleList() {
        TreeHash tree = new TreeHash();
        NodeHash a = new NodeHash("a", null, TreeHash.KEY_ROOT, "t");
        NodeHash b = new NodeHash("b", null, TreeHash.KEY_ROOT, "t");
        b.setVisibleInSearch(true);
        tree.add(a);
        tree.add(b);
        assertEquals(2, tree.getNodeVisibleList().size());
        assertEquals(List.of(b), tree.getNodeVisibleInSearchList());
        assertEquals(2, tree.getNodeVisibleList().size());
    }
}
