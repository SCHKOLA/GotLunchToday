package de.schkola.kitchenscanner.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import de.schkola.kitchenscanner.R;
import java.util.ArrayList;

public class LunchListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final ArrayList<String> getLunchA;
    private final ArrayList<String> getLunchB;
    private final ArrayList<String> getLunchS;

    public LunchListAdapter(Context context, ArrayList<String> getLunchA, ArrayList<String> getLunchB, ArrayList<String> getLunchS) {
        this.context = context;
        this.getLunchA = getLunchA;
        this.getLunchB = getLunchB;
        this.getLunchS = getLunchS;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<String> arrayList = getGroup(groupPosition);
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
        ArrayList<String> collection = getGroup(groupPosition);
        return collection != null ? collection.size() : 0;
    }

    @Override
    public ArrayList<String> getGroup(int groupPosition) {
        switch (groupPosition) {
            case 0:
                return getLunchA;
            case 1:
                return getLunchB;
            case 2:
                return getLunchS;
            default:
                return null;
        }
    }

    @Override
    public int getGroupCount() {
        return 3;
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
                headerTitle = R.string.getLunchA;
                break;
            case 1:
                headerTitle = R.string.getLunchB;
                break;
            case 2:
                headerTitle = R.string.getLunchS;
                break;
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
