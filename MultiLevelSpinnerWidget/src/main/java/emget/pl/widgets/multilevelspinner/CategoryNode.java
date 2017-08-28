package emget.pl.widgets.multilevelspinner;

/**
 * Internal model representation of an item/node.
 * This item is populated with the data from external/public model: {@link SpinnerItem}, {@link SpinnerItemElement}, {@link SpinnerItemHeader}.
 */
class CategoryNode {

    public String id; // external ID added for the node
    public String name; // external name added for the node

    int level; // node level starting from 0 (top node) ascending by 1
    int index; // node index on the list of all items (regardless of the visibility)
    boolean visible; // node visibility on the spinner dropdown list
    boolean expanded; // valid for node with children, tells if the row is expanded and children are visible
    CheckboxState checkboxState; // checkbox state for the node
    int childrenCount; // number of children nodes (is 0 for nodes without children)

    CategoryNode(int level, int index, String id, String name, int childrenCount) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.index = index;
        this.childrenCount = childrenCount;
        // default visibility for top level nodes (level 0) is visible
        if (level == 0) {
            visible = true;
        }
        // default state for a node is unchecked
        checkboxState = CheckboxState.UNCHECKED;
    }

    boolean hasChildren() {
        return childrenCount > 0;
    }

}
