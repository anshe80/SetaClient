package com.seta.android.fragment;

import com.sys.android.xmpp.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ling on 2015/4/29.
 */
public class accountManagerFragment extends Fragment{
    private TextView tv_first;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accountmanagerfragment, container, false);
        return view;
    }
}

