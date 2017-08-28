package emget.pl.multilevelspinner.example;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import emget.pl.widgets.multilevelspinner.CheckboxState;
import emget.pl.widgets.multilevelspinner.SpinnerItem;
import emget.pl.widgets.multilevelspinner.SpinnerItemHeader;

public class CheckedItemsPrinter {

    private static final String TAG = CheckedItemsPrinter.class.getSimpleName();

    List<String> checkedItems;
    List<String> semiCheckedItems;
    List<SpinnerItem> inputItems;

    CheckedItemsPrinter(List<SpinnerItem> inputItems) {
        checkedItems = new ArrayList<>();
        semiCheckedItems = new ArrayList<>();
        this.inputItems = inputItems;
    }

    public void printToConsole() {
        checkedItems.clear();
        semiCheckedItems.clear();
        countCheckedAndSemiCheckedItems(inputItems);
        Log.d(TAG, "Checked = " + checkedItems.size() + " : " +  Arrays.toString(checkedItems.toArray()));
        Log.d(TAG, "Semichecked = " + semiCheckedItems.size() + " : " +  Arrays.toString(semiCheckedItems.toArray()));
    }

    private void countCheckedAndSemiCheckedItems(List<SpinnerItem> items) {
        for (SpinnerItem item : items) {
            if (item.getState() == CheckboxState.CHECKED) {
                checkedItems.add(item.getText());
            } else if (item.getState() == CheckboxState.SEMICHECKED) {
                semiCheckedItems.add(item.getText());
            }
            if (item instanceof SpinnerItemHeader) {
                countCheckedAndSemiCheckedItems(((SpinnerItemHeader) item).getChildren());
            }
        }
    }

}
