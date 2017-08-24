package emget.pl.widgets.multilevelspinner.model;

public abstract class SpinnerItem {

    public enum CheckboxState {
        UNCHECKED, SEMICHECKED, CHECKED
    }

    protected String text;
    private String id;

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

}
