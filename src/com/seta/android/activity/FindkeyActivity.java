package com.seta.android.activity;
//create by ling 2015.4.30
import com.sys.android.xmpp.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class FindkeyActivity extends ActionBarActivity implements OnClickListener{
	private Button mfind_back_btn;
	private EditText mfind_email;
	private EditText mfind_uid;
	private Button mfind_key_btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.find_key);
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		mfind_back_btn=(Button) findViewById(R.id.find_back_btn);
		mfind_email=(EditText) findViewById(R.id.find_email);
		mfind_uid=(EditText) findViewById(R.id.find_uid);
		mfind_key_btn=(Button) findViewById(R.id.find_key_btn);
		mfind_back_btn.setOnClickListener(this);
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
		}
	}
	//。。。。未完待续
	private void find_key() {
		// TODO Auto-generated method stub
		
	}
	private void back() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(FindkeyActivity.this, LoginActivity.class);
		startActivity(intent);
	}

}
