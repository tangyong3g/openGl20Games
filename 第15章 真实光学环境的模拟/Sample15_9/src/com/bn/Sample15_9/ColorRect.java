package com.bn.Sample15_9;
import static com.bn.Sample15_9.Constant.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;

//矩形
public class ColorRect 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用 
    //3D世界中的量
    int muColorHandle; //片元颜色属性引用 
    int mu3DPosHandle; //3D世界中顶点位置属性引用 
    int muNormalHandle; //顶点法向量属性引用
    int muLightLocationHandle;//光源位置属性引用
    int muCameraHandle; //摄像机位置属性引用
    int muIsShadow;//是否绘制阴影属性引用  
    
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    int vCount=0;   
    float[] color3 = new float[3];//3D世界中顶点颜色
    float[] vertexPos3D = new float[3];//3D世界中顶点位置
    float[] normal3D = new float[3];//3D世界中顶点法向量
    float[] lightPos3D = new float[3];//3D世界中光源位置
    float[] cameraPos3D = new float[3];//3D世界中摄像机位置
    int isShadow;//是否在阴影中的标志
    
    float u;//基本块在视口上的位置
    float v;
    public ColorRect(MySurfaceView mv)
    {    	
    	//初始化顶点坐标与着色数据
    	initVertexData();
    	//初始化shader        
    	intShader(mv);
    }
    
    //初始化顶点坐标与着色数据的方法
    public void initVertexData()
    {
    	//顶点坐标数据的初始化================begin============================
        vCount=6;
       
        float vertices[]=new float[]
        {	
        	0,0,0,//0
        	Constant.blockSize,0,0,//1
        	Constant.blockSize,Constant.blockSize,0,//2
        	  
        	0,0,0,//0
        	Constant.blockSize,Constant.blockSize,0,//2
        	0,Constant.blockSize,0//3
        };
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
    }

    //初始化shader
    public void intShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用 
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");       
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        
        //3D世界中的量
        //获取程序中3D世界中顶点颜色属性引用 
        muColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        //获取程序中3D世界中顶点位置属性引用 
        mu3DPosHandle = GLES20.glGetUniformLocation(mProgram, "uPosition");
        //获取程序中顶点法向量属性引用  
        muNormalHandle = GLES20.glGetUniformLocation(mProgram, "uNormal");
        //获取程序中光源位置引用
        muLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        muCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中是否绘制阴影属性引用
        muIsShadow=GLES20.glGetUniformLocation(mProgram, "isShadow"); 
    }
	public void drawSelf() {
		MatrixState.pushMatrix();
		MatrixState.translate(u, v, 0);
		// 制定使用某套着色器程序
		GLES20.glUseProgram(mProgram);
		// 将最终变换矩阵传入着色器程序
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);
		
		//3D世界中的量
		// 将3D世界中顶点颜色传入着色器程序
		GLES20.glUniform3fv(muColorHandle, 1, color3, 0);	
		// 将3D世界中顶点的位置传入着色器程序
		GLES20.glUniform3fv(mu3DPosHandle, 1, vertexPos3D, 0);
		// 将3D世界中顶点的法向量传入着色器程序
		GLES20.glUniform3fv(muNormalHandle, 1, normal3D, 0);
		// 将3D世界中灯光位置传入着色器程序
		GLES20.glUniform3fv(muLightLocationHandle, 1, lightPos3D, 0);
		// 将3D世界中摄像机的位置传入着色器程序
		GLES20.glUniform3fv(muCameraHandle, 1, cameraPos3D, 0);	 
        //将是否绘制阴影属性传入着色器程序 
        GLES20.glUniform1i(muIsShadow, isShadow);      
		
		// 将顶点位置数据传入渲染管线
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);
		// 允许顶点位置数据数组
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		// 绘制矩形
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
		
		MatrixState.popMatrix();
	}

    public void setColor(float r,float g,float b){
    	this.color3[0] = r;
    	this.color3[1] = g;
    	this.color3[2] = b;
    }
    public void setPos3D(float x,float y,float z){
    	this.vertexPos3D[0] = x;
    	this.vertexPos3D[1] = y;
    	this.vertexPos3D[2] = z;
    }
    public void setNormal3D(float x,float y,float z){
    	this.normal3D[0] = x;
    	this.normal3D[1] = y;
    	this.normal3D[2] = z;
    }
    public void setLightPos3D(float x,float y,float z){
    	this.lightPos3D[0] = x;
    	this.lightPos3D[1] = y;
    	this.lightPos3D[2] = z;
    }
    public void setCameraPos3D(float x,float y,float z){
    	this.cameraPos3D[0] = x;
    	this.cameraPos3D[1] = y;
    	this.cameraPos3D[2] = z;
    }
    public void setShadow(int isShadow){
    	this.isShadow = isShadow;
    }
    
    public void setPos(float u,float v){
    	this.u=u;
    	this.v=v;
    }
    public void setColRow(int col,int row){
    	//根据行列数计算基本块在屏幕上的位置
    	float u=-W+W*(2*col/nCols);
        float v=-H+H*(2*row/nRows);
        this.setPos(u, v);//设置基本块位置
    }
}
