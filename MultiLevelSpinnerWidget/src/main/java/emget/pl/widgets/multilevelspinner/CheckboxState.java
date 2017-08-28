package emget.pl.widgets.multilevelspinner;

/**
 * Checkbox state for an item/node.
 */
public enum CheckboxState {
    /**
     * Default, when no children are checked or if node has no children and is not checked.
     */
    UNCHECKED,
    /**
     * Valid for nodes which children. Applies when some children are checked, but not all.
     */
    SEMICHECKED,
    /**
     * State when all children are checked (on all levels) or if node has no children and is checked.
     */
    CHECKED
}
