package emget.pl.multilevelspinner.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import emget.pl.multilevelspinner.R;
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
        mAdapter = new MultiLevelSpinnerAdapter(this, R.layout.custom_spinner_item, mItems);

        spinner.setAdapter(mAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpinnerItem item = mItems.get(i);
                if(item instanceof SpinnerItemHeader){
                    item.setExpanded(true);
                    for (SpinnerItem subItem : ((SpinnerItemHeader) item).getChildren()){
                        subItem.setExpanded(true);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private List<SpinnerItem> prepareList(){
        List<SpinnerItem> list = new ArrayList<>();
        SpinnerItemHeader drinks = new SpinnerItemHeader("Drinks");
        drinks.setExpanded(true);
        drinks.addChild(new SpinnerItemElement("water"));
        drinks.addChild(new SpinnerItemElement("cola"));
        drinks.addChild(new SpinnerItemElement("sprite"));
        drinks.addChild(new SpinnerItemElement("lemoniade"));
        list.add(drinks);
        list.addAll(drinks.getChildren());
        SpinnerItemHeader food = new SpinnerItemHeader("Food");
        food.setExpanded(true);
        food.addChild(new SpinnerItemElement("cheese"));
        food.addChild(new SpinnerItemElement("bread"));
        food.addChild(new SpinnerItemElement("ham"));
        food.addChild(new SpinnerItemElement("fries"));
        list.add(food);
        list.addAll(food.getChildren());
        SpinnerItemHeader chemicals = new SpinnerItemHeader("Chemicals");
        chemicals.setExpanded(true);
        chemicals.addChild(new SpinnerItemElement("domestos"));
        chemicals.addChild(new SpinnerItemElement("cif"));
        chemicals.addChild(new SpinnerItemElement("vanish"));
        chemicals.addChild(new SpinnerItemElement("ace"));
        list.add(chemicals);
        list.addAll(chemicals.getChildren());

        return list;
    }
}
