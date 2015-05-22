package com.seta.android.fragment;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.seta.android.activity.ChatActivity;
import com.seta.android.entity.Msg;
import com.seta.android.record.IatSettings;
import com.seta.android.record.JsonParser;
import com.sys.android.util.JNIMp3Encode;
import com.sys.android.util.TimeRender;
import com.seta.android.recordchat.R;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ling on 2015/4/29.
 */
public class privateFragment extends Fragment {
	

	public static String RECORD_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/seta/record";
	private SpeechRecognizer mIat;// 语音听写对象
	private String caseAudio=RECORD_ROOT_PATH;
	private SharedPreferences mSharedPreferences;
	private String audioPath;
	private long startTime;
	private long time;
	private static String TAG = ChatActivity.class.getSimpleName();
	private Button mRecordButton;
	private EditText msgText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.privatefragment, container, false);

		SpeechUtility.createUtility(this.getActivity(), SpeechConstant.APPID + "="+getString(R.string.app_id));
		// 初始化识别对象     +","+ SpeechConstant.FORCE_LOGIN +"=true"
		mIat = SpeechRecognizer.createRecognizer(this.getActivity(), mInitListener);
		mSharedPreferences = this.getActivity().getSharedPreferences(IatSettings.PREFER_NAME,
				Activity.MODE_PRIVATE);
		msgText=(EditText) view.findViewById(R.id.formclient_text);
		mRecordButton=(Button) view.findViewById(R.id.record_button);
		mRecordButton.setText(getString(R.string.start_record));
		mRecordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!mIat.isListening()) {
					startTime = System.currentTimeMillis();
					caseAudio =RECORD_ROOT_PATH+ "/" + System.currentTimeMillis() + ".pcm";
					record();
				} else {
					if(mIat.isListening()){
						mIat.stopListening();
					}
					mRecordButton.setText(getString(R.string.start_record));
					time = (System.currentTimeMillis() - startTime)/1000;
					audioPath=caseAudio.replace("pcm", "mp3");
					Thread convert=new Thread(new convertToMp3());
					convert.start();

				}
			}
			
		});
		
        return view;
    }
	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			}
		}
	};
	
	//start add by anshe 2015.5.20
		class convertToMp3 implements Runnable{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				JNIMp3Encode convert=new JNIMp3Encode();
				convert.convertmp3(caseAudio, audioPath, 8000);
				File file=new File(audioPath);	
				if(file.exists()){
					file=new File(caseAudio);
					file.delete();	
				}else{
					Log.e("发送文件情况：", "发送失败");
				}
			}
			
		}
		//end add by anshe 2015.5.20
		
	public void record(){
		setParam();
		int ret = 0; // 函数调用返回值
		ret = mIat.startListening(recognizerListener);
		if (ret != ErrorCode.SUCCESS) {
			showTip("听写失败,错误码：" + ret);
		} else {
			showTip(getString(R.string.text_begin));			
		}
	}
	/**
	 * 听写监听器。
	 */
	public void recognizeStream(RecognizerListener listener, String ent, String params, String grammar){
		
	}
	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			mRecordButton.setText(getString(R.string.recording));
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语音+）需要提示用户开启语音+的录音权限。
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {			
			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			String resultsString=printResult(results);

			msgText.setText(resultsString.toString());
			Log.e("语音识别后的消息：", resultsString.toString());
			msgText.setSelection(msgText.length());
			if (isLast) {
				// TODO 最后的结果				
				mRecordButton.setText(getString(R.string.start_record));
				mIat.stopListening();
			}
		}

		@Override
		public void onVolumeChanged(int volume) {			
			showTip("当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};
	
	private String printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());
		// 用HashMap存储听写结果
		HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mIatResults.put(sn, text);
		 
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		return resultBuffer.toString();
		
	}
	private void showTip(final String str) {
		Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
	}
	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎 云端
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "4000"));
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, caseAudio);
		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "1"));
		
	}

	
}
