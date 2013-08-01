package com.bn.Sample11_9;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

public class MySurfaceView extends GLSurfaceView
{
	SceneRenderer mRender;
	public MySurfaceView(Context context)
	{
		super(context);
		this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRender = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRender);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染 
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		TextRect tRect;
		int wlWidth=512;//文字纹理宽度
		int wlHeight=512;//文字纹理高度
		long timeStamp=System.currentTimeMillis();
		int texId=-1;
		@Override
		public void onDrawFrame(GL10 gl)
		{
			long tts=System.currentTimeMillis();
        	if(tts-timeStamp>500)
        	{
        		timeStamp=tts;
        		FontUtil.cIndex=(FontUtil.cIndex+1)%FontUtil.content.length;
            	FontUtil.updateRGB();
        	}
        	
        	if(texId!=-1)
        	{
        		GLES20.glDeleteTextures(1, new int[]{texId}, 0);
        	}
        	//生成文字纹理
        	Bitmap bm=FontUtil.generateWLT(FontUtil.getContent(FontUtil.cIndex, FontUtil.content), wlWidth, wlHeight);
        	texId=initTexture(bm);
        	
			//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -2);
            tRect.drawSelf(texId);
            MatrixState.popMatrix();
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			//设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,1,0,0,0,0f,1.0f,0.0f);
		}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//设置屏幕背景色RGBA
            GLES20.glClearColor(0,0,0,1.0f);
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            
            MatrixState.setInitStack();
            tRect=new TextRect(MySurfaceView.this);
		}
    }
	//生成纹理的id
	public int initTexture(Bitmap bitmap)
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmap, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmap.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
}