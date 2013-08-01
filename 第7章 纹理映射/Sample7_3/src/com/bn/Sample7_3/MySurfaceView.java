package com.bn.Sample7_3;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器
    
    int currenttexId32;				//当前纹理的id 
    int currenttexId256;				//当前纹理的id 
    int[] texId = new int[8];          //系统分配的纹理id
    
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	TextureRect texRect; 	//纹理矩形对象的引用
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //绘制小纹理矩形
            MatrixState.pushMatrix();	//进栈
            MatrixState.transtate(0, 1.3f, 1);	//平移 
            MatrixState.rotate(-20, 0, 0, 1);	//旋转
            MatrixState.scale(0.3f, 0.3f, 0.3f); //缩放
            texRect.drawSelf(currenttexId256);	//绘制小四边形 
            MatrixState.popMatrix();//出栈
            
            //绘制大纹理矩形
            MatrixState.pushMatrix();	//进栈
            MatrixState.transtate(0, -0.6f, 1);	//平移   
            MatrixState.rotate(-20, 0, 0, 1);	//旋转         
            texRect.drawSelf(currenttexId32);	//绘制大四边形 
            MatrixState.popMatrix();//出栈
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProject(-ratio, ratio, -1, 1, 1, 10);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,3,0f,0f,0f,0f,1.0f,0.0f);
            //初始化旋转矩阵
            MatrixState.setInitStack();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.5f,0.5f,0.5f, 1.0f);  
            //创建纹理矩形对象
            texRect = new TextureRect(MySurfaceView.this);
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //初始化32*32像素的四个纹理
            texId[0] = initTexture(R.drawable.bw32,GLES20.GL_NEAREST,GLES20.GL_NEAREST);
            texId[1] = initTexture(R.drawable.bw32,GLES20.GL_LINEAR,GLES20.GL_LINEAR);
            texId[2] = initTexture(R.drawable.bw32,GLES20.GL_NEAREST,GLES20.GL_LINEAR);
            texId[3] = initTexture(R.drawable.bw32,GLES20.GL_LINEAR,GLES20.GL_NEAREST);
            //初始化256*256像素的四个纹理
            texId[4] = initTexture(R.drawable.bw256,GLES20.GL_NEAREST,GLES20.GL_NEAREST);
            texId[5] = initTexture(R.drawable.bw256,GLES20.GL_LINEAR,GLES20.GL_LINEAR);
            texId[6] = initTexture(R.drawable.bw256,GLES20.GL_NEAREST,GLES20.GL_LINEAR);
            texId[7] = initTexture(R.drawable.bw256,GLES20.GL_LINEAR,GLES20.GL_NEAREST);
            
            currenttexId32 = texId[0];	//设置默认纹理
            currenttexId256 = texId[4];	//设置默认纹理
            //关闭背面剪裁   
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }
    }
		
	public int initTexture(int drawableId,float sample1,float sample2){
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
					
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,sample1);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,sample2);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally {
            try {
                is.close();
            } 
            catch(IOException e) {
                e.printStackTrace();
            }
        }
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
