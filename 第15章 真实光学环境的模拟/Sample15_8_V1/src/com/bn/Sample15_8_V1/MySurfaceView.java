package com.bn.Sample15_8_V1;
import static com.bn.Sample15_8_V1.Constant.SCREEN_HEIGHT;
import static com.bn.Sample15_8_V1.Constant.SCREEN_WIDTH;
import static com.bn.Sample15_8_V1.Constant.SHADOW_TEX_HEIGHT;
import static com.bn.Sample15_8_V1.Constant.SHADOW_TEX_WIDTH;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
 
class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器    
    
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标    
    
    //摄像机位置相关
    float cx=0;
    float cy=1;
    float cz=60;  
    float cAngle=0;
    final float cR=60;
    
    //灯光位置
	  float lx=0;
	  final float ly=10;
	  float lz=45;   
	  float lAngle=0;
	  final float lR=45;
	  
      final float cDis=15;
    
    //光源总变换矩阵
    float[] mMVPMatrixGY;
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
        
        new Thread()
        {
        	public void run()
        	{
        		while(true) 
        		{
        			lAngle += 0.5;//设置沿x轴旋转角度                    
                    lx=(float) Math.sin(Math.toRadians(lAngle))*lR;
                    lz=(float) Math.cos(Math.toRadians(lAngle))*lR;
                    try {
   					Thread.sleep(40);
	   				} catch (InterruptedException e) {
	   					e.printStackTrace();
	   				}
        		}
        	}
        }.start();
    }
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            cAngle += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
              
            cx=(float) Math.sin(Math.toRadians(cAngle))*cR;
            cz=(float) Math.cos(Math.toRadians(cAngle))*cR;
            
            cy+= dy/10.0f;//设置沿z轴移动
            requestRender();//重绘画面
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormal lovo_pm;//平面
		LoadedObjectVertexNormal lovo_ch;//茶壶
		LoadedObjectVertexNormal lovo_cft;//长方体
		LoadedObjectVertexNormal lovo_qt;//球体
		LoadedObjectVertexNormal lovo_yh;//圆环
		
		TextureRect tr;		
		int frameBufferId;
		int shadowId;// 动态产生的阴影纹理Id
		int renderDepthBufferId;// 动态产生的阴影纹理Id
		
		
		boolean isBegin=true;
		//初始化帧缓冲和渲染缓冲
		public void initFRBuffers()
		{
			int[] tia=new int[1];
			GLES20.glGenFramebuffers(1, tia, 0);
			frameBufferId=tia[0];
			
			if(isBegin)
			{
				GLES20.glGenRenderbuffers(1, tia, 0);
				renderDepthBufferId=tia[0];
				GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderDepthBufferId);
            	GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, SHADOW_TEX_WIDTH, SHADOW_TEX_HEIGHT);
				isBegin=false;
			}
			
			
			int[] tempIds = new int[1];
    		GLES20.glGenTextures
    		(
    				1,          //产生的纹理id的数量
    				tempIds,   //纹理id的数组
    				0           //偏移量
    		);   
    		
    		shadowId=tempIds[0];
    		
		}
		
        //通过绘制产生阴影纹理        
        public void generateShadowImage()
        {
        	initFRBuffers();
        	
        	GLES20.glViewport(0, 0, SHADOW_TEX_WIDTH, SHADOW_TEX_HEIGHT);             	
        	GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId);            	
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowId);
        	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
    		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
    		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
    		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE); 
           
            GLES20.glFramebufferTexture2D
            (
            	GLES20.GL_FRAMEBUFFER, 
            	GLES20.GL_COLOR_ATTACHMENT0,
            	GLES20.GL_TEXTURE_2D, 
            	shadowId, 
            	0
            );     
        	
        	GLES20.glTexImage2D
        	(
        		GLES20.GL_TEXTURE_2D, 
        		0, 
        		GLES20.GL_RGB, 
        		SHADOW_TEX_WIDTH, 
        		SHADOW_TEX_HEIGHT, 
        		0, 
        		GLES20.GL_RGB, 
        		GLES20.GL_UNSIGNED_SHORT_5_6_5, 
        		null
        	);
        	
        	GLES20.glFramebufferRenderbuffer
        	(
        		GLES20.GL_FRAMEBUFFER, 
        		GLES20.GL_DEPTH_ATTACHMENT,
        		GLES20.GL_RENDERBUFFER, 
        		renderDepthBufferId
        	);

        	
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(lx,ly,lz,0f,0f,0f,0f,1,0);
            MatrixState.setProjectFrustum(-1, 1, -1.0f, 1.0f, 1.5f, 400);  
            mMVPMatrixGY=MatrixState.getViewProjMatrix();
            
            //绘制最下面的平面
            lovo_pm.drawSelfForShadow();  
            
            //绘制球体
            MatrixState.pushMatrix(); 
            MatrixState.translate(-cDis, 0, 0);
            //若加载的物体部位空则绘制物体
            lovo_qt.drawSelfForShadow();
            MatrixState.popMatrix();    
            
            //绘制圆环
            MatrixState.pushMatrix();            
            MatrixState.translate(cDis, 0, 0);
            MatrixState.rotate(30, 0, 1, 0);
            //若加载的物体部位空则绘制物体
            lovo_yh.drawSelfForShadow();
            MatrixState.popMatrix();  
            
            //绘制长方体
            MatrixState.pushMatrix(); 
            MatrixState.translate(0, 0, -cDis);
            //若加载的物体部位空则绘制物体
            lovo_cft.drawSelfForShadow();
            MatrixState.popMatrix();
            
            //绘制茶壶
            MatrixState.pushMatrix(); 
            MatrixState.translate(0, 0, cDis);
            //若加载的物体部位空则绘制物体
            lovo_ch.drawSelfForShadow();
            MatrixState.popMatrix();     
        }
        
        //绘制阴影纹理
        public void drawShadowTexture()
        {
        	//设置视窗大小及位置 
        	GLES20.glViewport(0, 0, (int)SCREEN_WIDTH, (int)SCREEN_HEIGHT); 
        	GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glClearColor(1f,0.3f,0.3f,1.0f);  
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,1.5f,0f,0f,-1f,0f,1.0f,0.0f);
            MatrixState.setProjectOrtho(-0.6f,0.6f, -0.6f, 0.6f, 1, 10);  
            tr.drawSelf(shadowId);   
            GLES20.glDeleteFramebuffers(1, new int[]{frameBufferId}, 0);
            GLES20.glDeleteTextures(1, new int[]{shadowId}, 0);
        }
        
        public void onDrawFrame(GL10 gl)
        {
        	MatrixState.setLightLocation(lx, ly, lz);        	
        	
        	//通过绘制产生阴影纹理
            generateShadowImage();
            //绘制阴影纹理
            drawShadowTexture();
        }
        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	Constant.SCREEN_HEIGHT=height;
        	Constant.SCREEN_WIDTH=width;
        }
       
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {     	
        	//设置屏幕背景色RGBA
            GLES20.glClearColor(0.3f,0.3f,0.3f,1.0f);    
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //初始化光源位置
            MatrixState.setLightLocation(lx, ly, lz);
            //加载要绘制的物体
            lovo_ch=LoadUtil.loadFromFileVertexOnly("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_pm=LoadUtil.loadFromFileVertexOnly("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_cft=LoadUtil.loadFromFileVertexOnly("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_qt=LoadUtil.loadFromFileVertexOnly("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_yh=LoadUtil.loadFromFileVertexOnly("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            //显示阴影贴图的纹理矩形
            tr=new TextureRect(MySurfaceView.this);
        }
    }
	
	

}
