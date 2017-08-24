package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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

    private static final int DEFAULT_LEVEL_INDEX = 30;

    private List<CategoryNode> allItems; // flat hierarchy list
    private LayoutInflater mInflater;
    private int levelIntend; // allows to set custom item intend (padding) based on the item level
    private int itemIndex; // index of item (allows to easily find desired item based on clicked item position)

    public MultiLevelSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, List<SpinnerItem> items) {
        super(context, resource);
        itemIndex = 0;
        // convert a list (which is actually a tree) into flat hierarchy - start with level and index 0
        allItems = flatList(items, 0);
        // get inflater for further usage
        mInflater = LayoutInflater.from(context);
        levelIntend = DEFAULT_LEVEL_INDEX;
    }

    /**
     * Allows to set different intend for items at higher levels.
     *
     * @param intendInPixels desired intend in pixels
     */
    public void setLevelIntend(int intendInPixels) {
        levelIntend = intendInPixels;
    }

    /**
     * Get all checked items.
     *
     * @return Return a list of checked items.
     */
    public List<CategoryNode> getCheckedItems() {
        List<CategoryNode> checkedItems = new ArrayList<>();
        for (CategoryNode node : allItems) {
            if (node.checkboxState == SpinnerItem.CheckboxState.CHECKED) {
                checkedItems.add(node);
            }
        }
        return checkedItems;
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

    private View getCustomView(final int position, View convertView, ViewGroup parent) {

        View row = mInflater.inflate(R.layout.custom_spinner_item, parent, false);

        final CategoryNode item = getItemAtPosition(position);

        /******* on click for item row ******/

        // on click for a whole row - fortunately this workarounds the default mechanism which closes the spinner on click
        // note that Spinner.setOnItemSelectedListener() will not work anymore
        row.findViewById(R.id.row_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get item based in the index not the clicked position!
                CategoryNode node = allItems.get(item.index);
                // if is category with sub-items
                if (node.hasChildren()) {
                    // get level of the category
                    int level = node.level;
                    boolean isHide;

                    // check each next item on the list, starting from the item.index + 1
                    for (int i = item.index + 1; i < allItems.size(); ++i) {
                        CategoryNode subnode = allItems.get(i);
                        // determine if we are hiding or showing items - check the first subnode, all others have the same visibility
                        isHide = subnode.visible; // if was visible then we want to hide

                        if (level == subnode.level) {
                            // stop when we reached the item at the same level
                            break;
                        }

                        if (isHide) {
                            // ensure elements on all levels are hidden
                            if (level < subnode.level) {
                                // change the visibility of item - if was visible make it gone and vice versa
                                subnode.visible = false;
                            }
                        } else {
                            // ensure only one level deeper items are affected (we don't want to expand all subcategories if there are any)
                            if (level + 1 == subnode.level) {
                                // change the visibility of item - if was visible make it gone and vice versa
                                subnode.visible = true;
                            }
                        }
                    }

                    // notify to force the spinner to refresh content (expand or collapse items)
                    notifyDataSetChanged();
                }
            }
        });

        /******* intend based on item level ******/

        // padding depends on the level of the item and levelIntend (intent per level)
        row.setPadding(item.level * levelIntend, 0, 0, 0);

        /******* icon for categories ******/

        ImageView expandIcon = (ImageView) row.findViewById(R.id.image);
        if (item.hasChildren()) {
            // draw expand icon for categories only
            expandIcon.setVisibility(View.VISIBLE);
        } else {
            expandIcon.setVisibility(View.INVISIBLE);
        }

        /******* item text ******/

        TextView textView = (TextView) row.findViewById(R.id.text);
        textView.setText(item.name);

        /******* checkbox selection ******/

        CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);

        if (item.hasChildren()) {
            List<CategoryNode> allChildren = getAllChildren(item);
            int checkedChildrenCount = 0;
            for (CategoryNode child : allChildren) {
                if (child.getCheckboxState() == SpinnerItem.CheckboxState.CHECKED) {
                    ++checkedChildrenCount;
                }
            }
            if (allChildren.size() == checkedChildrenCount) {
                item.setCheckboxState(SpinnerItem.CheckboxState.CHECKED);
            } else if (checkedChildrenCount > 0) {
                item.setCheckboxState(SpinnerItem.CheckboxState.SEMICHECKED);
            } else {
                item.setCheckboxState(SpinnerItem.CheckboxState.UNCHECKED);
            }
        }

        if (item.getCheckboxState() == SpinnerItem.CheckboxState.CHECKED) {
            checkbox.setChecked(true);
        } else if (item.getCheckboxState() == SpinnerItem.CheckboxState.SEMICHECKED) {
            checkbox.setChecked(false);
            checkbox.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            checkbox.setChecked(false);
        }

        /******* on click for a checkbox ******/

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setCheckboxState(isChecked ? SpinnerItem.CheckboxState.CHECKED : SpinnerItem.CheckboxState.UNCHECKED);
                if (item.hasChildren()) {
                    // check all children
                    List<CategoryNode> allChildren = getAllChildren(item);
                    for (CategoryNode child : allChildren) {
                        child.setCheckboxState(isChecked ? SpinnerItem.CheckboxState.CHECKED : SpinnerItem.CheckboxState.UNCHECKED);
                    }
                }
                notifyDataSetChanged();
            }
        });

        // finally return the prepared View (a row in the spinner)
        return row;
    }

    /**
     * Converts the tree-like list with {@link SpinnerItem} elements into flat hierarchy list of {@link CategoryNode} items.
     *
     * @param items a tree-like list with {@link SpinnerItem} elements to convert
     * @param level a hierarchy level of items (initially should be 0, then recursively it is incremented for {@link SpinnerItemHeader} elements)
     * @return Returns a flat list of {@link CategoryNode} items.
     */
    private List<CategoryNode> flatList(List<SpinnerItem> items, int level) {
        List<CategoryNode> list = new ArrayList<>();
        for (SpinnerItem item : items) {
            int childCount = 0;
            if (item instanceof SpinnerItemHeader) {
                childCount = getTopLevelChildrenCount((SpinnerItemHeader) item);
            }
            CategoryNode createdNode = new CategoryNode(level, itemIndex, item.getId(), item.getText(), childCount);
            list.add(createdNode);
            ++itemIndex;
            if (item instanceof SpinnerItemHeader) {
                list.addAll(flatList(((SpinnerItemHeader) item).getChildren(), level + 1));
            }
        }
        return list;
    }

    /**
     * Calculate the corresponding item index for the clicked item on the Spinner dropdown list.
     *
     * @param position position of the clicked item on the Spinner dropdown list
     * @return Returns a corresponding CategoryNode object.
     */
    private CategoryNode getItemAtPosition(int position) {
        if (position == 0) {
            // this is always true, item 0 is always level 0 category
            return allItems.get(0);
        }
        // first get list of visible items
        List<CategoryNode> visibleItems = new ArrayList<>();
        for (CategoryNode node : allItems) {
            if (node.visible) {
                visibleItems.add(node);
            }
        }
        // on the visible items list just find the item at position - it was the one clicked
        return visibleItems.get(position);
    }

    /**
     * Counts only top level children. No need to go any deeper.
     *
     * @param header a {@link SpinnerItemHeader} to check
     * @return Returns count of top level children for the given {@link SpinnerItemHeader} element. Can be 0.
     */
    private int getTopLevelChildrenCount(SpinnerItemHeader header) {
        return header.getChildren().size();
    }

    /**
     * Gets list of children on all sub-levels.
     *
     * @param node a {@link CategoryNode} to go through
     * @return Returns all level children for the given {@link CategoryNode} element. Can be empty list.
     */
    private List<CategoryNode> getAllChildren(CategoryNode node) {
        List<CategoryNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (int i = node.index + 1; i < allItems.size(); ++i) {
                CategoryNode subnode = allItems.get(i);
                if (node.level >= subnode.level) {
                    // stop when we reached the item at the same or higher level
                    break;
                }
                children.add(subnode);
            }
        }
        return children;
    }

}
