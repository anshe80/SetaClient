package com.seta.android.fragment;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.seta.android.activity.ChatActivity;
import com.seta.android.activity.ChatConvertActivity;
import com.seta.android.activity.CreatRoomActivity;
import com.seta.android.activity.adapter.RoomListAdapter;
import com.seta.android.entity.ServerRooms;
import com.seta.android.xmppmanager.XmppConnection;
import com.seta.android.recordchat.R;
import com.sys.android.util.MutilUserChatUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


//create by ling 2015.5.5
public class groupFragment extends Fragment {
	

	private List<ServerRooms> mRoomListData;
	private RoomListAdapter mRoomListAdapter;
	private Button creatRoomButton;
	private Activity activity;
	private ListView mRoomListView;
	private View view;
	private XMPPConnection connection;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        view = inflater.inflate(R.layout.group_layout, container, false);
	        getRoomListView();
			activity=this.getActivity();
	        return view;
	    }
	 
	 public void getRoomListView(){	
		 	creatRoomButton = (Button) view.findViewById(R.id.bt_create_group);
			mRoomListView = (ListView) view.findViewById(R.id.lv_group);
			creatRoomButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(activity,CreatRoomActivity.class));
					
				}
			});
			connection=XmppConnection.getConnection(activity);
			if (connection!=null) {
				//modify by anshe 2015.7.8
				final MutilUserChatUtil mutilUserRoomList=new MutilUserChatUtil(connection);
				try {
					mRoomListData = mutilUserRoomList.getConferenceRoom();
					mRoomListAdapter = new RoomListAdapter(this.getActivity(),
							mRoomListData, R.layout.group_friend_list_item_layout);
					mRoomListView.setAdapter(mRoomListAdapter);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//end modify by anshe 2015.7.8
			}
			
			mRoomListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parentView, View v,
						int position, long itemId) {
					/*进入聊天室*/
					ServerRooms room=mRoomListData.get(position);
					if (connection==null) {
						Toast.makeText(activity, getString(R.string.not_Connect_to_Server), Toast.LENGTH_SHORT).show();
						return;
					}else{
						String LoginUser=connection.getUser();
						if (LoginUser!=null) {
							Toast.makeText(activity, "成功加入聊天室["+room.getName()+"]", Toast.LENGTH_SHORT).show();
							Intent ChatListIntent = new Intent(activity, ChatActivity.class);
							ChatListIntent.putExtra("FRIENDID", room.getName());
							ChatListIntent.putExtra("user", LoginUser);
							ChatListIntent.putExtra("USERID",LoginUser);
							startActivity(ChatListIntent);
						}else{
							Toast.makeText(activity, getString(R.string.not_Connect_to_Server), Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		}
		
		public void onResume(){
			getRoomListView();
			super.onResume();
		}

	public static Fragment newInstance() {
		// TODO Auto-generated method stub
		return new groupFragment();
	}
	
	// start add by anshe 2015.7.8
	@Override  
	   public void setUserVisibleHint(boolean isVisibleToUser) {  
	       super.setUserVisibleHint(isVisibleToUser);  
	       if (isVisibleToUser) {  
	           //相当于Fragment的onResume  
	           //加载你的数据
	    	   connection=XmppConnection.getConnection(activity);
				if (connection!=null&&connection.getUser()!=null) {
					//modify by anshe 2015.7.8
					final MutilUserChatUtil mutilUserRoomList=new MutilUserChatUtil(connection);
					try {
						//Toast.makeText(activity, "正在拉取服务器聊天室"+mRoomListData.size(), Toast.LENGTH_SHORT).show();
						mRoomListData = mutilUserRoomList.getConferenceRoom();
						mRoomListAdapter.setmServerRoomsList(mRoomListData);
						mRoomListView.setAdapter(mRoomListAdapter);
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					mRoomListData.clear();
					mRoomListAdapter.setmServerRoomsList(mRoomListData);
					mRoomListAdapter.notifyDataSetChanged();
					Toast.makeText(activity, getString(R.string.not_Connect_to_Server),
							Toast.LENGTH_SHORT).show();
				}
	       } else {  
	           //相当于Fragment的onPause  
	       }  
	   }  
		// end add by anshe 2015.7.8
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	

}
