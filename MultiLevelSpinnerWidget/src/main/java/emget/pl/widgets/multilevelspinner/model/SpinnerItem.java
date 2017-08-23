package emget.pl.widgets.multilevelspinner.model;


public abstract class SpinnerItem {

    public enum CheckboxState {
        UNCHECKED,
        SEMICHECKED,
        CHECKED
    }

    protected String mText;
    protected CheckboxState mState;
    protected boolean mExpanded;

    public SpinnerItem(String text){
        mText = text;
    }

    public CheckboxState getState() {
        return mState;
    }

    public void setState(CheckboxState mState) {
        this.mState = mState;
    }


    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean mExpanded) {
        this.mExpanded = mExpanded;
    }
}
