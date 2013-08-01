package com.bn.Sample17_6;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class Sample17_6_Activity extends Activity {
	
	MySurfaceView surfaceView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉标头
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制横屏
        
        surfaceView = new MySurfaceView(this);
        setContentView(surfaceView);
    }
    
    @Override
	protected void onPause() {
		super.onPause();
		surfaceView.onPause();
		surfaceView.flag=false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		surfaceView.onResume();
		surfaceView.flag=true;
	}
	
	  @Override
		public boolean onKeyDown(int keyCode, KeyEvent e)
	    {
	    	if(keyCode==4)
	    	{
	    		System.exit(0);
	    	}
			return false;
	    }
}