package com.bn.pp5;

import java.io.File;										//引入相关包
import java.io.FileInputStream;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sample2_5_Activity extends Activity {			//创建Activity   
    @Override
    public void onCreate(Bundle savedInstanceState) {		//重写onCreate方法
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);						//设置layout        
        Button ok=(Button)this.findViewById(R.id.Button01);	//获取打开Button
        ok.setOnClickListener								//为打开按钮添加监听器
        (	new OnClickListener()
        	{	public void onClick(View v) 
				{	EditText et1=(EditText)findViewById(R.id.EditText01);
					//调用loadText方法获取对应文件名的文件
					String nr=loadText(et1.getText().toString().trim());
					EditText et2=(EditText)findViewById(R.id.EditText02);
					//设置显示框内容
					et2.setText(nr);
		}});}    
    public String loadText(String name)						//加载SD卡文件方法
    {  	String nr=null;										//内容字符串
    	try 
    	{	File f=new File("/sdcard/"+name);				//创建对应文件
        	byte[] buff=new byte[(int) f.length()];			//创建响应大小的byte数组
			FileInputStream fis=new FileInputStream(f);
			fis.read(buff);									//读入文件
			fis.close();									//关闭输入流
			nr=new String(buff,"utf-8");					//转码生成字符串
			nr=nr.replaceAll("\\r\\n","\n");} 				//替换换行符
    	catch (Exception e) 
    	{	//没有找到文件提示
    		Toast.makeText(getBaseContext(), "对不起，没有找到指定文件。", Toast.LENGTH_LONG).show();	}
		return nr; 											//返回内容字符串   	
    }}