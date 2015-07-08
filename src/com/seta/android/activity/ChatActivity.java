package com.seta.android.activity;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.seta.android.activity.adapter.ChatListAdapter;
import com.seta.android.entity.Msg;
import com.seta.android.record.utils.IatSettings;
import com.seta.android.record.utils.JsonParser;
import com.seta.android.xmppmanager.XmppConnection;
import com.sys.android.util.JNIMp3Encode;
import com.sys.android.util.MutilUserChatUtil;
import com.sys.android.util.NetWorkConnection;
import com.sys.android.util.TimeRender;
import com.sys.android.util.Utils;
import com.seta.android.recordchat.R;

public class ChatActivity extends Activity {

	private String userChat = "";// 当前聊天 userChat
	private String userChatSendFile = "";// 给谁发文件
	private ChatListAdapter adapter;
	private List<Msg> listMsg = new LinkedList<Msg>();
	private String pUSERID;// 自己的user
	private String pFRIENDID;// 窗口的 名称
	private EditText msgText;
	private TextView chat_name;
	private NotificationManager mNotificationManager;
	private MultiUserChat muc;
	private Button mRecordButton;
	private XMPPConnection connection;
	private SpeechRecognizer mIat;// 语音听写对象
	private SharedPreferences mSharedPreferences;
	private static String TAG = ChatActivity.class.getSimpleName();
	private Activity context;
	private String caseAudio = RECORD_ROOT_PATH;
	private String audioPath;
	private boolean recording = false;
	private long startTime;
	private long time;
	StringBuffer resultsString = new StringBuffer();

