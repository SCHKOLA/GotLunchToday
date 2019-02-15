package de.schkola.kitchenscanner.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getArguments().getInt(ARG_LAYOUT), container, false);
    }
}
