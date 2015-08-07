package com.seta.android.selfview;

import java.util.ArrayList;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.seta.android.record.utils.IatSettings;
import com.seta.android.recordchat.R;
import com.sys.android.util.ApkInstaller;
import com.sys.android.util.FucUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CustomerSpinner extends Spinner implements OnItemClickListener {

	public static SelectDialog dialog = null;
	private ArrayList<String> list;
	public static String text,typeString,language;
	public Context context;
	private SharedPreferences rememberSettingPreferences;
	// 语音+安装助手类
	ApkInstaller mInstaller;
	
	public CustomerSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		rememberSettingPreferences = context.getSharedPreferences(
				IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);	
	}
	// 如果视图定义了OnClickListener监听器，调用此方法来执行
	@Override
	public boolean performClick() {
		Context context = getContext();
		final LayoutInflater inflater = LayoutInflater.from(getContext());
		final View view = inflater.inflate(R.layout.formcustomspinner, null);
		final ListView listview = (ListView) view
				.findViewById(R.id.formcustomspinner_list);
		ListviewAdapter adapters = new ListviewAdapter(context, getList());
		listview.setAdapter(adapters);
		listview.setOnItemClickListener(this);
		dialog = new SelectDialog(context, R.style.dialog);// 创建Dialog并设置样式主题
		LayoutParams params = new LayoutParams(650, LayoutParams.FILL_PARENT);
		dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		dialog.show();
		dialog.addContentView(view, params);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> view, View itemView, int position,
			long id) {
		setSelection(position);
		System.out.println("选择了："+list.get(position));		
		String[] typeStrings=context.getResources().getStringArray(R.array.type_values);
		if(list.size()==3){
				if (position>0&&!SpeechUtility.getUtility().checkServiceInstalled()) {
					mInstaller.install();
					String typeString = rememberSettingPreferences.getString(
							"iat_type_preference", SpeechConstant.TYPE_CLOUD);
					for (int i = 0; i < typeStrings.length; i++) {
						if (typeString.equals(typeStrings[i])) {
							setSelection(i);
							setText(list.get(i));
							break;
						}
					}
				} else {
					if (position>0) {
						String result = FucUtil.checkLocalResource();
						if (!TextUtils.isEmpty(result)) {
							Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
						}
					}
					setText(list.get(position));
					Editor editor = rememberSettingPreferences.edit();					
					editor.putString("iat_type_preference", typeStrings[position]);
					editor.apply();
				}
		}else if (list.size()==4) {
			Editor editor = rememberSettingPreferences.edit();
			String[] languageStrings=context.getResources().getStringArray(R.array.language_values);
			editor.putString("iat_language_preference", languageStrings[position]);
			editor.apply();
			setText(list.get(position));
		}
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	public void setSelection(int position){
		super.setSelection(position);
	}
	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list,Activity activity) {
		mInstaller = new ApkInstaller(activity);
		this.list = list;		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}