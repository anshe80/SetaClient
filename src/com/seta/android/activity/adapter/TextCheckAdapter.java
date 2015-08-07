/**
 *Filename: TextCheckAdapter.java
 *Copyright: Copyright (c)2015
 *@Author:anshe
 *@Creat at:2015-8-4 下午6:39:27
 *@version 1.0
 */
package com.seta.android.activity.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jeasy.analysis.MMAnalyzer;

import com.seta.android.db.WordsDb;
import com.seta.android.recordchat.R;
import com.sys.android.util.FucUtil;
import com.sys.android.util.GetPinyin;
import com.sys.android.util.SqlUtils;
import com.sys.android.util.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class TextCheckAdapter extends BaseAdapter implements Filterable {
	private ArrayFilter mFilter;
	private List<String> mList;
	private Context context;
	private ArrayList<String> mUnfilteredData;
	
	public TextCheckAdapter(List<String> mList, Context context) {
		this.mList = mList;
		this.context = context;
	}

	@Override
	public int getCount() {
		
		return mList==null ? 0:mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if (convertView == null) {
			view = View.inflate(context, R.layout.textcheckview, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) view
					.findViewById(R.id.textcheckresults);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		String pc = mList.get(position);
		SpannableString ss = new SpannableString(pc);
		for (int i = 0; i < colorIndex.size(); i++) {
			int start=colorIndex.get(i);
			System.out.println("开始上颜色的位置："+colorIndex.get(i));
			if (start<=(ss.length()-1)) {
				ss.setSpan(new ForegroundColorSpan(Color.RED), start, start+1,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			
		}
    	if (errorWordList.size()>50) {//缓存最近使用的50个错别字
    		errorWordList.clear();
    		correctWordList.clear();
		}
		//ss.setSpan(new ForegroundColorSpan(Color.RED), 0, 1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.tv_name.setText(ss);
		holder.tv_name.setMinHeight(150);
		return view;
	}
	
	static class ViewHolder{
		public TextView tv_name;
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter {//对输入的prefix进行处理。

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<String>(mList);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<String> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<String> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();
                ArrayList<String> newValues = new ArrayList<String>();
                //newValues.add("原文："+prefixString);
                String newString=checkWords(prefixString.replaceAll("</s>", ""));
                if (!prefixString.equals(newString)) {
                    newValues.add(newString);
				}
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {//更新UI
			 //noinspection unchecked
            mList = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
		}
		
	}
	boolean isFirst=true;
	List<String> errorWordList=new ArrayList<String>();
	List<String> correctWordList=new ArrayList<String>();
	List<Integer> colorIndex=new ArrayList<Integer>();
	public  String checkWords(String str){
		MMAnalyzer analyzer = new MMAnalyzer();
		String cacheStr=str;
		try {			
			/*if (isFirst) {
				//addData(correctWordList);//增加分词库，便于识别错别字
				isFirst=false;
			}*/
            while (Utils.isStartWithPunctuation(str)) {
            	str=str.substring(1, str.length());
			}
			String[] filterStrings=analyzer.segment(str, ",").split(",");
			for (int i = 0; i < filterStrings.length; i++) {
				if (errorWordList.contains(filterStrings[i])) {
					String cacheString=correctWordList.get(errorWordList.indexOf(filterStrings[i]));			
					System.out.println("缓存中发现错别字："+filterStrings[i]+" 正确的为："+cacheString);
					str=str.replaceAll(filterStrings[i], cacheString);	
				}else{
					//System.out.println("没有找到错别字！"+filterStrings[i]);
					//实例化数据库帮助类
			        final SqlUtils helper=new SqlUtils(context);
			        //查询获得游标
			        Cursor c=helper.query(filterStrings[i]);
			        if (c.moveToFirst()) {
			        	correctWordList.add(c.getString(c.getColumnIndex("correctWords")));
			        	errorWordList.add(c.getString(c.getColumnIndex("errorWords")));
			        	str=str.replaceAll(errorWordList.get(errorWordList.size()-1), correctWordList.get(correctWordList.size()-1));
			        	System.out.println("数据库中发现错别字："+filterStrings[i]+" 正确的为："+correctWordList.get(correctWordList.size()-1));
			        }
			        c.close();
			        helper.closeDatabase();
				}
			}
			System.out.println("修改后的："+str);

		} catch (IOException e) {
			e.printStackTrace();
		}
		if (colorIndex.size()>0) {
			colorIndex.clear();
		}
		for (int i = 0; i < cacheStr.length(); i++) {
			if (cacheStr.charAt(i)!=str.charAt(i)) {//寻找错别字位置，标志
				colorIndex.add(i);
			}
		}
		return str;
	}

	public void addData(List<String> correctWordList){
		//实例化数据库帮助类
        final SqlUtils helper=new SqlUtils(context);
        //查询获得游标
        Cursor c=helper.query();
        while (c.moveToNext()) {
        	MMAnalyzer.addWord(c.getString(c.getColumnIndex("errorWords")));
            System.out.println("正确的单词："+c.getString(c.getColumnIndex("correctWords"))+" 错误的词语："+c.getString(c.getColumnIndex("errorWords")));
		}
		c.close();
		helper.closeDatabase();
		/*
		//从文本添加错别字词库到wordDb.db的sqllite数据库中
		 // delete by anshe 2015.8.7
		for (int i = 0; i < correctWordList.size(); i++) {

			String[] wordStrings=correctWordList.get(i).split(",");
			if (wordStrings.length>1) {

				WordsDb  wordsDb=new WordsDb();
				wordsDb.setCorrectWords(wordStrings[1].split(" ")[0]);
				wordsDb.setErrorWords(wordStrings[0]);
				wordsDb.setErrorWordsAllpy(GetPinyin.getPinYin(wordsDb.getErrorWords()));
				wordsDb.setErrorWordsSimplepy(GetPinyin.getPinYinHeadChar(wordsDb.getErrorWords()));
				wordsDb.setErrorWordsFirstpy(GetPinyin.getPinYinHeadChar(wordsDb.getErrorWords()).charAt(0)+"");
				wordsDb.setCorrectWordsAllpy(GetPinyin.getPinYin(wordsDb.getCorrectWords()));
				wordsDb.setCorrectWordsSimplepy(GetPinyin.getPinYinHeadChar(wordsDb.getCorrectWords()));
				wordsDb.setCorrectWordsFirstpy(GetPinyin.getPinYinHeadChar(wordsDb.getCorrectWords()).charAt(0)+"");
				
				//内容值实例
				ContentValues values=new ContentValues();
				//在value中添加信息
				values.put("correctWords",wordsDb.getCorrectWords());
				values.put("correctWordsAllpy", wordsDb.getCorrectWordsAllpy());
				values.put("correctWordsSimplepy",wordsDb.getCorrectWordsSimplepy());
				values.put("correctWordsFirstpy", wordsDb.getCorrectWordsFirstpy());
				values.put("errorWords",wordsDb.getErrorWords());
				values.put("errorWordsAllpy", wordsDb.getCorrectWordsAllpy());
				values.put("errorWordsSimplepy",wordsDb.getErrorWordsSimplepy());
				values.put("errorWordsFirstpy", wordsDb.getErrorWordsFirstpy());
				//实例化数据帮助类
				SqlUtils helper=new SqlUtils(context);
				//插入数据
				helper.insert(values);
				//System.out.println("插入数据库成功！");
			}else {
				System.out.println("插入失败："+wordStrings);
			}
		}
	*/}
	public List<String> getmList() {
		return mList;
	}

	public void setmList(List<String> mList) {
		this.mList = mList;
	}
}

