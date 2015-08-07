package com.seta.android.service;

import com.sys.android.util.JNIMp3Encode;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class ConvertService extends Service{

	public String pcmPath;
	public String mp3Path;
	public Intent context;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		context=intent;
		pcmPath=context.getStringExtra("pcmPath");
		mp3Path=context.getStringExtra("mp3Path");
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		if(pcmPath==null||mp3Path==null){
			pcmPath=Environment.getExternalStorageDirectory().getPath() + "/seta/record/1432138970154.pcm";
			mp3Path=Environment.getExternalStorageDirectory().getPath()+"/mp.mp3";
		}
		Thread convert=new Thread(new convertToMp3());
		convert.start();
	}
	class convertToMp3 extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e("正在转码","开始进行。。。。");
			JNIMp3Encode.convertmp3(pcmPath, mp3Path, 8000);
			Log.e("转码完成","转码成功。。。。");
		}
		
	}
	public void onDestroy(){
		super.onDestroy();
	}

}
