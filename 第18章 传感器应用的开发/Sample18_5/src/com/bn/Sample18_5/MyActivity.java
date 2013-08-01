package com.bn.Sample18_5;

import com.bn.Sample18_5.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
public class MyActivity extends Activity {
	SensorManager mySensorManager;	//SensorManager对象引用	
	Sensor myIsNear; 	//传感器类型
	TextView isNear;	//TextView对象引用	
	TextView info;	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        isNear = (TextView)findViewById(R.id.distance);	//用于显示距离值
        info= (TextView)findViewById(R.id.info);//用于显示手机中距离传感器的相关信息
        
        //获得SensorManager对象
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);	
        //传感器的类型为距离传感器
        myIsNear=mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        
        //创建一个StringBuffer
        StringBuffer strb=new StringBuffer();
        strb.append("\n名称: ");
        strb.append(myIsNear.getName());
        strb.append("\n耗电量(mA): ");
        strb.append(myIsNear.getPower());
        strb.append("\n类型编号  : ");
        strb.append(myIsNear.getType());
        strb.append("\n制造商: ");  
        strb.append(myIsNear.getVendor());
        strb.append("\n版本: ");
        strb.append(myIsNear.getVersion());
        strb.append("\n最大测量范围: ");
        strb.append(myIsNear.getMaximumRange());
        
        info.setText(strb.toString());	//将信息字符串赋予名为info的TextView
    }
    @Override
	protected void onResume(){ //重写onResume方法
		super.onResume();
		mySensorManager.registerListener(
				mySensorListener, 		//添加监听
				myIsNear, 		//传感器类型
				SensorManager.SENSOR_DELAY_NORMAL	//传感器事件传递的频度
		);
	}	
	@Override
	protected void onPause(){//重写onPause方法	
		super.onPause();
		mySensorManager.unregisterListener(mySensorListener);//取消注册监听器
	}
	private SensorEventListener mySensorListener = 
		new SensorEventListener(){//开发实现了SensorEventListener接口的传感器监听器
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy){}
		@Override
		public void onSensorChanged(SensorEvent event){
			float []values=event.values;
			isNear.setText("距离为："+values[0]);			
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent e)
	{
		switch(keyCode)
	    	{
		case 4:
			System.exit(0);
			break;
	    	}
		return true;
	}
	
}