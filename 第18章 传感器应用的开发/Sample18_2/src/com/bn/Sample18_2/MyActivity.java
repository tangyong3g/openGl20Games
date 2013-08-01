package com.bn.Sample18_2;

import com.bn.Sample18_2.R;

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
	Sensor myMagnetic; 	//传感器类型
	TextView tvX;	//TextView对象引用	
	TextView tvY;	//TextView对象引用	
	TextView tvZ;	//TextView对象引用
	TextView info;	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tvX = (TextView)findViewById(R.id.tvX);	//用于显示x轴方向磁场
        tvY = (TextView)findViewById(R.id.tvY);	//用于显示y轴方向磁场	
        tvZ = (TextView)findViewById(R.id.tvZ); //用于显示z轴方向磁场
        info= (TextView)findViewById(R.id.info);//用于显示手机中磁场传感器的相关信息
        //获得SensorManager对象
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);	
        //传感器的类型为磁场传感器
        myMagnetic=mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        //创建一个StringBuffer        
        StringBuffer strb=new StringBuffer();
        strb.append("\n名称: ");
        strb.append(myMagnetic.getName());
        strb.append("\n耗电量(mA): ");
        strb.append(myMagnetic.getPower());
        strb.append("\n类型编号 : ");
        strb.append(myMagnetic.getType());
        strb.append("\n制造商: ");
        strb.append(myMagnetic.getVendor());
        strb.append("\n版本: ");
        strb.append(myMagnetic.getVersion());
        strb.append("\n最大测量范围: ");
        strb.append(myMagnetic.getMaximumRange());
        
        info.setText(strb.toString());	//将信息字符串赋予名为info的TextView
    }
	private SensorEventListener mySensorListener = 
		new SensorEventListener(){//开发实现了SensorEventListener接口的传感器监听器
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy){}
		@Override
		public void onSensorChanged(SensorEvent event){
			float []values=event.values;//获取三个轴方向上的磁场值
			tvX.setText("x轴方向上的磁场强度为： "+values[0]);		
			tvY.setText("y轴方向上的磁场强度为： "+values[1]);		
			tvZ.setText("z轴方向上的磁场强度为： "+values[2]);		
		}
	};
    @Override
	protected void onResume(){ //重写onResume方法
		super.onResume();
		mySensorManager.registerListener(
				mySensorListener, 		//添加监听
				myMagnetic, 		//传感器类型
				SensorManager.SENSOR_DELAY_NORMAL	//传感器事件传递的频度
		);
	}	
	@Override
	protected void onPause(){//重写onPause方法	
		super.onPause();
		mySensorManager.unregisterListener(mySensorListener);//取消注册监听器
	}
	
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