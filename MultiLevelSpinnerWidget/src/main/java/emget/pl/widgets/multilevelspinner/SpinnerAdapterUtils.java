package emget.pl.widgets.multilevelspinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class with helper methods for {@link MultiLevelSpinnerAdapter}.
 */
class SpinnerAdapterUtils {

    private int itemIndex; // index of item (allows to easily find desired item based on clicked item position), used to flat tree-based list to flat

    /**
     * Converts the tree-like list with {@link SpinnerItem} elements into flat hierarchy list of {@link CategoryNode} items.
     *
     * @param items a tree-like list with {@link SpinnerItem} elements to convert
     * @return Returns a flat list of {@link CategoryNode} items.
     */
    List<CategoryNode> flatList(List<SpinnerItem> items) {
        itemIndex = 0;
        return flatListInternal(items, 0);
    }

    /**
     * Ensures the checkboxes states are propagated from the internal model to the external (from {@link CategoryNode} to {@link SpinnerItem}).
     *
     * @param inputItems         input a tree-like list with {@link SpinnerItem} elements
     * @param flatHierarchyItems a flat hierarchy list with {@link CategoryNode} items
     */
    void validateCheckedItems(List<SpinnerItem> inputItems, List<CategoryNode> flatHierarchyItems) {
        for (CategoryNode node : flatHierarchyItems) {
            // we operate on the original input list!
            propagateCheckboxState(inputItems, node.id, node.checkboxState);
        }
    }

    /**
     * Calculate the corresponding item index (in the internal data model) for the clicked item on the Spinner dropdown list.
     *
     * @param flatHierarchyItems a flat hierarchy list with {@link CategoryNode} items
     * @param position           position of the clicked item on the Spinner dropdown list
     * @return Returns a corresponding CategoryNode object.
     */
    CategoryNode getItemAtPosition(List<CategoryNode> flatHierarchyItems, int position) {
        if (position == 0) {
            // this is always true, item 0 is always level 0 category and always visible (may not be visible for user, but has 'visible = true'
            return flatHierarchyItems.get(0);
        }
        // first get list of visible items
        List<CategoryNode> visibleItems = new ArrayList<>();
        for (CategoryNode node : flatHierarchyItems) {
            if (node.visible) {
                visibleItems.add(node);
            }
        }
        // on the visible items list just find the item at position - it was the one clicked
        return visibleItems.get(position);
    }

    /**
     * Updates visibility and expanded status for the given node. Is called only for {@link CategoryNode} items with children.
     *
     * @param flatHierarchyItems a flat hierarchy list with {@link CategoryNode} items
     * @param node               a node to update
     * @param nodeIndex          index of the node - in most cases different from the clicked position
     */
    void updateVisibilityStatus(List<CategoryNode> flatHierarchyItems, CategoryNode node, int nodeIndex) {
        // get level of the category
        int level = node.level;
        boolean isHide = false;

        // check each next item on the list, starting from the item.index + 1
        for (int i = nodeIndex + 1; i < flatHierarchyItems.size(); ++i) {
            CategoryNode subnode = flatHierarchyItems.get(i);

            if (level == subnode.level) {
                // stop when we reached the item at the same level
                break;
            }

            // determine if we are hiding or showing items - check the first subnode, all others have the same visibility
            isHide = subnode.visible; // if was visible then we want to hide

            if (isHide) {
                // ensure elements on all levels are hidden
                if (level < subnode.level) {
                    // change the visibility of item - if was visible make it gone and vice versa
                    subnode.visible = false;
                }
            } else {
                // ensure only one level deeper items are affected (we don't want to expand all subcategories if there are any)
                if (level + 1 == subnode.level) {
                    // change the visibility of item - if was visible make it gone and vice versa
                    subnode.visible = true;
                }
            }
        }

        // change arrow for
        if (isHide) {
            // when we hide items, then the arrow should refer to "I can be opened" image.
            node.expanded = false;
        } else {
            node.expanded = true;
        }
    }

