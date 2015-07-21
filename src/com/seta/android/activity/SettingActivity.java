package com.seta.android.activity;

/**
 *Filename: SettingActivity.java
 *Copyright: Copyright (c)2015
 *@Author:anshe
 *@Creat at:2015-7-20 下午4:50:18
 *@version 1.0
 */
import java.util.ArrayList;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.seta.android.record.utils.IatSettings;
import com.seta.android.recordchat.R;
import com.seta.android.selfview.CustomerSpinner;
import com.seta.android.selfview.IPEditText;
import com.seta.android.selfview.SlipButton;
import com.seta.android.selfview.SlipButton.OnChangedListener;
import com.seta.android.xmppmanager.XmppConnection;

public class SettingActivity extends ActionBarActivity {
	private SlipButton chooseRecordButton = null;
	private Button chooseRecordText = null, ipButton;
	public static ArrayList<String> chooseRecordList = null;
	public static ArrayList<String> languageList = null;
	private ArrayAdapter<String> adapter;
	private CustomerSpinner chooseRecordSpinner, languageSpinner;
	private View spinner_layout;
	private SharedPreferences rememberSettingPreferences;
	private IPEditText mEditText;
	private TextView ipTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingfragment);
		rememberSettingPreferences = this.getSharedPreferences(
				IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		chooseRecordButton = (SlipButton) findViewById(R.id.chooseRecordButton);
		chooseRecordText = (Button) findViewById(R.id.chooseRecordText);
		spinner_layout = (View) findViewById(R.id.spinner_layout);
		chooseRecordSpinner = (CustomerSpinner) findViewById(R.id.spinner);
		languageSpinner = (CustomerSpinner) findViewById(R.id.language_spinner);
		mEditText = (IPEditText) findViewById(R.id.ip_custom);
		ipTextView = (TextView) findViewById(R.id.ip_textview);
		ipButton = (Button) findViewById(R.id.ip_button);
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "="
				+ getString(R.string.app_id));
		initData();
	}

	public void initData() {
		//start add by anshe 2015.7.21
		String ipString = rememberSettingPreferences.getString("SERVER_HOST",
				XmppConnection.SERVER_HOST);
		ipTextView.setText(ipString);

		ipButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*Editor editor = rememberSettingPreferences.edit();
				editor.putString("SERVER_HOST",mEditText.getText(SettingActivity.this));
				editor.apply();*/
				ipTextView.setText(mEditText.getText(SettingActivity.this));
			}
		});
		//end add by anshe 2015.7.21
		chooseRecordText.setText(getString(R.string.record_auto_choose));
		String typeString = rememberSettingPreferences.getString(
				"iat_type_preference", SpeechConstant.TYPE_CLOUD);
		chooseRecordButton.SetOnChangedListener(new OnChangedListener() {
			public void OnChanged(boolean CheckState) {
				// btn.setText(CheckState ? "True" : "False");

				Editor editor = rememberSettingPreferences.edit();
				if (CheckState) {
					editor.putString("iat_type_preference",
							SpeechConstant.TYPE_AUTO);
					chooseRecordSpinner.setClickable(false);
					spinner_layout.setBackgroundColor(Color
							.parseColor("#50323232"));
				} else {
					editor.putString("iat_type_preference",
							SpeechConstant.TYPE_CLOUD);
					chooseRecordSpinner.setClickable(true);
					spinner_layout.setBackgroundColor(Color
							.parseColor("#ffffffff"));
				}
				editor.apply();
			}
		});
		chooseRecordList = new ArrayList<String>();
		chooseRecordList.add(getString(R.string.record_type_cloud));
		chooseRecordList.add(getString(R.string.record_type_mix));
		chooseRecordList.add(getString(R.string.record_type_local));
		chooseRecordSpinner.setList(chooseRecordList, this);
		adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item,
				chooseRecordList);
		chooseRecordSpinner.setAdapter(adapter);
		if (typeString.equals(SpeechConstant.TYPE_AUTO)) {
			chooseRecordButton.setCheck(true);
			chooseRecordSpinner.setClickable(false);
			spinner_layout.setBackgroundColor(Color.parseColor("#50323232"));
		} else {
			chooseRecordButton.setCheck(false);
		}
		System.out.println("默认语音识别引擎为：" + typeString);
		String[] typeStrings = this.getResources().getStringArray(
				R.array.type_values);
		for (int i = 0; i < typeStrings.length; i++) {
			if (typeString.equals(typeStrings[i])) {
				chooseRecordSpinner.setSelection(i);
				break;
			}
		}

		String[] languageStrings = this.getResources().getStringArray(
				R.array.language_entries);
		String language = rememberSettingPreferences.getString(
				"iat_language_preference", "mandarin");
		languageList = new ArrayList<String>();
		for (int i = 0; i < languageStrings.length; i++) {
			languageList.add(languageStrings[i]);
		}
		System.out.println("默认语言为：" + language);
		languageSpinner.setList(languageList, this);
		adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item,
				languageList);
		languageSpinner.setAdapter(adapter);
		languageStrings = this.getResources().getStringArray(
				R.array.language_values);
		for (int i = 0; i < languageStrings.length; i++) {
			if (language.equals(languageStrings[i])) {
				languageSpinner.setSelection(i);
				break;
			}
		}
		// 开启ActionBar上APP的图标功能
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportParentActivityIntent();
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * if(keyCode == KeyEvent.KEYCODE_BACK){ list.clear(); } return
	 * super.onKeyDown(keyCode, event); }
	 */

	@Override
	public void onBackPressed() {
		this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
