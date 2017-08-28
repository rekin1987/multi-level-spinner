package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SpinnerAdapter;

/**
 * A Spinner allowing multiple levels in items hierarchy.
 */
public class MultiLevelSpinner extends android.support.v7.widget.AppCompatSpinner {

    private boolean isOpened = false;
    private OnSpinnerEventsListener listener;

    /**
     * Construct a new spinner with the given context's theme.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public MultiLevelSpinner(Context context) {
        super(context);
    }

    /**
     * Construct a new spinner with the given context's theme and the supplied attribute set.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public MultiLevelSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Construct a new spinner with the given context's theme, the supplied attribute set,
     * and default style attribute.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    public MultiLevelSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        isOpened = true;
        if (listener != null) {
            listener.onSpinnerOpened(this);
        }
        return super.performClick();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (isOpened() && hasFocus) {
            // this state means the dropdown list is being closed
            isOpened = false;
            if (listener != null) {
                listener.onSpinnerClosed(this);
            }
        }
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof MultiLevelSpinnerAdapter) {
            ((MultiLevelSpinnerAdapter) adapter).setParentSpinner(this);
        }
    }

    /**
     * Register the listener which will listen for events.
     */
    void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener) {
        listener = onSpinnerEventsListener;
    }

    /**
     * Checks whether the dropdown list is open.
     *
     * @return Returns true if the dropdown list is open, otherwise returns false.
     */
    boolean isOpened() {
        return isOpened;
    }

}