	// 发送文件
	private OutgoingFileTransfer sendTransfer;
	public static String FILE_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/seta/file";
	public static String RECORD_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/seta/record";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_client);
		context = this;
		init();
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "="
				+ getString(R.string.app_id));
		// 初始化识别对象 +","+ SpeechConstant.FORCE_LOGIN +"=true"
		mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
		// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
		mSharedPreferences = this.getSharedPreferences(IatSettings.PREFER_NAME,
				Activity.MODE_PRIVATE);
		mRecordButton = (Button) findViewById(R.id.record_button);
		File file = new File(caseAudio);
		if (!file.exists()) {
			file.mkdirs();
		}
		mRecordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!recording) {
					recording = true;
					startTime = System.currentTimeMillis();
					mRecordButton.setText(getString(R.string.recording));
					// 清空text modify by anshe 2015.5.26
					if (resultsString != null && resultsString.length() > 1)
						resultsString.delete(0, resultsString.length() - 1);
					msgText.setText("");
					caseAudio = RECORD_ROOT_PATH + "/"
							+ System.currentTimeMillis() + ".pcm";
					record();
				} else {
					completeRecord();
				}
			}

		});

	}

	// start add by anshe 2015.5.24
	public void completeRecord() {

		recording = false;
		mIat.stopListening();
		mRecordButton.setText(getString(R.string.start_record));
		time = (System.currentTimeMillis() - startTime) / 1000;
		audioPath = caseAudio.replace("pcm", "mp3");
		String fileName = audioPath.split("/")[audioPath.split("/").length - 1];
		Log.e("缓存的音频路径：", "caseAudio=" + caseAudio);
		Log.e("转码后的音频路径：", "audioPath=" + audioPath + " fileName=" + fileName);
		Thread convert = new Thread(new convertToMp3());
		convert.start();
		try {
			// 发送 对方的消息
			Msg sendChatMsg = new Msg(pFRIENDID, time
					+ getString(R.string.record_info), TimeRender.getDate(),
					pUSERID, Msg.TYPE[0], Msg.STATUS[3], time + "",
					audioPath.split("/")[audioPath.split("/").length - 1]);
			// 刷新适配器
			adapter.notifyDataSetChanged();
			// 发送消息
			muc.sendMessage(Msg.toJson(sendChatMsg));
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(ChatActivity.this, "发送异常", Toast.LENGTH_SHORT)
					.show();
		}

	}

	// end add by anshe 2015.5.24

	// start add by anshe 2015.5.20
	class convertToMp3 extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e("正在转码", "开始进行。。。。");
			File file = new File(caseAudio);
			while (!file.exists())
				;// waiting for audio
			try {
				if (file.exists()) {
					JNIMp3Encode.convertmp3(caseAudio, audioPath, 8000);
					Log.e("转码完成", "文件生成成功");
				} else {
					Log.e("转码完成", "转码失败。。。。文件不存在");
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("转码完成", "转码失败。。。。");

			}
			file = new File(audioPath);
			if (file.exists()) {
				Msg myChatMsg = new Msg(pUSERID, time
						+ getString(R.string.record_info),
						TimeRender.getDate(), pUSERID, Msg.TYPE[0],
						Msg.STATUS[3], time + "", audioPath);
				sendFile(audioPath, myChatMsg);
				file = new File(caseAudio);
				file.delete();
			} else {
				Log.e("发送文件情况：", "发送失败");
			}
		}

	}

	// end add by anshe 2015.5.20
	// start add by anshe 2015.5.18

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

	/**
	 * 听写监听器。
	 */
	public void recognizeStream(RecognizerListener listener, String ent,
			String params, String grammar) {

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
			mRecordButton.setText(getString(R.string.start_record));
			completeRecord();
			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			resultsString.append(printResult(results));

			msgText.setText(resultsString.toString());
			Log.e("语音识别后的消息：", resultsString.toString());
			msgText.setSelection(msgText.length());
			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			// showTip("当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};

	private StringBuffer printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());
		// 用HashMap存储听写结果
		HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			Log.e("语音识别结果：", "resultJson=" + resultJson.toString());
			sn = resultJson.optString("sn");
			Log.e("语音识别结果：", "sn=" + sn.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mIatResults.put(sn, text);
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
			Log.e("语音识别结果：", "resultBuffer=" + resultBuffer);
		}
		return resultBuffer;

	}

	private void showTip(final String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	// end add by anshe 2015.5.18
	private void init() {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Service.NOTIFICATION_SERVICE);
		// 获取Intent传过来的用户名
		this.pUSERID = getIntent().getStringExtra("USERID");
		this.userChat = getIntent().getStringExtra("user");
		this.pFRIENDID = getIntent().getStringExtra("FRIENDID");
		// start modify by anshe 2015.5.25
		userChatSendFile = pFRIENDID + "@" + XmppConnection.SERVER_NAME
				+ "/Smack";
		joinChatRoom();
		Log.e("发送的用户：", "userChat=" + userChat + " pFRIENDID=" + pFRIENDID
				+ " userChatSendFile=" + userChatSendFile);

		chat_name = (TextView) findViewById(R.id.chat_name);
		chat_name.setText(pFRIENDID);
		ListView listview = (ListView) findViewById(R.id.formclient_listview);
		listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		this.adapter = new ChatListAdapter(this, listMsg);
		listview.setAdapter(adapter);
		// 获取文本信息
		this.msgText = (EditText) findViewById(R.id.formclient_text);
		// 消息监听
		
		// 返回按钮
		Button mBtnBack = (Button) findViewById(R.id.chat_back);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//start modify by anshe 2015.7.6
				if (connection!=null&&connection.isAuthenticated()
						&&NetWorkConnection.isNetworkAvailable(context)
						&&muc != null) {   
					muc.leave();
				}
				finish();
				//end modify by anshe 2015.7.6
			}
		});
		if (muc != null) {
			receivedMsg();// 接收消息
			sendMsg();// 发送消息
			receivedFile();// 接收文件
		}

	}

	public boolean joinChatRoom() {//modify by anshe 2015.7.6
		connection = XmppConnection.getConnection(this);
		if (connection!=null) {
			final MutilUserChatUtil mutilUserRoomList = new MutilUserChatUtil(connection);
			userChat=connection.getUser();
			muc = mutilUserRoomList.joinMultiUserChat(userChat, pFRIENDID, "");
			if (muc!=null) {
				return true;
			}else {
				return false;
			}
		}else{
			Toast.makeText(context, getString(R.string.not_Connect_to_Server), Toast.LENGTH_LONG).show();
			return false;
		}	
	}

	// end modify by anshe 2015.5.25
	/**
	 * 接收消息
	 */
	public void receivedMsg() {
		boolean addListener=true;
		if (muc == null) {
			addListener=joinChatRoom();
		}
		if (addListener) {
			muc.addMessageListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					String from = Utils.getJidToUsername(message.getFrom());
					String toUser = Utils.getJidToUsername(message.getTo());
					String nowUser = Utils.getJidToUsername(XmppConnection
							.getConnection(context).getUser());
					if (!from.equals(toUser) && !nowUser.equals(from)) {
						// Msg.analyseMsgBody(message.getBody(),userChat);
						// 获取用户、消息、时间、IN
						// 在handler里取出来显示消息
						android.os.Message msg = handler.obtainMessage();
						System.out.println("服务器发来的消息是 chat：" + message.getBody());
						msg.what = 1;
						msg.obj = message.getBody();
						msg.sendToTarget();

					}
				}
			});
		}
	}

	/**
	 * 发送消息
	 * 
	 * @author Administrator
	 * 
	 */
	public void sendMsg() {
		// 发送消息
		Button btsend = (Button) findViewById(R.id.formclient_btsend);
		btsend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取text文本
				final String msg = msgText.getText().toString();
				if (msg.length() > 0) {
					// 发送对方
					Log.e("发送的消息内容：", msg);
					Msg sendChatMsg = new Msg(pFRIENDID, msg, TimeRender
							.getDate(), pUSERID, Msg.TYPE[2]);
					// 刷新适配器
					adapter.notifyDataSetChanged();
					try {
						// 发送消息
						muc.sendMessage(Msg.toJson(sendChatMsg));

					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(ChatActivity.this,
								getString(R.string.failedconnection_info),
								Toast.LENGTH_SHORT).show();
					}
					// 清空text modify by anshe 2015.5.26
					if (resultsString != null && resultsString.length() > 1)
						resultsString.delete(0, resultsString.length() - 1);
					msgText.setText("");

				} else {
					Toast.makeText(ChatActivity.this, getString(R.string.send_message_is_null),
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	public void record() {
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
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		//add by anshe 2015.7.8
		String typeString=mSharedPreferences.getString("iat_type_preference",SpeechConstant.TYPE_CLOUD);
		typeString=typeString.equals(SpeechConstant.TYPE_AUTO)?SpeechConstant.TYPE_MIX:typeString;
		// 设置听写引擎 云端
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, typeString);
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
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "1000"));
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString(
				"iat_vadeos_preference", getString(R.string.record_stop_time)));
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("iat_punc_preference", "1"));
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, caseAudio);
		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA,
				mSharedPreferences.getString("iat_dwa_preference", "1"));

	}

	/**
	 * 接收文件
	 * 
	 * @author anshe
	 * 
	 */
	//start modify by anshe 2015.7.5
	public void receivedFile() {
		/**
		 * 接收文件
		 */
		if (connection==null) {
			connection = XmppConnection.getConnection(this);
		}
		if (connection!=null) {
			// Create the file transfer manager
			final FileTransferManager manager = new FileTransferManager(connection);
			// Create the listener
			Log.e("receivedFile ", " receive file=" + userChat);
			if (manager!=null) {
				manager.addFileTransferListener(new FileTransferListener() {

					public void fileTransferRequest(FileTransferRequest request) {
						// Check to see if the request should be accepted
						if (shouldAccept(request)) {
							// Accept it
							IncomingFileTransfer transfer = request.accept();
							try {
								// start modify by anshe 2015.5.25
								File file = new File(RECORD_ROOT_PATH + "/"
										+ request.getFileName());
								android.os.Message msg = handler.obtainMessage();
								transfer.recieveFile(file);
								Msg msgInfo = queryMsgForListMsg(file.getName());
								msgInfo.setFilePath(request.getFileName());// 更新
																			// filepath
								new MyFileStatusThread(transfer, msgInfo).start();
								Log.e("receivedFile", "userChat=" + userChat
										+ "接收到的文件名：" + request.getFileName()
										+ " 接收的路径：" + file.getPath());
								// end modify by anshe 2015.5.25
							} catch (XMPPException e) {
								e.printStackTrace();
							}
						} else {
							// Reject it
							request.reject();
							String[] args = new String[] { userChat,
									request.getFileName(), TimeRender.getDate(),
									pFRIENDID, Msg.TYPE[0], Msg.STATUS[1] };
							Msg msgInfo = new Msg(args[0], "redio", args[2], args[3],
									Msg.TYPE[0], Msg.STATUS[1]);
							// 在handler里取出来显示消息
							android.os.Message msg = handler.obtainMessage();
							msg.what = 4;
							msg.obj = msgInfo;
							handler.sendMessage(msg);
						}
					}
				});
			}			
		}else{
			Toast.makeText(context, getString(R.string.not_Connect_to_Server), Toast.LENGTH_LONG).show();
		}		
	}
	//end modify by anshe 2015.7.5
	/**
	 * 发送文件
	 * 
	 * @param path
	 */
	public void sendFile(String path, Msg msg) {
		/**
		 * 发送文件
		 */

		if (connection==null) {
			connection = XmppConnection.getConnection(this);
		}
		if (connection!=null) {
			// Create the file transfer manager
			FileTransferManager sendFilemanager = new FileTransferManager(connection);
			if (sendFilemanager!=null&&muc!=null) {
				// Create the outgoing file transfer
				List<String> user = MutilUserChatUtil.findMulitUser(muc);
				for (int i = 0; i < user.size(); i++) {
					if (!userChat.contains(user.get(i))) {
						userChatSendFile = user.get(i) + "@"
								+ XmppConnection.SERVER_NAME + "/Smack";
						Log.e("发送给哪些用户：", user.get(i) + "文件路径：" + path);
						sendTransfer = sendFilemanager.createOutgoingFileTransfer(userChatSendFile);// 账号@域名/资源名
						// Send the file
						try {
							sendTransfer.sendFile(new java.io.File(path), "send file");
							while (!sendTransfer.isDone());
							new MyFileStatusThread(sendTransfer, msg).start();
							/**
							 * 监听
							 */
						} catch (XMPPException e) {
							e.printStackTrace();
						}
					}
				}
			}			
		}else{
			Toast.makeText(context, getString(R.string.not_Connect_to_Server), Toast.LENGTH_LONG).show();
		}	
	}

	class MyFileStatusThread extends Thread {
		private FileTransfer transfer;
		private Msg msg;

		public MyFileStatusThread(FileTransfer tf, Msg msg) {
			transfer = tf;
			this.msg = msg;
		}

		public void run() {
			android.os.Message message = new android.os.Message();// handle
			message.what = 3;
			while (!transfer.isDone()) {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (transfer.getStatus().equals(Status.error)) {
				msg.setReceive(Msg.STATUS[2]);
			} else if (transfer.getStatus().equals(Status.refused)) {
				msg.setReceive(Msg.STATUS[1]);
			} else {
				msg.setReceive(Msg.STATUS[0]);// 成功
			}
			handler.sendMessage(message);
			/*
			 * System.out.println(transfer.getStatus());
			 * System.out.println(transfer.getProgress());
			 */
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Msg chatMsg = Msg.analyseMsgBody(msg.obj.toString());
				if (chatMsg != null) {
					listMsg.add(chatMsg);// 添加到聊天消息
					adapter.notifyDataSetChanged();
				}
				break;
			case 2: // 发送文件 add by anshe 2015.5.11
				// sendMsg();
				// 2015.5.11
				break;
			case 3: // 更新文件发送状态
				adapter.notifyDataSetChanged();
				break;
			case 4: // 接收文件
				Toast.makeText(context,
						getString(R.string.record_receive_success),
						Toast.LENGTH_SHORT).show();
				Msg msg2 = (Msg) msg.obj;
				System.out.println(msg2.getFrom());
				listMsg.add(msg2);
				adapter.notifyDataSetChanged();
			default:
				break;
			}
		};
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//start modify by anshe 2015.7.6
		if (connection!=null&&connection.isAuthenticated()
				&&NetWorkConnection.isNetworkAvailable(context)
				&&muc!=null) {
			muc.leave(); // 退出聊天聊天室
		}
		//end modify by anshe 2015.7.6
		ChatActivity.this.finish();
	}

	protected void setNotiType(int iconId, String s) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent appIntent = PendingIntent.getActivity(this, 0, intent, 0);
		Notification myNoti = new Notification();
		myNoti.icon = iconId;
		myNoti.tickerText = s;
		myNoti.defaults = Notification.DEFAULT_SOUND;
		myNoti.flags |= Notification.FLAG_AUTO_CANCEL;
		myNoti.setLatestEventInfo(this, getString(R.string.seta_info), s,
				appIntent);
		mNotificationManager.notify(0, myNoti);
	}

	/**
	 * 是否接收
	 * 
	 * @param request
	 * @return
	 */
	private boolean shouldAccept(FileTransferRequest request) {
		final boolean isAccept[] = new boolean[1];

		return true;
	}

	protected void dialog() {

	}

	/**
	 * init file
	 */
	static {
		File root = new File(FILE_ROOT_PATH);
		root.mkdirs();// 没有根目录创建根目录
		root = new File(RECORD_ROOT_PATH);
		root.mkdirs();
	}

	/**
	 * 从list 中取出 分拣名称相同的 Msg
	 */
	private Msg queryMsgForListMsg(String filePath) {

		Msg msg = null;
		for (int i = listMsg.size() - 1; i >= 0; i--) {
			msg = listMsg.get(i);
			if (filePath != null && filePath.contains(msg.getFilePath())) {// 对方传过来的只是文件的名称
				return msg;
			}
		}
		return msg;
	}

	public void onDestroy() {
		super.onDestroy();
	}
}