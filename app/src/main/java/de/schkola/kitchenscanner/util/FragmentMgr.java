package de.schkola.kitchenscanner.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMgr extends Fragment {

    private static final String ARG_LAYOUT = "layout";

    public static FragmentMgr newInstance(int layout) {
        FragmentMgr fragment = new FragmentMgr();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layout);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getArguments().getInt(ARG_LAYOUT), container, false);
    }
}
