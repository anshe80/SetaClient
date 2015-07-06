package com.seta.android.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.seta.android.activity.adapter.UserListAdapter;
import com.seta.android.fragment.accountManagerFragment;
import com.seta.android.fragment.filemanagerFragment;
import com.seta.android.fragment.mainFragment;
import com.seta.android.fragment.privateFragment;
import com.seta.android.recordchat.R;
import com.seta.android.xmppmanager.XmppConnection;
import com.sys.android.util.MutilUserChatUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


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

	private XMPPConnection conn = null;
	private MultiUserChat pubicRoomMuc = null;
	final String PUBLICROOM = "publicroom";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		if (savedInstanceState==null) {
			mainFragment mFragment = new mainFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, mFragment).commit();
		}
		initView();		
	}
	//start add by anshe 2015.7.5
	protected void initView(){
		// 把登录后的用户都加入到公共聊天室
		conn = XmppConnection.getConnection(this);
		String userName = null;
		if (conn != null) {
			userName = conn.getUser();
		} else {
			userName = this.getIntent().getStringExtra("pUSERID");
		}
		// System.out.println("当前用户："+userName);
		// 创建公共聊天室，并把所有登录用户加入其中
		if (conn != null && this.pubicRoomMuc == null) {
			this.pubicRoomMuc = JoinCommonGroup(conn, PUBLICROOM, userName);
		} else {
			if (conn == null) {
				Toast.makeText(MainActivity.this,
						getString(R.string.not_Connect_to_Server),
						Toast.LENGTH_SHORT).show();
			}
		}

		mTitle = (String) getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftDrawerList = (ListView) findViewById(R.id.left_drawer);
		menuList = new ArrayList<String>();
		menuList.add(getString(R.string.mainpage));
		menuList.add(getString(R.string.manage_ID));
		menuList.add(getString(R.string.manage_files));
		menuList.add(getString(R.string.private_notes));
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, menuList);
		leftDrawerList.setAdapter(adapter);
		leftDrawerList.setOnItemClickListener(this);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.database, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle("Seta");
				invalidateOptionsMenu();// call onPrepareOptionmenu()
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}
		};

		if (conn != null) {
			conn.addConnectionListener(XmppConnection.connectionListener);
		}
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		// 开启ActionBar上APP的图标功能
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerLayout.closeDrawer(leftDrawerList);

		// 显示右侧的在线用户列表
		right_drawer();
	}
	//end add by anshe 2015.7.5
	
	/**
	 * 创建公共聊天分组，所有用户登录进去都加入这个分组
	 * 
	 * @author WYG
	 * @param conn
	 *            连接
	 * @param groupName
	 *            = "commonRoom"
	 * @param userName
	 *            用户昵称
	 * 
	 */
	public MultiUserChat JoinCommonGroup(XMPPConnection conn, String groupName, String userName) {
		System.out.println("当前用户：" + conn.getUser()+"未知用户："+userName+"groupName="+groupName);
		MutilUserChatUtil mucUtil = new MutilUserChatUtil(conn);
		MultiUserChat muc = null;
		muc = mucUtil.joinMultiUserChat(userName, groupName, "");
		if (muc == null) {
			muc=mucUtil.createRoom("admin", groupName, "");
			System.out.println("聊天室不存在！创建情况："+muc);
		}

		System.out.println("muc:" + muc);

		return muc;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(leftDrawerList);
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
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fx = fragmentManager.beginTransaction();
		Fragment mFragment = null;

		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			break;
		case R.id.accountManagerFragment:
			mFragment = new accountManagerFragment();
			fx.replace(R.id.content_frame, mFragment);
			fx.addToBackStack(null);
			fx.commit();
			break;
		case R.id.GroupSquareListFragment:
			mFragment = new mainFragment();
			fx.replace(R.id.content_frame, mFragment);
			fx.addToBackStack(null); // 将此fragment加入到回退栈
			fx.commit();
			break;
		case R.id.filemanagerFragment:
			mFragment = new filemanagerFragment();
			fx.replace(R.id.content_frame, mFragment);
			fx.addToBackStack(null);
			fx.commit();
			break;
		case R.id.IatDemoFragment:
			mFragment = new privateFragment();
			fx.replace(R.id.content_frame, mFragment);
			fx.addToBackStack(null);
			fx.commit();
			break;
		default:
			break;
		}
		return true;
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
                    mainFragment mFragment=new mainFragment();
	                FragmentManager fragmentManager=getSupportFragmentManager();
	                fragmentManager.beginTransaction().replace(R.id.content_frame,mFragment).commit();
	                mDrawerLayout.closeDrawer(leftDrawerList);
	                break;
	            }
	            case 1:
	            {
	                accountManagerFragment mFragment=new accountManagerFragment();
	                FragmentManager fragmentManager=getSupportFragmentManager();
	                fragmentManager.beginTransaction().replace(R.id.content_frame,mFragment).commit();
	                mDrawerLayout.closeDrawer(leftDrawerList);
	                break;
	            }
	            case 2:
	            {
	                filemanagerFragment mFragment=new filemanagerFragment();
	                FragmentManager fragmentManager=getSupportFragmentManager();
	                fragmentManager.beginTransaction().replace(R.id.content_frame,mFragment).commit();
	                mDrawerLayout.closeDrawer(leftDrawerList);
	                break;
	            }
	            case 3:
	            {
	                privateFragment mFragment=new privateFragment();
	                FragmentManager fragmentManager=getSupportFragmentManager();
	                fragmentManager.beginTransaction().replace(R.id.content_frame,mFragment).commit();
	                mDrawerLayout.closeDrawer(leftDrawerList);
	                break;
	            }
	        }
	   }
	    //delete by anshe 2015.5.17
	    /*public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	
	    	if (keyCode== KeyEvent.KEYCODE_BACK) {
	    			XmppConnection.closeConnection();
	    			finish();
	    			return true;
	    	}
	    	return super.onKeyDown(keyCode, event);
	    }*/
	  //end delete by anshe 2015.5.17
	    
	    //start add by anshe 2015.5.17
	    public void onBackPressed() { 
	        new AlertDialog.Builder(this).setTitle("确认退出吗？") 
	            .setIcon(android.R.drawable.ic_dialog_info) 
	            .setPositiveButton("确定", new DialogInterface.OnClickListener() { 
	         
	                @Override 
	                public void onClick(DialogInterface dialog, int which) { 
	                // 点击“确认”后的操作 
		    			XmppConnection.closeConnection();
	                    MainActivity.this.finish(); 
	                    System.exit(0);
	                } 
	            }) 
	            .setNegativeButton("返回", new DialogInterface.OnClickListener() { 
	         
	                @Override 
	                public void onClick(DialogInterface dialog, int which) { 
	                // 点击“返回”后的操作,这里不设置没有任何操作 
	                } 
	            }).show(); 
       }
	    //end add by anshe 2015.5.17
	    
	    //start add by anshe 2015.5.23
	    /*
	     * 用于监控断线
	     * 15秒检测一次
	     * */
	    public class  reConnnectionListener extends Thread {
			
	            public void run() {
	                while(XmppConnection.getConnection(null).isConnected()){
	                    try {
	                        sleep(15*1000);
	                 
	                    } catch (InterruptedException e) {
	                        // TODO Auto-generated catch block
	                        e.printStackTrace();
	                    }
	                }
	            };	        
	    }
	    //end add by anshe 2015.5.23
	    
	    
		public void right_drawer() {
			// 获取服务器连接
			if (conn == null) {
				conn = XmppConnection.getConnection(this);
			}
			if (conn==null||null == this.pubicRoomMuc) {
				Toast.makeText(MainActivity.this,
						getString(R.string.not_Connect_to_Server), Toast.LENGTH_SHORT).show();
				return;
			}
			List<String> userNameList = MutilUserChatUtil.findMulitUser(this.pubicRoomMuc);
			Collections.sort(userNameList);
			for(int i=0;i<userNameList.size();i++)
				System.out.println(userNameList.get(i));

			rightDrawerList = (ListView) findViewById(R.id.right_drawer);			
			UserListAdapter adapter = new UserListAdapter(this, userNameList);
			rightDrawerList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
		
		protected void onResume() {
			super.onResume();
			//initView();
		}
		
		protected void onDestroy(){
			super.onDestroy();
			conn.removeConnectionListener(XmppConnection.connectionListener);
		}
}
