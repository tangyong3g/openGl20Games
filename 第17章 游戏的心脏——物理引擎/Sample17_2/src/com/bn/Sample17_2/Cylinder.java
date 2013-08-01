package com.bn.Sample17_2;
public class Cylinder
{
	Circle bottomCircle;//底圆
	Circle topCircle;//顶圆
	CylinderSide cylinderSide;//侧面
    float h;
    MySurfaceView mv;
	public Cylinder(float r, float h, int n,int mProgram,MySurfaceView mv)
	{
		topCircle=new Circle(r,n,new float[]{0,1,0},mProgram);
		bottomCircle=new Circle(r,n,new float[]{0,-1,0},mProgram);
		cylinderSide=new CylinderSide(r, h, n,mProgram);
		// 初始化完成后再改变各量的值
		this.h=h;
		this.mv=mv;
	}
	public void drawSelf(int topTexId, int BottomTexId, int sideTexId)
	{	
		bottomCircle.intShader(mv);
		topCircle.intShader(mv);
		cylinderSide.intShader(mv);
		//顶面
		MatrixState.pushMatrix();
		MatrixState.translate(0, h, 0);
		MatrixState.rotate(-90, 1, 0, 0);
		topCircle.drawSelf(topTexId);
		MatrixState.popMatrix();
		//底面
		MatrixState.pushMatrix();
		MatrixState.rotate(90, 1, 0, 0);
		MatrixState.rotate(180, 0, 0, 1);
		bottomCircle.drawSelf(BottomTexId);
		MatrixState.popMatrix();
		//侧面
		MatrixState.pushMatrix();
		cylinderSide.drawSelf(sideTexId);
		MatrixState.popMatrix();
	}
}
