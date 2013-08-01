package com.bn.pp7;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sample2_7_Activity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {		//重写onCreate方法
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);						//设置layout        
        Button ok=(Button)this.findViewById(R.id.Button01);	//获取打开Button
        ok.setOnClickListener								//为打开按钮添加监听器
        (   new OnClickListener()
        {	public void onClick(View v) 
			{
				EditText et1=(EditText)findViewById(R.id.EditText01);
				//调用loadText方法获取对应文件名的文件
				String nr=loadText(et1.getText().toString().trim());
				EditText et2=(EditText)findViewById(R.id.EditText02);
				//设置显示框内容
				et2.setText(nr);
			}});}    
    public String loadText(String name)						//加载assets文件方法
    { 	String nr=null;    									//内容字符串	
    	try 
    	{	//打开对应名称文件的输入流
    		InputStream is=this.getResources().getAssets().open(name);
    		int ch=0;										
    		//创建字节数组输出流
    		ByteArrayOutputStream baos=new ByteArrayOutputStream();
    		while((ch=is.read())!=-1)
    		{	baos.write(ch);		}						//读取文件
    		byte[] buff=baos.toByteArray();					//转化为字节数组
    		baos.close();									//关闭输入输出流
    		is.close();										//关闭输入输出流
			nr=new String(buff,"utf-8");					//转码生产新字符串
			nr=nr.replaceAll("\\r\\n","\n");				//替换换行符等空白字符
		} catch (Exception e) 
    	{	//没有找到对应文件，进行提示
			Toast.makeText(getBaseContext(), "对不起，没有找到指定文件。", Toast.LENGTH_LONG).show();	}    	
		return nr;    										//返回内容字符串	
}}