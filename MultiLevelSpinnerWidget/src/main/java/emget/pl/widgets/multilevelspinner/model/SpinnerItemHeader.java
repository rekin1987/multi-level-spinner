package emget.pl.widgets.multilevelspinner.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpinnerItemHeader extends SpinnerItem {

    private List<SpinnerItem> mChildren;

    public SpinnerItemHeader(String id, String text) {
        this(id, text, new ArrayList<SpinnerItem>());
    }

    public SpinnerItemHeader(String id, String text, List<SpinnerItem> children) {
        super(id, text);
        mChildren = children;
    }

    public List<SpinnerItem> getChildren() {
        return mChildren;
    }

    public void setChildren(List<SpinnerItem> mChildren) {
        this.mChildren = mChildren;
    }

    public void addChild(SpinnerItem item) {
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        mChildren.add(item);
    }

    public void addChildren(SpinnerItem... items) {
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        mChildren.addAll(Arrays.asList(items));
    }

}
