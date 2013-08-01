package com.bn.Sample18_6;

public class Cube 
{
	MySurfaceView mv;
	TextureRect[] rect=new TextureRect[6];
	float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    float a;	//立方体的长
    float b;	//立方体的高
    float c;	//立方体的宽（厚度）
    float size;//尺寸
	public Cube(MySurfaceView mv,float scale,float[] abc)
	{
		a=abc[0];
		b=abc[1];
		c=abc[2];
		rect[0]=new TextureRect(mv,scale,a,b);
		rect[1]=new TextureRect(mv,scale,a,b);
		rect[2]=new TextureRect(mv,scale,c,b);
		rect[3]=new TextureRect(mv,scale,c,b);
		rect[4]=new TextureRect(mv,scale,a,c);
		rect[5]=new TextureRect(mv,scale,a,c);
		// 初始化完成后再改变各量的值
		size=scale;
		a*=size;
		b*=size;
		c*=size;
	}
	public void drawSelf(int ballTexId)
	{
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
        MatrixState.rotate(zAngle, 0, 0, 1);
        //前面
        MatrixState.pushMatrix();
        MatrixState.translate(0, 0, c/2);
		rect[0].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//后面
        MatrixState.pushMatrix();
        MatrixState.translate(0, 0, -c/2);
		MatrixState.rotate(180.0f, 0, 1, 0);
		rect[1].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//右面
        MatrixState.pushMatrix();
        MatrixState.translate(a/2, 0, 0);
		MatrixState.rotate(90.0f, 0, 1, 0);
		rect[2].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//左面
        MatrixState.pushMatrix();
        MatrixState.translate(-a/2, 0, 0);
		MatrixState.rotate(-90.0f, 0, 1, 0);
		rect[3].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//下面
        MatrixState.pushMatrix();
        MatrixState.translate(0, -b/2, 0);
		MatrixState.rotate(90.0f, 1, 0, 0);
		rect[4].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//上面
        MatrixState.pushMatrix();
        MatrixState.translate(0, b/2, 0);
		MatrixState.rotate(-90.0f, 1, 0, 0);
		rect[5].drawSelf(ballTexId);
        MatrixState.popMatrix();
	}
}
