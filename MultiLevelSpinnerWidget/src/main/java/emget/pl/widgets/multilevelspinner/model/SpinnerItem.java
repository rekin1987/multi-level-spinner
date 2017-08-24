package emget.pl.widgets.multilevelspinner.model;


public abstract class SpinnerItem {

    public enum CheckboxState {
        UNCHECKED,
        SEMICHECKED,
        CHECKED
    }

    protected String text;
    protected CheckboxState state;
    protected boolean expanded;
    private int level;
    private String id;

    public SpinnerItem(String text){
        this.text = text;
    }

    public CheckboxState getState() {
        return state;
    }

    public void setState(CheckboxState mState) {
        this.state = mState;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String mText) {
        this.text = mText;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean mExpanded) {
        this.expanded = mExpanded;
    }
}
