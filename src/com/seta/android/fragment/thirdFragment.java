package com.seta.android.fragment;

import com.seta.android.recordchat.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ling on 2015/4/29.
 */
public class thirdFragment extends Fragment {
    private TextView tv_third;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.iatdemo, container, false);
        tv_third = (TextView) view.findViewById(R.id.tv_third);
        return view;
    }
}
