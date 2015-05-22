package com.seta.android.fragment;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.seta.android.activity.ChatActivity;
import com.seta.android.activity.ChatConvertActivity;
import com.seta.android.activity.CreatRoomActivity;
import com.seta.android.chat.RoomListAdapter;
import com.seta.android.entity.ServerRooms;
import com.seta.android.xmppmanager.XmppConnection;
import com.seta.android.recordchat.R;
import com.sys.android.util.MutilUserChatUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	

	List<ServerRooms> mRoomListData;
	RoomListAdapter mRoomListAdapter;
	Button creatRoomButton;
	Activity activity;
	ListView mRoomListView;
	View view;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        view = inflater.inflate(R.layout.group_layout, container, false);
	        getRoomListView();
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
			activity=this.getActivity();
			final XMPPConnection connection=XmppConnection.getConnection();
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
			mRoomListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parentView, View v,
						int position, long itemId) {
					/*进入房间*/
					ServerRooms room=mRoomListData.get(position);
					String LoginUser=connection.getUser();
					Toast.makeText(activity, "成功加入房间["+room.getName()+"]", Toast.LENGTH_LONG).show();
					Intent ChatListIntent = new Intent(activity, ChatActivity.class);
					ChatListIntent.putExtra("FRIENDID", room.getName());
					ChatListIntent.putExtra("user", LoginUser);
					ChatListIntent.putExtra("USERID",LoginUser);
					startActivity(ChatListIntent);
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	

}
