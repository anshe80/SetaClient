package com.seta.android.activity;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.seta.android.xmppmanager.XmppConnection;
import com.sys.android.util.DialogFactory;
import com.sys.android.util.NetWorkConnection;
import com.seta.android.recordchat.R;

@SuppressWarnings("all")
public class LoginActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private Button mBtnRegister;
	private Button mBtnLogin;
	private EditText mAccounts, mPassword;
	//add by ling 2015.4.28
	private Button mBtnFind_key;
	private SharedPreferences rememberPassword;
	private CheckBox auto_save_password;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loginpage);
		rememberPassword = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		initView();
	}

	public void initView() {
		mBtnRegister = (Button) findViewById(R.id.regist_btn);
		mBtnRegister.setOnClickListener(this);
		mBtnLogin = (Button) findViewById(R.id.login_btn);
		mBtnLogin.setOnClickListener(this);
		mAccounts = (EditText) findViewById(R.id.lgoin_accounts);
		mPassword = (EditText) findViewById(R.id.login_password);
        auto_save_password = (CheckBox) findViewById(R.id.auto_save_password);
        auto_save_password.setChecked(true);        
		rememberPassword.edit().putBoolean("ISCHECK", true).commit();//modify by anshe 2015.8.4
		//监听记住密码多选框按钮事件
	  	auto_save_password.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	  		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
	  			if (auto_save_password.isChecked()) {	                  
	  				Log.e("密码","记住密码已选中");
	  				rememberPassword.edit().putBoolean("ISCHECK", true).commit();
	  				
	  			}else {	  				
	  				Log.e("密码","记住密码没有选中");
	  				rememberPassword.edit().putBoolean("ISCHECK", false).commit();
	  				
	  			}

	  		}
	  	});
		//add by ling 2015.4.28
		mBtnFind_key=(Button) findViewById(R.id.find_key);
		mBtnFind_key.setOnClickListener(this);
		//判断记住密码多选框的状态
		if(rememberPassword.getBoolean("ISCHECK", false))
	        {
	    	  //设置默认是记录密码状态
	          auto_save_password.setChecked(true);
	          Log.e("记住密码", rememberPassword.getString("USER_NAME", "获取账号失败"));
	          mAccounts.setText(rememberPassword.getString("USER_NAME", ""));
	          mPassword.setText(rememberPassword.getString("PASSWORD", ""));	       	 
	        }

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
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_settings);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	 
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, SettingActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		return true;
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
		finish();
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
			if (NetWorkConnection.isNetworkAvailable(this)) {
				try {
					SmackAndroid.init(LoginActivity.this);
					// 连接服务器
					XMPPConnection connection=XmppConnection.openConnection(this);
					if(connection!=null){
						connection.login(accounts, password);
						// 连接服务器成功，更改在线状态
						Presence presence = new Presence(Presence.Type.available);
						XmppConnection.getConnection(this).sendPacket(presence);
						
						//start modify by anshe 2015.7.5
						// 登录成功和记住密码框为选中状态才保存用户信息
						if (auto_save_password.isChecked()) {
							// 记住用户名、密码、
							Editor editor = rememberPassword.edit();
							editor.putString("USER_NAME", accounts);
							editor.putString("PASSWORD", password);
							editor.apply();
						}else{
							// 为保证用户离线后自动重连，默认记住用户名、密码.
							Editor editor = rememberPassword.edit();
							editor.putString("USER_NAME", accounts);
							editor.putString("PASSWORD", password);
							editor.apply();
						}
						//end modify by anshe 2015.7.5
						// 跳转到好友列表
						Intent intent = new Intent();
						// delete by ling 2015.4.29
						// intent.putExtra("USERID", accounts);
						// intent.setClass(LoginActivity.this,
						// FriendListActivity.class);
						// add by ling 2015.4.29
						intent.putExtra("pUSERID", accounts);
						intent.setClass(LoginActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
					}else{
						DialogFactory.ToastDialog(LoginActivity.this, getString(R.string.login_Tips),
								getString(R.string.service_not_open));						
					}					
				} catch (XMPPException e) {
					XmppConnection.closeConnection();
					handler.sendEmptyMessage(2);
					e.printStackTrace();
				}
			}else{
				//test no server add by anshe 2015.8.5
				if(accounts.equals("admin") && password.equals("admin")){
					Intent intent = new Intent();
					intent.putExtra("pUSERID", accounts);
					intent.setClass(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
				//test no server add by anshe 2015.8.5
				/*DialogFactory.ToastDialog(LoginActivity.this, getString(R.string.login_Tips),
						getString(R.string.failedconnection_info));		*/		
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
	
	public void onDestroy(){
		super.onDestroy();
	}
}