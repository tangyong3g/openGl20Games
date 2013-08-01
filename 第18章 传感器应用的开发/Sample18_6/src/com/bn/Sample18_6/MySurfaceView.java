package com.bn.Sample18_6;

import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLES20;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView {
    
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
	
	private float cameraX=0;//摄像机的位置
	private float cameraY=30;
	private float cameraZ=0;
	
	private float targetX=0;//看点
	private float targetY=0;
	private float targetZ=0;
	
	private float sightDis=26;//摄像机和目标的距离
	private float angdegElevation=90;//仰角
	private float angdegAzimuth=0;//方位角
    
	private SceneRenderer mRenderer;//场景渲染器
    int texFloorId;		//地板的纹理id
    int texWallId;		//墙面的纹理

    BallGoThread ballGoThread;		//球运动的线程
    
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            
            angdegAzimuth += dx * TOUCH_SCALE_FACTOR;//设置沿y轴旋转角度
            angdegElevation+= dy * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
            
            //仰角
            if(angdegElevation>=90){
            	angdegElevation=90;
            }else if(angdegElevation<=0){
            	angdegElevation=0;
            } 
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
    
	private class SceneRenderer implements GLSurfaceView.Renderer 
    { 
		CubeGroup cubeGroup;//立方体组
		BallForControl ballForControl;	//球
		
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            
            double angradElevation=Math.toRadians(angdegElevation);//仰角（弧度）
        	double angradAzimuth=Math.toRadians(angdegAzimuth);//方位角
            cameraX=(float) (targetX+sightDis*Math.cos(angradElevation)*Math.sin(angradAzimuth));
            cameraY=(float) (targetY+sightDis*Math.sin(angradElevation));
            cameraZ=(float) (targetZ+sightDis*Math.cos(angradElevation)*Math.cos(angradAzimuth));
            
            MatrixState.setCamera(//设置camera位置 
            		cameraX, //人眼位置的X
            		cameraY, //人眼位置的Y
            		cameraZ, //人眼位置的Z
            		
            		targetX, //人眼球看的点X
            		targetY, //人眼球看的点Y
            		targetZ, //人眼球看的点Z
            		
            		0,  //头的朝向
            		1, 
            		0
            );
            
            //绘制墙面
            MatrixState.pushMatrix();
            cubeGroup.drawSelf(texFloorId,texWallId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            //绘制球
            ballForControl.drawSelf();
            MatrixState.popMatrix();
            
        }   

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio= (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
	        //初始化光源
	        MatrixState.setLightLocation(0 , 12 , 0);
	        //创建线程对象
	        ballGoThread=new BallGoThread(ballForControl);
	        //线程标志位设为true
	        ballGoThread.setFlag(true);
	        //开启线程
	        ballGoThread.start();
	        
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.0f,0.0f,0.0f, 1.0f);  
            //启用深度测试
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    		//设置为打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //地板的纹理id
            texFloorId=initTexture(R.drawable.tex_floor);
            //墙面的纹理id
            texWallId=initTexture(R.drawable.tex_wall);
            
            //创建各个立方体
            cubeGroup=new CubeGroup(MySurfaceView.this,Constant.SCALE, 
            		Constant.CUBE_LENGTH, Constant.CUBE_HEIGHT, Constant.CUBE_WIDTH ,Constant.WALL_WIDTH);//立方体组

            //创建球的对象
            ballForControl=new BallForControl(MySurfaceView.this,Constant.SCALE,Constant.AHALF,5);

            
            
        }
    }
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//关闭线程
		ballGoThread.setFlag(false);
	}
	
	public int initTexture(int drawableId)//textureId
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
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================  
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        
        return textureId;
	}
}
