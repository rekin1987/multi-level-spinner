package emget.pl.widgets.multilevelspinner;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import emget.pl.widgets.multilevelspinner.model.SpinnerItem;
import emget.pl.widgets.multilevelspinner.model.SpinnerItemHeader;

public class MultiLevelSpinnerAdapter extends ArrayAdapter<SpinnerItem> {

    private List<SpinnerItem> mItems;
    private LayoutInflater mInflater;

    public MultiLevelSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, List<SpinnerItem> items) {
        super(context, resource);
        mItems = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return getExpandedItemsCount();
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = mInflater.inflate(R.layout.custom_spinner_item, parent, false);


        CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
        TextView textView = (TextView) row.findViewById(R.id.text);
        ImageView expandIcon = (ImageView) row.findViewById(R.id.image);

        SpinnerItem.CheckboxState state = SpinnerItem.CheckboxState.UNCHECKED;
        SpinnerItem item = mItems.get(position);
        if (item instanceof SpinnerItemHeader) {
            int selectedCount = getSelectedItemsCount((SpinnerItemHeader) item);
            if (selectedCount > 0) {
                int totalItems = getChildCount((SpinnerItemHeader) item);
                if (selectedCount == totalItems) {
                    state = SpinnerItem.CheckboxState.CHECKED;
                } else {
                    state = SpinnerItem.CheckboxState.SEMICHECKED;
                }
            }


            expandIcon.setVisibility(View.VISIBLE);
            if (item.isExpanded()) {
                expandIcon.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.arrow_down_float));
            } else {
                expandIcon.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.arrow_up_float));
            }
            row.setPadding(0,0,0,0);
        } else {
            expandIcon.setVisibility(View.GONE);
            row.setPadding(30,0,0,0);
        }

        if (state == SpinnerItem.CheckboxState.CHECKED) {
            checkbox.setChecked(true);
        } else if (state == SpinnerItem.CheckboxState.SEMICHECKED) {
            checkbox.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_bright));
        } else {
            checkbox.setChecked(false);
        }

        textView.setText(item.getText());

        return row;
    }

    private int getChildCount(SpinnerItemHeader header) {
        int count = 0;
        for (SpinnerItem item : header.getChildren()
                ) {
            if (item instanceof SpinnerItemHeader) {
                count += getChildCount((SpinnerItemHeader) item);
            } else {
                ++count;
            }
        }
        return count;
    }

    private int getSelectedItemsCount(SpinnerItemHeader header) {
        int count = 0;
        for (SpinnerItem item : header.getChildren()) {
            if (item.getState() == SpinnerItem.CheckboxState.CHECKED) {
                if (item instanceof SpinnerItemHeader) {
                    count += ((SpinnerItemHeader) item).getChildren().size();
                } else {
                    ++count;
                }
            } else if (item.getState() == SpinnerItem.CheckboxState.SEMICHECKED) {
                ++count;
                count += getSelectedItemsCount((SpinnerItemHeader) item);
            }
        }
        return count;
    }

    private int getExpandedItemsCount() {
        int count = 0;
        for (SpinnerItem item : mItems) {
            if (item.isExpanded()) {
                ++count;
            }
        }
        return count;
    }

}
