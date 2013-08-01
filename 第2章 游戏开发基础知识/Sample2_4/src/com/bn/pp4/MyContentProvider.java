package com.bn.pp4;
import android.content.ContentProvider; //导入相关包
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
public class MyContentProvider extends ContentProvider { // 继承ContentProvider
	private static final UriMatcher um; // 声明Uri匹配引用
	static {
		um = new UriMatcher(UriMatcher.NO_MATCH); // 创建UriMatcher
		um.addURI("com.bn.pp4.provider.student", "stu", 1); // 设置匹配字符串
	}
	SQLiteDatabase sld; // 声明SQLiteDatabase引用
	@Override
	public String getType(Uri uri) {
		return null;
	}
	@Override	// 调用数据库的query方法时会自动调用该方法
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch (um.match(uri)) {// 若匹配成功
		case 1: // 执行操作，获取Cursor对象引用
			Cursor cur = sld.query("student", projection, selection,
					selectionArgs, null, null, sortOrder);
			return cur; // 返回Cursor对象引用
		}
		return null;
	}
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {// 空实现
		return 0;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {// 空实现
		return null;
	}
	@Override
	public boolean onCreate() { // 创建数据库时自动调用该方法
		sld = SQLiteDatabase.openDatabase(
				"/data/data/com.bn.pp4/mydb", // 数据库所在路径
				null, // 游标工厂，默认为null
				SQLiteDatabase.OPEN_READWRITE| 
				SQLiteDatabase.CREATE_IF_NECESSARY // 读写、若不存在则创建
		);
		return false;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {// 空实现
		return 0;
	}
}
