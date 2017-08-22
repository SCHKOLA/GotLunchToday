package de.schkola.kitchenscanner.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import de.schkola.kitchenscanner.R;

public class LunchListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private JSONArray getLunchA;
    private JSONArray getLunchB;
    private JSONArray getLunchS;

    public LunchListAdapter(Context context, JSONArray getLunchA, JSONArray getLunchB, JSONArray getLunchS) {
        this.context = context;
        this.getLunchA = getLunchA;
        this.getLunchB = getLunchB;
        this.getLunchS = getLunchS;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        try {
            JSONArray jsonArray = (JSONArray) getGroup(groupPosition);
            return jsonArray == null ? null : jsonArray.getString(childPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
        JSONArray jsonArray = (JSONArray) getGroup(groupPosition);
        return jsonArray != null ? jsonArray.length() : null;
    }

    @Override
    public Object getGroup(int groupPosition) {
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
        String headerTitle;
        switch (groupPosition) {
            case 0:
                headerTitle = "Auszugegebende Essen A";
                break;
            case 1:
                headerTitle = "Auszugegebende Essen B";
                break;
            case 2:
                headerTitle = "Auszugegebende Essen S";
                break;
            default:
                headerTitle = "";
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
