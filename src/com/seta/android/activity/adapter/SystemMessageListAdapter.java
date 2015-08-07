package com.seta.android.activity.adapter;

import java.util.List;

import com.seta.android.db.SystemMessagePacketBean;
import com.seta.android.recordchat.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SystemMessageListAdapter extends BaseAdapter {

	Context mContext;
	List<SystemMessagePacketBean> mMessageListData;
	int mItemResId;
	LayoutInflater mLayoutInflater;

	public SystemMessageListAdapter(Context context,
			List<SystemMessagePacketBean> messageListData, int itemResId) {
		mContext = context;
		mMessageListData = messageListData;
		mItemResId = itemResId;
		mLayoutInflater = LayoutInflater.from(context);

	}

	public void changeData(List<SystemMessagePacketBean> messageListData) {
		mMessageListData = messageListData;
	}

	@Override
	public int getCount() {
		return mMessageListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessageListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mMessageListData.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (null == convertView) {
			convertView = mLayoutInflater.inflate(mItemResId, null);
		}

		TextView messageBodyTv = (TextView) convertView
				.findViewById(R.id.system_message_item_body);
		messageBodyTv.setText(mMessageListData.get(position).SystemMessageInfo);

		ImageView messageImageView = (ImageView) convertView
				.findViewById(R.id.system_message_item_image);

		messageImageView
				.setImageResource(mMessageListData.get(position).SystemMessageImageId);

		convertView.setTag(mMessageListData.get(position));

		return convertView;
	}

}
