package com.seta.android.file.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 该类主要用于对文件的操作，根据文件路径查询文件列表
 * 
 * @author WYG
 * 
 */
public class FileManageUtil {
	private String filePath = null;
	private int fileNum = 0;

	public FileManageUtil() {

	}

	// 只获取文件夹下文件的列表
	public List<File> queryFile(String filePath) {
		File fileDir = new File(filePath);
		List<File> list = new ArrayList<File>();

		if (fileDir.isDirectory()) {
			File files[] = fileDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File currFile = files[i];
				if (currFile.isFile()) {
					System.out.println(currFile.getName());

					list.add(currFile);
					fileNum++;
				}
			}
		}

		return list;
	}

	// 获取文件的文件名和对应的文件大小<fileName,size>
	public Map<String, String> getFileNameSizeMap(String filePath) {
		Map<String, String> map = new HashMap<String, String>();
		List<File> list = queryFile(filePath);
		Iterator<File> it = list.iterator();
		while (it.hasNext()) {
			File file = it.next();
			double size = (double) (file.length() / 1024.0);
			DecimalFormat df = new DecimalFormat("0.00");
			String strSize = df.format(size);

			map.put(file.getName(), new Date(file.lastModified()).toLocaleString() + "      大小:"
					+ strSize + "kB"); // 以字节形式显示文件大小
		}
		return map;
	}

	public boolean delFile(String filePath) {
		boolean state = true;
		File fileDir = new File(filePath);
		if (fileDir.isDirectory()) {
			File[] files = fileDir.listFiles();
			if (files.length > 0) {
				for (File f : files) {
					state = f.delete();
					
					if (state == false)
						return state;
				}
			}
		}
		return state;
	}

	// 返回文件数量
	public int getFileNum() {
		return this.fileNum;
	}

}
