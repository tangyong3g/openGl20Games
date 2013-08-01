package com.bn.Sample8_1;
//圆柱类
public class Cylinder
{
	Circle bottomCircle;//底圆
	Circle topCircle;//顶圆
	CylinderSide cylinderSide;//侧面
	float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    float h;
    float scale;

    int topTexId; //顶面纹理
    int BottomTexId;  //底面纹理
    int sideTexId;  //侧面纹理
    
	public Cylinder(MySurfaceView mySurfaceView,float scale,float r, float h,int n,
			int topTexId, int BottomTexId, int sideTexId)
	{
		
		this.h=h;
		this.scale=scale;
		this.topTexId=topTexId;
		this.BottomTexId=BottomTexId;
		this.sideTexId=sideTexId;
		
		topCircle=new Circle(mySurfaceView,scale,r,n);	//创建顶面圆对象
		bottomCircle=new Circle(mySurfaceView,scale,r,n);  //创建底面圆对象
		cylinderSide=new CylinderSide(mySurfaceView,scale,r,h,n); //创建侧面无顶圆柱对象
	}
	public void drawSelf()
	{
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);		
		//顶面
		MatrixState.pushMatrix();
		MatrixState.translate(0, h/2*scale, 0);
		MatrixState.rotate(-90, 1, 0, 0);
		topCircle.drawSelf(topTexId);
		MatrixState.popMatrix();
		
		//底面
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2*scale, 0);
		MatrixState.rotate(90, 1, 0, 0);
		MatrixState.rotate(180, 0, 0, 1);
		bottomCircle.drawSelf(BottomTexId);
		MatrixState.popMatrix();
		
		//侧面
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2*scale, 0);
		cylinderSide.drawSelf(sideTexId);
		MatrixState.popMatrix();
	}
}
