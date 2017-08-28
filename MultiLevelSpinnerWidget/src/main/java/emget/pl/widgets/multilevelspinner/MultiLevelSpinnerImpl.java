package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.util.AttributeSet;

/**
 * A Spinner allowing multiple levels in items hierarchy.
 */
class MultiLevelSpinnerImpl extends android.support.v7.widget.AppCompatSpinner {

    /**
     * Listener used to inform that the dropdown list is closed.
     */
    interface OnDropdownCloseListener {
        /**
         * Triggered when a dropdown list is closed.
         */
        void onDropdownClosed();
    }

    private boolean isOpened = false;
    private OnDropdownCloseListener dropdownCloseListener;

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
            if (dropdownCloseListener != null) {
                dropdownCloseListener.onDropdownClosed();
            }
            // we want to refresh the input list, on which the adapter depends
            getAdapter().notifyContentNeedRefresh();
        }
    }

    @Override
    public MultiLevelSpinnerAdapter getAdapter() {
        return (MultiLevelSpinnerAdapter) super.getAdapter();
    }

    /**
     * Registers a dropdown close listener
     *
     * @param dropdownCloseListener a listener to register
     */
    public void setOnDropdownCloseListener(OnDropdownCloseListener dropdownCloseListener) {
        this.dropdownCloseListener = dropdownCloseListener;
    }

}
