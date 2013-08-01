package com.bn.Sample5_1;
import static com.bn.Sample5_1.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;
import android.opengl.Matrix;

//六角星
public class SixPointedStar 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用  
    int maColorHandle; //顶点颜色属性引用  
    String mVertexShader;	//顶点着色器代码脚本 
    String mFragmentShader;	//片元着色器代码脚本
    static float[] mMMatrix = new float[16];	//具体物体的3D变换矩阵，包括旋转、平移、缩放
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mColorBuffer;//顶点着色数据缓冲
    int vCount=0;    
    float yAngle=0;//绕y轴旋转的角度
    float xAngle=0;//绕z轴旋转的角度
    final float UNIT_SIZE=1;
    
    public SixPointedStar(MySurfaceView mv,float r,float R,float z)
    {    	
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(R,r,z);
    	//调用初始化着色器的intShader方法     
    	initShader(mv);
    }
    
    //自定义初始化顶点数据的initVertexData方法
    public void initVertexData(float R,float r,float z)
    {
		List<Float> flist=new ArrayList<Float>();
		float tempAngle=360/6;
		for(float angle=0;angle<360;angle+=tempAngle)
		{
			//第一个三角形
			//第一个中心点
			flist.add(0f);
			flist.add(0f);
			flist.add(z);
			//第二个点
			flist.add((float) (R*UNIT_SIZE*Math.cos(Math.toRadians(angle))));
			flist.add((float) (R*UNIT_SIZE*Math.sin(Math.toRadians(angle))));
			flist.add(z);
			//第三个点
			flist.add((float) (r*UNIT_SIZE*Math.cos(Math.toRadians(angle+tempAngle/2))));
			flist.add((float) (r*UNIT_SIZE*Math.sin(Math.toRadians(angle+tempAngle/2))));
			flist.add(z);
			
			//第二个三角形
			//第一个中心点
			flist.add(0f);
			flist.add(0f);
			flist.add(z);
			//第二个点
			flist.add((float) (r*UNIT_SIZE*Math.cos(Math.toRadians(angle+tempAngle/2))));
			flist.add((float) (r*UNIT_SIZE*Math.sin(Math.toRadians(angle+tempAngle/2))));
			flist.add(z);
			//第三个点
			flist.add((float) (R*UNIT_SIZE*Math.cos(Math.toRadians(angle+tempAngle))));
			flist.add((float) (R*UNIT_SIZE*Math.sin(Math.toRadians(angle+tempAngle))));
			flist.add(z);
		}
		vCount=flist.size()/3;
		float[] vertexArray=new float[flist.size()];
		for(int i=0;i<vCount;i++)
		{
			vertexArray[i*3]=flist.get(i*3);
			vertexArray[i*3+1]=flist.get(i*3+1);
			vertexArray[i*3+2]=flist.get(i*3+2);
		}
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertexArray.length*4);
		vbb.order(ByteOrder.nativeOrder());	//设置字节顺序为本地操作系统顺序
		mVertexBuffer=vbb.asFloatBuffer();
		mVertexBuffer.put(vertexArray);
		mVertexBuffer.position(0);
		
		
		
        
        //顶点着色数据的初始化================begin============================
		float[] colorArray=new float[vCount*4];
		for(int i=0;i<vCount;i++)
		{
			if(i%3==0){//中心点为白色
				colorArray[i*4]=1;
				colorArray[i*4+1]=1;
				colorArray[i*4+2]=1;
				colorArray[i*4+3]=0;
			}
			else{//边上的点为淡蓝色
				colorArray[i*4]=0.45f;
				colorArray[i*4+1]=0.75f;
				colorArray[i*4+2]=0.75f;
				colorArray[i*4+3]=0;
			}
		}
		ByteBuffer cbb=ByteBuffer.allocateDirect(colorArray.length*4);
		cbb.order(ByteOrder.nativeOrder());	//设置字节顺序为本地操作系统顺序
		mColorBuffer=cbb.asFloatBuffer();
		mColorBuffer.put(colorArray);
		mColorBuffer.position(0);
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================

    }

    //自定义初始化着色器的intShader方法
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id  
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    
    public void drawSelf()
    {        
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mProgram);        
    	 //初始化变换矩阵
         Matrix.setRotateM(mMMatrix,0,0,0,1,0);
         //设置沿Z轴正向位移1
         Matrix.translateM(mMMatrix,0,0,0,1);
         //设置绕y轴旋转
         Matrix.rotateM(mMMatrix,0,yAngle,0,1,0);
         //设置绕z轴旋转
         Matrix.rotateM(mMMatrix,0,xAngle,1,0,0);  
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0); 
         //为画笔指定顶点位置数据
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4, 
                mVertexBuffer
         );       
         //为画笔指定顶点着色数据
         GLES20.glVertexAttribPointer  
         (
        		maColorHandle, 
         		4, 
         		GLES20.GL_FLOAT, 
         		false,
                4*4,   
                mColorBuffer
         );   
         //允许顶点位置数据数组
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maColorHandle);  
         //绘制六角星
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
}
