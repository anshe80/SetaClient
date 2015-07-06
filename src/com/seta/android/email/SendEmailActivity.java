package com.seta.android.email;

import java.io.File;

import javax.mail.internet.MimeMultipart;

import com.seta.android.activity.FindkeyActivity;
import com.seta.android.activity.LoginActivity;
import com.seta.android.recordchat.R;
import com.sys.android.util.NetWorkConnection;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendEmailActivity extends Activity {
	private Button   send,mget_verifycode,mgo_back_btn; 
	private EditText muser_email; 
	private EditText password; 
	private EditText to; 
	private EditText memail_text,memail_tip;
	private TextView verify_code;
	private Activity activity;
	private String   code;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.email);
        activity=this;
        
        muser_email = (EditText) findViewById(R.id.userid); 
        password = (EditText) findViewById(R.id.password); 
        to = (EditText) findViewById(R.id.toUser); 
        memail_tip=(EditText) findViewById(R.id.send_email_tips);
        memail_text=(EditText) findViewById(R.id.send_email_context);
        verify_code=(TextView) findViewById(R.id.verify_code);
        mgo_back_btn=(Button) findViewById(R.id.go_back_btn);
        mgo_back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				
			}
		});
        mget_verifycode=(Button) findViewById(R.id.get_verifycode);
		mget_verifycode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				code = Code.getInstance().createCode();
				verify_code.setTextColor(activity.getResources().getColor(R.color.red));
				verify_code.setText(code);
			}

		});   		
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() 
        {
            @Override 
            public void onClick(View v) 
            {
            	if(memail_tip.getText().toString().isEmpty()||
            			muser_email.getText().toString().isEmpty()||
            			memail_text.getText().toString().isEmpty()||
            			password.getText().toString().isEmpty()||
            			to.getText().toString().isEmpty()||
            			verify_code.getText().toString().isEmpty()){
            		
            		Toast.makeText(activity, "请填写所有项！", Toast.LENGTH_SHORT).show();          		
            		
            	}else if(!EmailFormat.isEmail(muser_email.getText().toString())||
        					!EmailFormat.isEmail(to.getText().toString())){
        			Toast.makeText(activity, activity.getString(R.string.email_format_error), Toast.LENGTH_SHORT).show();
        		}else{
            		if (code.equalsIgnoreCase(verify_code.getText().toString())) {
            			
        				String[] fileName = activity.getIntent()
        						.getStringExtra("filePath").split(",");
        				if (fileName.length > 0 && (new File(fileName[0])).exists()) {
		            		if(NetWorkConnection.isNetworkAvailable(activity)){
	        					Toast.makeText(activity, activity.getString(R.string.sending_email), Toast.LENGTH_LONG).show();
			            		Thread send=new sendEmail(fileName);
			            		send.run();
		            		}else{
		    					Toast.makeText(activity,
		    							activity.getString(R.string.failedconnection),
		    							Toast.LENGTH_SHORT).show();
		            		}
	            		} else {
	    					Toast.makeText(activity,
	    							activity.getString(R.string.file_not_exits),
	    							Toast.LENGTH_SHORT).show();
	    				}
            		}else{
                    	Toast.makeText(activity, activity.getString(R.string.verify_toast), Toast.LENGTH_SHORT).show();
                    }
            	}
            } 
        }); 
    }
    
    public class sendEmail extends Thread{
    	
    	String[] fileName;
    	public sendEmail(String[] fileName){
    		this.fileName=fileName;
    	}
    	
		public void run() {

			// TODO Auto-generated method stub
			try {
				Log.e("邮件发送的附件地址：", fileName[0]);
				MailSenderInfo mailInfo = EmailFormat.getMailSender(muser_email
						.getText().toString());
				mailInfo.setUserName(muser_email.getText().toString()); // 你的邮箱地址
				mailInfo.setPassword(password.getText().toString());// 您的邮箱密码
				mailInfo.setFromAddress(muser_email.getText().toString());
				mailInfo.setToAddress(to.getText().toString());
				mailInfo.setSubject(memail_tip.getText().toString());
				mailInfo.setContent(memail_text.getText().toString());
				mailInfo.setAttachFileNames(fileName);

				// 这个类主要来发送邮件
				SimpleMailSender sms = new SimpleMailSender();
				// sms.sendTextMail(mailInfo);
				sms.sendMutilMail(mailInfo);// 发送多接收者，多附件格式
				// sms.sendHtmlMail(mailInfo);//发送html格式
				Toast.makeText(activity,
						activity.getString(R.string.email_sendfile_success),
						Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				Log.e("SendMail", e.getMessage(), e);
				Toast.makeText(activity,
						activity.getString(R.string.send_email_error),
						Toast.LENGTH_SHORT).show();

			}

		}

	}
    
    public void onDestroy(){
    	super.onDestroy();
    	finish();
    }

}