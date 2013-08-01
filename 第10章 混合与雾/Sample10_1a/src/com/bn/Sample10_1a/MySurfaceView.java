package com.bn.Sample10_1a;
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
    private SceneRenderer mRenderer;//场景渲染器  
	
	//矩形的位置
	static float rectX;
	static float rectY;
	static int rectState = KeyThread.Stop;
	static final float moveSpan = 0.1f;
	private KeyThread keyThread;
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
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	if(x<Constant.SCREEN_WIDTH/3.0f) {//按下屏幕左面1/3向左移
        		rectState = KeyThread.left;
        	}
        	else if(x>Constant.SCREEN_WIDTH*2/3.0f){//按下屏幕右面2/3向右移
        		rectState = KeyThread.right;
        	}
        	else {
            	if(y<Constant.SCREEN_HEIGHT/2.0f) {   //按下屏幕上方向上移     		
            		rectState = KeyThread.up;
            	}
            	else {//按下屏幕下方向下移 
            		rectState = KeyThread.down;
            	}
        	}
        	break;
        case MotionEvent.ACTION_UP://抬起时停止移动
        	rectState = KeyThread.Stop;
        	break;
        }
        return true;
    }
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		int rectTexId;//纹理id
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormalFace pm;
		LoadedObjectVertexNormalFace cft;
		LoadedObjectVertexNormalAverage qt;
		LoadedObjectVertexNormalAverage yh;
		LoadedObjectVertexNormalAverage ch;
		TextureRect rect;
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
           
            MatrixState.pushMatrix();
            MatrixState.pushMatrix();
            MatrixState.rotate(25, 1, 0, 0);       
            //若加载的物体部位空则绘制物体            
            pm.drawSelf();//平面
            
            //缩放物体
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);          
            //绘制物体 
            //绘制长方体
            MatrixState.pushMatrix();
            MatrixState.translate(-10f, 0f, 0);
            cft.drawSelf();
            MatrixState.popMatrix();   
            //绘制球体
            MatrixState.pushMatrix();
            MatrixState.translate(10f, 0f, 0);
            qt.drawSelf();
            MatrixState.popMatrix();  
            //绘制圆环
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -10f);
            yh.drawSelf();
            MatrixState.popMatrix();  
            //绘制茶壶
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, 10f);
            ch.drawSelf();
            MatrixState.popMatrix();
            MatrixState.popMatrix(); 
            MatrixState.popMatrix(); 
              
            //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子c
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); 
            //绘制纹理矩形
            MatrixState.pushMatrix();
            MatrixState.translate(rectX, rectY, 25f);
            rect.drawSelf(rectTexId);
            MatrixState.popMatrix();
            //关闭混合
            GLES20.glDisable(GLES20.GL_BLEND);
            
            MatrixState.popMatrix();                  
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            //设置camera位置
            MatrixState.setCamera
            (
            		0,   //人眼位置的X
            		0, 	//人眼位置的Y
            		50,   //人眼位置的Z
            		0, 	//人眼球看的点X
            		0,   //人眼球看的点Y
            		0,   //人眼球看的点Z
            		0, 	//up位置
            		1, 
            		0
            );
            //初始化光源位置
            MatrixState.setLightLocation(100, 100, 100);
            keyThread = new KeyThread(MySurfaceView.this);
            keyThread.start();
        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.3f,0.3f,0.3f,1.0f);    
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();         
            //纹理id
            rectTexId=initTexture(R.raw.lgq);    
            //加载要绘制的物体
            ch=LoadUtil.loadFromFileVertexOnlyAverage("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            pm=LoadUtil.loadFromFileVertexOnlyFace("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);;
    		cft=LoadUtil.loadFromFileVertexOnlyFace("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);;
    		qt=LoadUtil.loadFromFileVertexOnlyAverage("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);;
    		yh=LoadUtil.loadFromFileVertexOnlyAverage("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);;
    		rect = new TextureRect(MySurfaceView.this, 10, 10);    
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
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        
        return textureId;
	}
	@Override
    public void onResume() {
    	super.onResume();
    	KeyThread.flag = true;
    	keyThread = new KeyThread(MySurfaceView.this);
        keyThread.start();
    }
	@Override
	public void onPause() {
		super.onPause();
		KeyThread.flag = false;
	}
}
