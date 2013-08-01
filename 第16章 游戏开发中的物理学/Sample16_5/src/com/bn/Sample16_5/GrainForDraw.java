package com.bn.Sample16_5;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;

//用户绘制粒子系统的类
public class GrainForDraw {
	
	private FloatBuffer   mVelocityBuffer;//顶点速度数据缓冲
    float scale;	//点尺寸
    
    String mVertexShader;	//顶点着色器    	 
    String mFragmentShader;	//片元着色器
    
    int mProgram;			//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;	//总变换矩阵引用   
    int uPointSizeHandle;	//顶点尺寸参数引用
    int uColorHandle;		//顶点颜色参数引用
    int uTimeHandle;		//顶点颜色参数引用
    int vCount=0;

    int maVelocityHandle; 	//顶点速度属性引用 
    float timeLive=0;
    long timeStamp=0;
    
    public GrainForDraw(MySurfaceView mv,float scale,int vCount)
    {
    	this.scale=scale;
    	this.vCount=vCount;
    	initVertexData(vCount);	//调用初始化顶点数据的initVertexData方法
    	initShader(mv);		//调用初始化着色器的initShader方法
    }
    
    //初始化顶点数据的initVertexData方法
    public void initVertexData(int vCount){
        float[] velocity=new float[vCount*3];
        for(int i=0;i<vCount;i++){
        	double fwj=2*Math.PI*Math.random();
        	double yj=0.35*Math.PI*Math.random()+0.15*Math.PI;
        	final double vTotal=1.5+1.5*Math.random();		//总的速度
        	double vy=vTotal*Math.sin(yj);		//y方向上的速度
        	double vx=vTotal*Math.cos(yj)*Math.sin(fwj);	//x方向上的速度
        	double vz=vTotal*Math.cos(yj)*Math.cos(fwj);	//z方向上的速度
        	velocity[i*3]=(float)vx;
        	velocity[i*3+1]=(float)vy;
        	velocity[i*3+2]=(float)vz;
        }
    	
        //创建顶点速度数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(velocity.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVelocityBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVelocityBuffer.put(velocity);//向缓冲区中放入顶点坐标数据
        mVelocityBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化着色器的initShader方法
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容       
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_yh.sh", mv.getResources());
        ShaderUtil.checkGlError("==ss==");   
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_yh.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        ShaderUtil.checkGlError("==ss==");      
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点速度属性引用id  
        maVelocityHandle = GLES20.glGetAttribLocation(mProgram, "aVelocity");        
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
        //获取顶点尺寸参数引用
        uPointSizeHandle = GLES20.glGetUniformLocation(mProgram, "uPointSize"); 
        //获取顶点颜色参数引用
        uColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor"); 
        //获取顶点颜色参数引用
        uTimeHandle=GLES20.glGetUniformLocation(mProgram, "uTime"); 

    }
    public void drawSelf()
    {  
    	long currTimeStamp=System.nanoTime()/1000000;
    	if(currTimeStamp-timeStamp>=10){
    		timeLive+=0.02f;
    		timeStamp=currTimeStamp;
    	}
    	
    	//制定使用某套着色器程序
   	    GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);  
        //将顶点尺寸传入着色器程序
        GLES20.glUniform1f(uPointSizeHandle, scale);
        //将时间传入着色器程序
        GLES20.glUniform1f(uTimeHandle, timeLive);    
        //将顶点颜色传入着色器程序
        GLES20.glUniform3fv(uColorHandle, 1, new float[]{1,1,1}, 0);
        //传入顶点速度数据    
        GLES20.glVertexAttribPointer(
        		maVelocityHandle,   
        		3, 
        		GLES20.GL_FLOAT, 
        		false,
                3*4, 
                mVelocityBuffer   
        );
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maVelocityHandle);         
        //绘制点    
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount); 
    }
}
