package de.schkola.kitchenscanner.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import de.schkola.kitchenscanner.R;
import java.util.Collections;
import java.util.List;

public class LunchListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> lunchA;
    private final List<String> lunchB;
    private final List<String> lunchS;

    public LunchListAdapter(Context context, List<String> lunchA, List<String> lunchB, List<String> lunchS) {
        this.context = context;
        this.lunchA = lunchA;
        this.lunchB = lunchB;
        this.lunchS = lunchS;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<String> arrayList = getGroup(groupPosition);
        return arrayList == null ? null : arrayList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String name = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView itemName = convertView.findViewById(R.id.item_name);
        itemName.setText(name);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<String> collection = getGroup(groupPosition);
        return collection != null ? collection.size() : 0;
    }

    @Override
    public List<String> getGroup(int groupPosition) {
        switch (groupPosition) {
            case 0:
                if (!lunchA.isEmpty()) {
                    return lunchA;
                }
            case 1:
                if (!lunchB.isEmpty()) {
                    return lunchB;
                }
            case 2:
                if (!lunchS.isEmpty()) {
                    return lunchS;
                }
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public int getGroupCount() {
        int i = 0;
        if (!lunchA.isEmpty()) {
            i++;
        }
        if (!lunchB.isEmpty()) {
            i++;
        }
        if (!lunchS.isEmpty()) {
            i++;
        }
        return i;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        int headerTitle;
        switch (groupPosition) {
            case 0:
                if (!lunchA.isEmpty()) {
                    headerTitle = R.string.getLunchA;
                    break;
                }
            case 1:
                if (!lunchB.isEmpty()) {
                    headerTitle = R.string.getLunchB;
                    break;
                }
            case 2:
                if (!lunchS.isEmpty()) {
                    headerTitle = R.string.getLunchS;
                    break;
                }
            default:
                headerTitle = 0;
        }
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
