package com.seta.android.activity;

import java.util.ArrayList;


import com.seta.android.fragment.accountManagerFragment;
import com.seta.android.fragment.mainFragment;
import com.seta.android.fragment.filemanagerFragment;
import com.seta.android.fragment.thirdFragment;
import com.sys.android.xmpp.R;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/*
 * create by ling on 2015.4.29*/
public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{
	private DrawerLayout mDrawerLayout;
    private ListView leftDrawerList;
    private ListView rightDrawerList;
    private ArrayList<String> menuList;
    private ArrayAdapter<String> adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
	    mTitle=(String)getTitle();
	    mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
	    leftDrawerList=(ListView)findViewById(R.id.left_drawer);
	    menuList=new ArrayList<String>();
	    menuList.add(getString(R.string.mainpage));
	    menuList.add(getString(R.string.manage_ID));
	    menuList.add(getString(R.string.manage_files));
	    menuList.add(getString(R.string.private_notes));
	    adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,menuList);
	    leftDrawerList.setAdapter(adapter);
	    leftDrawerList.setOnItemClickListener(this);
	    mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.database,R.string.drawer_open,R.string.drawer_close)
	    {
	        @Override
	        public void onDrawerOpened(View drawerView) {
	        super.onDrawerOpened(drawerView);
	        getSupportActionBar().setTitle("Seta");
	        invalidateOptionsMenu();//call onPrepareOptionmenu()
	    }

	        @Override
	        public void onDrawerClosed(View drawerView) {
	        super.onDrawerClosed(drawerView);
	        getSupportActionBar().setTitle(mTitle);
	        invalidateOptionsMenu();
	    }
	    };
	    mDrawerLayout.setDrawerListener(mDrawerToggle);
	    //开启ActionBar上APP的图标功能
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setHomeButtonEnabled(true);
	}
	 @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	        boolean isDrawerOpen=mDrawerLayout.isDrawerOpen(leftDrawerList);
	        menu.findItem(R.id.action_settings).setVisible(!isDrawerOpen);
	        return super.onPrepareOptionsMenu(menu);
	    }
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.menu_main, menu);
	        return true;
	    }
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        //将ActionBar上的图标与Drawer结合起来
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        if(mDrawerToggle.onOptionsItemSelected(item))
	        {
	            return true;
	        }
	        int id = item.getItemId();

	        //noinspection SimplifiableIfStatement
	        if (id == R.id.action_settings) {
	            return true;
	        }

	        return super.onOptionsItemSelected(item);
	    }

	    @Override
	    public void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        //需要将ActionDrawerToggle与DrawerLayout的状态同步
	        //将ActionBarDrawerToggle中的drawer图标，设置为ActionBar中的Home_Button的Icon
	        mDrawerToggle.syncState();
	    }

	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        //动态插入一个fragment到FrameLayout中
	        switch (position){
	            case 0:{
	                Fragment mFragment=new mainFragment();
	                FragmentManager fragmentManager=getFragmentManager();
	                fragmentManager.beginTransaction().replace(R.id.content_frame,mFragment).commit();
	                mDrawerLayout.closeDrawer(leftDrawerList);
	                break;
	            }
	            case 1:
	            {
	                Fragment mFragment=new accountManagerFragment();
	                FragmentManager fragmentManager=getFragmentManager();
	                fragmentManager.beginTransaction().replace(R.id.content_frame,mFragment).commit();
	                mDrawerLayout.closeDrawer(leftDrawerList);
	                break;
	            }
	            case 2:
	            {
	                Fragment mFragment=new filemanagerFragment();
	                FragmentManager fragmentManager=getFragmentManager();
	                fragmentManager.beginTransaction().replace(R.id.content_frame,mFragment).commit();
	                mDrawerLayout.closeDrawer(leftDrawerList);
	                break;
	            }
	            case 3:
	            {
	                Fragment mFragment=new thirdFragment();
	                FragmentManager fragmentManager=getFragmentManager();
	                fragmentManager.beginTransaction().replace(R.id.content_frame,mFragment).commit();
	                mDrawerLayout.closeDrawer(leftDrawerList);
	                break;
	            }

	        }

	        }


}
