package com.bn.Sample16_4;
import java.util.ArrayList;

//代表粒子系统的类
public class GrainGroup {
	//用于绘制的粒子
	static GrainForDraw gfd;     
	//所有粒子的列表
	ArrayList<SingleGrain> al=new ArrayList<SingleGrain>();
 	static final float SPEED_SPAN=(float) (1.5f+1.5f*Math.random());//粒子初速度
	static final float SPEED=0.02f;//粒子移动每一步的模拟时延，也就是时间戳间隔
	
     public GrainGroup(MySurfaceView mv)
     {
    	//初始化用于绘制的六个不同颜色的粒子
    	gfd=new GrainForDraw(4,1,1,1,mv);
    	//随机添加粒子
 		for(int i=0;i<400;i++)
 		{
 			//随机产生粒子的方位角及仰角
 			double elevation=0.35f*Math.random()*Math.PI+Math.PI*0.15f;//仰角
 			double direction=Math.random()*Math.PI*2;//方位角
 			//计算出粒子在XYZ轴方向的速度分量
 			float vy=(float)(SPEED_SPAN*Math.sin(elevation));	
 			float vx=(float)(SPEED_SPAN*Math.cos(elevation)*Math.cos(direction));	
 			float vz=(float)(SPEED_SPAN*Math.cos(elevation)*Math.sin(direction));	
 			//创建粒子对像并添加进粒子列表
 			al.add(new SingleGrain(vx,vy,vz));
 		}
     }
     
     long timeStamp=0;
     public void drawSelf()
     {
    	 long currTimeStamp=System.nanoTime()/1000000;
    	 if(currTimeStamp-timeStamp>10)
    	 {
    		 for(SingleGrain sp:al)
        	 {//扫描粒子列表，并修改粒子时间戳
        		sp.timeSpan=sp.timeSpan+SPEED;
        		if(sp.timeSpan>10)  
        		{
        			sp.timeSpan=0;
        		}
        	 }
    		 timeStamp=currTimeStamp;
    	 }
    	 
		 int size=al.size();
		 //循环扫描所有粒子的列表并绘制各个粒子
		 for(int i=0;i<size;i++)
    	 {
			 try
	    	 {
    		   al.get(i).drawSelf();
	    	 }
			 catch(Exception e){}
    	 } 
     }
}