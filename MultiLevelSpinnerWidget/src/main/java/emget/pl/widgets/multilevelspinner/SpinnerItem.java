package emget.pl.widgets.multilevelspinner;

/**
 * External data model item representing an abstract spinner item (a row on a spinner's dropdown list)
 */
public abstract class SpinnerItem {

    private String id;
    protected String text;
    protected CheckboxState state;

    /**
     * Constructor.
     *
     * @param text a text which will show up on the spinner's dropdown list
     */
    public SpinnerItem(String text) {
        this.id = this.toString() + "_" + this.hashCode();
        this.text = text;
    }

    /**
     * Constructor.
     *
     * @param id   an arbitrary given ID
     * @param text a text which will show up on the spinner's dropdown list
     */
    public SpinnerItem(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return this.text;
    }

    public CheckboxState getState() {
        return this.state;
    }

    // package access only
    void setCheckboxState(CheckboxState checkboxState) {
        this.state = checkboxState;
    }
}
