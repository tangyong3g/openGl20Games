package com.bn.Sample16_6;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import static com.bn.Sample16_6.Constant.*;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
		Rope rope;
		Stick stick;
		
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            
            MatrixState.pushMatrix();
            
            for(int i=0;i<rope.massCount-1;i++){
            	Mass mass1 = rope.massList.get(i);
            	Mass mass2 = rope.massList.get(i+1);
            	Vector3 stickVector = mass1.pos.add(mass2.pos.multiConstant(-1));
            	
            	MatrixState.pushMatrix();
            	MatrixState.translate(mass2.pos.x, mass2.pos.y, mass2.pos.z);
            	Vector3.moveXToSomeVector(new double[]{stickVector.x,stickVector.y,stickVector.z});
            	stick.drawSelf(springLength/2,0);
            	MatrixState.popMatrix();
            }
            MatrixState.popMatrix();
        }   

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
        	float ratio=0;
            ratio= (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1.5f, 10);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,2.2f,0f,0f,0f,0f,1.0f,0.0f);    
            //打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //设置灯光的初始位置
            MatrixState.setLightLocation(0,2,2);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(1f,1f,1f, 1.0f);  
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            MatrixState.setInitStack();
            rope = new Rope(
            		40, //massCount
            		0.1f, //m
            		new Vector3(0,-9.8f,0),//G 
            		10.0f, //groundRepulsion地面弹性
            		0.2f, //friction 地面摩擦性
            		20f, //地面的缓冲系数
            		groundHeight, //地面的高度
            		0.2f,//空气阻力
            		new Vector3(-1,0,0),//绳头的速度 
            		 new Vector3(0,groundHeight,-1)//绳头的初始位置
            		);
            stick = new Stick(MySurfaceView.this,springLength,springR,10);//创建棍
            
            new Thread(){
        	   float time=0;
        	   float dt = 0.01f;
        	   public void run(){
        		   while(flag && time<deadTime){
        			   time+=0.01f;
        			   rope.operate(dt);
        			   try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		   }
        	   }
            }.start();
        }
    }
}
