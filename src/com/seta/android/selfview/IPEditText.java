package com.seta.android.selfview;

import com.seta.android.record.utils.IatSettings;
import com.seta.android.recordchat.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 自定义控件实现ip地址特殊输入
 * 
 * @author anshe
 * 
 *         2015-7-20
 */
public class IPEditText extends LinearLayout {

	private EditText mFirstIP;
	private EditText mSecondIP;
	private EditText mThirdIP;
	private EditText mFourthIP;

	private String mText;
	private String mText1;
	private String mText2;
	private String mText3;
	private String mText4;

	private SharedPreferences mPreferences;

	public IPEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		/**
		 * 初始化控件
		 */
		View view = LayoutInflater.from(context).inflate(
				R.layout.custom_my_edittext, this);
		mFirstIP = (EditText) findViewById(R.id.ip_first);
		mSecondIP = (EditText) findViewById(R.id.ip_second);
		mThirdIP = (EditText) findViewById(R.id.ip_third);
		mFourthIP = (EditText) findViewById(R.id.ip_fourth);

		mPreferences = context.getSharedPreferences(IatSettings.PREFER_NAME,
				Activity.MODE_PRIVATE);
		String[] ips = mPreferences.getString("SERVER_HOST", "0.0.0.0").split(
				".");
		if (ips.length > 0) {
			mFirstIP.setText(ips[0]);
		}
		if (ips.length > 1) {
			mThirdIP.setText(ips[1]);
		}
		if (ips.length > 2) {
			mThirdIP.setText(ips[2]);
		}
		if (ips.length > 3) {
			mFourthIP.setText(ips[3]);
		}
		OperatingEditText(context);
	}

	/**
	 * 获得EditText中的内容,当每个Edittext的字符达到三位时,自动跳转到下一个EditText,当用户点击.时,
	 * 下一个EditText获得焦点
	 */
	private void OperatingEditText(final Context context) {
		mFirstIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0&&!s.toString().startsWith(".")) {
					if (s.length() > 2 || s.toString().trim().contains(".")) {
						if (s.toString().trim().contains(".")) {
							mText1 = s.toString().substring(0, s.length() - 1);
							mFirstIP.setText(mText1);
							mFirstIP.setSelection(mText1.length());
						} else {
							mText1 = s.toString().trim();
						}

						if (Integer.parseInt(mText1) > 255) {
							Toast.makeText(context, "请输入合法的ip地址",
									Toast.LENGTH_LONG).show();
							mText1=mText1.substring(0, mText1.length()-1);
							mFirstIP.setText(mText1);
							mFirstIP.setSelection(mText1.length());
							return;
						}
						mSecondIP.setFocusable(true);
						mSecondIP.requestFocus();
						if (s.length()>3&&!s.toString().endsWith(".")) {
							if (mText2==null) {
								mText2=s.subSequence(3, s.length()).toString();
								mSecondIP.setText(mText2);
							}
						}
						if (mText2!=null&&mText2.length()>0) {
							mSecondIP.setSelection(mText2.length());							
						}
					}
				}
				if (s.toString().startsWith(".")) {
					mFirstIP.setText("");
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mSecondIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0&&!s.toString().startsWith(".")) {
					if (s.toString().trim().contains(".")) {
						mText2 = s.toString().substring(0, s.length() - 1);
						mSecondIP.setText(mText2);
						mSecondIP.setSelection(mText2.length());
					} else {
						mText2 = s.toString().trim();
					}

					if (Integer.parseInt(mText2) > 255) {
						Toast.makeText(context, "请输入合法的ip地址", Toast.LENGTH_LONG)
								.show();
						mText2=mText2.substring(0, mText2.length()-1);
						mSecondIP.setText(mText2);
						mSecondIP.setSelection(mText2.length());
						return;
					}

					if (s.length() > 2 || s.toString().trim().contains(".")) {
						mThirdIP.setFocusable(true);
						mThirdIP.requestFocus();
						if (s.length()>3&&!s.toString().endsWith(".")) {
							if (mText3==null) {
								mText3=s.subSequence(3, s.length()).toString();
								mThirdIP.setText(mText3);
							}
						}
						if (mText3!=null&&mText3.length()>0) {
							mThirdIP.setSelection(mText3.length());
						}						
					}
				}
				if (s.toString().startsWith(".")) {
					mSecondIP.setText("");
				}

				/**
				 * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
				 */
				if (start == 0 && s.length() == 0 && mFirstIP.length() < 4) {
					mFirstIP.setFocusable(true);
					mFirstIP.requestFocus();
					mFirstIP.setSelection(mFirstIP.length());
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mThirdIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0&&!s.toString().startsWith(".")) {

					if (s.toString().trim().contains(".")) {
						mText3 = s.toString().substring(0, s.length() - 1);
						mThirdIP.setText(mText3);
						mThirdIP.setSelection(mText3.length());
					} else {
						mText3 = s.toString().trim();
					}

					if (Integer.parseInt(mText3) > 255) {
						Toast.makeText(context, "请输入合法的ip地址", Toast.LENGTH_LONG)
								.show();
						mText3=mText3.substring(0, mText3.length()-1);
						mThirdIP.setText(mText3);
						mThirdIP.setSelection(mText3.length());
						return;
					}
					if (s.length() > 2 || s.toString().trim().contains(".")) {
						mFourthIP.setFocusable(true);
						mFourthIP.requestFocus();
						if (s.length()>3&&!s.toString().endsWith(".")) {
							if (mText4==null) {
								mText4=s.subSequence(3, s.length()).toString();
								mFourthIP.setText(mText4);
							}
						}
						if (mText4!=null&&mText4.length()>0) {
							mFourthIP.setSelection(mText4.length());
						}						
					}
				}
				if (s.toString().startsWith(".")) {
					mThirdIP.setText("");
				}

				/**
				 * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
				 */
				if (start == 0 && s.length() == 0 && mSecondIP.length() < 3) {
					mSecondIP.setFocusable(true);
					mSecondIP.requestFocus();
					mSecondIP.setSelection(mSecondIP.length());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mFourthIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0&&!s.toString().startsWith(".")) {
					if (s.toString().trim().contains(".")) {
						mText4 = s.toString().substring(0, s.length() - 1);
						mFourthIP.setText(mText4);
						mFourthIP.setSelection(mText4.length());
					} else {
						mText4 = s.toString().trim();
					}
					if (Integer.parseInt(mText4) > 255) {
						Toast.makeText(context, "请输入合法的ip地址", Toast.LENGTH_LONG)
								.show();
						mText4=mText4.substring(0, mText4.length()-1);
						mFourthIP.setText(mText4);
						mFourthIP.setSelection(mText4.length());
						return;
					}

					Editor editor = mPreferences.edit();
					editor.putInt("IP_FOURTH", mText4.length());
					editor.commit();
				}
				/**
				 * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
				 */
				if (start == 0 && s.length() == 0 && mThirdIP.length() < 3) {
					mThirdIP.setFocusable(true);
					mThirdIP.requestFocus();
					mThirdIP.setSelection(mThirdIP.length());
				}

				if (s.toString().startsWith(".")) {
					mFourthIP.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	public String getText(Context context) {
		String ipString = mText1 + "." + mText2 + "." + mText3 + "." + mText4;
		if (TextUtils.isEmpty(mText1) || TextUtils.isEmpty(mText2)
				|| TextUtils.isEmpty(mText3) || TextUtils.isEmpty(mText4)) {
			Toast.makeText(context, "请输入合法的ip地址" + ipString, Toast.LENGTH_SHORT)
					.show();
		} else {
			Editor editor = mPreferences.edit();
			editor.putString("SERVER_HOST", ipString);
			editor.apply();
		}
		return ipString;
	}
}
