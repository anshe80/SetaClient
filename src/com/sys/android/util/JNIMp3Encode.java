package com.sys.android.util;

public class JNIMp3Encode {
	static {
		System.loadLibrary("convert");
	}

   /**
	 * wav转换成mp3的本地方法
	 * 
	 * @param wav
	 * @param mp3
	 * @param sampleRate 音频采样率：常用的如44100Hz、22050Hz、16000Hz、11025Hz、8000Hz，
	 */
	public native static void convertmp3(String wav, String mp3, int sampleRate);



   


}

