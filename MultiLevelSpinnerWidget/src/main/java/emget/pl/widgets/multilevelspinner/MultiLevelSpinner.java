package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

/**
 * A Spinner allowing multiple levels in items hierarchy.
 */
public class MultiLevelSpinner extends RelativeLayout {

    /**
     * The MultiLevelSpinner is backed by two Spinners:
     * - AppCompatSpinner which serves as an overlay spinner with a title
     * - MultiLevelSpinnerImpl which is the actual main spinner with data model and shows a dropdown list with items
     */

    // allows smooth transition between both spinners
    private static final int SMOOTH_UI_TRANSITION_DELAY = 200;

    // main spinner, which dropdown list is shown
    private MultiLevelSpinnerImpl mainSpinner;
    // overlay spinner - works a a "title" when main spinner is collapsed
    private android.support.v7.widget.AppCompatSpinner overlaySpinner;
    // layout parameters for both spinners
    private RelativeLayout.LayoutParams spinnerParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    // flag determining if title was set - title is mandatory
    private boolean isTitleSet;

    /**
     * Construct a new spinner with the given context's theme.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public MultiLevelSpinner(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Construct a new spinner with the given context's theme, the supplied attribute set.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public MultiLevelSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
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
        init(context, attrs);
    }

    /**
     * Allows to set different intend (left margin) for items at levels higher than top/zero.
     *
     * @param intendInPixels desired intend in pixels
     */
    public void setLevelIntend(int intendInPixels) {
        mainSpinner.getAdapter().setLevelIntend(intendInPixels);
    }

    /**
     * Adds a custom title instead of the first element when {@link MultiLevelSpinner} is closed.
     *
     * @param titleString a title to show when spinner is collapsed
     */
    public void addSpinnerTitle(String titleString) {
        // we overlay another spinner on top of the existing one - it will work as a "title"
        overlaySpinner = new android.support.v7.widget.AppCompatSpinner(getContext()) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                // when clicked just perform the click on main Spinner - it will show the dropdown list on top of the overlay Spinner
                mainSpinner.performClick();
                // we need to invert the spinners visibility - use delay for smooth transition! in example SMOOTH_UI_TRANSITION_DELAY delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // make overlay spinner invisible
                        overlaySpinner.setVisibility(INVISIBLE);
                        // the main spinner visible to ensure it handles onClick events correctly
                        mainSpinner.setVisibility(VISIBLE);
                    }
                }, SMOOTH_UI_TRANSITION_DELAY);
                // we listen for closing event for the main spinner, to make sure the overlay title spinner gets on top
                mainSpinner.setOnDropdownCloseListener(new MultiLevelSpinnerImpl.OnDropdownCloseListener() {
                    @Override
                    public void onDropdownClosed() {
                        // make overlay spinner visible on top of the main spinner
                        overlaySpinner.setVisibility(VISIBLE);
                        // the main spinner is invisible
                        mainSpinner.setVisibility(INVISIBLE);
                    }
                });
                // consume event, so the overlay Spinner won't show it's dropdown
                return true;
            }
        };

        // set array adapter with one item - given title string
        overlaySpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{titleString}));

        // update overlay spinner params to match main spinner height - use delay for smooth transition! in example SMOOTH_UI_TRANSITION_DELAY delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // match the height of the main spinner
                if (mainSpinner.getHeight() > 0) {
                    RelativeLayout.LayoutParams spinnerParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainSpinner.getHeight());
                    overlaySpinner.setLayoutParams(spinnerParams);
                }
            }
        }, SMOOTH_UI_TRANSITION_DELAY);

        // set default params - will be updated after delay to match main spinner height
        overlaySpinner.setLayoutParams(spinnerParams);
        this.addView(overlaySpinner);

        // at the start make overlay spinner visible on top of the main spinner
        overlaySpinner.setVisibility(VISIBLE);
        // the main spinner will change visibility when dropdown will show up - done in a separate delayed Handler
        mainSpinner.setVisibility(INVISIBLE);

        isTitleSet = true;
    }

    /**
     * Sets the adapter for this {@link MultiLevelSpinner}.
     *
     * @param adapter {@link MultiLevelSpinnerAdapter} to set
     */
    public void setAdapter(MultiLevelSpinnerAdapter adapter) {
        if (!isTitleSet) {
            throw new NullPointerException("Must set title first! Use #addSpinnerTitle(String) method.");
        }
        mainSpinner.setAdapter(adapter);
    }

    /**
     * Inits layout.
     *
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     */
    private void init(Context context, AttributeSet attrs) {
        mainSpinner = new MultiLevelSpinnerImpl(context, attrs);
        mainSpinner.setLayoutParams(spinnerParams);
        this.addView(mainSpinner);
    }

}
