package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.util.AttributeSet;

public class MultiLevelSpinner extends android.support.v7.widget.AppCompatSpinner {

    private boolean mOpenInitiated = false;

    public MultiLevelSpinner(Context context) {
        super(context);
    }

    public MultiLevelSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiLevelSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        mOpenInitiated = true;
        return super.performClick();
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        if (isOpened() && hasFocus) {
            mOpenInitiated = false;
        }
    }

    public boolean isOpened() {
        return mOpenInitiated;
    }

}
