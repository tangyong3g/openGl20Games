package com.bn.Sample12_3;
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
import static com.bn.Sample12_3.Constant.*;

class MySurfaceView extends GLSurfaceView   
{
    private SceneRenderer mRenderer;//场景渲染器    
    int textureFloor;//系统分配的不透明地板纹理id
    int textureFloorBTM;//系统分配的半透明地板纹理id
    int textureBallId;//系统分配的篮球纹理id
	 
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	TextureRect texRect;//表示地板的纹理矩形
    	BallTextureByVertex btbv;//用于绘制的球
    	BallForControl bfd;//用于控制的球
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
             
            MatrixState.pushMatrix();
            MatrixState.translate(0, -2, 0);
            
            //清除模板缓存
            GLES20.glClear(GLES20.GL_STENCIL_BUFFER_BIT);
            //允许模板测试
            GLES20.glEnable(GLES20.GL_STENCIL_TEST);
            //设置模板测试参数
            GLES20.glStencilFunc(GLES20.GL_ALWAYS, 1, 1);
            //设置模板测试后的操作
            GLES20.glStencilOp(GLES20.GL_KEEP, GLES20.GL_KEEP, GLES20.GL_REPLACE);            
            //绘制反射面地板
            texRect.drawSelf(textureFloor);  
            
            //设置模板测试参数
            GLES20.glStencilFunc(GLES20.GL_EQUAL,1, 1); 
            //设置模板测试后的操作
            GLES20.glStencilOp(GLES20.GL_KEEP, GLES20.GL_KEEP, GLES20.GL_KEEP);            
            //绘制镜像体
            bfd.drawSelfMirror( textureBallId);
            //禁用模板测试
            GLES20.glDisable(GLES20.GL_STENCIL_TEST);
            
            //绘制半透明地板
            //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            texRect.drawSelf(textureFloorBTM);    
            //关闭混合
            GLES20.glDisable(GLES20.GL_BLEND);  
            //绘制实际物体 
            bfd.drawSelf(textureBallId);  
            MatrixState.popMatrix();   
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0.0f,8.0f,8.0f,0,0f,0,0,1,0);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);  
            //创建纹理矩形对对象 
            texRect=new TextureRect(MySurfaceView.this,4,2.568f);  
            //创建用于绘制的篮球对象
            btbv=new BallTextureByVertex(MySurfaceView.this,BALL_SCALE);
            //创建用于控制的篮球对象
            bfd=new BallForControl(btbv,3f);
            //关闭深度检测
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            //初始化纹理
            textureFloor=initTexture(R.drawable.mdb);
            textureFloorBTM=initTexture(R.drawable.mdbtm);
            textureBallId=initTexture(R.drawable.basketball);            
            //打开背面剪裁   
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
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
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GLES20.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
}
