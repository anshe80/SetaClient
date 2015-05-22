package com.seta.android.fragment;

import com.astuetz.PagerSlidingTabStrip;
import com.seta.android.recordchat.R;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by ling on 2015/4/29.
 */
public class mainFragment extends Fragment {
    @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setRetainInstance(true);    
   }

	public static final String TAG=mainFragment.class.getSimpleName();
	public static final mainFragment newInstance()
	{
		return new mainFragment();
		
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.mainfragment,container,false);

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onViewCreated(view, savedInstanceState);
    	PagerSlidingTabStrip tabs=(PagerSlidingTabStrip) view.findViewById(R.id.tabs);
    	ViewPager pager=(ViewPager) view.findViewById(R.id.id_viewpager);
    	MyPagerAdapter adapter=new MyPagerAdapter(getChildFragmentManager());
    	pager.setAdapter(adapter);
    	tabs.setViewPager(pager);
    }
    public class MyPagerAdapter extends FragmentPagerAdapter
    {
    	public MyPagerAdapter(android.support.v4.app.FragmentManager fm)
    	{
    		super(fm);
    		
    	}
    	private final String[] TITLES={"广场","群组"};
    	public CharSequence getPageTitle(int position)
    	{
    		return TITLES[position];
    	}
		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			switch (position) {
			case 0:
				return publicFragment.newInstance();
				
				

			case 1:
				return groupFragment.newInstance();
			}
			return null;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return TITLES.length;
		}
    	
    }
}
