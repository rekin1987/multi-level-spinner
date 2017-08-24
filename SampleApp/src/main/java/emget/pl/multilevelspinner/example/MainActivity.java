package emget.pl.multilevelspinner.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import emget.pl.multilevelspinner.R;
import emget.pl.widgets.multilevelspinner.ListItemClickCallback;
import emget.pl.widgets.multilevelspinner.MultiLevelSpinner;
import emget.pl.widgets.multilevelspinner.MultiLevelSpinnerAdapter;
import emget.pl.widgets.multilevelspinner.model.SpinnerItem;
import emget.pl.widgets.multilevelspinner.model.SpinnerItemElement;
import emget.pl.widgets.multilevelspinner.model.SpinnerItemHeader;

public class MainActivity extends AppCompatActivity {

    private List<SpinnerItem> mItems;
    private MultiLevelSpinnerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mItems = prepareList();

        MultiLevelSpinner spinner = (MultiLevelSpinner) findViewById(R.id.spinner);
        mAdapter = new MultiLevelSpinnerAdapter(this, R.layout.custom_spinner_item, mItems, new ListItemClickCallback() {

            @Override
            public void onRowClicked(int position) {
                SpinnerItem item = mItems.get(position);
                if(item instanceof SpinnerItemHeader){
                    if((item.isExpanded())){
                        // will tell all children to collapse
                        item.setExpanded(false);
                    } else {
                        // will tell all children to expand
                        item.setExpanded(true);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCheckboxClicked(int position, boolean checked) {

            }
        });

        spinner.setAdapter(mAdapter);
    }


    private List<SpinnerItem> prepareList(){
        List<SpinnerItem> list = new ArrayList<>();
        SpinnerItemHeader chemicals = new SpinnerItemHeader("Chemicals");
        chemicals.addChild(new SpinnerItemElement("domestos"));
        chemicals.addChild(new SpinnerItemElement("cif"));
        list.add(chemicals);

        SpinnerItemHeader drinks = new SpinnerItemHeader("Drinks");
        SpinnerItemHeader still = new SpinnerItemHeader("Still");
        SpinnerItemHeader sparkling = new SpinnerItemHeader("Sparkling");
        still.addChild(new SpinnerItemElement("water"));
        still.addChild(new SpinnerItemElement("lemoniade"));
        sparkling.addChild(new SpinnerItemElement("cola"));
        drinks.addChild(still);
        drinks.addChild(sparkling);
        list.add(drinks);

        SpinnerItemHeader food = new SpinnerItemHeader("Food");
        food.addChild(new SpinnerItemElement("cheese"));
        food.addChild(new SpinnerItemElement("bread"));
        food.addChild(new SpinnerItemElement("ham"));
        list.add(food);

        return list;
    }

}
