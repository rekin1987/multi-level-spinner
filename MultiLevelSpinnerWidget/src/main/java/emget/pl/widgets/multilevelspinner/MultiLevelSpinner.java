package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.util.AttributeSet;

public class MultiLevelSpinner extends android.support.v7.widget.AppCompatSpinner {

    private boolean mOpenInitiated = false;
    private OnSpinnerEventsListener mListener;

    public MultiLevelSpinner(Context context) {
        super(context);
    }

    public MultiLevelSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiLevelSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Register the listener which will listen for events.
     */
    void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

    @Override
    public boolean performClick() {
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerOpened(this);
        }
        return super.performClick();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (isOpened() && hasFocus) {
            mOpenInitiated = false;
            if (mListener != null) {
                mListener.onSpinnerClosed(this);
            }
        }
    }

    public boolean isOpened() {
        return mOpenInitiated;
    }

}
