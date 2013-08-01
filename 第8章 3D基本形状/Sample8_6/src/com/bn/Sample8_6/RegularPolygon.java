package com.bn.Sample8_6;

import java.util.ArrayList;
import java.util.List;

public class RegularPolygon {
    
    int vCount=0;    
    int iCount=0;

    float length;
    int borderCount;
    List<Float> verticesList;//多边形的顶点数据
    double[][] vectors;//每条便的方向向量，每条边的起点的索引与该边方向向量的索引一致
    double[] initVector;//保存初始的方向向量
    double[] pivot;//旋转轴
    
    int[] vertices;//记录要绘制的球的索引
    int[] borders;//记录要绘制的圆管的索引
    
    List<RegularPolygon> children;
    MySurfaceView mv;

    public RegularPolygon(MySurfaceView mv,
    		int borderCount,	//圆管的编号
    		double angle,		//旋转的角度
    		float length,		//长度
    		double[] initPoint,	//初始的点
    		double[] initVector,//初始向量
    		double[] pivot,		//旋转轴
    		int[] vertices,		//绘制球的索引
    		int[] borders		//绘制圆管的索引
    		){    	
    	this.mv=mv;
    	this.borderCount=borderCount;
    	this.length=length;
    	this.vectors = new double[borderCount][3];
    	this.initVector=initVector;
    	this.vertices=vertices;
    	this.borders=borders;
    	this.pivot = pivot;//父对象的旋转轴，本对象的旋转轴在父对象的旋转轴的基础上旋转得到 
    	children = new ArrayList<RegularPolygon>();
    	this.verticesList = Utils.getRegularPentagonVertexData(
    			initPoint, initVector, length,angle,vectors,borderCount,pivot);

    } 
    
    public void drawSelf(float xOffset,float yOffset) 
    {  
    	//绘制顶点
    	for(int i=0;i<vertices.length;i++){
    		int index = vertices[i];
    		float x = verticesList.get(3*index);
    		float y = verticesList.get(3*index+1);
    		float z = verticesList.get(3*index+2);
    		MatrixState.pushMatrix();
    		//移动到顶点的位置，绘制球
    		MatrixState.translate(x, y, z);
    		mv.ball.drawSelf();
    		MatrixState.popMatrix();
    	}
    	//绘制圆管
    	for(int i=0;i<borders.length;i++){
    		int index = borders[i];
    		//获取圆管的起点坐标
    		float x = verticesList.get(3*index);
    		float y = verticesList.get(3*index+1);
    		float z = verticesList.get(3*index+2);
    		//获取圆管的向量
    		double[] vector = vectors[index];
    		MatrixState.pushMatrix();
    		//首先移动到起点
    		MatrixState.translate(x, y, z);
    		MatrixState.pushMatrix();
    		Utils.moveXToSomeVector(vector);	//x轴变换到指定向量的坐标系
    		MatrixState.translate(Constant.LENGTH/2, 0, 0);
    		mv.stick.drawSelf();			//绘制木棒
    		MatrixState.popMatrix();
    		MatrixState.popMatrix();
    	}
         drawChildren( xOffset, yOffset);		//绘制
         

    }
	
    public RegularPolygon buildChild(
    		int borderCount,	//圆管的数量	
    		double angle,		//旋转角度
    		int position,		
    		int[] vertices,		//球索引
    		int[] borders		//圆管索引
    	){
    	double[] initPoint = new double[3];
    	for(int i=0;i<3;i++){
    		initPoint[i]=verticesList.get(3*position+i);
    	}
    	double[] initVector = vectors[position];
    	double[] tempPivot = Utils.copyVecor(this.pivot);
    	RegularPolygon child = new RegularPolygon(this.mv, 
    			borderCount, angle, length, initPoint, initVector,tempPivot,vertices,borders);
    	children.add(child);
    	return child;
    }
    
    private void drawChildren(float xOffset,float yOffset){
    	for(int i=0;i<children.size();i++){
    		RegularPolygon child = children.get(i);
    		MatrixState.pushMatrix();
    		child.drawSelf(xOffset, yOffset);
    		MatrixState.popMatrix();
    	}
    }
}
