package emget.pl.widgets.multilevelspinner;

/**
 * External data model item representing an spinner element (a row on a spinner's dropdown list). Element is a bottom level item with no children.
 */
public class SpinnerItemElement extends SpinnerItem {

    /**
     * Constructor.
     *
     * @param text a text which will show up on the spinner's dropdown list
     */
    public SpinnerItemElement(String text) {
        super(text);
    }

    /**
     * Constructor.
     *
     * @param id   an arbitrary given ID
     * @param text a text which will show up on the spinner's dropdown list
     */
    public SpinnerItemElement(String id, String text) {
        super(id, text);
    }
}
