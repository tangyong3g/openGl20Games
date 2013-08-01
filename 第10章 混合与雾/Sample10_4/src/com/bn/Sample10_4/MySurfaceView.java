package com.bn.Sample10_4;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/200;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器    
    private float mPreviousX;//上次的触控位置X坐标
	//关于摄像机的变量
	float cx=0;//摄像机x位置
	float cy=150;//摄像机y位置
	float cz=400;//摄像机z位置
	
	float pmScale = 200f;//平面矩形的边长
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dx = x - mPreviousX;//计算触控笔X位移
            cx += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
            //将cx限制在一定范围内
            cx = Math.max(cx, -200);
            cx = Math.min(cx, 200);
            break;
        }
        mPreviousX = x;//记录触控笔位置
        return true;
    }
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormalFace cft;
		LoadedObjectVertexNormalAverage qt;
		LoadedObjectVertexNormalAverage yh;
		LoadedObjectVertexNormalAverage ch;
		TextureRect pm;
		final float disWithCenter = 12.0f;//物体离中心点的距离
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //设置camera位置
            MatrixState.setCamera
            (
            		cx,	//人眼位置的X
            		cy, //人眼位置的Y
            		cz, //人眼位置的Z
            		0, 	//人眼球看的点X
            		0,  //人眼球看的点Y
            		0,  //人眼球看的点Z
            		0, 	//up向量
            		1, 
            		0
            );
            MatrixState.pushMatrix();      
            //若加载的物体部位空则绘制物体   
            MatrixState.pushMatrix();
            pm.drawSelf();//平面
            MatrixState.popMatrix();   
            //缩放物体
            MatrixState.pushMatrix();
            MatrixState.scale(5.0f, 5.0f, 5.0f);          
            //绘制物体 
            //绘制长方体
            MatrixState.pushMatrix();
            MatrixState.translate(-disWithCenter, 0f, 0);
            cft.drawSelf();
            MatrixState.popMatrix();   
            //绘制球体
            MatrixState.pushMatrix();
            MatrixState.translate(disWithCenter, 0f, 0);
            qt.drawSelf();
            MatrixState.popMatrix();  
            //绘制圆环
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -disWithCenter);
            yh.drawSelf();
            MatrixState.popMatrix();  
            //绘制茶壶
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, disWithCenter);
            ch.drawSelf();
            MatrixState.popMatrix();
            MatrixState.popMatrix();             
          
            MatrixState.popMatrix();                  
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            float a = 0.5f;
            MatrixState.setProjectFrustum(-ratio*a, ratio*a, -1*a, 1*a, 2, 1000);
            //初始化光源位置
            MatrixState.setLightLocation(100, 100, 100);
        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);    
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();  
            //加载要绘制的物体
            ch=LoadUtil.loadFromFileVertexOnlyAverage("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		cft=LoadUtil.loadFromFileVertexOnlyFace("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		qt=LoadUtil.loadFromFileVertexOnlyAverage("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		yh=LoadUtil.loadFromFileVertexOnlyAverage("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		pm = new TextureRect(MySurfaceView.this,pmScale, pmScale);
        }
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
        		GLES20.GL_TEXTURE_2D, //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        
        return textureId;
	}
}
