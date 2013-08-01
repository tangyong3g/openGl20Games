package com.bn.Sample8_6;

import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

class MySurfaceView extends GLSurfaceView {
    
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
	
	private SceneRenderer mRenderer;//场景渲染器
    
    boolean lightFlag=true;		//光照旋转的标志位
    
    float yAngle=0;//绕y轴旋转的角度       
    float xAngle=0;//绕x轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    
    Ball ball;
    Stick stick;
    
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
            yAngle += dx * TOUCH_SCALE_FACTOR;//设置绕y轴旋转角度
            zAngle+= dy * TOUCH_SCALE_FACTOR;//设置绕z轴旋转角度
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
    
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
		
		RegularPolygon first;
        
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -6f);
            MatrixState.pushMatrix();       
   	   	 	MatrixState.rotate(xAngle, 1, 0, 0);
   	   	 	MatrixState.rotate(yAngle, 0, 1, 0);
   	   	 	MatrixState.rotate(zAngle, 0, 0, 1);
        	 for(int i=0;i<5;i++){		//五部分循环
         		MatrixState.pushMatrix();
         	 	MatrixState.rotate(72*i,0,0,1);	//根据绘制的为第几部分，旋转72*i度
         	 	first.drawSelf(0, 0);	//绘制足球碳的五分之一部分，最后的五边形由五部分组合形成，所以不用绘制
         	 	MatrixState.popMatrix();
         	}
        	 MatrixState.popMatrix();
        	 MatrixState.popMatrix();
        	 Utils.drawnVertices.clear();
            
        }   

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio= (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,8.0f,0f,0f,0f,0f,1.0f,0.0f); 
            
	        //初始化光源
	        MatrixState.setLightLocation(10 , 0 , -10);
	                      
	        //启动一个线程定时修改灯光的位置
	        new Thread()
	        {
				public void run()
				{
					float redAngle = 0;
					while(lightFlag)
					{	
						//根据角度计算灯光的位置
						redAngle=(redAngle+5)%360;
						float rx=(float) (15*Math.sin(Math.toRadians(redAngle)));
						float rz=(float) (15*Math.cos(Math.toRadians(redAngle)));
						MatrixState.setLightLocation(rx, 0, rz);
						
						try {
								Thread.sleep(100);
							} catch (InterruptedException e) {				  			
								e.printStackTrace();
							}
					}
				}
	        }.start();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.9f,0.9f,0.9f, 1.0f);    
            //启用深度测试
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    		//设置为打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            
            float[] colorValue = {1,0,0,1};	//创建颜色数组
            ball = new Ball(MySurfaceView.this,Constant.BALL_R,colorValue);//创建球对象
            colorValue = new float[]{1,1,0,1};
            stick = new Stick(MySurfaceView.this,Constant.LENGTH,Constant.R,Constant.ANGLE_SPAN,colorValue);//创建圆管对象
            
            
            double[] initPoint = Utils.getFirstPoint(Constant.LENGTH);//得到第一个五边形左下点的坐标
            double[] initVector={1,0,0,1};	//初始化方向向量
            double[] zPivot = {0,0,1,1};	//以z轴为旋转轴
            int[] vertices = {0,1,2,3,4};	//球的索引
            int[] borders = {0,1,2,3,4};	//圆管的索引
            first = new RegularPolygon(MySurfaceView.this, 5,72 , 
            		Constant.LENGTH, initPoint, initVector,zPivot,vertices,borders);//1
            
            vertices = new int[]{2,3,4};	//球的索引
            borders = new int[]{1,2,3,4};	//圆管的索引
            RegularPolygon rp2 = first.buildChild( 6, -60,1,vertices,borders);//2
            
            vertices = new int[]{2,3,4,5};
            borders = new int[]{1,2,3,4,5};
            RegularPolygon rp4 = rp2.buildChild( 6, 60,3,vertices,borders);//4
            
            
            vertices = new int[]{};
            borders = new int[]{1,5};
            rp4.buildChild( 6, -60,2,vertices,borders);//5
            
            vertices = new int[]{2};
            borders = new int[]{1,2};
            RegularPolygon rp6 = rp4.buildChild( 5, -72,3,vertices,borders);//6
            
            vertices = new int[]{3,4,5};
            borders = new int[]{2,3,4,5};
            rp6.buildChild( 6, 60,2,vertices,borders);//7
            
        }
    }
}
