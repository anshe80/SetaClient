package com.seta.android.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import com.seta.android.activity.adapter.UserListAdapter;
import com.seta.android.fragment.accountManagerFragment;
import com.seta.android.fragment.filemanagerFragment;
import com.seta.android.fragment.mainFragment;
import com.seta.android.fragment.privateFragment;
import com.seta.android.fragment.settingFragment;
import com.seta.android.recordchat.R;
import com.seta.android.xmppmanager.XmppConnection;
import com.sys.android.util.MutilUserChatUtil;
import com.sys.android.util.NetWorkConnection;
import com.sys.android.util.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/*
 * create by ling on 2015.4.29*/
public class MainActivity extends ActionBarActivity implements
		AdapterView.OnItemClickListener {
	private DrawerLayout mDrawerLayout;
	private ListView leftDrawerList;
	private ListView rightDrawerList;
	private ArrayList<String> menuList;
	private ArrayAdapter<String> adapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private String mTitle;

	private XMPPConnection conn = null;
	private MultiUserChat publicRoomMuc = null;
	final String PUBLICROOM = "publicroom";
	// start add by anshe 2015.7.8
	private ParticipantStatus participantStatusListener = null;
	UserListAdapter userListAdapter = null;
	private String TAG = MainActivity.class.getSimpleName() + "广场监听的结果";
	private MyPacketListener myPacketListener;
	private mainFragment mainFragment;
	private accountManagerFragment accountManagerFragment;
	private filemanagerFragment filemanagerFragment;
	private privateFragment privateFragment;
	private settingFragment settingFragment;
	String userName = null;
	List<String> userNameList = new ArrayList<String>();
	boolean startListener=false;
	// end add by anshe 2015.7.8

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			mainFragment mFragment = new mainFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, mFragment).commit();
		}
		initView();
	}

	// start add by anshe 2015.7.5
	protected void initView() {
		// 把登录后的用户都加入到公共聊天室
		conn = XmppConnection.getConnection(this);
		if (conn != null && conn.getUser() != null) {
			userName = conn.getUser();
		} /*
		 * else { userName = this.getIntent().getStringExtra("pUSERID"); }
		 */
		// System.out.println("当前用户："+userName);
		// 创建公共聊天室，并把所有登录用户加入其中
		if (conn != null && this.publicRoomMuc == null) {
			(new joinRoom(conn, PUBLICROOM, userName)).start();
		} else {
			if (conn == null) {
				Toast.makeText(MainActivity.this,
						getString(R.string.not_Connect_to_Server),
						Toast.LENGTH_SHORT).show();
			}
		}
		if (!userNameList.contains(userName)) {
			userNameList.add(userName);
		}
		mTitle = (String) getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftDrawerList = (ListView) findViewById(R.id.left_drawer);
		rightDrawerList = (ListView) findViewById(R.id.right_drawer);

		userListAdapter = new UserListAdapter(this, userNameList);
		rightDrawerList.setAdapter(userListAdapter);
		menuList = new ArrayList<String>();
		menuList.add(getString(R.string.mainpage));
		menuList.add(getString(R.string.manage_ID));
		menuList.add(getString(R.string.manage_files));
		menuList.add(getString(R.string.private_notes));
		menuList.add(getString(R.string.menu_settings));
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
				mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
						GravityCompat.START);
				if (mDrawerLayout.isDrawerOpen(rightDrawerList)) {
					mDrawerLayout.closeDrawer(leftDrawerList);
					if (!startListener) {
						new Thread(new Runnable() {

							@Override
							public void run() {

								/*
								 * Message message = new Message(); message.what =1;
								 * handler.sendMessage(message);
								 */

								System.out.println("正在刷新用户列表！");
								handler.postDelayed(LOAD_DATA, 500);
							}
						}).start();
					}else {
						userListAdapter.notifyDataSetChanged();
					}					
				}
				invalidateOptionsMenu();// call onPrepareOptionmenu()
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				// (new joinRoom(conn, PUBLICROOM, userName)).start();
				if (!mDrawerLayout.isDrawerVisible(rightDrawerList)) {
					
					if (!startListener) {
						System.out.println("停止刷新用户列表！");
						handler.removeCallbacks(LOAD_DATA);						
					}
				}
				if (mDrawerLayout.isDrawerOpen(rightDrawerList)) {
					mDrawerLayout.closeDrawer(leftDrawerList);
					userListAdapter.notifyDataSetChanged();
				} else if (mDrawerLayout.isDrawerOpen(leftDrawerList)) {
					mDrawerLayout.closeDrawer(rightDrawerList);
				}

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
		// right_drawer();
	}

	// end add by anshe 2015.7.5
	/*
	 * //start add by anshe2015.7.11 //左边菜单开关事件
	 * 
	 * public void openLeftLayout() {
	 * 
	 * if (drawerLayout.isDrawerOpen(left_menu_layout)) {
	 * 
	 * drawerLayout.closeDrawer(left_menu_layout);
	 * 
	 * } else {
	 * 
	 * drawerLayout.openDrawer(left_menu_layout);
	 * 
	 * 
	 * 
	 * }
	 * 
	 * }
	 * 
	 * // 右边菜单开关事件
	 * 
	 * public void openRightLayout() {
	 * 
	 * if (drawerLayout.isDrawerOpen(right_xiaoxi_layout)) {
	 * 
	 * drawerLayout.closeDrawer(right_xiaoxi_layout);
	 * 
	 * } else {
	 * 
	 * drawerLayout.openDrawer(right_xiaoxi_layout);
	 * 
	 * }
	 * 
	 * } //end add by anshe 2015.7.11
	 */
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
	public MultiUserChat JoinCommonGroup(XMPPConnection conn, String groupName,
			String userName) {

		MutilUserChatUtil mucUtil = new MutilUserChatUtil(conn);
		MultiUserChat muc = null;
		if (NetWorkConnection.isNetworkAvailable(this)) {
			muc = mucUtil.joinMultiUserChat(userName, groupName, "");
			if (muc == null) {
				muc = mucUtil.createRoom("admin", groupName, "");
				System.out.println("聊天室不存在！创建情况：" + muc);
			}

			if (muc != null) {
				System.out.println("开始加入监听" + muc.getRoom()+"监听结果："+startListener);
				participantStatusListener = new ParticipantStatus();
				myPacketListener = new MyPacketListener();
				/*
				 * muc.addParticipantListener(myPacketListener);
				 * muc.addParticipantStatusListener(participantStatusListener);
				 */
				muc.addParticipantStatusListener(participantStatusListener);
			}
		}
		System.out.println("连接用户：" + conn.getUser() + "本地用户：" + userName
				+ "groupName=" + groupName);
		return muc;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(leftDrawerList);
		//menu.findItem(R.id.action_settings).setVisible(!isDrawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// do something
			System.out.println("单击菜单键："+event.isCanceled());
			if (!mDrawerLayout.isDrawerOpen(leftDrawerList)) {
				mDrawerLayout.openDrawer(leftDrawerList);
				mDrawerLayout.closeDrawer(rightDrawerList);		
			}else if (mDrawerLayout.isDrawerOpen(leftDrawerList)) {
				mDrawerLayout.closeDrawer(rightDrawerList);
				mDrawerLayout.closeDrawer(leftDrawerList);	
			}	
			
		}
		return super.onKeyDown(keyCode, event);
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
		/*case R.id.accountManagerFragment:
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
			break;*/
		default:
			break;
		}
		return true;
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// 需要将ActionDrawerToggle与DrawerLayout的状态同步
		// 将ActionBarDrawerToggle中的drawer图标，设置为ActionBar中的Home_Button的Icon
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 动态插入一个fragment到FrameLayout中
		switch (position) {
		case 0: {
			/*
			 * if(mainFragment==null){ mainFragment = new mainFragment(); }
			 */
			mainFragment = new mainFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, mainFragment).commit();
			mDrawerLayout.closeDrawer(leftDrawerList);
			break;
		}
		case 1: {
			/*
			 * if (accountManagerFragment==null) { accountManagerFragment = new
			 * accountManagerFragment(); }
			 */
			accountManagerFragment = new accountManagerFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, accountManagerFragment)
					.commit();
			mDrawerLayout.closeDrawer(leftDrawerList);
			break;
		}
		case 2: {
			/*
			 * if (filemanagerFragment==null) { filemanagerFragment = new
			 * filemanagerFragment(); }
			 */
			filemanagerFragment = new filemanagerFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, filemanagerFragment).commit();
			mDrawerLayout.closeDrawer(leftDrawerList);
			break;
		}
		case 3: {
			/*
			 * if (privateFragment==null) { privateFragment = new
			 * privateFragment(); }
			 */
			privateFragment = new privateFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, privateFragment).commit();
			mDrawerLayout.closeDrawer(leftDrawerList);
			break;
		}
		case 4: {
			/*
			 * if (settingFragment==null) { settingFragment = new
			 * settingFragment(); }
			 */
			settingFragment = new settingFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, settingFragment).commit();
			mDrawerLayout.closeDrawer(leftDrawerList);
			break;
		}
		}
	}

	// delete by anshe 2015.5.17
	/*
	 * public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * 
	 * if (keyCode== KeyEvent.KEYCODE_BACK) { XmppConnection.closeConnection();
	 * finish(); return true; } return super.onKeyDown(keyCode, event); }
	 */
	// end delete by anshe 2015.5.17

	// start add by anshe 2015.5.17
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("确认退出吗？")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“确认”后的操作
						if (conn != null && conn.getUser() != null
								&& publicRoomMuc != null) {
							publicRoomMuc.leave();
						}
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

	// end add by anshe 2015.5.17

	// start add by anshe 2015.5.23
	/*
	 * 用于监控断线 3秒检测一次
	 */
	public class reConnnectionListener extends Thread {

		public void run() {
			while (XmppConnection.getConnection(null).isConnected()) {
				try {
					sleep(3 * 1000);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	// end add by anshe 2015.5.23

	// start add by anshe 2015.7.8
	class joinRoom extends Thread {
		XMPPConnection conn;
		String groupName;
		String userName;

		public joinRoom(XMPPConnection conn, String groupName, String userName) {
			this.conn = conn;
			this.groupName = groupName;
			this.userName = userName;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("正在加入聊天室..开始进行。。。。");
			publicRoomMuc = JoinCommonGroup(conn, groupName, userName);
			right_drawer(publicRoomMuc);
			System.out.println("成功加入聊天室..完成。。。。");
		}
	}

	// end add by anshe 2015.7.8

	public void right_drawer(MultiUserChat muc) {
		// 获取服务器连接
		if (conn == null || null == muc) {
			Toast.makeText(MainActivity.this,
					getString(R.string.not_Connect_to_Server),
					Toast.LENGTH_SHORT).show();
			return;
		}
		List<String> userNameCahceList = MutilUserChatUtil.findMulitUser(muc);
		if (userNameCahceList != null && userNameCahceList.size() > 0) {
			userNameList.clear();
			userNameList.addAll(userNameCahceList);
			if (!userNameList.contains(userName.split("@")[0])) {
				userNameList.add(userName.split("@")[0]);
			}
			System.out.println("正在修改用户列表！");
			Collections.sort(userNameList);
			userListAdapter.setList(userNameList);
			rightDrawerList.setAdapter(userListAdapter);
			userListAdapter.notifyDataSetChanged();
		}
		System.out.println("当前的用户数量:" + muc.getOccupantsCount() + " 获取到的"
				+ userNameList.size());
		/*
		 * else { Collections.sort(userNameList);
		 * userListAdapter.notifyDataSetChanged(); }
		 */
	}

	// start add by anshe 2015.7.8
	public class MyPacketListener implements PacketListener {

		@Override
		public void processPacket(Packet arg0) {
			// 线上--------------chat
			// 忙碌--------------dnd
			// 离开--------------away
			// 隐藏--------------xa
			Presence presence = (Presence) arg0;
			// PacketExtension pe = presence.getExtension("x",
			// "http://jabber.org/protocol/muc#user");
			String LogKineName = presence.getFrom().toString();
			String kineName = LogKineName
					.substring(LogKineName.indexOf("/") + 1);
			String stats = "";
			if ("chat".equals(presence.getMode().toString())) {
				stats = "[线上]";
			}
			if ("dnd".equals(presence.getMode().toString())) {
				stats = "[忙碌]";
			}
			if ("away".equals(presence.getMode().toString())) {
				stats = "[离开]";
			}
			if ("xa".equals(presence.getMode().toString())) {
				stats = "[隐藏]";
			}
			System.out.println("状态改变成：" + kineName + stats);
			Log.e(TAG, "状态改变成：" + kineName + stats);
			// right_drawer(publicRoomMuc);
			/*
			 * for (int i = 0; i < affiliates.size(); i++) { String name =
			 * affiliates.get(i); if
			 * (kineName.equals(name.substring(name.indexOf("]") + 1))) {
			 * affiliates.set(i, stats + kineName); System.out.println("状态改变成："
			 * + affiliates.get(i)); android.os.Message msg = new
			 * android.os.Message(); msg.what = MEMBER;
			 * handler.sendMessage(msg); break; } }
			 */
		}
	}

	class ParticipantStatus implements ParticipantStatusListener {

		@Override
		public void voiceRevoked(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		@Override
		public void voiceGranted(String participant) {
			// TODO Auto-generated method stub
			startListener=true;

		}

		@Override
		public void ownershipRevoked(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		@Override
		public void ownershipGranted(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		@Override
		public void nicknameChanged(String participant, String newNickname) {
			// TODO Auto-generated method stub
			startListener=true;
			userNameList.remove(participant);
			userNameList.add(newNickname);
			Collections.sort(userNameList);
			userListAdapter.setList(userNameList);
			System.out.println(StringUtils.parseResource(participant)
					+ " is now known as " + newNickname + ".");
		}

		@Override
		public void moderatorRevoked(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		@Override
		public void moderatorGranted(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		@Override
		public void membershipRevoked(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		@Override
		public void membershipGranted(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		@Override
		public void left(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
			boolean remove=userNameList.remove(StringUtils.parseResource(participant));
			Collections.sort(userNameList);
			userListAdapter.setList(userNameList);
			/*rightDrawerList.setAdapter(userListAdapter);*/
			System.out.println(StringUtils.parseResource(participant)
					+ " has left the room."+remove);
		}

		@Override
		public void kicked(String participant, String actor, String reason) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		@Override
		public void joined(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
			if (!userNameList.contains(StringUtils.parseResource(participant))) {
				boolean add=userNameList.add(StringUtils.parseResource(participant));
				Collections.sort(userNameList);
				userListAdapter.setList(userNameList);
				System.out.println(StringUtils.parseResource(participant)
						+ " has joined the room."+add);
			}
		}

		@Override
		public void banned(String participant, String actor, String reason) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		@Override
		public void adminRevoked(String participant) {
			// TODO Auto-generated method stub
			startListener=true;
		}

		
		@Override
		public void adminGranted(String arg0) {
			// TODO Auto-generated method stub
			startListener=true;
		}

	}

	// end add by anshe 2015.7.8

	protected void onResume() {
		super.onResume();
		// initView();
	}

	protected void onDestroy() {
		super.onDestroy();
		conn.removeConnectionListener(XmppConnection.connectionListener);
		if (conn != null && conn.getUser() != null && publicRoomMuc != null) {
			publicRoomMuc.leave();
			publicRoomMuc.removeParticipantListener(myPacketListener);
			publicRoomMuc
					.removeParticipantStatusListener(participantStatusListener);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case 1:
				if (conn != null) {
					if (publicRoomMuc != null) {
						publicRoomMuc.leave();
					}
					publicRoomMuc = JoinCommonGroup(conn, PUBLICROOM, userName);
				}
				right_drawer(publicRoomMuc);
				break;

			default:
				break;
			}
		}
	};

	private Runnable LOAD_DATA = new Runnable() {
		@Override
		public void run() {
			// 在这里讲数据内容加载到Fragment上
			if (conn != null
					&& NetWorkConnection.isNetworkAvailable(getApplication())) {
				System.out.println("正在重新获取用户列表！");
				if (publicRoomMuc != null) {
					publicRoomMuc.leave();
				}
				publicRoomMuc = JoinCommonGroup(conn, PUBLICROOM, userName);
				right_drawer(publicRoomMuc);
			}
		}
	};

}
