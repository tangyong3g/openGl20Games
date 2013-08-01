package com.bn.Sample16_2;
import java.util.ArrayList;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器
    LovoGoThread lgt;
    ArrayList<RigidBody> aList=new ArrayList<RigidBody>();
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	private class SceneRenderer implements GLSurfaceView.Renderer
    {
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormal ch;
		LoadedObjectVertexNormal pm;
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            
            //若加载的物体部位空则绘制物体
            if(ch!=null)
            {
            	for(int i=0;i<aList.size();i++)
            	{
            		aList.get(i).drawSelf();
            	}
            }
            if(pm!=null)
            {
            	pm.drawSelf();
            }               
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,35,0,0f,0f,0f,0f,0f,1f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);    
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //初始化光源位置
            MatrixState.setLightLocation(0, 10, -15);
            //加载要绘制的物体
            ch=LoadUtil.loadFromFile("gxq.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            pm=LoadUtil.loadFromFile("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            //旋转两侧的光线枪
            aList.add(new RigidBody(ch,true,new Vector3f(-18f,0f,0),new Vector3f(0,0,0),new Orientation(45,0,1,0)));
            aList.add(new RigidBody(ch,true,new Vector3f(20f,0f,0),new Vector3f(0,0,0),new Orientation(45,0,1,0)));		
            aList.add(new RigidBody(ch,false,new Vector3f(0f,0f,0),new Vector3f(0.1f,0,0),new Orientation(0,0,1,0)));
            //全部旋转90度
//            aList.add(new RigidBody(ch,true,new Vector3f(-18f,0f,0),new Vector3f(0,0,0),new Orientation(90,0,1,0)));
//            aList.add(new RigidBody(ch,true,new Vector3f(20f,0f,0),new Vector3f(0,0,0),new Orientation(90,0,1,0)));		
//            aList.add(new RigidBody(ch,false,new Vector3f(0f,0f,0),new Vector3f(0.1f,0,0),new Orientation(90,0,1,0)));
            
            lgt=new LovoGoThread(aList);
            lgt.start();
        }
    }
}