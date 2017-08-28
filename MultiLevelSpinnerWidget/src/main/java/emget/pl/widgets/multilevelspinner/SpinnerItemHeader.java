package emget.pl.widgets.multilevelspinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * External data model item representing an spinner header (a row on a spinner's dropdown list). Header can be any level item with or without
 * children.
 */
public class SpinnerItemHeader extends SpinnerItem {

    private List<SpinnerItem> children;

    /**
     * Constructor.
     *
     * @param text a text which will show up on the spinner's dropdown list
     */
    public SpinnerItemHeader(String text) {
        super(text);
        children = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param id   an arbitrary given ID
     * @param text a text which will show up on the spinner's dropdown list
     */
    public SpinnerItemHeader(String id, String text) {
        this(id, text, new ArrayList<SpinnerItem>());
    }

    /**
     * Constructor.
     *
     * @param id       an arbitrary given ID
     * @param text     a text which will show up on the spinner's dropdown list
     * @param children the initial list of children, which this {@link SpinnerItemHeader} has
     */
    public SpinnerItemHeader(String id, String text, List<SpinnerItem> children) {
        super(id, text);
        this.children = children;
    }

    /**
     * Gets a list of children this {@link SpinnerItemHeader} has. Note that these may be mix of {@link SpinnerItemHeader} or
     * {@link SpinnerItemElement}
     *
     * @return Returns a list of children this {@link SpinnerItemHeader} has.
     */
    public List<SpinnerItem> getChildren() {
        return children;
    }

    /**
     * Adds a children list to this {@link SpinnerItemHeader}.
     *
     * @param children a list of items to be added - either {@link SpinnerItemHeader} or {@link SpinnerItemElement}
     */
    public void setChildren(List<SpinnerItem> children) {
        this.children = children;
    }

    /**
     * Adds a child item to this {@link SpinnerItemHeader}.
     *
     * @param item an item to be added - either {@link SpinnerItemHeader} or {@link SpinnerItemElement}
     */
    public void addChild(SpinnerItem item) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(item);
    }

    /**
     * Adds an array of children items to this {@link SpinnerItemHeader}.
     *
     * @param items an array of items to be added - either {@link SpinnerItemHeader} or {@link SpinnerItemElement}
     */
    public void addChildren(SpinnerItem... items) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.addAll(Arrays.asList(items));
    }

}
