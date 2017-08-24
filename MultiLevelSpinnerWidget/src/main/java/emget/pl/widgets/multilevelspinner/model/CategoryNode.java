package emget.pl.widgets.multilevelspinner.model;

import java.util.List;

/**
 * Created by psuszek on 2017-08-24.
 */

public class CategoryNode {

    public int level;

    public CategoryNode(int level, int index, String id, String name, int childrenCount) {
        this.level = level;
        this.index = index;
        this.id = id;
        this.name = name;
        this.childrenCount = childrenCount;

        if(level==0){
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

    //private List<CategoryNode> subcategories;

}
