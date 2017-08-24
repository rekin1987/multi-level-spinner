package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import emget.pl.widgets.multilevelspinner.model.CategoryNode;
import emget.pl.widgets.multilevelspinner.model.SpinnerItem;
import emget.pl.widgets.multilevelspinner.model.SpinnerItemHeader;

public class MultiLevelSpinnerAdapter extends ArrayAdapter<SpinnerItem> {

    //private List<SpinnerItem> mItems;
    private List<CategoryNode> allItems; // shallow list
    private LayoutInflater mInflater;
    private ListItemClickCallback mListItemClickCallback;

    public MultiLevelSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, List<SpinnerItem> items, ListItemClickCallback callback) {
        super(context, resource);
        allItems = convertToShallowList(items, 0, new MyInt(0));
        // mItems = items;
        mInflater = LayoutInflater.from(context);
        mListItemClickCallback = callback;
    }

    private class MyInt {
        public int index;

        public MyInt(int index) {
            this.index = index;
        }
    }

    private List<CategoryNode> convertToShallowList(List<SpinnerItem> items, int level, MyInt index) {
        List<CategoryNode> list = new ArrayList<>();
        for (SpinnerItem item : items) {
            int childCount = 0;
            if (item instanceof SpinnerItemHeader) {
                childCount = getChildCount((SpinnerItemHeader) item);
            }
            list.add(new CategoryNode(level, index.index, "99", item.getText(), childCount));
            ++index.index;
            if (item instanceof SpinnerItemHeader) {
                list.addAll(convertToShallowList(((SpinnerItemHeader) item).getChildren(), level + 1, index));
            }
        }
        return list;
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
        int visibleItemsCount = 0;
        for (CategoryNode node : allItems) {
            if (node.visible) {
                ++visibleItemsCount;
            }
        }
        return visibleItemsCount;
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        View row = mInflater.inflate(R.layout.custom_spinner_item, parent, false);

        final CategoryNode item = getItemAtPosition(position);

        // on click for a whole row - fortunately this workarounds the default mechanism which closes the spinner on click
        // note that Spinner.setOnItemSelectedListener() will not work anymore
        row.findViewById(R.id.row_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryNode node = allItems.get(item.index);
                if (node.childrenCount > 0) { // if is category
                    int level = node.level;
                    for (int i = item.index + 1; i < allItems.size(); ++i) {
                        CategoryNode subnode = allItems.get(i);
                        if (level == subnode.level) {
                            break;
                        } else if (level + 1 == subnode.level) {

                            subnode.visible = !subnode.visible ;
                        }
                    }
                    notifyDataSetChanged();
                }

                //mListItemClickCallback.onRowClicked(item.index);
            }
        });

        int levelPadding = 30;
        row.setPadding(item.level * levelPadding, 0, 0, 0);

        ImageView expandIcon = (ImageView) row.findViewById(R.id.image);
        if (item.childrenCount > 0) {
            expandIcon.setVisibility(View.VISIBLE);
        } else {
            expandIcon.setVisibility(View.INVISIBLE);
        }

        // on click for a checkbox
        CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListItemClickCallback.onCheckboxClicked(item.index, isChecked);
            }
        });

        TextView textView = (TextView) row.findViewById(R.id.text);
        //ImageView expandIcon = (ImageView) row.findViewById(R.id.image);

        //allItems.get(allItems.get(position).index);

        textView.setText(item.name);

        //        SpinnerItem.CheckboxState state = SpinnerItem.CheckboxState.UNCHECKED;
        //        SpinnerItem item = mItems.get(position);
        //        if (item instanceof SpinnerItemHeader) {
        //            int selectedCount = getSelectedItemsCount((SpinnerItemHeader) item);
        //            if (selectedCount > 0) {
        //                int totalItems = getChildCount((SpinnerItemHeader) item);
        //                if (selectedCount == totalItems) {
        //                    state = SpinnerItem.CheckboxState.CHECKED;
        //                } else {
        //                    state = SpinnerItem.CheckboxState.SEMICHECKED;
        //                }
        //            }
        //
        //            expandIcon.setVisibility(View.VISIBLE);
        //            if (item.isExpanded()) {
        //                expandIcon.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.arrow_down_float));
        //            } else {
        //                expandIcon.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.arrow_up_float));
        //            }
        //            row.setPadding(0, 0, 0, 0);
        //        } else {
        //            expandIcon.setVisibility(View.GONE);
        //            row.setPadding(30, 0, 0, 0);
        //        }
        //
        //        if (state == SpinnerItem.CheckboxState.CHECKED) {
        //            checkbox.setChecked(true);
        //        } else if (state == SpinnerItem.CheckboxState.SEMICHECKED) {
        //            checkbox.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_bright));
        //        } else {
        //            checkbox.setChecked(false);
        //        }

        //textView.setText(item.name);

        return row;
    }

    private CategoryNode getItemAtPosition(int position) {
        if (position == 0) {
            return allItems.get(0);
        }
        List<CategoryNode> visibleItems = new ArrayList<>();
        for (CategoryNode node : allItems) {
            if (node.visible) {
                visibleItems.add(node);
            }
        }
        return visibleItems.get(position);

        //        int calculatedPosition = position;
        //        for (int i=position;i<allItems.size();++i) {
        //            if (!allItems.get(i).visible) {
        //                ++calculatedPosition;
        //            } else {
        //                break;
        //            }
        //        }
        //        return allItems.get(calculatedPosition);
    }

    private int getChildCount(SpinnerItemHeader header) {
        int count = 0;
        for (SpinnerItem item : header.getChildren()) {
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
        for (CategoryNode item : allItems) {
            if (item.expanded || item.level == 0) {
                ++count;
            }
        }
        return count;
    }

}