    /**
     * Handles changing checkboxes state. Takes children and parent nodes into consideration.
     *
     * @param flatHierarchyItems a flat hierarchy list with {@link CategoryNode} items
     * @param item               an item which checkbox state changes
     * @param isChecked          true if checkbox is checked, otherwise false
     */
    void handleCheckboxStateChange(List<CategoryNode> flatHierarchyItems, CategoryNode item, boolean isChecked) {
        // first set the state for the given item itself
        item.checkboxState = isChecked ? CheckboxState.CHECKED : CheckboxState.UNCHECKED;

        // if item has children mark them all the same as the current item - checked or unchecked
        if (item.hasChildren()) {
            // check all children
            List<CategoryNode> allChildren = getAllChildren(flatHierarchyItems, item);
            for (CategoryNode child : allChildren) {
                child.checkboxState = isChecked ? CheckboxState.CHECKED : CheckboxState.UNCHECKED;
            }
        }

        // now go up the list and see if a any parent state should change
        // consider the following scenarios:
        // - when any item below parent (direct or indirect child) is checked then parents (up to the top level) are semichecked
        // - when all items below parent (direct or indirect child) are checked then parent is checked but the parent-parent may be only semichecked
        CategoryNode parent;
        CategoryNode currentItem = item;

        // find parent for subsequent levels until top level parent is found
        // important to note: this works recursively from bottom to the top, so the state is set for the lowest levels first
        while ((parent = findParent(flatHierarchyItems, currentItem)) != null) {
            // get direct parent
            List<CategoryNode> directChildren = getDirectChildren(flatHierarchyItems, parent);
            // calculate checked and semichecked children
            int checkedChildrenCount = 0;
            int semiCheckedChildrenCount = 0;
            for (CategoryNode child : directChildren) {
                if (child.checkboxState == CheckboxState.CHECKED) {
                    ++checkedChildrenCount;
                } else if (child.checkboxState == CheckboxState.SEMICHECKED) {
                    ++semiCheckedChildrenCount;
                }
            }
            // set the actual state based on the number of checked and semichecked children
            if (directChildren.size() == checkedChildrenCount) {
                parent.checkboxState = CheckboxState.CHECKED;
            } else if (checkedChildrenCount > 0 || semiCheckedChildrenCount > 0) {
                parent.checkboxState = CheckboxState.SEMICHECKED;
            } else {
                parent.checkboxState = CheckboxState.UNCHECKED;
            }
            // make current item a parent, so we propagate towards the top (towards the level 0 node)
            currentItem = parent;
        }
    }

    /**
     * Converts the tree-like list with {@link SpinnerItem} elements into flat hierarchy list of {@link CategoryNode} items.
     *
     * @param items a tree-like list with {@link SpinnerItem} elements to convert
     * @param level a hierarchy level of items (initially should be 0, then recursively it is incremented for {@link SpinnerItemHeader} elements)
     * @return Returns a flat list of {@link CategoryNode} items.
     */
    private List<CategoryNode> flatListInternal(List<SpinnerItem> items, int level) {
        List<CategoryNode> list = new ArrayList<>();
        for (SpinnerItem item : items) {
            int childCount = 0;
            if (item instanceof SpinnerItemHeader) {
                childCount = ((SpinnerItemHeader) item).getChildren().size();
            }
            // create an item of our internal model (flat list) based on the {@link SpinnerItem}
            CategoryNode createdNode = new CategoryNode(level, itemIndex, item.getId(), item.getText(), childCount);
            list.add(createdNode);
            ++itemIndex; // increment node index
            if (item instanceof SpinnerItemHeader) {
                // if we encounter a node with children add all children to flat list recursively (incrementing a level of these sub-nodes)
                list.addAll(flatListInternal(((SpinnerItemHeader) item).getChildren(), level + 1));
            }
        }
        return list;
    }

