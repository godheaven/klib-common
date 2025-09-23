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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pablo
 */
class TreeHashTest {
    static final String NODE_TYPE = "custom";

    /**
     * Test of propagateCheckUp method, of class TreeHash.
     */
    @Test
    void testPropagateCheckUp() {
        TreeHash tree = new TreeHash();
        NodeHash node = new NodeHash("node0", null, NODE_TYPE);
        NodeHash node1 = new NodeHash("node1", null, NODE_TYPE);
        NodeHash node1_1 = new NodeHash("node1_1", null, node1.getKey(), NODE_TYPE);
        NodeHash node1_2 = new NodeHash("node1_2", null, node1.getKey(), NODE_TYPE);

        node1.setExpanded(true);
        tree.add(node);
        tree.add(node1);
        tree.add(node1_1);
        tree.add(node1_2);
        for (int i = 0; i < 10; i++) {
            NodeHash nodew = new NodeHash("node1_2_" + i, null, node1_2.getKey(), NODE_TYPE);
            tree.add(nodew);
        }

        for (int i = 0; i < 4; i++) {
            NodeHash nodew = new NodeHash("node1_1_" + i, null, node1_1.getKey(), NODE_TYPE);
            tree.add(nodew);
        }

        System.out.println("############### PRINT ALL NODES ########################");

        Assertions.assertNotNull(tree);
        Assertions.assertEquals(4, tree.getNodeVisibleList().size());
        Assertions.assertEquals(18, tree.getNodeList().size());

        for (NodeHash n : tree.getNodeList()) {
            System.out.println("node:" + generateIndent(n.getDepth()) + "" + n.getKey() + "[selecteable:" + n.isSelectable() + ", selected:" + n.isSelected() + "]");
        }

        System.out.println("############### PRINT VISIBLE NODES ########################");
        for (NodeHash n : tree.getNodeVisibleList()) {
            System.out.println("node:" + generateIndent(n.getDepth()) + "" + n.getKey() + "[selecteable:" + n.isSelectable() + ", selected:" + n.isSelected() + "]");
        }

        System.out.println("############### AFTER REMOVE node1_2 PRINT ALL NODES ########################");
        tree.remove("node1_2");

        //tree.printTree();

    }

    private String generateIndent(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("    ");
        }
        return indent.toString();
    }


}
