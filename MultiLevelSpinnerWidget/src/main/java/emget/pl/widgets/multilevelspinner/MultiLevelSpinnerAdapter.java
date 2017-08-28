package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MultiLevelSpinnerAdapter extends ArrayAdapter<SpinnerItem> {

    private static final int DEFAULT_LEVEL_INDEX = 30;

    private List<SpinnerItem> inputItemsInTreeHierarchy; // input list
    private List<CategoryNode> convertedItemsInFlatHierarchy; // flat hierarchy list
    private LayoutInflater inflater; // layout inflater
    private int levelIntend; // allows to set custom item intend (padding) based on the item level

    private SpinnerAdapterUtils adapterUtils;
    private MultiLevelSpinner spinner;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param items    initial list of items to be wrapped into this adapter
     */
    public MultiLevelSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, List<SpinnerItem> items) {
        super(context, resource);
        adapterUtils = new SpinnerAdapterUtils();
        // convert a list (which is actually a tree) into flat hierarchy - start with level 0 (top level items)
        convertedItemsInFlatHierarchy = adapterUtils.flatList(items);
        // get inflater for further usage
        inflater = LayoutInflater.from(context);
        // configurable left intend for items
        levelIntend = DEFAULT_LEVEL_INDEX;
        inputItemsInTreeHierarchy = items;

        spinner.setSpinnerEventsListener(new OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened(AppCompatSpinner spinner) {
                // NO-OP
            }

            @Override
            public void onSpinnerClosed(AppCompatSpinner spinner) {
                // we want to propagate items checkboxes state from our internal model to external
                adapterUtils.validateCheckedItems(inputItemsInTreeHierarchy, convertedItemsInFlatHierarchy);
            }
        });
    }

    /**
     * Allows to set different intend (left margin) for items at higher levels.
     *
     * @param intendInPixels desired intend in pixels
     */
    public void setLevelIntend(int intendInPixels) {
        levelIntend = intendInPixels;
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
        for (CategoryNode node : convertedItemsInFlatHierarchy) {
            if (node.visible) {
                ++visibleItemsCount;
            }
        }
        return visibleItemsCount;
    }

    /**
     * Associates with a {@link MultiLevelSpinner}
     *
     * @param parent a {@link MultiLevelSpinner} to be associated with this adapter
     */
    void setParentSpinner(MultiLevelSpinner parent) {
        spinner = parent;
    }

    /**
     * Gets a custom View. Used in {@link #getView(int, View, ViewGroup)} and {@link #getDropDownView(int, View, ViewGroup)}.
     *
     * @param position    position of the clicked item (note that in most cases it is different than index on the 'convertedItemsInFlatHierarchy'
     *                    list)
     * @param convertView
     * @param parent
     * @return
     */
    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        // get item based on the index not the clicked position!
        final CategoryNode item = adapterUtils.getItemAtPosition(convertedItemsInFlatHierarchy, position);
        prepareView(row, item);
//        if(position == 0){
//            row.findViewById(R.id.row_wrapper).setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (v.getParent() != null && v.getParent() instanceof MultiLevelSpinner)
//                        ((MultiLevelSpinner) v.getParent()).performClick();
//                    return true;
//                }
//            });
//        }
        // finally return the prepared View (a row in the spinner)
        return row;
    }

    /**
     * Prepares a View for a particular row on the dropdown list.
     *
     * @param row  a view to be prepared
     * @param item node which is used to populate the view content
     */
    private void prepareView(final View row, final CategoryNode item) {
        // on click for a whole row - fortunately this workarounds the default mechanism which closes the spinner on click
        // note that Spinner.setOnItemSelectedListener() will not work anymore
        row.findViewById(R.id.row_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spinner.isOpened()) {
                    spinner.performClick();
                    // ignore all the rest, we are only opening/expanding the spinner
                    return;
                }

                // change visibility status to show or hide for item's children based on the item index not the clicked position!
                CategoryNode node = convertedItemsInFlatHierarchy.get(item.index);
                // if is category with sub-items
                if (node.hasChildren()) {
                    adapterUtils.updateVisibilityStatus(convertedItemsInFlatHierarchy, node, item.index);
                    // notify to force the spinner to refresh content (expand or collapse items)
                    notifyDataSetChanged();
                }
            }
        });

        // padding depends on the level of the item and levelIntend (intent per level)
        row.setPadding(item.level * levelIntend, 0, 0, 0);

        // add 'expand' arrow for each item which has children
        ImageView expandIcon = (ImageView) row.findViewById(R.id.image);
        if (item.hasChildren()) {
            // draw expand icon for categories with more than 0 items
            expandIcon.setVisibility(View.VISIBLE);
            if (item.expanded) {
                expandIcon.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.arrow_up_float));
            } else {
                expandIcon.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.arrow_down_float));
            }
        } else {
            expandIcon.setVisibility(View.INVISIBLE);
        }

        // set item text
        TextView textView = (TextView) row.findViewById(R.id.text);
        textView.setText(item.name);

        // draw the checkbox selection based on the state: checked/unchecked/semichecked
        CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
        if (item.checkboxState == CheckboxState.CHECKED) {
            checkbox.setChecked(true);
        } else if (item.checkboxState == CheckboxState.SEMICHECKED) {
            checkbox.setChecked(false);
            checkbox.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            checkbox.setChecked(false);
        }

        // on click for a checkbox
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapterUtils.handleCheckboxStateChange(convertedItemsInFlatHierarchy, item, isChecked);
                // notify to force the spinner to refresh content (show proper checked/semichecked/unchecked states for children and parents of
                // this node)
                notifyDataSetChanged();
            }
        });
    }

}
