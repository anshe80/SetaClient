package com.seta.android.activity;
//create by ling 2015.4.30
import org.jivesoftware.smack.XMPPException;

import com.seta.android.email.Code;
import com.seta.android.email.EmailFormat;
import com.seta.android.email.MailSenderInfo;
import com.seta.android.email.SimpleMailSender;
import com.seta.android.xmppmanager.XmppConnection;
import com.seta.android.recordchat.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.TextureView;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindkeyActivity extends ActionBarActivity implements OnClickListener{
	private Button mfind_back_btn;
	private EditText mfind_email;
	private EditText mverifycode_input_prompt,password_edit,repeat_password_edit;
	private Button mfind_key_btn,mget_verifycode;
	private TextView email_warnning;
	private TimeCount time;
	private String memail,mrepeat_get,password,repeat_password;
	private Activity activity;
	String code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.find_key);
		activity=this;
		code=Code.getInstance().createCode();
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		mfind_back_btn=(Button) findViewById(R.id.find_back_btn);
		mfind_email=(EditText) findViewById(R.id.find_email);
		mfind_email.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {  
			
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  

		        if(hasFocus) {

				// 此处为得到焦点时的处理内容
		        	email_warnning.setText(getString(R.string.find_key_Tips));
		
				} else {
				// 此处为失去焦点时的处理内容		
					memail=mfind_email.getText().toString();
					if(!EmailFormat.isEmail(memail)){
						Toast.makeText(activity, getString(R.string.email_format_error), Toast.LENGTH_SHORT).show();
						email_warnning.setText(getString(R.string.email_format_error)+"\n"+getString(R.string.find_key_Tips));
						repeat_password_edit.setFocusable(true);
						mfind_key_btn.setClickable(false);
					}else{
						mfind_key_btn.setClickable(true);
					}			
		
				}

		    }

		});
		password_edit=(EditText) findViewById(R.id.password);
		repeat_password_edit=(EditText) findViewById(R.id.repeat_password);
		mverifycode_input_prompt=(EditText) findViewById(R.id.verifycode_input_prompt);		
		email_warnning=(TextView) findViewById(R.id.email_warnning);
		mget_verifycode=(Button) findViewById(R.id.get_verifycode);
		mget_verifycode.setOnClickListener(this);
		mfind_key_btn=(Button) findViewById(R.id.find_key_btn);
		mfind_back_btn.setOnClickListener(this);
		mfind_key_btn.setOnClickListener(this);
		mfind_key_btn.setClickable(false);
		repeat_password_edit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {  

		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {  

		        if(hasFocus) {

				// 此处为得到焦点时的处理内容
		        	email_warnning.setText(getString(R.string.find_key_Tips));
							
				} else {
		
				// 此处为失去焦点时的处理内容
					password=password_edit.getText().toString();
					repeat_password=repeat_password_edit.getText().toString();
					if(!password.equals(repeat_password)){
						Toast.makeText(activity, getString(R.string.password_unlike), Toast.LENGTH_SHORT).show();
						email_warnning.setText(getString(R.string.password_unlike)+"\n"+getString(R.string.find_key_Tips));
						repeat_password_edit.setFocusable(true);
						mfind_key_btn.setClickable(false);
					}else{
						mfind_key_btn.setClickable(true);
					}				
		
				}

		    }

		});

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.find_back_btn:
			back();
			break;
		case R.id.find_key_btn:
			find_key();
			break;
		case R.id.get_verifycode:
			get_code();
			break;
		default:
			break;
		}
	}
	
	//start modify by anshe 2015.5.23
	private void find_key() {
		// TODO Auto-generated method stub
		String code_Edit=mverifycode_input_prompt.getText().toString();		
		if(!code_Edit.equalsIgnoreCase(code)){
			Toast.makeText(activity, getString(R.string.password_unlike), Toast.LENGTH_SHORT).show();
			mverifycode_input_prompt.setFocusable(true);
		}else{
			try {
				XmppConnection.getConnection(this).getAccountManager().changePassword(password);
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast.makeText(activity, getString(R.string.text_upload_success), Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	private void get_code() {
		// TODO Auto-generated method stub

		memail=mfind_email.getText().toString();
		Log.e("获取邮箱：", memail);
		if (EmailFormat.isEmail(memail)) {
			time = new TimeCount(60000, 1000);
			mget_verifycode.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {					
					(new sendEmail()).run();
					time.start();
				}
			});
		}else if(memail.isEmpty()){
			Toast.makeText(activity, getString(R.string.email_null), Toast.LENGTH_SHORT).show();
	 	}else{
	 		Toast.makeText(activity, getString(R.string.email_format_error), Toast.LENGTH_SHORT).show();
	 	}
		
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
    		mrepeat_get=getString(R.string.qr_reget_verify_code);
            mget_verifycode.setText(mrepeat_get);
            mget_verifycode.setClickable(true);
            mget_verifycode.setBackgroundColor(Color.parseColor("#B6B6D8"));
 
        }
    }		
	
	private void back() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(FindkeyActivity.this, LoginActivity.class);
		startActivity(intent);
	}
	
	public class sendEmail extends Thread {

		public void run() {
			try {
				String email_conent = getString(R.string.email_verycode) + code
						+ getString(R.string.email_warning);
				MailSenderInfo mailInfo = EmailFormat
						.getMailSender(getString(R.string.email_account));
				mailInfo.setUserName(getString(R.string.email_account)); // 你的邮箱地址
				mailInfo.setPassword(getString(R.string.email_password));// 您的邮箱密码
				mailInfo.setFromAddress(getString(R.string.email_account));
				mailInfo.setToAddress(mfind_email.getText().toString());
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
	//end modify by anshe 2015.5.23
}
