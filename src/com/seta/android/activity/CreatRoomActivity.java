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
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
public class CreatRoomActivity extends Activity{
	
	private String pUSERID;//当前用户
	private Button create_button;
//	private Button goback_button;
	private String queryResult="";
	//private ServerRooms queryResult=new ServerRooms();
	private ListView list;
	//add by ling 2015.5.19
	private List<ServerRooms> roomList;
	//end
	//Roster roster = XmppConnection.getConnection().getRoster();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.create_room);
		this.pUSERID = XmppConnection.getConnection().getUser();
		list = (ListView) findViewById(R.id.testlistshow);
		create_button = (Button) findViewById(R.id.create_button);
		create_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//modify by ling 2015.5.19
				  createRoom();
		
				//end
				}
		});		
		
	}
	
	
	
	/**
	 * author ling 
	 * date 2015.5.19
	 * 
	 * */
	public void createRoom() 
	{
		String search_text = ((EditText) findViewById(R.id.search_text)).getText().toString();
		
		if (search_text.equals("")) {
			Toast.makeText(CreatRoomActivity.this, getString(R.string.info_null), Toast.LENGTH_SHORT).show();
		} else {
			final XMPPConnection connection = XmppConnection.getConnection();
			final MutilUserChatUtil muc=new MutilUserChatUtil(connection);
			try {
				roomList=muc.getConferenceRoom();
				ServerRooms serverRooms=new ServerRooms();
				for(int i=0;i<roomList.size();i++)
				{
					serverRooms=roomList.get(i);
					if(serverRooms.getName().equals(search_text))
					{
						queryResult=serverRooms.getName();
						break;
					}
					
				}if(!queryResult.equals("")){
					// 生成动态数组，加入数据
					ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
					    HashMap<String, Object> map = new HashMap<String, Object>();	     
					    map.put("groupName", queryResult); //群组名
						listItem.add(map);
					// 生成适配器的Item和动态数组对应的元素
					SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
							R.layout.group_search_view,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "groupName", },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.itemtext });
					// 添加并且显示
					list.setAdapter(listItemAdapter);
					// 添加短点击事件
					list.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							HashMap<String, String> map = (HashMap<String, String>) list.getItemAtPosition(position);
							final String groupName = map.get("groupName");
							AlertDialog.Builder dialog=new AlertDialog.Builder(CreatRoomActivity.this);
							dialog.setTitle("加入群组")
							      .setIcon(R.drawable.icon)
							      .setMessage("该群组已存在，你是否加入【"+groupName+"】")
							      .setPositiveButton("确定", new DialogInterface.OnClickListener() {
						                     @Override
						                     public void onClick(DialogInterface dialog, int which) {		 
						                         // TODO Auto-generated method stub	
						                    	 String GroupName = groupName;						                    	 
						                    	 muc.joinMultiUserChat(pUSERID, GroupName, "");
						                    	 Intent intent = new Intent(CreatRoomActivity.this, ChatActivity.class);
						     					 intent.putExtra("FRIENDID", GroupName);
						     					 intent.putExtra("user", pUSERID);
						     					 intent.putExtra("USERID",pUSERID);
						     					 startActivity(intent);
						     					 
						                     }
						                   })
							       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
							                 public void onClick(DialogInterface dialog, int which) {			 
							                     // TODO Auto-generated method stub
							                     dialog.cancel();//取消弹出框
							                 }
							               }).create().show();
						       }
					     });	
				  }else{
					  //创建新群组
					  
					  MultiUserChat mmuc=muc.createRoom(pUSERID, search_text, "");
					  Roster roster = XmppConnection.getConnection().getRoster();
                 	  String groupName = search_text;
					  XmppService.addGroup(roster, groupName);
					  Toast.makeText(CreatRoomActivity.this,"成功创建："+groupName, Toast.LENGTH_SHORT).show();
					  XmppService.addUserToGroup(XmppConnection.getConnection().getUser(), groupName, connection);
					  muc.joinMultiUserChat(pUSERID, groupName, "");
                 	 Intent intent = new Intent(CreatRoomActivity.this, ChatActivity.class);
  					 intent.putExtra("FRIENDID", groupName);
  					 intent.putExtra("user", pUSERID);
  					 intent.putExtra("USERID",pUSERID);
  					 startActivity(intent);
					  
					  
				  }
			}catch (Exception e) {
					// TODO: handle exception
					  e.printStackTrace();
				}
			}
				
		
	}
	//add by ling 2015.5.21
	@Override
    public void onBackPressed() { 
        
                    CreatRoomActivity.this.finish(); 
         
   }
}
