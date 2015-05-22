package com.seta.android.activity.adapter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.seta.android.activity.ChatActivity;
import com.seta.android.activity.CreatRoomActivity;
import com.seta.android.email.SendEmailActivity;
import com.seta.android.entity.Msg;
import com.seta.android.xmppmanager.XmppConnection;
import com.sys.android.util.Utils;
import com.seta.android.recordchat.R;

public class ChatListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Msg> listMsg;
	private SoundPlayer mSoundPlayer;

	public ChatListAdapter(Context formClient, List<Msg> list) {
		this.context = formClient;
		listMsg = list;
	}

	@Override
	public int getCount() {
		return listMsg.size();
	}

	@Override
	public Object getItem(int position) {
		return listMsg.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		this.inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		String fromUser=Utils.getJidToUsername(listMsg.get(position).getFrom());
        String toUser=Utils.getJidToUsername(listMsg.get(position).getToUser());
        String nowUser=Utils.getJidToUsername(XmppConnection.getConnection().getUser());
        
        if (!fromUser.equals(toUser)&&!nowUser.equals(fromUser)){
			convertView = this.inflater.inflate(R.layout.formclient_chat_in,
					null);
		} else {
			convertView = this.inflater.inflate(R.layout.formclient_chat_out,
					null);
		}

		TextView fromUserView = (TextView) convertView
				.findViewById(R.id.formclient_row_userid);
		TextView dateView = (TextView) convertView
				.findViewById(R.id.formclient_row_date);
		TextView msgView = (TextView) convertView
				.findViewById(R.id.formclient_row_msg);
		
		fromUserView.setText(listMsg.get(position).getFrom().split("@")[0]);
		dateView.setText(listMsg.get(position).getDate());
		msgView.setText(listMsg.get(position).getMsg());
		
		if (!Msg.TYPE[2].equals(listMsg.get(position).getType())) {// normal 普通msg
			Log.e("显示的消息：","第"+position+"条  now="+nowUser+" from="+fromUser + "   toUser=" + toUser);
			final Msg msg = listMsg.get(position);
			TextView msgStatus = (TextView) convertView
					.findViewById(R.id.msg_status);
			msgStatus.setText(listMsg.get(position).getReceive() + "");
			convertView.setOnClickListener(new OnClickListener() {// 点击查看
						@Override
						public void onClick(View v) {
							mSoundPlayer=new SoundPlayer();
							File file=new File(msg.getFilePath());
							if(file.exists()){
								mSoundPlayer.startPlaying(msg.getFilePath());
							}else{
								Toast.makeText(context, context.getString(R.string.record_sending), Toast.LENGTH_SHORT).show();
							}
						}
					});
			convertView.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View arg0) {
					// TODO Auto-generated method stub
					File file = new File(msg.getFilePath());
					if (file.exists()) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								context);
						dialog.setTitle("发送邮件")
								.setIcon(R.drawable.icon)
								.setMessage("确定将此文件发送给好友吗？")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												Intent intent = new Intent(
														context,
														SendEmailActivity.class);
												intent.putExtra("filePath",
														msg.getFilePath());
												context.startActivity(intent);

											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												dialog.cancel();// 取消弹出框
											}
										}).create().show();
					} else {
						Toast.makeText(context,
								context.getString(R.string.record_sending),
								Toast.LENGTH_SHORT).show();
					}
					return true;
				}
				
			});
		} else {
			TextView msgStatus = (TextView) convertView
					.findViewById(R.id.msg_status);
			msgStatus.setVisibility(View.GONE);// 影藏
		}

		return convertView;
	}
	
	public class SoundPlayer {
		private MediaPlayer mPlayer;

		public void startPlaying(String fileName) {
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(fileName);
				mPlayer.prepare();
				mPlayer.start();
			} catch (IOException e) {
				Log.e("播放音频文件", "prepare() failed");
			}
		}

		public void stopPlaying() {
			mPlayer.release();
			mPlayer = null;
		}
	}	
	
	
}