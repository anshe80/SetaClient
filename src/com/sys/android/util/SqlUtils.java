package com.sys.android.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.seta.android.recordchat.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class SqlUtils extends SQLiteOpenHelper {
	private final int BUFFER_SIZE = 400000;
	public static final String DB_NAME = "wordcheck.db"; // 保存的数据库文件名
	public static final String PACKAGE_NAME = "com.seta.android.recordchat";
	public static final String DB_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME+"/databases"; // 在手机里存放数据库的位置(/data/data/PACKAGE_NAME/databases/wordcheck.db)
	//表名称
    private static final String TBL_NAME="wordsDb";
    //创建表SQL语句
    private static final String CREATE_TBL=" create table if not exists "
            +" wordsDb(wordsId integer primary key autoincrement,"
            + "errorWords text,errorWordsAllpy text,errorWordsSimplepy text,errorWordsFirstpy text,"
            + "correctWords text,correctWordsAllpy text,correctWordsSimplepy text,correctWordsFirstpy text)";
    
	private SQLiteDatabase database;
	private Context context;

	  
    /*
     * 构造方法
     */
	public SqlUtils(Context context){		
        super(context,DB_NAME,null,2);
        this.context=context;
        openDatabase();
    };
	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}

	public void openDatabase() {
		System.out.println("打开的数据库位置："+DB_PATH + "/" + DB_NAME);
		this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
	}
	
	

	private SQLiteDatabase openDatabase(String dbfile) {
		try {			
			if (!(new File(dbfile).exists())) {
				// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
				InputStream is = context.getResources().openRawResource(
						R.raw.wordcheck); // 欲导入的数据库
				OpenfileFunction.makeFilePath(DB_PATH+"/", DB_NAME);
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
				System.out.println("数据库不存在，导入成功！");
			}else {
				SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,null);
				db.execSQL("DROP TABLE IF EXISTS city");	
				System.out.println("数据库已经存在");				
				return db;
			}
		} catch (FileNotFoundException e) {
			Log.e("Database", "File not found");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("Database", "IO exception");
			e.printStackTrace();
		}
		return null;
	}
	
	/*
     * 插入方法
     */
    public void insert(ContentValues values)
    {
        //获得SQLiteDatabase实例
        SQLiteDatabase db=getWritableDatabase();
        //插入
        db.insert(TBL_NAME, null, values);
        //关闭
        db.close();
    }
    /*
     * 查询所有的数据方法
     */
    public Cursor query()
    {
        //获得SQLiteDatabase实例
        SQLiteDatabase db=getWritableDatabase();
        //查询获得Cursor
        Cursor c=db.query(TBL_NAME, null, null, null, null, null, null);
        return c;
    }
    
    /*
     * 查询有str项的方法
     */
    public Cursor query(String str)
    {
        //获得SQLiteDatabase实例
        SQLiteDatabase db=getWritableDatabase();
        //查询获得Cursor
        Cursor c=db.query(TBL_NAME, new String[]{"errorWords,correctWords"}, "errorWords like ?", new String[]{str}, null, null, null);
        return c;
    }
    
    /*
     * 删除方法
     */
    public void del(int id)
    {
        if(database==null)
        {
            //获得SQLiteDatabase实例
            database=getWritableDatabase();           
        }
        //执行删除
        database.delete(TBL_NAME, "_id=?", new String[]{String.valueOf(id)});     
    }

	public void closeDatabase() {
		if (this.database!=null) {
			this.database.close();
		}
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//this.database=db;
        //db.execSQL(CREATE_TBL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	

}
