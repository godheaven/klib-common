package cl.kanopus.common.treehash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 *
 * Clase utilitaria que permite generar un arbol de objetos y ademas tiene la
 * capacidad de retornarlos en forma de Lista.
 *
 */
public class TreeHash implements Serializable {

    private enum TYPE_NODE_LIST {

        ALL,
        VISIBLE
    }
    private final Map<String, NodeHash> hash = new HashMap<>();
    private TYPE_NODE_LIST lastRefresh = TYPE_NODE_LIST.ALL;
    private boolean refresh = false;
    private List<NodeHash> nodeList = new ArrayList<>();
    public static final transient String KEY_ROOT = "root";

    public TreeHash() {
        NodeHash root = new NodeHash(KEY_ROOT, null, null);
        add(root);
    }

    public final void add(NodeHash node) {
        if (hash.containsKey(node.getKey())) {
            //Eliminar referencia del antiguo nodo padre (TODO: probar este reemplazo)
            NodeHash oldNode = hash.get(node.getKey());
            NodeHash oldParent = hash.get(oldNode.getKeyParent());
            if (oldParent != null) {
                oldParent.keyChildren.remove(node.getKey());
            }
        }

        if (node.getKeyParent() != null) {
            NodeHash nodeParent = hash.get(node.getKeyParent());
            if (nodeParent != null && !nodeParent.keyChildren.contains(node.getKey())) {
                //Agregamos la key del hijo
                nodeParent.keyChildren.add(node.getKey());
            }
        }

        hash.put(node.getKey(), node);
        refresh = true;
    }

    public final void remove(String keyNode) {
        removeInParent(keyNode);
        removeChildren(keyNode, false);
        refresh = true;
    }

    public final void removeChildren(String keyNode) {
        NodeHash node = hash.get(keyNode);
        if (node != null) {
            removeChildren(keyNode, true);
            node.keyChildren = new ArrayList<>();
        }
        refresh = true;
    }

    public final void removeNodeBranch(String keyNode) {
        NodeHash node = hash.get(keyNode);
        if (node != null) {
            NodeHash nodeParent = hash.get(node.getKeyParent());
            if (nodeParent != null) {
                if (nodeParent.keyChildren.size() == 1) {
                    removeNodeBranch(nodeParent.getKey());
                } else if (nodeParent.keyChildren.size() > 1) {
                    for (int i = 0; i < nodeParent.keyChildren.size(); i++) {
                        //eliminamos la rama completa
                        if (keyNode.equals(nodeParent.keyChildren.get(i))) {
                            remove(keyNode);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void removeInParent(String keyNode) {
        NodeHash node = hash.get(keyNode);
        if (node != null) {
            NodeHash nodeParent = hash.get(node.getKeyParent());
            if (nodeParent != null) {
                for (int i = 0; i < nodeParent.keyChildren.size(); i++) {
                    //eliminamos referencia en nodo padre
                    if (keyNode.equals(nodeParent.keyChildren.get(i))) {
                        nodeParent.keyChildren.remove(i);
                        break;
                    }
                }
            }
        }
    }

    private void removeChildren(String keyNode, boolean root) {
        NodeHash node = hash.get(keyNode);
        if (node != null) {
            //eliminamos nodos hijos recursivamente
            for (String keyChildren : node.keyChildren) {
                removeChildren(keyChildren, false);
            }
        }
        if (!root) {
            hash.remove(keyNode);
        }
    }

    public NodeHash getNodeHash(String key) {
        return hash.get(key);
    }

    private void fillNodeList(String keyNode, int depth, List<NodeHash> newNodeList, boolean onlyVisible) {
        NodeHash node = hash.get(keyNode);
        if (node != null) {
            if (!TreeHash.KEY_ROOT.equals(keyNode)) {
                //Solo se retorna en la listas los nodos distintos al root
                node.setDepth(depth);
                newNodeList.add(node);
                depth++;
            }

            if (onlyVisible && !TreeHash.KEY_ROOT.equals(keyNode)) {
                if (node.isExpanded()) {
                    for (String child : node.keyChildren) {
                        fillNodeList(child, depth, newNodeList, onlyVisible);
                    }
                }
            } else {
                for (String child : node.keyChildren) {
                    fillNodeList(child, depth, newNodeList, onlyVisible);
                }
            }
        }
    }

    /**
     * Metodo que retorna todos los nodos contenidos en el TreeHash
     *
     * @author Pablo Diaz Saavedra (pd47753)
     * @return
     *
     */
    public List<NodeHash> getNodeList() {
        if (refresh || lastRefresh != TYPE_NODE_LIST.ALL) {
            //Refrescamos la lista para retornar los nodos
            List<NodeHash> newNodeList = new ArrayList<>();
            fillNodeList(TreeHash.KEY_ROOT, 0, newNodeList, false);
            nodeList = newNodeList;
            refresh = false;
        }
        lastRefresh = TYPE_NODE_LIST.ALL;
        return nodeList;
    }

    /**
     * Metodo que retorna solo los nodos donde el padre sea visible dentro del
     * TreeHash
     *
     * @author Pablo Diaz Saavedra (pd47753)
     * @return
     *
     */
    public List<NodeHash> getNodeVisibleList() {
        if (refresh || lastRefresh != TYPE_NODE_LIST.VISIBLE) {
            //Refrescamos la lista para retornar los nodos
            List<NodeHash> newNodeList = new ArrayList<>();
            fillNodeList(TreeHash.KEY_ROOT, 0, newNodeList, true);
            nodeList = newNodeList;
            refresh = false;
        }
        lastRefresh = TYPE_NODE_LIST.VISIBLE;
        return nodeList;
    }

    public List<NodeHash> getNodeVisibleInSearchList() {
        if (refresh || lastRefresh != TYPE_NODE_LIST.VISIBLE) {
            //Refrescamos la lista para retornar los nodos
            List<NodeHash> newNodeList = new ArrayList<>();
            fillNodeList(TreeHash.KEY_ROOT, 0, newNodeList, true);

            nodeList = new ArrayList<>();

            for (NodeHash node : newNodeList) {
                if (node.isVisibleInSearch()) {
                    nodeList.add(node);
                }
            }

            refresh = false;
        }
        lastRefresh = TYPE_NODE_LIST.VISIBLE;
        return nodeList;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public void propagateUncheckDown(String keyNode) {
        NodeHash node = this.getNodeHash(keyNode);
        if (node != null) {
            if (node.isSelectable()) {
                node.setSelected(false);
            }
            for (String child : node.keyChildren) {
                propagateUncheckDown(child);
            }
        }
    }

    public void propagateCheckDown(String keyNode) {
        NodeHash node = this.getNodeHash(keyNode);
        if (node != null) {
            if (node.isSelectable()) {
                node.setSelected(true);
            }
            for (String child : node.keyChildren) {
                propagateCheckDown(child);
            }
        }
    }

    public void propagateCheckUp(String keyNode) {
        NodeHash node = this.getNodeHash(keyNode);
        if (node != null) {
            if (node.isSelectable()) {
                node.setSelected(true);
            }
            if (!KEY_ROOT.equals(keyNode)) {
                propagateCheckUp(node.getKeyParent());
            }
        }
    }

}
