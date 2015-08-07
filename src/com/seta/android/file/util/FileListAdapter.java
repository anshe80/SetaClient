package com.seta.android.file.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seta.android.email.SendEmailActivity;
import com.seta.android.fragment.privateFragment;
import com.seta.android.recordchat.R;
import com.sys.android.util.OpenfileFunction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FileListAdapter extends BaseExpandableListAdapter {
	public static String FILE_ROOT_PATH = Environment.getExternalStorageDirectory().getPath()
			+ "/seta/file";
	public static String RECORD_ROOT_PATH = Environment.getExternalStorageDirectory().getPath()
			+ "/seta/record";
	// added by wyg
	public static String PERSONAL_ROOT_PATH = Environment.getExternalStorageDirectory().getPath()
			+ "/seta/personal";
	//end wyg
	
	private String[] fileTypes = new String[] {  "传输文件列表","录音文件列表","私人记录列表" };
	private List<List<File>> list = null;
	private Map<String,String> fileMap = new HashMap<String,String>();
	private Map<String,String> recordMap = new HashMap<String,String>();
	//added wyg
	private Map<String,String> personalMap = new HashMap<String,String>();
	private FileManageUtil fileUtil = new FileManageUtil();
	private Context context;
	private LayoutInflater inflater = null;
	private String filePath;

	public FileListAdapter() {
	}

	public FileListAdapter(Context context,List<List<File>> list ) {
		this.context = context;
		this.list = list;	
		this.inflater = LayoutInflater.from(context);
		this.fileMap = fileUtil.getFileNameSizeMap(FILE_ROOT_PATH);
		this.recordMap = fileUtil.getFileNameSizeMap(RECORD_ROOT_PATH);
		this.personalMap = fileUtil.getFileNameSizeMap(PERSONAL_ROOT_PATH);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return this.list.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.file_name_size, null);
		}
		TextView fileName = (TextView) convertView.findViewById(R.id.file_name);
		TextView fileSize = (TextView) convertView.findViewById(R.id.file_size);
		View imageView=(View) convertView.findViewById(R.id.file_button);
		String strName = this.list.get(groupPosition).get(childPosition).getName();
		filePath=this.list.get(groupPosition).get(childPosition).getPath();
		fileName.setText(strName);
		if(this.fileMap.containsKey(strName)){
			fileSize.setText(this.fileMap.get(strName));
		} else if(this.recordMap.containsKey(strName)){
			fileSize.setText(this.recordMap.get(strName));
		}//added by wyg
		else if(this.personalMap.containsKey(strName)){
			fileSize.setText(this.personalMap.get(strName));
		}
		//end wyg
		imageView.setOnClickListener(new OnClickListener() {			

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/**
				 *anshe
				 *2015-7-7下午8:44:35
				 *@param
				 */
				filePath=list.get(groupPosition).get(childPosition).getPath();
				System.out.println("文件路径："+filePath);
				Intent intent = OpenfileFunction.openFile(filePath);
				if (intent != null) {
					context.startActivity(intent);
				}
			}
		});
		imageView.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				filePath=list.get(groupPosition).get(childPosition).getPath();
				System.out.println("文件路径："+filePath);
				File file = new File(filePath);
				if (file.exists()) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							context);
					dialog.setTitle("发送邮件")
							.setIcon(R.drawable.icon)
							.setMessage("确定将此文件发送给好友吗？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method
											// stub
											Intent intent = new Intent(
													context,
													SendEmailActivity.class);
											intent.putExtra("filePath",
													filePath);
											context.startActivity(intent);

										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method
											// stub
											dialog.cancel();// 取消弹出框
										}
									}).create().show();
				} else {
					Toast.makeText(context,
							context.getString(R.string.record_sending),
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
			
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return this.list.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return this.fileTypes[groupPosition];
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return this.fileTypes.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = this.inflater.inflate(R.layout.file_type, null);
		}
		TextView fileType = (TextView) convertView.findViewById(R.id.file_type);
		fileType.setText(this.fileTypes[groupPosition]);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
