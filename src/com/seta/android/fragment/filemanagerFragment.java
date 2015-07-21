package com.seta.android.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.Toast;

import com.seta.android.file.util.FileListAdapter;
import com.seta.android.file.util.FileUtil;
import com.seta.android.recordchat.R;
import com.sys.android.util.OpenfileFunction;

/**
 * Created by ling on 2015/4/29.
 */
public class filemanagerFragment extends Fragment {

	private ExpandableListView listView = null;
	private FileListAdapter adapter = null;
	private List<List<File>> list = new ArrayList<List<File>>();
	private Map<String, String> fileMap = new HashMap<String, String>();
	private Map<String, String> recordMap = new HashMap<String, String>();
	private Map<String, String> personalMap = new HashMap<String, String>();
	private Button btnDelFile = null;
	private Button btnDelRecord = null;
	private Button btnDelPersonal = null;

	public static String FILE_ROOT_PATH = Environment.getExternalStorageDirectory().getPath()
			+ "/seta/file";
	public static String RECORD_ROOT_PATH = Environment.getExternalStorageDirectory().getPath()
			+ "/seta/record";
	// added by wyg
	public static String PERSONAL_ROOT_PATH = Environment.getExternalStorageDirectory().getPath()
				+ "/seta/personal";
		//end wyg

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.filemanagerfragment, container, false);
		listView = (ExpandableListView) view.findViewById(R.id.filelist);
		
		//added by wyg 
		loadData();
		/*
		FileUtil fileUtil = new FileUtil();
		List<File> temp = fileUtil.queryFile(FILE_ROOT_PATH);
		if (temp != null) {
			this.list.add(temp);
			this.fileMap = fileUtil.getFileNameSizeMap(FILE_ROOT_PATH);
		}
		temp = fileUtil.queryFile(RECORD_ROOT_PATH);
		if (temp != null) {
			this.list.add(temp);
			this.recordMap = fileUtil.getFileNameSizeMap(RECORD_ROOT_PATH);
		}
		temp = fileUtil.queryFile(PERSONAL_ROOT_PATH);
		if (temp != null) {
			this.list.add(temp);
			this.personalMap = fileUtil.getFileNameSizeMap(PERSONAL_ROOT_PATH);
		}
*/
		adapter = new FileListAdapter(this.getActivity(), list);
		listView.setAdapter(adapter);

		btnDelFile = (Button) view.findViewById(R.id.btnDelFile);
		btnDelRecord = (Button) view.findViewById(R.id.btnDelRecord);
		btnDelFile.setOnClickListener(new DelFileListener());
		btnDelRecord.setOnClickListener(new DelRecordListener());
		btnDelPersonal = (Button) view.findViewById(R.id.btnDelPersonal);
		btnDelPersonal.setOnClickListener(new DelPersonalListener());

		listView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged();
			}
		});
		listView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
					long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), adapter.getGroup(groupPosition).toString(),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
					int childPosition, long id) {
				// TODO Auto-generated method stub
//				String[] path = adapter.getChild(groupPosition, childPosition).toString()
//						.split("/");
//				String childFileName = path[path.length - 1];
//				Toast.makeText(getActivity(), childFileName, Toast.LENGTH_SHORT).show();
				
				Intent intent = OpenfileFunction.openFile(adapter.getChild(groupPosition, childPosition).toString());
				if (intent != null) {
					getActivity().startActivity(intent);
				}
				
				return false;
			}
		});

		return view;
	}
	//added by wyg
	//从文件夹中读取数据
	public void loadData(){
		FileUtil fileUtil = new FileUtil();
		List<File> temp = fileUtil.queryFile(FILE_ROOT_PATH);
		this.list.clear();
		
		if (temp != null) {
			this.list.add(temp);
			this.fileMap = fileUtil.getFileNameSizeMap(FILE_ROOT_PATH);
		}
		temp = fileUtil.queryFile(RECORD_ROOT_PATH);
		if (temp != null) {
			this.list.add(temp);
			this.recordMap = fileUtil.getFileNameSizeMap(RECORD_ROOT_PATH);
		}
		temp = fileUtil.queryFile(PERSONAL_ROOT_PATH);
		if (temp != null) {
			this.list.add(temp);
			this.personalMap = fileUtil.getFileNameSizeMap(PERSONAL_ROOT_PATH);
		}
	}
	
	class DelFileListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			FileUtil fileUtil = new FileUtil();
			fileUtil.delFile(FILE_ROOT_PATH);
			try {
				Thread.sleep(2000);
				//added by wyg
				loadData();
				
				Toast.makeText(getActivity(), getString(R.string.file_delete_success), Toast.LENGTH_SHORT).show();
				adapter.notifyDataSetChanged();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	class DelRecordListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			FileUtil fileUtil = new FileUtil();
			fileUtil.delFile(RECORD_ROOT_PATH);
			try {
				Thread.sleep(2000);
				
				//added by wyg
				loadData();
				
				Toast.makeText(getActivity(), getString(R.string.record_delete_success), Toast.LENGTH_SHORT).show();
				adapter.notifyDataSetChanged();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	//added by wyg
	class DelPersonalListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			FileUtil fileUtil = new FileUtil();
			fileUtil.delFile(PERSONAL_ROOT_PATH);
			try {
				Thread.sleep(2000);
				//added by wyg
				loadData();
				
				Toast.makeText(getActivity(), getString(R.string.personal_delete_success), Toast.LENGTH_SHORT).show();
				adapter.notifyDataSetChanged();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_menu, menu);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
