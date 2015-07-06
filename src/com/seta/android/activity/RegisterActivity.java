package com.seta.android.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;

import com.seta.android.activity.FindkeyActivity.TimeCount;
import com.seta.android.activity.FindkeyActivity.sendEmail;
import com.seta.android.email.Code;
import com.seta.android.email.EmailFormat;
import com.seta.android.email.MailSenderInfo;
import com.seta.android.email.SimpleMailSender;
import com.seta.android.xmppmanager.XmppConnection;
import com.sys.android.util.DialogFactory;
import com.sys.android.util.NetWorkConnection;
import com.seta.android.recordchat.R;

@SuppressWarnings("all")
public class RegisterActivity extends Activity implements OnClickListener {

	private Button mBtnRegister;
	private Button mRegBack,mget_verifycode;
	private EditText mEmailEt, mNameEt, mPasswdEt, mPasswdEt2,nameMCH,verifycode_input_prompt;
	private TimeCount time;
	private String mrepeat_get,code;
	private Activity activity;
	String email_conent=null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.register);
		activity=this;
		code=Code.getInstance().createCode();
		mBtnRegister = (Button) findViewById(R.id.register_btn);
		mRegBack = (Button) findViewById(R.id.reg_back_btn);
		mBtnRegister.setOnClickListener(this);
		mRegBack.setOnClickListener(this);
		mget_verifycode=(Button) findViewById(R.id.get_verifycode);
		mget_verifycode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				time = new TimeCount(60000, 1000);
				String email = mEmailEt.getText().toString();
				if (email != null&&!email.contains(getString(R.string.input_email))) {
					if (!EmailFormat.isEmail(email)) {
						Toast.makeText(getApplicationContext(), getString(R.string.email_format_error), Toast.LENGTH_SHORT).show();
						return;			
					}
					if (NetWorkConnection.isNetworkAvailable(activity)) {
						email_conent = getString(R.string.email_register_info)
								+ code + getString(R.string.email_warning);
						(new sendEmail()).run();
					} else {
						Toast.makeText(
								activity,
								activity.getString(R.string.failedconnection_info),
								Toast.LENGTH_SHORT).show();
					}
					time.start();
				}else{
					Toast.makeText(
							activity,
							activity.getString(R.string.email_null),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		verifycode_input_prompt=(EditText) findViewById(R.id.verifycode_input_prompt);
		nameMCH = (EditText) findViewById(R.id.reg_nameMCH);
		mEmailEt = (EditText) findViewById(R.id.reg_email);
		/*delete by anshe 2015.5.29
		mEmailEt.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (hasFocus) {

					// 此处为得到焦点时的处理内容

				} else {

					// 此处为失去焦点时的处理内容
					String email = mEmailEt.getText().toString();
					if (email != null) {
						if (!EmailFormat.isEmail(email)) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.email_format_error),
									Toast.LENGTH_SHORT).show();
							mEmailEt.findFocus();
							mBtnRegister.setClickable(false);
						} else {
							mBtnRegister.setClickable(true);
						}
					} else {
						Toast.makeText(getApplicationContext(),
								getString(R.string.email_null),
								Toast.LENGTH_SHORT).show();

					}

				}

			}

		});*/
		mNameEt = (EditText) findViewById(R.id.reg_name);
		mPasswdEt = (EditText) findViewById(R.id.reg_password);
		mPasswdEt2 = (EditText) findViewById(R.id.reg_password2);
	}	

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.reg_back_btn:
			login();
			finish();
			break;
		case R.id.register_btn:
			//start modify by anshe 2015.5.24
			boolean like=mPasswdEt.getText().toString().equals(mPasswdEt2.getText().toString());
			if(code.equalsIgnoreCase(verifycode_input_prompt.getText().toString())&&like){				
				registered();
				finish();
			}else{
				Toast.makeText(this, getString(R.string.verify_toast), Toast.LENGTH_SHORT).show();
			}
			break;
			//end modify by anshe 2015.5.24
		default:
			break;
		}
		
	}


	private void registered() {

		String accounts = mNameEt.getText().toString();
		String password = mPasswdEt.getText().toString();
		String email = mEmailEt.getText().toString();
		String name = nameMCH.getText().toString();			
		
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(XmppConnection.getConnection(this).getServiceName());
		reg.setUsername(accounts);
		reg.setPassword(password);
		reg.addAttribute("name", name);
		reg.addAttribute("email", email);		
		reg.addAttribute("android", "geolo_createUser_android");
		PacketFilter filter = new AndFilter(new PacketIDFilter(
		                                reg.getPacketID()), new PacketTypeFilter(
		                                IQ.class));
		PacketCollector collector = XmppConnection.getConnection(this).
		createPacketCollector(filter);
		XmppConnection.getConnection(this).sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		                        // Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）
		if (result == null) {
			Toast.makeText(getApplicationContext(), getString(R.string.link_error), Toast.LENGTH_SHORT).show();
		} else if (result.getType() == IQ.Type.ERROR) {
				if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				        Toast.makeText(getApplicationContext(), getString(R.string.account_tips), Toast.LENGTH_SHORT).show();
				    } else {
				        Toast.makeText(getApplicationContext(),getString(R.string.register_error),Toast.LENGTH_SHORT).show();
				    }
		} else if (result.getType() == IQ.Type.RESULT) {
			//start modify by anshe 2015.5.24
			//发送成功注册邮件
			if(NetWorkConnection.isNetworkAvailable(activity)){
				email_conent =getString(R.string.email_register_body);
				(new sendEmail()).run();
			}
			// 记住用户名、密码、
			SharedPreferences rememberPassword;
			rememberPassword = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
			rememberPassword.edit().putBoolean("ISCHECK", true).commit();
			Editor editor = rememberPassword.edit();
			editor.putString("USER_NAME", accounts);
			editor.putString("PASSWORD", password);
			editor.apply();
			Toast.makeText(this, getString(R.string.email_register_body)+getString(R.string.email_subject), Toast.LENGTH_LONG).show();
			//end modify by anshe 2015.5.24
			DialogFactory.ToastDialog(this, getString(R.string.registerID), getString(R.string.register_success));
			Intent intent = new Intent();
			intent.putExtra("pUSERID", name);
			intent.setClass(RegisterActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		
	}

	private void login() {
		Intent intent = new Intent();
		intent.setClass(RegisterActivity.this, LoginActivity.class);
		startActivity(intent);
	}
    class TimeCount extends CountDownTimer {
    	 
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
 
        @Override
        public void onTick(long millisUntilFinished) {
            mget_verifycode.setBackgroundColor(Color.parseColor("#4EB84A"));
            mget_verifycode.setClickable(false);
            mget_verifycode.setText(millisUntilFinished / 1000 +getString(R.string.qr_time_reget_verify_code));
        }
 
        @Override
        public void onFinish() {
            mget_verifycode.setText(getString(R.string.qr_reget_verify_code));
            mget_verifycode.setClickable(true);
            mget_verifycode.setBackgroundColor(Color.parseColor("#B6B6D8"));
 
        }
    }
    
    public class sendEmail extends Thread {

		public void run() {
			try {
				MailSenderInfo mailInfo = EmailFormat
						.getMailSender(getString(R.string.email_account));
				mailInfo.setUserName(getString(R.string.email_account)); // 你的邮箱地址
				mailInfo.setPassword(getString(R.string.email_password));// 您的邮箱密码
				mailInfo.setFromAddress(getString(R.string.email_account));
				mailInfo.setToAddress(mEmailEt.getText().toString());
				mailInfo.setSubject(getString(R.string.email_subject));
				mailInfo.setContent(email_conent);

				// 这个类主要来发送邮件
				SimpleMailSender sms = new SimpleMailSender();
				sms.sendTextMail(mailInfo);// 发送文体格式
				// sms.sendHtmlMail(mailInfo);//发送html格式
				Toast.makeText(activity,
						getString(R.string.email_send_success),
						Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				Log.e("SendMail", e.getMessage(), e);
			}
		}

	}

}