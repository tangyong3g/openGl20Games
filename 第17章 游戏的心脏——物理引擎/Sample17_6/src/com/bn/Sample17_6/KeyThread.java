package com.bn.Sample17_6;
import static com.bn.Sample17_6.Constant.*;
public class KeyThread extends Thread //监听键盘状态的线程
{
	MySurfaceView mv; 
	public KeyThread(MySurfaceView mv)
	{
		this.mv=mv;
	}
	public void run() 
	{
		while(keyFlag)
		{
			if((MySurfaceView.keyState&0x1)!=0) 
			{//有UP键按下
				mv.slideFB(1);
			}
			else if((MySurfaceView.keyState&0x2)!=0)
			{//有down键按下
				mv.slideFB(-1);
			}
			if((MySurfaceView.keyState&0x4)!=0)
			{//有left键按下
				mv.slideLR(-1);
			}
			else if((MySurfaceView.keyState&0x8)!=0)
			{//有right键按下
				mv.slideLR(1);
			}		
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
