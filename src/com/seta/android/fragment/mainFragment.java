package com.seta.android.fragment;

import java.util.ArrayList;
import java.util.List;
import com.sys.android.xmpp.R;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by ling on 2015/4/29.
 */
public class mainFragment extends Fragment {
//    @Override
//   public void onCreate(Bundle savedInstanceState) {
//       super.onCreate(savedInstanceState);
//       
//   }

	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.mainfragment,container,false);
//        mViewPager=(ViewPager)view.findViewById(R.id.id_viewpager);
//        mData=new ArrayList<Fragment>();
//        publicFragment publicfragment=new publicFragment();
//        groupFragment groupfragment=new groupFragment();
//        mData.add(publicfragment);
//        mData.add(groupfragment);
//        mAdapter=new FragmentPagerAdapter(getFragmentManager()) {
// 			
// 			@Override
// 			public int getCount() {
// 				// TODO Auto-generated method stub
// 				return mData.size();
// 			}
// 			
// 			@Override
// 			public Fragment getItem(int arg0) {
// 				// TODO Auto-generated method stub
// 				return mData.get(arg0);
// 			}
// 		};
// 		mAdapter.startUpdate(mViewPager);
        return view;
    }
	
    
}
