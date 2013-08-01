package com.bn.Sample16_4;
//存储粒子系统中的某个粒子物理信息的类
public class SingleGrain {
	float vx;//x轴速度分量
	float vy;//y轴速度分量
	float vz;//z轴速度分量
	float timeSpan=0;//累计时间
	
	public SingleGrain(float vx,float vy,float vz)
	{
		this.vx=vx;
		this.vy=vy;
		this.vz=vz;		
	}
	
	public void drawSelf()
	{
		MatrixState.pushMatrix();	
		//根据当前时间戳计算出粒子位置
		float x=vx*timeSpan;
		float z=vz*timeSpan;
		float y=vy*timeSpan-0.5f*timeSpan*timeSpan*1.5f+3.0f;		
		MatrixState.translate(x, y, z);
		//绘制粒子
		GrainGroup.gfd.drawSelf();
		MatrixState.popMatrix();
	}
}