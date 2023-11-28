package cl.kanopus.common.treehash;

import org.junit.jupiter.api.Test;

/**
 *
 * @author pablo
 */
public class TreeHashTest {

    /**
     * Test of propagateCheckUp method, of class TreeHash.
     */
    @Test
    public void testPropagateCheckUp() {
        String NODE_TYPE = "custom";
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
