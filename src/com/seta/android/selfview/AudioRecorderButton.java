package com.seta.android.selfview;

import com.seta.android.recordchat.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
//create by ling 2015.5.12
public class AudioRecorderButton extends Button {
	private static final int STATE_NORMAL=1;//Ĭ��״̬
	private static final int STATE_RECORDING=2;//¼��״̬
	private static final int STATE_WANT_TO_CANCEL=3;//ȡ��״̬
	private static final int DISTANCE_Y_CANCEL=50;
	
	private int mCurState=STATE_NORMAL;//��ǰ��¼״̬
	//�Ѿ���ʼ¼��
	private boolean isRecording=false;
	public AudioRecorderButton(Context context) {
		this(context,null);
	}
	public AudioRecorderButton(Context context,AttributeSet attrs) {
		super(context,attrs);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action=event.getAction();
		int x=(int)event.getX();
		int y=(int)event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			changeState(STATE_RECORDING);
			
			break;
		case MotionEvent.ACTION_MOVE:
			
			if(isRecording)
			{
				//����x,y�����꣬�ж��Ƿ���Ҫȡ��
				if(wantToCancel(x,y))
				{
					changeState(STATE_WANT_TO_CANCEL);
				}
				else
				{
					changeState(STATE_RECORDING);
				}
			}
			
			break;
		case MotionEvent.ACTION_UP:
			if(mCurState==STATE_RECORDING)
			{
				
			}
			else if(mCurState==STATE_WANT_TO_CANCEL)
			{
				
			}
			reset();
			break;

		}
		return super.onTouchEvent(event);
	}
	/**
	 * �ָ�״̬����־λ
	 */
	private void reset() {
		// TODO Auto-generated method stub
		isRecording=false;
		changeState(STATE_NORMAL);
	}
	private boolean wantToCancel(int x, int y) {
		// TODO Auto-generated method stub
		if(x<0||x>getWidth())
		{
			return true;
		}
		if(y<-DISTANCE_Y_CANCEL||y>getHeight()+DISTANCE_Y_CANCEL)
		{
			
		}
		return false;
	}
	private void changeState(int state) {
		// TODO Auto-generated method stub
		if(mCurState!=state)
		{
			mCurState=state;
			switch (state) {
			case STATE_NORMAL:
				setBackgroundResource(R.drawable.chat_bottom_send_normal);
				setText(R.string.str_recorder_normal);
				break;
			case STATE_RECORDING:
				setBackgroundResource(R.drawable.chat_bottom_send_pressed);
				setText(R.string.str_recorder_recording);
				if(isRecording)
				{
					//Dialog.recording();
				}
				break;
			case STATE_WANT_TO_CANCEL:
				setBackgroundResource(R.drawable.chat_bottom_send_pressed);
				setText(R.string.str_recorder_want_cancel);
				//Dialog.want_cancel();
				break;

			default:
				break;
			}
		}
	}

}
