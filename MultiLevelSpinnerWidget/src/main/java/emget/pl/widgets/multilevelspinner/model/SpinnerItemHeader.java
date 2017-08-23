package emget.pl.widgets.multilevelspinner.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpinnerItemHeader extends SpinnerItem {

    private int mLevel;
    private List<SpinnerItem> mChildren;

    public SpinnerItemHeader(String text) {
        this(0, text, null);
    }

    public SpinnerItemHeader(int level, String text) {
        this(level, text, null);
    }

    public SpinnerItemHeader(int level, String text, List<SpinnerItem> children) {
        super(text);
        mLevel = level;
        mChildren = children;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int mHeaderLevel) {
        this.mLevel = mHeaderLevel;
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
