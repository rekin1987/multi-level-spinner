package emget.pl.multilevelspinner.example;

import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import emget.pl.widgets.multilevelspinner.CheckboxState;
import emget.pl.widgets.multilevelspinner.SpinnerItem;
import emget.pl.widgets.multilevelspinner.SpinnerItemHeader;

/**
 * Helper class which prints out the checked and semichecked items.
 */
public class CheckedItemsPrinter {

    private static final String TAG = CheckedItemsPrinter.class.getSimpleName();

    private List<String> checkedItems;
    private List<String> semiCheckedItems;

    /**
     * Constructor.
     *
     * @param inputItems input list of {@link SpinnerItem} items
     */
    CheckedItemsPrinter(List<SpinnerItem> inputItems) {
        checkedItems = new ArrayList<>();
        semiCheckedItems = new ArrayList<>();
        countCheckedAndSemiCheckedItems(inputItems);
    }

    /**
     * Prints out the checked and semichecked items to the LogCat.
     */
    public void printToConsole() {
        Log.d(TAG, "Checked = " + checkedItems.size() + " : " + Arrays.toString(checkedItems.toArray()));
        Log.d(TAG, "Semichecked = " + semiCheckedItems.size() + " : " + Arrays.toString(semiCheckedItems.toArray()));
    }

    /**
     * Goes through the items looking for checked and semichecked items.
     *
     * @param items input list of {@link SpinnerItem} items
     */
    private void countCheckedAndSemiCheckedItems(List<SpinnerItem> items) {
        for (SpinnerItem item : items) {
            if (item.getState() == CheckboxState.CHECKED) {
                checkedItems.add(item.getText());
            } else if (item.getState() == CheckboxState.SEMICHECKED) {
                semiCheckedItems.add(item.getText());
            }
            // go inside recursively if any item has children
            if (item instanceof SpinnerItemHeader) {
                countCheckedAndSemiCheckedItems(((SpinnerItemHeader) item).getChildren());
            }
        }
    }

    /**
     * Prints out the checked and semichecked items to the provided TextView.
     */
    public void printToTextView(TextView view) {
        view.setText("Checked = " + checkedItems.size() + " : " + Arrays.toString(checkedItems.toArray()) + "\nSemichecked = " + semiCheckedItems.size() + " : " + Arrays.toString(semiCheckedItems.toArray()));
    }
}
