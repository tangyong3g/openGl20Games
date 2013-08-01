package com.bn.Sample13_3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import static com.bn.Sample13_3.Constant.*;
/*
 * 灰度图生成地形
 */
public class LandForm 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用  
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
    int uSandTexHandle;//土层纹理属性引用  
    int uGrassTexHandle;//草地纹理属性引用
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
    public LandForm(float[][]landArray,int mProgram)
    {    	
    	this.mProgram=mProgram;
    	//初始化顶点坐标与着色数据
    	initVertexData(landArray);
    	//初始化shader        
    	intShader();
    }
    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float[][] landArray)
    {
    	int cols=landArray[0].length-1;//该灰度图的列数
    	int rows=landArray.length-1;//该灰度图的行数
    	vCount=cols*rows*2*3;//每个格子两个三角形，每个三角形3个顶点   
        float vertices[]=new float[vCount*3];//每个顶点xyz三个坐标
        int count=0;//顶点计数器
        for(int i=0;i<rows;i++)
        {
        	for(int j=0;j<cols;j++)
        	{        		
        		//计算当前格子左上侧点坐标 
        		float zsx=j*LAND_SPAN;
        		float zsz=i*LAND_SPAN;
        		//绘制左上三角形
        	    	//左上点
            		vertices[count++]=zsx;
            		vertices[count++]=landArray[i][j];
            		vertices[count++]=zsz;
            		//左下点
            		vertices[count++]=zsx;
            		vertices[count++]=landArray[i+1][j];
            		vertices[count++]=zsz+LAND_SPAN;
            		//右上点
            		vertices[count++]=zsx+LAND_SPAN;
            		vertices[count++]=landArray[i][j+1];
            		vertices[count++]=zsz;
        			//右上点
            		vertices[count++]=zsx+LAND_SPAN;
            		vertices[count++]=landArray[i][j+1];
            		vertices[count++]=zsz;
            		//左下点
            		vertices[count++]=zsx;
            		vertices[count++]=landArray[i+1][j];
            		vertices[count++]=zsz+LAND_SPAN;
            		//右下点
            		vertices[count++]=zsx+LAND_SPAN;
            		vertices[count++]=landArray[i+1][j+1];
            		vertices[count++]=zsz+LAND_SPAN; 
        	}
        }
        //创建顶点坐标数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        float[] texCoor=generateTexCoor(cols,rows);
        //创建顶点纹理坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }
    //初始化shader
    public void intShader()
    {
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //纹理id
        uSandTexHandle=GLES20.glGetUniformLocation(mProgram, "sTextureSand");  
        uGrassTexHandle=GLES20.glGetUniformLocation(mProgram, "sTextureGrass");  
    }
    public void drawSelf(int tex_grassId,int tex_sandId)
    {        
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mProgram); 
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
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
         //将顶点纹理坐标数据传入渲染管线
         GLES20.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   
         //启用顶点位置、纹理坐标数据
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         //绑定纹理
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex_sandId);
         GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex_grassId);    
         GLES20.glUniform1i(uSandTexHandle, 0);
         GLES20.glUniform1i(uGrassTexHandle, 1);  
         //绘制纹理矩形
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES , 0, vCount); 
    }
    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=8.0f/bw;//列数
    	float sizeh=8.0f/bh;//行数
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
