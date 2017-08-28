package emget.pl.widgets.multilevelspinner;

/**
 * An interface which a client of this MultiLevelSpinner could use to receive open/closed events for the MultiLevelSpinner.
 */
interface OnSpinnerEventsListener {

    /**
     * Callback triggered when the spinner was opened.
     */
    void onSpinnerOpened(android.support.v7.widget.AppCompatSpinner spinner);

    /**
     * Callback triggered when the spinner was closed.
     */
    void onSpinnerClosed(android.support.v7.widget.AppCompatSpinner spinner);

}
