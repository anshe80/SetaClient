package com.seta.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.seta.android.entity.ServerRooms;
import com.seta.android.recordchat.R;
import com.seta.android.xmppmanager.XmppConnection;
import com.seta.android.xmppmanager.XmppService;
import com.sys.android.util.MutilUserChatUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressWarnings("all")
public class CreatRoomActivity extends ActionBarActivity{
	
	private String pUSERID;//当前用户
	private Button create_button;
//	private Button goback_button;
	private String queryResult="";
	//private ServerRooms queryResult=new ServerRooms();
	private ListView list;
	//add by ling 2015.5.19
	private List<ServerRooms> roomList;
	//end
	//add by anshe 2015.7.5
	private XMPPConnection connection =null;
    private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private String  mTitle="";
	//end add by anshe 2015.7.5
	//Roster roster = XmppConnection.getConnection().getRoster();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.create_room);

		connection = XmppConnection.getConnection(this);
		if (connection!=null) {
			this.pUSERID =connection.getUser();
		}
		list = (ListView) findViewById(R.id.testlistshow);
		create_button = (Button) findViewById(R.id.create_button);
		create_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//modify by ling 2015.5.19
				  createRoom();		
				//end modify by ling 2015.5.19
				}
		});
		//start add by anshe 2015.7.5
		/*mTitle=(String)getTitle();
	    mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.database,R.string.drawer_open,R.string.drawer_close)
	    {
	        @Override
	        public void onDrawerOpened(View drawerView) {
		        Intent intent = new Intent(CreatRoomActivity.this, MainActivity.class);
				intent.putExtra("pUSERID", pUSERID);
				startActivity(intent);        
		        CreatRoomActivity.this.finish(); 
		        System.out.println("打开界面");
		    }

	        @Override
	        public void onDrawerClosed(View drawerView) {
		        super.onDrawerClosed(drawerView);
		        getSupportActionBar().setTitle(mTitle);
		        invalidateOptionsMenu();
		        System.out.println("关闭界面");
	        }
	    };*/
		// 开启ActionBar上APP的图标功能
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportParentActivityIntent();
		//end add by anshe 2015.7.5
	}
	
	//start add by anshe 2015.7.6
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	//end add by anshe 2015.7.6
	
	/**
	 * author ling 
	 * date 2015.5.19
	 * 
	 * */
	public void createRoom() {
		String search_text = null;
		queryResult = "";
		search_text = ((EditText) findViewById(R.id.search_text)).getText().toString();

		if (search_text.equals("")) {
			Toast.makeText(CreatRoomActivity.this, getString(R.string.info_null), Toast.LENGTH_SHORT).show();
		} else {			
			if (connection==null) {
				connection=XmppConnection.getConnection(this);
				if (connection!=null) {
					this.pUSERID=connection.getUser();
				}				
			}
			final MutilUserChatUtil muc=new MutilUserChatUtil(connection);
			try {
				ServerRooms serverRooms = new ServerRooms();
				if (muc != null) {
					roomList = muc.getConferenceRoom();
					for (int i = 0; i < roomList.size(); i++) {
						serverRooms = roomList.get(i);
						if (serverRooms.getName().equals(search_text.trim())) {
							queryResult = serverRooms.getName();
							break;
						}
					}
					if (!queryResult.equals("")) {
						Toast.makeText(CreatRoomActivity.this,
								getString(R.string.room_is_exist),
								Toast.LENGTH_SHORT).show();
						// 生成动态数组，加入数据
						ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("groupName", queryResult); // 聊天室名
						listItem.add(map);
						// 生成适配器的Item和动态数组对应的元素
						SimpleAdapter listItemAdapter = new SimpleAdapter(this,
								listItem,// 数据源
								R.layout.group_search_view,// ListItem的XML实现
								// 动态数组与ImageItem对应的子项
								new String[] { "groupName", },
								// ImageItem的XML文件里面的一个ImageView,两个TextView ID
								new int[] { R.id.itemtext });
						// 添加并且显示
						list.setAdapter(listItemAdapter);
						// 添加短点击事件
						list.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								HashMap<String, String> map = (HashMap<String, String>) list
										.getItemAtPosition(position);
								final String groupName = map.get("groupName");
								AlertDialog.Builder dialog = new AlertDialog.Builder(
										CreatRoomActivity.this);
								dialog.setTitle("加入聊天室")
										.setIcon(R.drawable.icon)
										.setMessage(
												"该聊天室已存在，你是否加入【" + groupName
														+ "】")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														// TODO Auto-generated
														// method stub
														String GroupName = groupName;
														// muc.joinMultiUserChat(pUSERID,
														// GroupName, "");
														Intent intent = new Intent(
																CreatRoomActivity.this,
																ChatActivity.class);
														intent.putExtra(
																"FRIENDID",
																GroupName);
														intent.putExtra("user",
																pUSERID);
														intent.putExtra(
																"USERID",
																pUSERID);
														startActivity(intent);
														finish();

													}
												})
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														// TODO Auto-generated
														// method stub
														dialog.cancel();// 取消弹出框
													}
												}).create().show();
							}
						});
					} else {
						list.setAdapter(null);
						String groupName = search_text;
						// 创建新聊天室
						muc.createRoom(pUSERID, groupName, "").leave();
						Toast.makeText(CreatRoomActivity.this,
								"成功创建：" + groupName, Toast.LENGTH_SHORT).show();

						// 只要跳转到ChatActivity都会把用户重新加入MUC
						Intent intent = new Intent(CreatRoomActivity.this,
								ChatActivity.class);
						intent.putExtra("FRIENDID", groupName);
						intent.putExtra("user", pUSERID);
						intent.putExtra("USERID", pUSERID);
						startActivity(intent);
						finish();
					}
				}else{
					Toast.makeText(CreatRoomActivity.this,
							getString(R.string.not_Connect_to_Server), Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
			
	}
	//add by ling 2015.5.21
	//start modify by anshe 2015.5.28
	@Override
    public void onBackPressed() { 

		Intent intent = new Intent(CreatRoomActivity.this, MainActivity.class);
		intent.putExtra("pUSERID", pUSERID);
		startActivity(intent);        
        CreatRoomActivity.this.finish(); 
         
   }
	//end modify by anshe 2015.5.28
	
	//start add by anshe 2015.7.5
	  @Override
	    public void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        //需要将ActionDrawerToggle与DrawerLayout的状态同步
	        //将ActionBarDrawerToggle中的drawer图标，设置为ActionBar中的Home_Button的Icon
	        //mDrawerToggle.syncState();
	    }

	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }
	//end add by anshe 2015.7.5
}
