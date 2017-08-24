package emget.pl.widgets.multilevelspinner.model;

import java.util.List;

/**
 * Created by psuszek on 2017-08-24.
 */

public class CategoryNode {

    public int level;
    public CategoryNode parent;

    public CategoryNode(CategoryNode parent, int level, int index, String id, String name, int childrenCount) {
        this.parent = parent;
        this.level = level;
        this.index = index;
        this.id = id;
        this.name = name;
        this.childrenCount = childrenCount;

        if (level == 0) {
            visible = true;
        }
    }

    public int index;
    public String id;
    public String name;
    public boolean expanded;
    public boolean visible;
    public SpinnerItem.CheckboxState checkboxState;

    public int childrenCount;
    public int checkedChildrenCount;

    public boolean hasChildren() {
        return childrenCount > 0;
    }

}
