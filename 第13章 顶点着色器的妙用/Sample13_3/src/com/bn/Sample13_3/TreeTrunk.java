package com.bn.Sample13_3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import android.opengl.GLES20;
/*
 * 用于绘制树干
 */
public class TreeTrunk
{
	//----------这里对树干的节点进行扭曲---------------------
	int fBendRHandle;//创建弯曲半径的句柄Id
	int fwind_direction_Handle;//风向角度的句柄
	//--------------------------------------------
	//自定义渲染管线着色器程序id
	int mProgram;
	//总变化矩阵引用的id
	int muMVPMatrixHandle;
	//顶点位置属性引用
	int maPositionHandle;
	//顶点纹理坐标属性引用
	int maTexCoorHandle;
	
	//顶点数据缓冲和纹理坐标数据缓冲顶点法向量数据缓冲
	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoorBuffer;
	//顶点数量
	int vCount=0;
	//经度切分的角度
	float longitude_span=12;
	//构造器 树的第一个节点底端半径，第一个节点的顶端半径，每个节点的高度，节点的数量
	public TreeTrunk(int mProgram,float bottom_Radius,float joint_Height,int jointNum,int availableNum)
	{
		this.mProgram=mProgram;
		initVertexData(bottom_Radius,joint_Height,jointNum,availableNum);
		initShader();
	}
	//初始化顶点数据的方法
	public void initVertexData(float bottom_radius,float joint_Height,int jointNum,int availableNum)//R代表底端半径，r代表顶端半径
	{
		List<Float> vertex_List=new ArrayList<Float>();//顶点坐标的集合
		List<float[]> texture_List=new ArrayList<float[]>();//纹理坐标的集合
		for(int num=0;num<availableNum;num++)//生成每个节点的顶点数据
		{
			//每个节点底端半径
			float temp_bottom_radius=bottom_radius*(jointNum-num)/(float)jointNum;
			//每个节点顶端半径
			float temp_top_radius=bottom_radius*(jointNum-(num+1))/(float)jointNum;
			//每个节点的底端高度
			float temp_bottom_height=num*joint_Height;
			//每个节点的顶端高度
			float temp_top_height=(num+1)*joint_Height;
			
			for(float hAngle=0;hAngle<360;hAngle=hAngle+longitude_span)//每个节点生成三角形数据
			{
				//左上点
				float x0=(float) (temp_top_radius*Math.cos(Math.toRadians(hAngle)));
				float y0=temp_top_height;
				float z0=(float) (temp_top_radius*Math.sin(Math.toRadians(hAngle)));
				//左下点
				float x1=(float) (temp_bottom_radius*Math.cos(Math.toRadians(hAngle)));
				float y1=temp_bottom_height;
				float z1=(float) (temp_bottom_radius*Math.sin(Math.toRadians(hAngle)));
				//右上点
				float x2=(float) (temp_top_radius*Math.cos(Math.toRadians(hAngle+longitude_span)));
				float y2=temp_top_height;
				float z2=(float) (temp_top_radius*Math.sin(Math.toRadians(hAngle+longitude_span)));
				//右下点
				float x3=(float) (temp_bottom_radius*Math.cos(Math.toRadians(hAngle+longitude_span)));
				float y3=temp_bottom_height;
				float z3=(float) (temp_bottom_radius*Math.sin(Math.toRadians(hAngle+longitude_span)));
				
				vertex_List.add(x0);vertex_List.add(y0);vertex_List.add(z0);
				vertex_List.add(x1);vertex_List.add(y1);vertex_List.add(z1);
				vertex_List.add(x2);vertex_List.add(y2);vertex_List.add(z2);
				
				vertex_List.add(x2);vertex_List.add(y2);vertex_List.add(z2);
				vertex_List.add(x1);vertex_List.add(y1);vertex_List.add(z1);
				vertex_List.add(x3);vertex_List.add(y3);vertex_List.add(z3);
			}
			//创建纹理数据
			//创建纹理坐标缓冲
			float[] texcoor=generateTexCoor//获取切分整图的纹理数组                
	        (
	  			 (int)(360/longitude_span), //纹理图切分的列数
	  			  1                    //(int)(180/ANGLE_SPAN)  //纹理图切分的行数
	        );
			texture_List.add(texcoor);
		}
		//创建顶点缓冲
		float[] vertex=new float[vertex_List.size()];
		for(int i=0;i<vertex_List.size();i++)
		{
			vertex[i]=vertex_List.get(i);
		}
		vCount=vertex_List.size()/3;
		
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer=vbb.asFloatBuffer();
		mVertexBuffer.put(vertex);
		mVertexBuffer.position(0);
		//创建纹理坐标缓冲
		ArrayList<Float> al_temp=new ArrayList<Float>(); 
		for(float []temp:texture_List)
		{
			for(float tem:temp)
			{
				al_temp.add(tem);
			}
		}
		float[]texcoor=new float[al_temp.size()];	
		int num=0;
		for(float temp:al_temp)
		{
			texcoor[num]=temp;
			num++;
		}
		ByteBuffer tbb=ByteBuffer.allocateDirect(texcoor.length*4);
		tbb.order(ByteOrder.nativeOrder());
		mTexCoorBuffer=tbb.asFloatBuffer();
		mTexCoorBuffer.put(texcoor);
		mTexCoorBuffer.position(0);
	}
	//初始化Shader的方法
	public void initShader() 
	{
		//------------------------------摆动处理方法-----------------------
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //获取程序中树干弯曲半径的引用
        fBendRHandle = GLES20.glGetUniformLocation(mProgram,"bend_R");
        //获取程序中方向的角度引用
        fwind_direction_Handle = GLES20.glGetUniformLocation(mProgram,"direction_degree");
	}
	//自定义的绘制方法drawSelf
	public void drawSelf(int texId,float bend_R,float wind_direction)
	{
		//制定使用某套shader程序
   	 	GLES20.glUseProgram(mProgram); 
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //将弯曲半径传入shader程序
        GLES20.glUniform1f(fBendRHandle, bend_R);
        //将风向传入shader程序
        GLES20.glUniform1f(fwind_direction_Handle, wind_direction);
		
        //将顶点位置数据传入渲染管线
		GLES20.glVertexAttribPointer
		(
			maPositionHandle, 
			3, 
			GLES20.GL_FLOAT, 
			false, 
			3*4, 
			mVertexBuffer
		);
		//将纹理坐标数据传入渲染管线
		GLES20.glVertexAttribPointer
		(
			maTexCoorHandle, 
			2, 
			GLES20.GL_FLOAT, 
			false, 
			2*4, 
			mTexCoorBuffer
		);
		//启用顶点位置数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
	}
	//自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=1.0f/bw;//列数
    	float sizeh=1.0f/bh;//行数
    	int c=0;
    	for(int i=0;i<bh;i++)
    	{
    		for(int j=0;j<bw;j++)
    		{
    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
    			float s=j*sizew;
    			float t=i*sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t;    			
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;    			
    		}
    	}
    	return result;
    }
}
