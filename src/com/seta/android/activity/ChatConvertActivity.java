package com.seta.android.activity;

import com.seta.android.recordchat.R;
import com.seta.android.service.ConvertService;
import com.sys.android.util.JNIMp3Encode;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChatConvertActivity extends Activity {

	private EditText et_wav;
	private EditText et_mp3;
	String mp3Path;
	String pcmPath;
	Activity activity;
	Intent convertIntent;
	/**
	 * 获取LAME的版本信
	 * 
	 * @return
	 */
	public native String getLameVersion();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_convert);
		et_wav = (EditText) this.findViewById(R.id.et_wav);
		et_mp3 = (EditText) this.findViewById(R.id.et_mp3);
		Button convertButton=(Button) this.findViewById(R.id.button1);
		pcmPath = et_wav.getText().toString().trim();
		mp3Path = et_mp3.getText().toString().trim();
		activity=this;
		convertButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*Thread convert=new Thread(new convertToMp3());
				convert.start();*/
				convertIntent=new Intent(activity,ConvertService.class);
				convertIntent.putExtra("pcmPath", pcmPath);
				convertIntent.putExtra("mp3Path", mp3Path);
				startService(convertIntent);				
				
			}
		});
	}
	class convertToMp3 implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e("正在转码","开始进行。。。。");
			JNIMp3Encode.convertmp3(pcmPath, mp3Path, 8000);
			Log.e("转码完成","转码成功。。。。");
		}
		
	}
	
	public void onDestroy(){
		this.stopService(convertIntent);
		super.onDestroy();
	}


}
