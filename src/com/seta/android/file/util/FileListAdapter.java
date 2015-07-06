package com.seta.android.file.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seta.android.email.SendEmailActivity;
import com.seta.android.recordchat.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
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

	private String[] fileTypes = new String[] {  "传输文件列表","录音文件列表" };
	private List<List<File>> list = null;
	private Map<String,String> fileMap = new HashMap<String,String>();
	private Map<String,String> recordMap = new HashMap<String,String>();
	private FileUtil fileUtil = new FileUtil();
	private Context cxt;
	private LayoutInflater inflater = null;
	private String filePath;

	public FileListAdapter() {
	}

	public FileListAdapter(Context context,List<List<File>> list ) {
		this.cxt = context;
		this.list = list;	
		this.inflater = LayoutInflater.from(context);
		this.fileMap = fileUtil.getFileNameSizeMap(FILE_ROOT_PATH);
		this.recordMap = fileUtil.getFileNameSizeMap(RECORD_ROOT_PATH);
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
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
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
		}
		imageView.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				File file = new File(filePath);
				if (file.exists()) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							cxt);
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
													cxt,
													SendEmailActivity.class);
											intent.putExtra("filePath",
													filePath);
											cxt.startActivity(intent);

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
					Toast.makeText(cxt,
							cxt.getString(R.string.record_sending),
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
