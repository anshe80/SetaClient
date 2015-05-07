package com.seta.android.activity;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.seta.android.xmppmanager.XmppConnection;
import com.sys.android.util.DialogFactory;
import com.sys.android.xmpp.R;

@SuppressWarnings("all")
public class LoginActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private Button mBtnRegister;
	private Button mBtnLogin;
	private EditText mAccounts, mPassword;
	//add by ling 2015.4.28
	private Button mBtnFind_key;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loginpage);
		initView();
	}

	public void initView() {
		mBtnRegister = (Button) findViewById(R.id.regist_btn);
		mBtnRegister.setOnClickListener(this);
		mBtnLogin = (Button) findViewById(R.id.login_btn);
		mBtnLogin.setOnClickListener(this);
		mAccounts = (EditText) findViewById(R.id.lgoin_accounts);
		mPassword = (EditText) findViewById(R.id.login_password);
		//add by ling 2015.4.28
		mBtnFind_key=(Button) findViewById(R.id.find_key);
		mBtnFind_key.setOnClickListener(this);

	}

	/**
	 * 处理点击事件
	 */
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.regist_btn:
			register();
			break;
		case R.id.login_btn:
			submit();
			break;
			// start add by ling 2015.4.28 
		case R.id.find_key:
			find_key();
			//end 
		default:
			break;
		}
	}
	/*
	 * add by ling 2015.4.28(。。。。。。。未完待续)
	 * 找回密码：邮箱激活并重新设置密码*/
	private void find_key() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, FindkeyActivity.class);
		startActivity(intent);
	}

	private void register() {
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

	/**
	 * 提交账号密码信息到服务器
	 */
	private void submit() {
		String accounts = mAccounts.getText().toString();
		String password = mPassword.getText().toString();
		
		if (accounts.length() == 0 || password.length() == 0) {
			DialogFactory.ToastDialog(this, getString(R.string.login_Tips), getString(R.string.empty_or_error));
		} else {
			try {
				SmackAndroid.init(LoginActivity.this);

				// 连接服务器
				XmppConnection.getConnection().login(accounts, password);
				// 连接服务器成功，更改在线状态
				Presence presence = new Presence(Presence.Type.available);
				XmppConnection.getConnection().sendPacket(presence);
				// 弹出登录成功提示
				DialogFactory.ToastDialog(this, getString(R.string.login_Tips), getString(R.string.login_success));
				// 跳转到好友列表
				Intent intent = new Intent();
				intent.putExtra("USERID", accounts);
				//delete by ling 2015.4.29
				//intent.setClass(LoginActivity.this, FriendListActivity.class);
				//add by ling 2015.4.29
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
			} catch (XMPPException e) {
				XmppConnection.closeConnection();
				handler.sendEmptyMessage(2);
				e.printStackTrace();
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 2) {
				DialogFactory.ToastDialog(LoginActivity.this, getString(R.string.login_Tips),
						getString(R.string.loginFailed));
			}
		};
	};
}