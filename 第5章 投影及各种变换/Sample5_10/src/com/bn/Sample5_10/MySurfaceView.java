package com.bn.Sample5_10;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import static com.bn.Sample5_10.Constant.*;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
	
    private float mPreviousX;//上次的触控位置X坐标
    
    float yAngle=0;//总场景绕y轴旋转的角度
	
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
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dx = x - mPreviousX;//计算触控笔X位移
            yAngle += dx * TOUCH_SCALE_FACTOR;//设置三角形对绕y轴旋转角度
        }
        mPreviousX=x;
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	Cube cube;//立方体
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //保护现场
            MatrixState.pushMatrix();
            //绕Y轴旋转
            MatrixState.rotate(yAngle, 0, 1, 0);
            
            //绘制左侧立方体
            MatrixState.pushMatrix();
            MatrixState.translate(-3, 0, 0);
            MatrixState.rotate(60, 0, 1, 0);
            cube.drawSelf();
            MatrixState.popMatrix();
            
            //绘制右侧立方体
            MatrixState.pushMatrix();
            MatrixState.translate(3, 0, 0);
            MatrixState.rotate(-60, 0, 1, 0);
            cube.drawSelf();
            MatrixState.popMatrix();
            
            //恢复现场
            MatrixState.popMatrix();
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            ratio = (float) width / height;
            
            //视角不合适导致变形
	        //调用此方法计算产生透视投影矩阵
	        MatrixState.setProjectFrustum(-ratio*0.7f, ratio*0.7f, -0.7f, 0.7f, 1, 10);
	        //调用此方法产生摄像机9参数位置矩阵
	        MatrixState.setCamera(0,0.5f,4,0f,0f,0f,0f,1.0f,0.0f);
            
            //初始化变换矩阵
            MatrixState.setInitStack();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.5f,0.5f,0.5f, 1.0f);  
            //创建立方体对象
            cube=new Cube(MySurfaceView.this);
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES20.glEnable(GLES20.GL_CULL_FACE);  
        }
    }
}