    /**
     * Ensures the checkboxes states are propagated from the internal model to the external (from {@link CategoryNode} to {@link SpinnerItem}).
     * Method visits children on all levels recursively.
     *
     * @param items list with states - it will propagate to external model
     * @param id    item ID we are looking for
     * @param state item state which will be set for the corresponding item in the external data model
     * @return Returns true if particular item was found, otherwise returns false.
     */
    private boolean propagateCheckboxState(List<SpinnerItem> items, String id, CheckboxState state) {
        boolean foundItem = false;
        if (items.isEmpty()) {
            return false;
        }
        for (SpinnerItem item : items) {
            if (item.getId().equals(id)) {
                // found match - update it's state
                item.setCheckboxState(state);
                return true;
            }
            if (item instanceof SpinnerItemHeader) {
                // found header -> check it's all children recursively
                foundItem = propagateCheckboxState(((SpinnerItemHeader) item).getChildren(), id, state);
                if (foundItem) {
                    // if match was found on children list stop the loop
                    break;
                }
            }
        }
        return foundItem;
    }

    /**
     * Gets list of children on all sub-levels.
     *
     * @param flatHierarchyItems a flat hierarchy list with {@link CategoryNode} items
     * @param parent             a {@link CategoryNode} to go through
     * @return Returns all level children for the given {@link CategoryNode} element. Can be empty list.
     */
    private List<CategoryNode> getAllChildren(List<CategoryNode> flatHierarchyItems, CategoryNode parent) {
        List<CategoryNode> children = new ArrayList<>();
        if (parent.hasChildren()) {
            for (int i = parent.index + 1; i < flatHierarchyItems.size(); ++i) {
                CategoryNode subnode = flatHierarchyItems.get(i);
                if (subnode.level <= parent.level) {
                    // break when we reach next item at the same level as parent or higher in the tree
                    break;
                }
                children.add(subnode);
            }
        }
        return children;
    }

    /**
     * Gets list of children on direct sub-level only.
     * Introduces better performance than {@link #getAllChildren(List, CategoryNode)}. Use when you need direct children for the given node.
     *
     * @param flatHierarchyItems a flat hierarchy list with {@link CategoryNode} items
     * @param parent             a {@link CategoryNode} to go through
     * @return Returns direct children for the given {@link CategoryNode} element. Can be empty list.
     */
    private List<CategoryNode> getDirectChildren(List<CategoryNode> flatHierarchyItems, CategoryNode parent) {
        List<CategoryNode> children = new ArrayList<>();
        if (parent.hasChildren()) {
            int expectedChildrenLevel = parent.level + 1; // children have one level greater than parent
            for (int i = parent.index + 1; i < flatHierarchyItems.size(); ++i) {
                CategoryNode subnode = flatHierarchyItems.get(i);
                if (subnode.level > expectedChildrenLevel) {
                    // continue when we reach the next level item (this is indirect children - child of direct child - and we don't want them)
                    continue;
                } else if (subnode.level <= parent.level) {
                    // break when we reach next item at the same level as parent or higher in the tree
                    break;
                }
                children.add(subnode);
            }
        }
        return children;
    }

    /**
     * Finds parent for the given item.
     *
     * @param flatHierarchyItems a flat hierarchy list with {@link CategoryNode} items
     * @param item               child for which we are looking for a parent
     * @return Returns a {@link CategoryNode} which is a parent of the given item or null if the item is top-level node.
     */
    private CategoryNode findParent(List<CategoryNode> flatHierarchyItems, CategoryNode item) {
        if (item.level == 0) {
            return null;
        }
        // iterate starting from the item before current - order descending until node with the lower level is reached (means a parent)
        for (int i = item.index - 1; i >= 0; --i) {
            if (flatHierarchyItems.get(i).level < item.level) {
                return flatHierarchyItems.get(i);
            }
        }
        return null;
    }

}
