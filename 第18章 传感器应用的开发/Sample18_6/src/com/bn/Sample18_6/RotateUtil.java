package com.bn.Sample18_6;
/*
 * 该类为静态工具类，提供静态方法来计算
 * 小球应该的运动方向
 */
public class RotateUtil
{
	//angle为弧度 gVector  为重力向量[x,y,z,1]
	//返回值为旋转后的向量
	public static double[] pitchRotate(double angle,double[] gVector)
	{
		double[][] matrix=//绕x轴旋转变换矩阵
		{
		   {1,0,0,0},
		   {0,Math.cos(angle),Math.sin(angle),0},		   
		   {0,-Math.sin(angle),Math.cos(angle),0},		   //原来为：{0,-Math.sin(angle),Math.cos(angle),0},
		   {0,0,0,1}	
		};
		
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		
		return gVector;
	}
	
	//angle为弧度 gVector  为重力向量[x,y,z,1]
	//返回值为旋转后的向量
	public static double[] rollRotate(double angle,double[] gVector)
	{
		double[][] matrix=//绕y轴旋转变换矩阵
		{
		   {Math.cos(angle),0,-Math.sin(angle),0},
		   {0,1,0,0},
		   {Math.sin(angle),0,Math.cos(angle),0},
		   {0,0,0,1}	
		};
		
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		
		return gVector;
	}		
	
	//angle为弧度 gVector  为重力向量[x,y,z,1]
	//返回值为旋转后的向量
	public static double[] yawRotate(double angle,double[] gVector)
	{
		double[][] matrix=//绕z轴旋转变换矩阵
		{
		   {Math.cos(angle),Math.sin(angle),0,0},		   
		   {-Math.sin(angle),Math.cos(angle),0,0},
		   {0,0,1,0},
		   {0,0,0,1}	
		};
		
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		
		return gVector;
	}
	
	
	public static float[] getDirectionDot(double[] values)
	{
		double yawAngle=-Math.toRadians(values[0]);//获取Yaw轴旋转角度弧度
		double pitchAngle=-Math.toRadians(values[1]);//获取Pitch轴旋转角度弧度
		double rollAngle=-Math.toRadians(values[2]);//获取Roll轴旋转角度弧度
		
		//虚拟一个重力向量
		double[] gVector={0,0,-100,1};
		
		//yaw轴恢复
		gVector=RotateUtil.yawRotate(yawAngle,gVector);
		
		//pitch轴恢复
		gVector=RotateUtil.pitchRotate(pitchAngle,gVector);	
		
		//roll轴恢复
		gVector=RotateUtil.rollRotate(rollAngle,gVector);
		
		double mapX=gVector[0];
		double mapY=gVector[1];		
		double mapZ=gVector[2];	
		
		float[] result={(float) mapX,(float) mapY,(float) mapZ};
		return result;
	}
}