package com.bn.pp3;

import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Sample2_3_Activity extends Activity {
   @Override
    public void onCreate(Bundle savedInstanceState) {	//重写onCreate方法
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);					//设置layout
        //获取SharedPreferences引用，存储名称为bn.xml，读写模式为私有
        SharedPreferences sp=this.getSharedPreferences("bn", Context.MODE_PRIVATE);        
        String lastLoginTime=sp.getString("time", null);//获取键为“time”的值       
        if(lastLoginTime==null)			//若值为空，则为第一次登录本程序
        {  	lastLoginTime="用户您好，欢迎您第一次光临本软件。";    }
        else							//不为空，则修改字符串为上次登录时间
        {  	lastLoginTime="用户您好，您上次进入时间为:"+lastLoginTime;    }        
        SharedPreferences.Editor editor=sp.edit();	//修改Preferences文件
        editor.putString("time", new Date().toLocaleString());	//修改键为“time”的值为当前时间
        editor.commit();	//提交修改        
        TextView tv=(TextView)this.findViewById(R.id.TextView01);	//获取用来显示的TextView
        tv.setText(lastLoginTime);	//设置显示的字符串
}}