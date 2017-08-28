package emget.pl.multilevelspinner.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import emget.pl.multilevelspinner.R;
import emget.pl.widgets.multilevelspinner.MultiLevelSpinner;
import emget.pl.widgets.multilevelspinner.MultiLevelSpinnerAdapter;
import emget.pl.widgets.multilevelspinner.SpinnerItem;
import emget.pl.widgets.multilevelspinner.SpinnerItemElement;
import emget.pl.widgets.multilevelspinner.SpinnerItemHeader;

public class MainActivity extends AppCompatActivity {

    private List<SpinnerItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = prepareList();

        MultiLevelSpinner spinner = (MultiLevelSpinner) findViewById(R.id.spinner);
        spinner.addSpinnerTitle("Custom title");
        spinner.setAdapter(new MultiLevelSpinnerAdapter(this, R.layout.custom_spinner_item, items));
    }

    /**
     * Prepare a mock list with items to be shown.
     * @return Returns a list of {@link SpinnerItem} items representing the items multi level hierarchy.
     */
    private List<SpinnerItem> prepareList(){
        List<SpinnerItem> list = new ArrayList<>();
        // top category
        SpinnerItemHeader chemicals = new SpinnerItemHeader("id1","Chemicals");
        chemicals.addChild(new SpinnerItemElement("id1_1","domestos"));
        chemicals.addChild(new SpinnerItemElement("id1_2","cif"));
        // add top level category to global list
        list.add(chemicals);

        // top category
        SpinnerItemHeader drinks = new SpinnerItemHeader("id2","Drinks");
        // subcategory under 'drinks'
        SpinnerItemHeader still = new SpinnerItemHeader("id2_1","Still");
        // subcategory under 'drinks'
        SpinnerItemHeader sparkling = new SpinnerItemHeader("id2_2","Sparkling");
        still.addChild(new SpinnerItemElement("id2_1_1","water"));
        still.addChild(new SpinnerItemElement("id2_1_2","lemoniade"));
        sparkling.addChild(new SpinnerItemElement("id2_2_1","cola"));
        // add subcategories to patent category
        drinks.addChild(still);
        drinks.addChild(sparkling);
        // add top level category to global list
        list.add(drinks);

        // top category
        SpinnerItemHeader food = new SpinnerItemHeader("id3","Food");
        food.addChild(new SpinnerItemElement("id3_1","cheese"));
        food.addChild(new SpinnerItemElement("id3_2","bread"));
        food.addChild(new SpinnerItemElement("id3_3","ham"));
        // add top level category to global list
        list.add(food);

        return list;
    }

    /**
     * Triggered when button is clicked.
     * @param view
     */
    public void printCheckedItems(View view) {
        // create a CheckedItemsPrinter and print checked and semichecked items to console
        CheckedItemsPrinter printer = new CheckedItemsPrinter(items);
        printer.printToConsole();
        printer.printToTextView((TextView)findViewById(R.id.selected_items_label));

    }


}
