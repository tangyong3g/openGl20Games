package com.bn.pp4f;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
public class ContentConsumerActivity extends Activity {
    ContentResolver cr;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cr=this.getContentResolver();
        //初始化查询按钮
        Button b=(Button)this.findViewById(R.id.Button01);
        b.setOnClickListener(
          new OnClickListener(){
			@Override
			public void onClick(View v) {
				String stuname="Android";				
				Cursor cur=cr.query(
				   Uri.parse("content://com.bn.pp4.provider.student/stu"), 
				   new String[]{"sno","stuname","sage","sclass"}, 
				   "stuname=?", 
				   new String[]{stuname}, 
				   "sage ASC"
				);	
	        	while(cur.moveToNext()){
	        		String sno=cur.getString(0);
	        		String sname=cur.getString(1);
	        		int sage=cur.getInt(2);
	        		String sclass=cur.getString(3);
	        		appendMessage(sno+"\t"+sname+"\t\t"+sage+"\t"+sclass);
	        	}
	        	cur.close();
	}});}
    public void appendMessage(String msg){    //向文本区中添加文本
    	EditText et=(EditText)this.findViewById(R.id.EditText02);
    	et.append(msg+"\n");
}}