package emget.pl.widgets.multilevelspinner;

public abstract class SpinnerItem {

    private String id;
    protected String text;
    protected CheckboxState state;

    public SpinnerItem(String text) {
        this.id = this.toString() + "_" + this.hashCode();
        this.text = text;
    }

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

    void setCheckboxState(CheckboxState checkboxState) {
        this.state = checkboxState;
    }
}
