package com.seta.android.fragment;
import com.seta.android.recordchat.R;

import android.os.Bundle;
//create by ling 2015.5.5
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class publicFragment extends Fragment {
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.public_layout, container, false);
	        return view;
	    }

	public static Fragment newInstance() {
		// TODO Auto-generated method stub
		return new publicFragment();
	}
}
