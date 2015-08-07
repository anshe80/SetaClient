/**
 *Filename: settingFragment.java
 *Copyright: Copyright (c)2015
 *@Author:anshe
 *@Creat at:2015-7-7 下午9:22:46
 *@version 1.0
 */
package com.seta.android.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.iflytek.cloud.SpeechConstant;
import com.seta.android.record.utils.IatSettings;
import com.seta.android.recordchat.R;
import com.seta.android.selfview.CustomerSpinner;
import com.seta.android.selfview.SlipButton;
import com.seta.android.selfview.SlipButton.OnChangedListener;
import com.seta.android.xmppmanager.XmppConnection;

/**
 *Filename: settingFragment.java
 *Copyright: Copyright (c)2015
 *@Author:anshe
 *@Creat at:2015-7-7 下午9:22:46
 *@version 1.0
 */
/**
 * @author anshe
 * 
 */
public class settingFragment extends Fragment {
	private SlipButton chooseRecordButton = null;
	private Button chooseRecordText = null, ipButton;
	public static ArrayList<String> chooseRecordList = null;
	public static ArrayList<String> languageList = null;
	private ArrayAdapter<String> adapter;
	private CustomerSpinner chooseRecordSpinner, languageSpinner;
	private View spinner_layout;
	private SharedPreferences rememberSettingPreferences;
	private View mEditText;
	private TextView ipTextView,server_status,before_spinner_text;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.settingfragment, container, false);
		chooseRecordButton = (SlipButton) view
				.findViewById(R.id.chooseRecordButton);
		chooseRecordText = (Button) view.findViewById(R.id.chooseRecordText);
		//spinner_layout = (View) view.findViewById(R.id.spinner_layout);
		chooseRecordSpinner = (CustomerSpinner) view.findViewById(R.id.spinner);
		before_spinner_text = (TextView) view.findViewById(R.id.before_spinner_text);
		languageSpinner = (CustomerSpinner) view
				.findViewById(R.id.language_spinner);
		mEditText=(View) view.findViewById(R.id.server_layout);
		ipTextView = (TextView) view.findViewById(R.id.ip_textview);
		server_status=(TextView) view.findViewById(R.id.server_status);
		ipButton = (Button) view.findViewById(R.id.ip_button);
		initData();
		return view;
	}

	public void initData() {
		// start add by anshe 2015.7.21
		String ipString = rememberSettingPreferences.getString("SERVER_HOST",
				XmppConnection.SERVER_HOST);
		server_status.setText(getString(R.string.server_status));
		ipTextView.setText(ipString);
		mEditText.setVisibility(View.GONE);
		ipButton.setVisibility(View.GONE);
		// end add by anshe 2015.7.21
		chooseRecordText.setText(getString(R.string.record_auto_choose));
		String typeString = rememberSettingPreferences.getString(
				"iat_type_preference", SpeechConstant.TYPE_CLOUD);
		chooseRecordButton.SetOnChangedListener(new OnChangedListener() {
			public void OnChanged(boolean CheckState) {
				// btn.setText(CheckState ? "True" : "False");
				if (CheckState) {
					Editor editor = rememberSettingPreferences.edit();
					editor.putString("iat_type_preference",
							SpeechConstant.TYPE_AUTO);
					editor.apply();
					chooseRecordSpinner.setClickable(false);
					/*spinner_layout.setBackgroundColor(Color
							.parseColor("#50323232"));*/
					before_spinner_text.setTextColor(getResources().getColor(R.color.textgray));
					chooseRecordText.setTextColor(getResources().getColor(R.color.black));
				} else {
					Editor editor = rememberSettingPreferences.edit();
					editor.putString("iat_type_preference",
							SpeechConstant.TYPE_CLOUD);
					editor.apply();
					chooseRecordSpinner.setClickable(true);
					/*spinner_layout.setBackgroundColor(Color
							.parseColor("#ffffffff"));*/
					before_spinner_text.setTextColor(getResources().getColor(R.color.black));
					chooseRecordText.setTextColor(getResources().getColor(R.color.textgray));
				}
			}
		});
		chooseRecordList = new ArrayList<String>();
		chooseRecordList.add(getString(R.string.record_type_cloud));
		chooseRecordList.add(getString(R.string.record_type_mix));
		chooseRecordList.add(getString(R.string.record_type_local));
		chooseRecordSpinner.setList(chooseRecordList, this.getActivity());
		adapter = new ArrayAdapter<String>(this.getActivity(),
				R.layout.simple_spinner_item, chooseRecordList);
		chooseRecordSpinner.setAdapter(adapter);
		if (typeString.equals(SpeechConstant.TYPE_AUTO)) {
			chooseRecordButton.setCheck(true);
			chooseRecordSpinner.setClickable(false);
			//spinner_layout.setBackgroundColor(Color.parseColor("#50323232"));
			before_spinner_text.setTextColor(getResources().getColor(R.color.textgray));
		} else {
			chooseRecordText.setTextColor(getResources().getColor(R.color.textgray));
			chooseRecordButton.setCheck(false);
		}
		System.out.println("默认语音识别引擎为：" + typeString);
		String[] typeStrings = this.getActivity().getResources()
				.getStringArray(R.array.type_values);
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
		languageSpinner.setList(languageList, this.getActivity());
		adapter = new ArrayAdapter<String>(this.getActivity(),
				R.layout.simple_spinner_item, languageList);
		languageSpinner.setAdapter(adapter);
		languageStrings = this.getResources().getStringArray(
				R.array.language_values);
		for (int i = 0; i < languageStrings.length; i++) {
			if (language.equals(languageStrings[i])) {
				languageSpinner.setSelection(i);
				break;
			}
		}
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * if(keyCode == KeyEvent.KEYCODE_BACK){ list.clear(); } return
	 * super.onKeyDown(keyCode, event); }
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		rememberSettingPreferences = this.getActivity().getSharedPreferences(
				IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_menu, menu);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
