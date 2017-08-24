package emget.pl.multilevelspinner.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
    }

    private List<SpinnerItem> prepareList(){
        List<SpinnerItem> list = new ArrayList<>();
        SpinnerItemHeader chemicals = new SpinnerItemHeader("id1","Chemicals");
        chemicals.addChild(new SpinnerItemElement("id1_1","domestos"));
        chemicals.addChild(new SpinnerItemElement("id1_2","cif"));
        list.add(chemicals);

        SpinnerItemHeader drinks = new SpinnerItemHeader("id2","Drinks");
        SpinnerItemHeader still = new SpinnerItemHeader("id2_1","Still");
        SpinnerItemHeader sparkling = new SpinnerItemHeader("id2_2","Sparkling");
        still.addChild(new SpinnerItemElement("id2_1_1","water"));
        still.addChild(new SpinnerItemElement("id2_1_2","lemoniade"));
        sparkling.addChild(new SpinnerItemElement("id2_2_1","cola"));
        drinks.addChild(still);
        drinks.addChild(sparkling);
        list.add(drinks);

        SpinnerItemHeader food = new SpinnerItemHeader("id3","Food");
        food.addChild(new SpinnerItemElement("id3_1","cheese"));
        food.addChild(new SpinnerItemElement("id3_2","bread"));
        food.addChild(new SpinnerItemElement("id3_3","ham"));
        list.add(food);

        return list;
    }

}
