package emget.pl.widgets.multilevelspinner;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
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

public class MultiLevelSpinnerAdapter extends ArrayAdapter<SpinnerItem> {

    private static final int DEFAULT_LEVEL_INDEX = 30;

    private List<SpinnerItem> inputItems; // input list
    private List<CategoryNode> allItems; // flat hierarchy list
    private LayoutInflater inflater; // layout inflater
    private int levelIntend; // allows to set custom item intend (padding) based on the item level
    private int itemIndex; // index of item (allows to easily find desired item based on clicked item position)

    private MultiLevelSpinner spinner;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param items    initial list of items to be wrapped into this adapter
     * @param parent   a {@link MultiLevelSpinner} associated with this adapter
     */
    public MultiLevelSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, List<SpinnerItem> items, MultiLevelSpinner parent) {
        super(context, resource);
        itemIndex = 0;
        // convert a list (which is actually a tree) into flat hierarchy - start with level 0 (top level items)
        allItems = flatList(items, 0);
        // get inflater for further usage
        inflater = LayoutInflater.from(context);
        // configurable left intend for items
        levelIntend = DEFAULT_LEVEL_INDEX;
        inputItems = items;
        spinner = parent;
        spinner.setSpinnerEventsListener(new OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened(AppCompatSpinner spinner) {
                // NO-OP
            }

            @Override
            public void onSpinnerClosed(AppCompatSpinner spinner) {
                // we want to propagate items checkboxes state from our internal model to external
                validateCheckedItems();
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
        for (CategoryNode node : allItems) {
            if (node.visible) {
                ++visibleItemsCount;
            }
        }
        return visibleItemsCount;
    }

    /**
     * Gets a custom View. Used in {@link #getView(int, View, ViewGroup)} and {@link #getDropDownView(int, View, ViewGroup)}.
     *
     * @param position    position of the clicked item (note that in most cases it is different than index on the 'allItems' list)
     * @param convertView
     * @param parent
     * @return
     */
    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        // get item based on the index not the clicked position!
        final CategoryNode item = getItemAtPosition(position);
        prepareView(row, item);
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

                // show or hide item's children based in the index not the clicked position!
                hideOrShowNodeChildren(item.index);
            }
        });

        // padding depends on the level of the item and levelIntend (intent per level)
        row.setPadding(item.level * levelIntend, 0, 0, 0);

        // add 'expand' arrow for each item which has children
        ImageView expandIcon = (ImageView) row.findViewById(R.id.image);
        if (item.hasChildren()) {
            // draw expand icon for categories only
            expandIcon.setVisibility(View.VISIBLE);
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
                handleCheckboxStateChange(item, isChecked);
            }
        });
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
                childCount = ((SpinnerItemHeader) item).getChildren().size();
            }
            // create an item of our internal model (flat list) based on the {@link SpinnerItem}
            CategoryNode createdNode = new CategoryNode(level, itemIndex, item.getId(), item.getText(), childCount);
            list.add(createdNode);
            ++itemIndex; // increment node index
            if (item instanceof SpinnerItemHeader) {
                // if we encounter a node with children add all children to flat list recursively (incrementing a level of these sub-nodes)
                list.addAll(flatList(((SpinnerItemHeader) item).getChildren(), level + 1));
            }
        }
        return list;
    }

    /**
     * Ensures the checkboxes states are propagated from the internal model to the external (from {@link CategoryNode} to {@link SpinnerItem}).
     */
    private void validateCheckedItems() {
        for (CategoryNode node : allItems) {
            // we operate on the original input list!
            propagateCheckboxState(inputItems, node.id, node.checkboxState);
        }
    }

    /**
     * Ensures the checkboxes states are propagated from the internal model to the external (from {@link CategoryNode} to {@link SpinnerItem}).
     * Method visits children on all levels recursively.
     *
     * @param items list with states - it will propagate to external model
     * @param id    item ID we are looking for
     * @param state item state which will be set for the corresponding item in the external data model
     * @return Returns true if particular item was found, otherwise returns false.
     */
    private boolean propagateCheckboxState(List<SpinnerItem> items, String id, CheckboxState state) {
        boolean foundItem = false;
        if (items.isEmpty()) {
            return false;
        }
        for (SpinnerItem item : items) {
            if (item.getId().equals(id)) {
                // found match - update it's state
                item.setCheckboxState(state);
                return true;
            }
            if (item instanceof SpinnerItemHeader) {
                // found header -> check it's all children recursively
                foundItem = propagateCheckboxState(((SpinnerItemHeader) item).getChildren(), id, state);
                if (foundItem) {
                    // if match was found on children list stop the loop
                    break;
                }
            }
        }
        return foundItem;
    }

    /**
     * Calculate the corresponding item index (in the internal data model) for the clicked item on the Spinner dropdown list.
     *
     * @param position position of the clicked item on the Spinner dropdown list
     * @return Returns a corresponding CategoryNode object.
     */
    private CategoryNode getItemAtPosition(int position) {
        if (position == 0) {
            // this is always true, item 0 is always level 0 category and always visible (may not be visible for user, but has 'visible = true'
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
     * Gets list of children on all sub-levels.
     *
     * @param parent a {@link CategoryNode} to go through
     * @return Returns all level children for the given {@link CategoryNode} element. Can be empty list.
     */
    private List<CategoryNode> getAllChildren(CategoryNode parent) {
        List<CategoryNode> children = new ArrayList<>();
        if (parent.hasChildren()) {
            for (int i = parent.index + 1; i < allItems.size(); ++i) {
                CategoryNode subnode = allItems.get(i);
                if (subnode.level <= parent.level) {
                    // break when we reach next item at the same level as parent or higher in the tree
                    break;
                }
                children.add(subnode);
            }
        }
        return children;
    }

    /**
     * Gets list of children on direct sub-level only.
     * Introduces better performance than {@link #getAllChildren(CategoryNode)}. Use when you need direct children for the given node.
     *
     * @param parent a {@link CategoryNode} to go through
     * @return Returns direct children for the given {@link CategoryNode} element. Can be empty list.
     */
    private List<CategoryNode> getDirectChildren(CategoryNode parent) {
        List<CategoryNode> children = new ArrayList<>();
        if (parent.hasChildren()) {
            int expectedChildrenLevel = parent.level + 1; // children have one level greater than parent
            for (int i = parent.index + 1; i < allItems.size(); ++i) {
                CategoryNode subnode = allItems.get(i);
                if (subnode.level > expectedChildrenLevel) {
                    // continue when we reach the next level item (this is indirect children - child of direct child - and we don't want them)
                    continue;
                } else if (subnode.level <= parent.level) {
                    // break when we reach next item at the same level as parent or higher in the tree
                    break;
                }
                children.add(subnode);
            }
        }
        return children;
    }

    /**
     * Finds parent for the given item.
     *
     * @param item child for which we are looking for a parent
     * @return Returns a {@link CategoryNode} which is a parent of the given item or null if the item is top-level node.
     */
    private CategoryNode findParent(CategoryNode item) {
        if (item.level == 0) {
            return null;
        }
        // iterate starting from the item before current - order descending until node with the lower level is reached (means a parent)
        for (int i = item.index - 1; i >= 0; --i) {
            if (allItems.get(i).level < item.level) {
                return allItems.get(i);
            }
        }
        return null;
    }

    /**
     * Shows or hides children items for a node at the specified index when a certain position was clicked.
     *
     * @param nodeIndex index of the node - in most cases different from the clicked position
     */
    private void hideOrShowNodeChildren(int nodeIndex) {
        // get item based in the index not the clicked position!
        CategoryNode node = allItems.get(nodeIndex);
        // if is category with sub-items
        if (node.hasChildren()) {
            // get level of the category
            int level = node.level;
            boolean isHide;

            // check each next item on the list, starting from the item.index + 1
            for (int i = nodeIndex + 1; i < allItems.size(); ++i) {
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

    /**
     * Handles changing checkboxes state. Takes children and parent nodes into consideration.
     * @param item an item which checkbox state changes
     * @param isChecked true if checkbox is checked, otherwise false
     */
    private void handleCheckboxStateChange(CategoryNode item, boolean isChecked) {
        // first set the state for the given item itself
        item.checkboxState = isChecked ? CheckboxState.CHECKED : CheckboxState.UNCHECKED;

        // if item has children mark them all the same as the current item - checked or unchecked
        if (item.hasChildren()) {
            // check all children
            List<CategoryNode> allChildren = getAllChildren(item);
            for (CategoryNode child : allChildren) {
                child.checkboxState = isChecked ? CheckboxState.CHECKED : CheckboxState.UNCHECKED;
            }
        }

        // now go up the list and see if a any parent state should change
        // consider the following scenarios:
        // - when any item below parent (direct or indirect child) is checked then parents (up to the top level) are semichecked
        // - when all items below parent (direct or indirect child) are checked then parent is checked but the parent-parent may be only semichecked
        CategoryNode parent;
        CategoryNode currentItem = item;

        // find parent for subsequent levels until top level parent is found
        // important to note: this works recursively from bottom to the top, so the state is set for the lowest levels first
        while ((parent = findParent(currentItem)) != null) {
            // get direct parent
            List<CategoryNode> directChildren = getDirectChildren(parent);
            // calculate checked and semichecked children
            int checkedChildrenCount = 0;
            int semiCheckedChildrenCount = 0;
            for (CategoryNode child : directChildren) {
                if (child.checkboxState == CheckboxState.CHECKED) {
                    ++checkedChildrenCount;
                } else if (child.checkboxState == CheckboxState.SEMICHECKED) {
                    ++semiCheckedChildrenCount;
                }
            }
            // set the actual state based on the number of checked and semichecked children
            if (directChildren.size() == checkedChildrenCount) {
                parent.checkboxState = CheckboxState.CHECKED;
            } else if (checkedChildrenCount > 0 || semiCheckedChildrenCount > 0) {
                parent.checkboxState = CheckboxState.SEMICHECKED;
            } else {
                parent.checkboxState = CheckboxState.UNCHECKED;
            }
            // make current item a parent, so we propagate towards the top (towards the level 0 node)
            currentItem = parent;
        }

        // notify to force the spinner to refresh content (show proper checked/semichecked/unchecked states for children and parents of this node)
        notifyDataSetChanged();
    }

}
