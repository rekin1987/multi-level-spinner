package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.util.AttributeSet;

/**
 * A Spinner allowing multiple levels in items hierarchy.
 */
class MultiLevelSpinnerImpl extends android.support.v7.widget.AppCompatSpinner {

    private boolean isOpened = false;

    /**
     * Construct a new spinner with the given context's theme and the supplied attribute set.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public MultiLevelSpinnerImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        isOpened = true;
        return super.performClick();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (isOpened && hasFocus) {
            // this state means the dropdown list is being closed
            isOpened = false;
            // we want to refresh the input list, on which the adapter depends
            getAdapter().notifyContentNeedRefresh();
        }
    }

    @Override
    public MultiLevelSpinnerAdapter getAdapter() {
        return (MultiLevelSpinnerAdapter) super.getAdapter();
    }

}
