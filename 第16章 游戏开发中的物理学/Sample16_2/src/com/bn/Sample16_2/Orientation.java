package com.bn.Sample16_2;
import android.opengl.Matrix;
public class Orientation //×ËÌ¬Àà
{
	float[] orientationData=new float[16];
	
	public Orientation(float angle,float zx,float zy,float zz)
	{
		Matrix.setRotateM(orientationData, 0, angle, zx, zy, zz);
	}
}
