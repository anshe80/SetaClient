package com.seta.android.activity.adapter;

import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.seta.android.activity.ChatActivity;
import com.seta.android.activity.MainActivity;
import com.seta.android.fragment.privateFragment;
import com.seta.android.recordchat.R;
import com.seta.android.xmppmanager.XmppConnection;
import com.seta.android.xmppmanager.XmppService;

public class UserListAdapter extends BaseAdapter {
	private Activity context = null;
	private LayoutInflater inflater = null;
	private List<String> list = null;
	private XMPPConnection conn = null;

	public UserListAdapter(Activity context, List<String> list) {
		this.context = context;
		this.list = list;
		conn = XmppConnection.getConnection(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (this.inflater == null) {
			this.inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		// 加载到ListAdapter中进行显示
		convertView = this.inflater.inflate(R.layout.user_list, null);
		// ImageView userIcon = (ImageView)
		// convertView.findViewById(R.id.user_icon);
		final TextView tvUuserName = (TextView) convertView.findViewById(R.id.user_name);
		final String userName = this.list.get(position);
		if (conn!=null&&conn.getUser()==null) {
			if (XmppConnection.reConnectSuccess) {
				conn=XmppConnection.reConnection();
			}else {
				conn=XmppConnection.getConnection(context);
			}
		}
		if (conn!=null&&conn.getUser()!=null&&conn.getUser().split("@")[0].equalsIgnoreCase(userName)) {
			tvUuserName.setText(userName.split("@")[0] + " [本人]");
		} else {
			tvUuserName.setText(userName.split("@")[0]);
		}

		tvUuserName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("点击邀请用户参与聊天");
				System.out.println("连接用户名：" + conn.getUser());
				System.out.println("聊天名字：" + userName);
				if (conn!=null&&conn.getUser()==null) {
					if (XmppConnection.reConnectSuccess) {
						conn=XmppConnection.reConnection();
					}else {
						conn=XmppConnection.getConnection(context);
					}
				}
				if (conn.getUser()==null) {
					Toast.makeText(context, context.getString(R.string.not_Connect_to_Server), Toast.LENGTH_LONG).show();
					return;
				}
				if (conn!=null&&conn.getUser()!=null&&conn.getUser().split("@")[0].equalsIgnoreCase(userName)) {
					Toast.makeText(context, "不能和自己聊天", Toast.LENGTH_SHORT).show();
					tvUuserName.setText(userName.split("@")[0] + " [本人]");
					return;
				}
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				dialog.setTitle("邀请聊天").setIcon(0)
						.setMessage("您确定要和【" + userName.split("@")[0] + "】聊天")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

								String friendName = userName;
								Presence subscription = new Presence(Presence.Type.subscribe);
								subscription.setTo(friendName); // 需要@符号
								dialog.cancel();// 取消弹出框
								((Activity) context).finish();
								Toast.makeText(context, "成功邀请：" + friendName, Toast.LENGTH_SHORT)
										.show();
								Intent intent = new Intent();
								intent.putExtra("USERID", conn.getUser());
								intent.putExtra("user", "和" + friendName + "聊天");
								intent.putExtra("FRIENDID", "和" + friendName + "聊天");
								intent.setClass(context, ChatActivity.class);
								context.startActivity(intent);
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.cancel();// 取消弹出框
							}
						}).create().show();
			}
		});
		return convertView;
	}

}
