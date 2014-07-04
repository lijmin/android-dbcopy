package com.cnrisk.microtown.test.db;

import java.io.File;
import java.io.IOException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.test.AndroidTestCase;

import com.cnrisk.microtown.app.LogHandler;
import com.cnrisk.microtown.gtask.db.DBHelper;
import com.cnrisk.microtown.gtask.db.table.T;
import com.cnrisk.microtown.gtask.log.Log;

public class testDB extends AndroidTestCase {
	public testDB() {
		new LogHandler().init();
	}
	
	/**
	 * 复制数据库
	 */
	public void DBCopy() throws IOException {
		String source = Environment.getExternalStorageDirectory() + "/microtown/cnrisk.db";
	//		String target = Environment.getExternalStorageDirectory() + "/microtown/cnriskCopy.db";
		String target = getContext().getDatabasePath("cnrisk").getAbsolutePath();
		
		
		File targetFile = new File(target);
		if(!targetFile.exists()){
			targetFile.getParentFile().mkdirs();
			targetFile.createNewFile();
		}
		FileUtil.copyFile(new File(source), targetFile);
	}

	/**
	 * 对复制的数据库操作
	 */
	public void CopyDBOperate() {
		SQLiteDatabase db = new DBHelper(getContext()).getWritableDatabase();
		Cursor cursor = db.query("T_PRM_personalInfo", null, null, null, null, null, null);
		int count = 0;
		try {
			count = cursor.getCount();
		} finally {
			cursor.close();
		}
		assertTrue(count > 0);
	}
	
	/**
	 * 打开sdcard里的db
	 */
	public void OpenSdcardDB(){
		String source = Environment.getExternalStorageDirectory() + "/microtown/cnrisk.db";
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(new File(source), null);
		Cursor cursor = db.query("T_PRM_personalInfo", null, null, null, null, null, null);
		int count = 0;
		try {
			count = cursor.getCount();
		} finally {
			cursor.close();
		}
		Log.e(""+count);
		assertTrue(count > 0);
	}
	
	/**
	 * 复制数据库表
	 */
	public void CopeTable(){
		long time = System.currentTimeMillis();
		
		String FIRST_DB_PATH = Environment.getExternalStorageDirectory() + "/microtown/cnrisk.db";
		String SECOND_DB_TABLE_NAME = "T_PATH_path";
		String FIRST_DB_TABLE_NAME = "T_PATH_path";
		
		SQLiteDatabase db = new DBHelper(getContext()).getWritableDatabase();
		//note there are single quotation marks around FIRST_DB_PATH but none around tempDb
        db.execSQL("ATTACH DATABASE '" + FIRST_DB_PATH + "' AS tempDb");

        //here you start to copy the tables from firstDB first by checking if the table exists in secondDB (secondDB is now the 'main' one, tempDB is the attached firstDB
        db.execSQL("DROP TABLE IF EXISTS main." + SECOND_DB_TABLE_NAME);

        //here you create a table as a copy of
        db.execSQL("CREATE TABLE main." + SECOND_DB_TABLE_NAME + " AS SELECT * FROM tempDb." + FIRST_DB_TABLE_NAME);

        //you can run the last two lines of code as many times as you need to copy all of the tables

        //after you have copied all of them, you need to detach the tempDB
        db.execSQL("DETACH tempDb"); 
        
        Log.e("时间："+(System.currentTimeMillis() - time));
	}
	
	public void testUseColumnIndex(){
		SQLiteDatabase db = new DBHelper(getContext()).getWritableDatabase();
		Cursor cursor = db.query("T_SHARE_ATT", null, null, null, null, null, null);
		String id = null;
		try {
			cursor.moveToFirst();
			id = cursor.getString(T.Person.ID_INDEX);
		} finally {
			cursor.close();
		}
		assertNotNull(id);
		
	}
	
	
}
