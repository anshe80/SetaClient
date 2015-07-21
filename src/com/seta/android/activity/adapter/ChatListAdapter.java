package com.seta.android.activity.adapter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.seta.android.email.SendEmailActivity;
import com.seta.android.entity.Msg;
import com.seta.android.fragment.privateFragment;
import com.seta.android.xmppmanager.XmppConnection;
import com.sys.android.util.OpenfileFunction;
import com.sys.android.util.Utils;
import com.seta.android.recordchat.R;

public class ChatListAdapter extends BaseAdapter {

	private Activity context;
	private LayoutInflater inflater;
	private List<Msg> listMsg;
	private SoundPlayer mSoundPlayer;
	public static String FILE_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/seta/file/";
	public static String RECORD_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/seta/record/";
	private String filePath = null,nowUserCache=null;

	public ChatListAdapter(Activity formClient, List<Msg> list) {
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
		final String fromUser = Utils.getJidToUsername(listMsg.get(position)
				.getFrom());
		final String toUser = Utils.getJidToUsername(listMsg.get(position)
				.getToUser());
		final String nowUser = Utils.getJidToUsername(XmppConnection.getConnection(this.context).getUser())==null
				?nowUserCache:Utils.getJidToUsername(XmppConnection.getConnection(this.context).getUser());

		if (fromUser!=null&&toUser!=null&&nowUser!=null&&!fromUser.equals(toUser) && !nowUser.equals(fromUser)) {
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
		final Msg msg = listMsg.get(position);
		fromUserView.setText(msg.getFrom().split("@")[0]);
		dateView.setText(msg.getDate());
		msgView.setText(msg.getMsg());

		if (!Msg.TYPE[2].equals(listMsg.get(position).getType())) {// normal
																	// 普通msg
			TextView msgStatus = (TextView) convertView
					.findViewById(R.id.msg_status);
			msgStatus.setText(listMsg.get(position).getReceive() + "");
		} else {
			TextView msgStatus = (TextView) convertView
					.findViewById(R.id.msg_status);
			msgStatus.setVisibility(View.GONE);// 影藏
		}
		convertView.setOnClickListener(new OnClickListener() {// 点击查看
					@Override
					public void onClick(View v) {
						mSoundPlayer = new SoundPlayer();
						File file = new File(RECORD_ROOT_PATH
								+ msg.getFilePath());
						Log.e("打开的音频路径：", RECORD_ROOT_PATH + msg.getFilePath());
						if (file.exists()) {
							mSoundPlayer.stopPlaying();
							mSoundPlayer.startPlaying(RECORD_ROOT_PATH
									+ msg.getFilePath());
						} else {

							if (toUser!=null&&fromUser!=null&&!fromUser.equals(toUser)
									&& !nowUser.equals(fromUser)
									&& msg.getMsg()
											.contains(
													context.getString(R.string.record_info))) {
								Toast.makeText(
										context,
										context.getString(R.string.record_receiving_lose),
										Toast.LENGTH_SHORT).show();
							} else {
								if (msg.getMsg()
										.contains(
												context.getString(R.string.record_info))) {
									Toast.makeText(
											context,
											context.getString(R.string.record_sending),
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}
				});
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				File file = new File(RECORD_ROOT_PATH + msg.getFilePath());
				if (file.exists()) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							context);
					dialog.setTitle("发送邮件")
							.setIcon(R.drawable.icon)
							.setMessage("确定将此音频文件发送给好友吗？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method
											// stub
											Intent intent = new Intent(context,
													SendEmailActivity.class);
											intent.putExtra("filePath",
													RECORD_ROOT_PATH +msg.getFilePath());
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
					if (msg.getMsg() != null) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								context);
						dialog.setTitle("发送邮件")
								.setIcon(R.drawable.icon)
								.setMessage("确定将此内容以文本的形式发送给好友吗？")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												filePath = System
														.currentTimeMillis()
														+ ".txt";
												OpenfileFunction
														.writeTxtToFile(
																msg.getMsg(),
																FILE_ROOT_PATH,
																filePath);
												filePath = FILE_ROOT_PATH
														+ filePath;
												File file = new File(filePath);
												while (!file.exists())
													;
												Intent intent = new Intent(
														context,
														SendEmailActivity.class);
												intent.putExtra("filePath",
														filePath);
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
								context.getString(R.string.file_create_error),
								Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}
		});

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
			if (mPlayer != null && mPlayer.isPlaying()) {
				mPlayer.release();
				mPlayer = null;
			}
		}
	}
	//add by anshe 2015.7.9
	public List<Msg> getListMsg() {
		return listMsg;
	}

	public void setListMsg(List<Msg> listMsg) {
		this.listMsg = listMsg;
	}
	//end add by anshe 2015.7.9 

}