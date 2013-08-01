package com.bn.pp4;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
public class Sample2_4_Activity extends Activity {
	SQLiteDatabase sld; // 声明SQLiteDatabase引用
	@Override
	public void onCreate(Bundle savedInstanceState) { // 重新onCreate方法
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); // 设置layout
		Button b = (Button) this.findViewById(R.id.Button01); // 获取打开/创建数据库按钮
		b.setOnClickListener( // 为打开/创建按钮添加监听器
		new OnClickListener() {
			@Override
			public void onClick(View v) {
				createOrOpenDatabase(); // 调用方法打开或创建数据库
			}
		});
		b = (Button) this.findViewById(R.id.Button02);// 获取关闭数据库按钮
		b.setOnClickListener( // 为关闭按钮添加监听器
		new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDatabase(); // 调用方法关闭数据库
			}
		});
		b = (Button) this.findViewById(R.id.Button03); // 获取添加记录按钮
		b.setOnClickListener( // 为添加按钮添加监听器
		new OnClickListener() {
			@Override
			public void onClick(View v) {
				insert(); // 调用方法插入记录
			}
		});
		b = (Button) this.findViewById(R.id.Button04);// 获取删除记录按钮
		b.setOnClickListener( // 为删除按钮添加监听器
		new OnClickListener() {
			@Override
			public void onClick(View v) {
				delete(); // 调用方法删除记录
			}
		});
		b = (Button) this.findViewById(R.id.Button05); // 获取查询记录按钮
		b.setOnClickListener( // 为查询按钮添加监听器
		new OnClickListener() {
			@Override
			public void onClick(View v) {
				query(); // 调用方法查询记录
			}
		});
	}
	public void createOrOpenDatabase() {// 创建或打开数据库的方法
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/com.bn.pp4/mydb", // 数据库所在路径
					null, // 游标工厂，默认为null
					SQLiteDatabase.OPEN_READWRITE |
					SQLiteDatabase.CREATE_IF_NECESSARY // 模式为读写，若不存在则创建
			);
			// 生成创建数据库的sql语句
			String sql = "create table if not exists student" +
					"(sno char(5),stuname varchar(20)," +
					"sage integer,sclass char(5))";
			sld.execSQL(sql); // 执行sql语句
			Toast.makeText(getBaseContext(), "成功创建数据库。", 
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void closeDatabase() {// 关闭数据库的方法
		try {
			sld.close(); // 关闭数据库
			Toast.makeText(getBaseContext(), "成功关闭数据库。", 
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void insert() {// 插入记录的方法
		try  {// 生成插入记录的sql语句
			String sql = "insert into student values" +
					"('001','Android',22,'283')";
			sld.execSQL(sql); // 执行sql语句
			Toast.makeText(getBaseContext(), "成功插入一条记录。",
					 Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void delete() {// 删除记录的方法
		try  {// 生成删除所有记录的sql语句
			String sql = "delete from student;";
			sld.execSQL(sql); // 执行sql语句
			Toast.makeText(getBaseContext(), "成功删除所有记录。", 
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void query(){// 查询的方法
		try {// 生成查询记录的sql语句
			String sql = "select * from student where sage>?";
			Cursor cur = sld.rawQuery(sql, new String[] { "20" }); // 获取Cursor对象引用
			while (cur.moveToNext()) {// 若存在记录
				String sno = cur.getString(0); // 获取第一列信息
				String sname = cur.getString(1); // 获取第二列信息
				int sage = cur.getInt(2); // 获取第三列信息
				String sclass = cur.getString(3); // 获取第四列信息
				Toast.makeText(
						getBaseContext(),
						"查询到的记录为：'" + sno + "'\t'" + sname 
						+ "'\t\t'" + sage+ "'\t'" + sclass + "'", 
						Toast.LENGTH_LONG).show();
			}
			cur.close(); // 关闭Cursor
		} catch (Exception e) {
			e.printStackTrace();
}}}