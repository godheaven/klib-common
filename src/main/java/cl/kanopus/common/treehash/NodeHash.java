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

import static cl.kanopus.common.treehash.TreeHash.KEY_ROOT;
import java.util.ArrayList;
import java.util.List;

public class NodeHash {

    private String key;
    private String keyParent;
    private Object data;
    private int depth;
    private boolean expanded;
    private String type;
    private boolean selected;
    private boolean originalSelected;
    private boolean selectable = true;
    private boolean highLight;
    private boolean visibleInSearch;

    List<String> keyChildren = new ArrayList<>();

    public NodeHash(String key, Object data, String type) {
        this.key = key;
        this.data = data;
        this.keyParent = KEY_ROOT;
        this.type = type;
    }

    public NodeHash(String key, Object data, String keyParent, String type) {
        this.key = key;
        this.data = data;
        this.keyParent = keyParent;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyParent() {
        return keyParent;
    }

    public void setKeyParent(String keyParent) {
        this.keyParent = keyParent;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHasChild() {
        return !keyChildren.isEmpty();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isOriginalSelected() {
        return originalSelected;
    }

    public void setOriginalSelected(boolean originalSelected) {
        this.originalSelected = originalSelected;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isHighLight() {
        return highLight;
    }

    public void setHighLight(boolean highLight) {
        this.highLight = highLight;
    }

    public boolean isVisibleInSearch() {
        return visibleInSearch;
    }

    public void setVisibleInSearch(boolean visibleInSearch) {
        this.visibleInSearch = visibleInSearch;
    }

    public List<String> getKeyChildren() {
        return keyChildren;
    }

}
