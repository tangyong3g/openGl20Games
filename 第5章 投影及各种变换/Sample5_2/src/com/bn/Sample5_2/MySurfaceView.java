package com.bn.Sample5_2;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
	 
	private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
	
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
            for(SixPointedStar h:mRenderer.ha)
            {
            	h.yAngle += dx * TOUCH_SCALE_FACTOR;//设置六角星数组中的各个六角星绕y轴旋转角度
                h.xAngle+= dy * TOUCH_SCALE_FACTOR;//设置六角星数组中的各个六角星绕x轴旋转角度
            }
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	SixPointedStar[] ha=new SixPointedStar[6];//六角星数组
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //绘制六角星数组中的各个六角星
            for(SixPointedStar h:ha)
            {
            	h.drawSelf();
            }
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
        	float ratio= (float) width / height;
            //设置透视投影
        	MatrixState.setProjectFrustum(-ratio*0.4f, ratio*0.4f, -1*0.4f, 1*0.4f, 1, 50); 
        	
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,6,0f,0f,0f,0f,1.0f,0.0f);
        }

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); // 设置屏幕背景色RGBA
			// 创建六角星数组中的各个对象
			for (int i = 0; i < ha.length; i++) {
				ha[i] = new SixPointedStar(MySurfaceView.this, 0.4f, 1.0f,-1.0f * i);						
			}			
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);// 打开深度检测
		}
    }
}
