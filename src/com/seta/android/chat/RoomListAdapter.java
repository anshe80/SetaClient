package com.seta.android.chat;

import java.util.List;

import com.seta.android.entity.ServerRooms;
import com.seta.android.recordchat.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RoomListAdapter extends BaseAdapter{

	Context mContext;
	List<ServerRooms> mServerRoomsList;
	int mListItemResId;
	LayoutInflater mLayoutInflater;

	public RoomListAdapter(Context context,List<ServerRooms> serverRoomsList, int listItemResId) {

		mContext = context;
		mServerRoomsList=serverRoomsList;
		mListItemResId = listItemResId;
		mLayoutInflater = LayoutInflater.from(context);

	}
	
	public void changeData(List<ServerRooms> serverRoomsList) {
		mServerRoomsList = serverRoomsList;
	}
	  
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mServerRoomsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mServerRoomsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mServerRoomsList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (null == convertView) {
			convertView = mLayoutInflater.inflate(
					R.layout.roster_group_list_item_layout, null);
		}
		TextView groupNameTv = (TextView) convertView
				.findViewById(R.id.roster_group_item_groupname);

		// 显示服务器上所有会议室
		String groupName = mServerRoomsList.get(position).getName();
		String groupEntryCount = "(在聊人数：" + mServerRoomsList.get(position).getOccupants() + ")";
		groupNameTv.setText(groupName + groupEntryCount);
		
		return convertView;
	}

}
