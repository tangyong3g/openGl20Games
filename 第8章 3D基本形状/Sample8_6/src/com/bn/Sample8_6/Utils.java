package com.bn.Sample8_6;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	static boolean first=true;
	
	//将一个向量规格化的方法
	public static float[] normalizeVector(float x, float y, float z){
		float mod=module(x,y,z);
		return new float[]{x/mod, y/mod, z/mod};//返回规格化后的向量
	}
	//求向量的模的方法
	public static float module(float x, float y, float z){
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
	public static double[] nRotate(
			double angle,		//旋转角度		
			double n[],			//旋转轴
			double gVector[]	//旋转向量
			){
		
		angle = Math.toRadians(angle);
		double[][] matrix=//绕任意轴旋转变换矩阵
		{
		   {n[0]*n[0]*(1-Math.cos(angle))+Math.cos(angle),n[0]*n[1]*(1-Math.cos(angle))+n[2]*Math.sin(angle),n[0]*n[2]*(1-Math.cos(angle))-n[1]*Math.sin(angle),0},		   
		   {n[0]*n[1]*(1-Math.cos(angle))-n[2]*Math.sin(angle),n[1]*n[1]*(1-Math.cos(angle))+Math.cos(angle),n[1]*n[2]*(1-Math.cos(angle))+n[0]*Math.sin(angle),0},
		   {n[0]*n[2]*(1-Math.cos(angle))+n[1]*Math.sin(angle),n[1]*n[2]*(1-Math.cos(angle))-n[0]*Math.sin(angle),n[2]*n[2]*(1-Math.cos(angle))+Math.cos(angle),0},
		   {0,0,0,1}	
		};
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		return gVector;		//返回结果
	}
	//给出初始点，初始向量，边长，获取正多边形的顶点坐标
	public static List<Float> getRegularPentagonVertexData(
			double[] initPoint,	//起点
			double[] initVector, //初始向量(方向向量)
			double length, //边长
			double angle,	//旋转角度
			double[][] vectors,		//保存上一条边的方向向量
			int borderCount,
			double[] pivot			//旋转轴
			){
		List<Float> verticesList = new ArrayList<Float>();	//新建一个ArrayList
		
		double[] startPoint=initPoint;//起点
		double[] endPoint;				//终点坐标
		double[] vector = copyVecor(initVector);//复制第一条边的方向向量
		int index=0;
		
		double[] vectorS = copyVecor(vector);	//将向量复制一份
		vectors[index++]=vectorS;		//将第一个向量保存
		
		for(int i=0;i<initPoint.length;i++){	//将第一个点的坐标添加到list中
			verticesList.add((float) initPoint[i]);
		}
		
		while(index<borderCount){	//循环计算其余的点的坐标
			
			endPoint = new double[3];//创建当前线段的终点
			//终点坐标等于起点加上长度与方向向量的点积
			endPoint[0]=startPoint[0]+length*vector[0];//计算终点x
			endPoint[1]=startPoint[1]+length*vector[1];//计算终点y
			endPoint[2]=startPoint[2]+length*vector[2];//计算终点z
			//如果计算出来的终点等于第一个点，则计算完毕，循环退出
			if(		
				compare(endPoint[0],initPoint[0])==0 	//调用compare方法进行比较
				&& compare(endPoint[1],initPoint[1])==0 
				&& compare(endPoint[2],initPoint[2])==0
			){
				break;
			}
			for(int i=0;i<endPoint.length;i++){		//将终点的坐标添加到list中
				float value = (float) endPoint[i];
				if(Math.abs(value)<0.000001){
					verticesList.add(new Float(0.0f));
					continue;
				}else{
					verticesList.add((float) endPoint[i]);
				}
			}
			//计算下一条边的方向向量
			if(index==1){
				vector = nRotate(angle,pivot,vector);//绕父对象的旋转轴生成第二个向量，与父对象在同一平面的
				if(!first){//如果不是第一个多边形
					double tempAngle = 39*angle/Math.abs(angle);//getDihedralAngle()*(angle/Math.abs(angle));
					vector = nRotate(tempAngle,initVector,vector);//将第二个向量绕第一个向量旋转
					pivot = nRotate(tempAngle,initVector,pivot);//生成新的旋转轴
				}
				first=false;
			}else{
				vector = nRotate(angle,pivot,vector);//将当前的方向向量旋转得到新的方向向量
			}
			
			vectorS = copyVecor(vector);//将向量复制一份
			
			vectors[index++]=vectorS;//将新的向量保存

			startPoint = endPoint;//将当前线段的终点作为下条线段的起点
		}

		return verticesList;
	}
	
	public static double[] copyVecor(double[] vector){	//复制数组中数组的方法
		double[] copy = new double[vector.length];
		for(int i=0;i<vector.length;i++){
			copy[i]=vector[i];
		}
		return copy;
	}
	
	//比较两个数的方法
	public static int compare(double x,double y){
		if(Math.abs(x-y)<0.000001){
			return 0;
		}else if(x-y>0.000001){
			return 1;
		}else{
			return -1;
		}
	}
	//计算第一个点的坐标--五边形的左下角点，使第一个五边形中心为坐标原点
	public static double[] getFirstPoint(
			float length	//正五边形的边长
			){
		double first[] = new double[3];		//正五边形坐下点坐标数组
		first[0]=-length/2;		//x坐标值
		first[1]=-length/(2*Math.tan(Math.toRadians(36))); //y坐标值
		first[2]=0;	//由于在xy平面上，z自然为0

		return first;
	}
	//求二面角--套结果公式
	public static double getDihedralAngle(){

		return Math.toDegrees(Math.acos(Math.sqrt(5)/3));
	}
	//计算两个向量的夹角--结果为度
	public static double getAngleBetweenTwoVector(double[] vector1,double[] vector2){
		double angle=0;
		double DJ = vector1[0]*vector2[0]+vector1[1]*vector2[1]+vector1[2]*vector2[2];//计算点积
		double mode = getMode(vector1)*getMode(vector2);//求向量模的积
		double cosa = DJ/mode;
		if(compare(cosa,1)==0){
			return 0;
		}
		angle = Math.toDegrees(Math.acos(cosa));
		return angle;
	}
	
	//求向量的模
	public static double getMode(double[] vector){
		return Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]);
	}
	
	public static double[] getCJ(double[] v1,double[] v2){//计算叉积--v1叉乘v2
		double[] result = new double[3];
		result[0]=v1[1]*v2[2]-v1[2]*v2[1];
		result[1]=v1[2]*v2[0]-v1[0]*v2[2];
		result[2]=v1[0]*v2[1]-v1[1]*v2[0];
		return result;
	}
	
	//变换坐标系--是x轴变换到指定向量的位置
	public static void moveXToSomeVector(double[] vector){
		double x[]={1,0,0};
		double angle = getAngleBetweenTwoVector(x,vector);//vector与x轴的夹角
		//通过x与vector的叉积计算出旋转轴的向量
		double pivot[] = getCJ(x,vector);	//调用求叉乘的方法
		MatrixState.rotate((float)angle, (float)pivot[0], (float)pivot[1],(float)pivot[2]);
	}
	
	static List<Float> drawnVertices = new ArrayList<Float>();//已经绘制的点的坐标
	
	public static boolean isExist(float x,float y,float z){

		for(int i=0;i<drawnVertices.size()/3;i++){
			float tempx = drawnVertices.get(3*i);
			float tempy = drawnVertices.get(3*i+1);
			float tempz = drawnVertices.get(3*i+2);
			double[] tempp=new double[]{tempx,tempy,tempz};
			double[] p = new double[]{x,y,z};
			if(getDistanceSquare(tempp, p)<=0.2*0.2*4){

				return true;
			}
		}

		return false;
	}
	
	public static double getDistanceSquare(double[] p1,double[] p2){
		return getSquare(p1[0]-p2[0])+getSquare(p1[1]-p2[1])+getSquare(p1[2]-p2[2]);
	}
	public static double getSquare(double x){
		return x*x;
	}
	
}
