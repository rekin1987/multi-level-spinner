package emget.pl.widgets.multilevelspinner.model;

/**
 * Created by psuszek on 2017-08-24.
 */

public class CategoryNode {

    public int level;
    public int index;
    public String id;
    public String name;
    public boolean visible;
    public SpinnerItem.CheckboxState checkboxState;
    public int childrenCount;

    public CategoryNode(int level, int index, String id, String name, int childrenCount) {
        this.level = level;
        this.index = index;
        this.id = id;
        this.name = name;
        this.childrenCount = childrenCount;

        if (level == 0) {
            visible = true;
        }

        checkboxState = SpinnerItem.CheckboxState.UNCHECKED;
    }

    public boolean hasChildren() {
        return childrenCount > 0;
    }

    public SpinnerItem.CheckboxState getCheckboxState() {
        return checkboxState;
    }

    public void setCheckboxState(SpinnerItem.CheckboxState checkboxState) {
        this.checkboxState = checkboxState;
    }

}
