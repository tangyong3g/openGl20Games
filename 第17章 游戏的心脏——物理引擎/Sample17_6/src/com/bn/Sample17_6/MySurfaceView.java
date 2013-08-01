package com.bn.Sample17_6;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

import com.bn.Sample17_6.R;
import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.collision.shapes.CylinderShapeX;
import com.bulletphysics.collision.shapes.CylinderShapeZ;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SliderConstraint;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;

import static com.bn.Sample17_6.Constant.*;

public class MySurfaceView extends GLSurfaceView {

	DiscreteDynamicsWorld dynamicsWorld;
	CollisionShape boxShape;
	CollisionShape stickShape;
	CollisionShape stickFBSliderShape;
	CollisionShape stickLRSliderShape;
	//刚体
	RigidBodyHelper cubeBody;
	RigidBodyHelper stickFBSliderBody;
	RigidBodyHelper stickLRFSliderBody;
	RigidBodyHelper stickLRNSliderBody;
	//添加滑动约束
	SliderConstraint sliderFB;
	SliderConstraint sliderLRF;//远端的横向轴
	SliderConstraint sliderLRN;//近端的横向轴
	SliderConstraint[] sliders=new SliderConstraint[3];
	static boolean sliding=false;
	static boolean init=true;
	int currIndex;
	MyRenderer renderer; 
    public static int keyState=0;
	boolean flag=true;
	
	float screenWidth;
	float screenHeight;
	float buttonPixels;
	
	public MySurfaceView(Context context) {
		super(context);
		this.setEGLContextClientVersion(2);
		initWorld();
		renderer = new MyRenderer();
		this.setRenderer(renderer);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	 @Override 
	    public boolean onTouchEvent(MotionEvent e) {
	        float y = e.getY();
	        float x = e.getX();
	        switch (e.getAction()) {	       
	        case MotionEvent.ACTION_DOWN:
	        	handleArrowDown(x,y);
	        	break;
	        case MotionEvent.ACTION_UP:
	        	handleArrowUp(x,y);
	        	break;
	        }
	        return true; 
	    }
	 public void handleArrowDown(float x,float y){
		 float buttonCenterX=screenWidth-buttonPixels/2;
		 float buttonCenterY=screenHeight-buttonPixels/2;
		 float baseUnit=buttonPixels/4;
		 
		 float upXMax=baseUnit+buttonCenterX;
		 float upXMin=-baseUnit+buttonCenterX;
		 float upYMin=-2*baseUnit+buttonCenterY;
		 float upYMax=-baseUnit+buttonCenterY;
		 
		 float downXMax=baseUnit+buttonCenterX;
		 float downXMin=-baseUnit+buttonCenterX;
		 float downYMax=2*baseUnit+buttonCenterY;
		 float downYMin=baseUnit+buttonCenterY;  
		 
		 float leftXMax=-baseUnit+buttonCenterX;
		 float leftXMin=-2*baseUnit+buttonCenterX;
		 float leftYMax=baseUnit+buttonCenterY;
		 float leftYMin=-baseUnit+buttonCenterY;  
		 
		 float rightXMax=2*baseUnit+buttonCenterX;
		 float rightXMin=baseUnit+buttonCenterX;
		 float rightYMax=baseUnit+buttonCenterY;
		 float rightYMin=-baseUnit+buttonCenterY;  
		 
		 if(upXMin<x && x<upXMax && upYMin<y && y<upYMax){
			 keyState=0x1;
		 }else if(rightXMin<x && x<rightXMax && rightYMin<y && y<rightYMax){
			 keyState=0x8;
		 }else if(downXMin<x && x<downXMax && downYMin<y && y<downYMax){
			 keyState=0x2;
		 }else if(leftXMin<x && x<leftXMax && leftYMin<y && y<leftYMax){
			 keyState=0x4;
		 }
	 }
	 public void handleArrowUp(float x,float y){ 
		 float buttonCenterX=screenWidth-buttonPixels/2;
		 float buttonCenterY=screenHeight-buttonPixels/2;
		 float baseUnit=buttonPixels/4;  
		 
		 float upXMax=baseUnit+buttonCenterX;
		 float upXMin=-baseUnit+buttonCenterX;
		 float upYMin=-2*baseUnit+buttonCenterY;
		 float upYMax=-baseUnit+buttonCenterY;
		 
		 float downXMax=baseUnit+buttonCenterX;  
		 float downXMin=-baseUnit+buttonCenterX;
		 float downYMax=2*baseUnit+buttonCenterY;
		 float downYMin=baseUnit+buttonCenterY;  
		 
		 float leftXMax=-baseUnit+buttonCenterX;
		 float leftXMin=-2*baseUnit+buttonCenterX;
		 float leftYMax=baseUnit+buttonCenterY;
		 float leftYMin=-baseUnit+buttonCenterY;  
		 
		 float rightXMax=2*baseUnit+buttonCenterX;
		 float rightXMin=baseUnit+buttonCenterX;
		 float rightYMax=baseUnit+buttonCenterY;
		 float rightYMin=-baseUnit+buttonCenterY;  
		 
		 if(upXMin<x && x<upXMax && upYMin<y && y<upYMax){
			 keyState=0;
			 stopSlide();
		 }else if(rightXMin<x && x<rightXMax && rightYMin<y && y<rightYMax){
			 keyState=0;
			 stopSlide();
		 }else if(downXMin<x && x<downXMax && downYMin<y && y<downYMax){
			 keyState=0;
			 stopSlide();
		 }else if(leftXMin<x && x<leftXMax && leftYMin<y && y<leftYMax){
			 keyState=0;
			 stopSlide();
		 }
	 }
	public void initWorld(){
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
		int maxProxies = 1024;
		AxisSweep3 overlappingPairCache =new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver,collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
		boxShape=new BoxShape(new Vector3f(cubeSize,cubeSize,cubeSize));
		stickShape = new CylinderShape(new Vector3f(Stick_R,Stick_Length,Stick_R));//Y方向圆柱
		stickFBSliderShape = new CylinderShapeZ(new Vector3f(Stick_R,Stick_R,Stick_Length));//Z方向圆柱
		stickLRSliderShape = new CylinderShapeX(new Vector3f(Stick_Length,Stick_R,Stick_R));//X方向圆柱
		initRigidBody();
		//添加长方体与前后方向的棍之间的约束
		Vector3f originA = new Vector3f(0, 0, 0);
		Vector3f originB = new Vector3f(0, 0, 0);
		addSliderConstraint(0,stickFBSliderBody.body,cubeBody.body,BulletGlobals.SIMD_PI/2,originA,originB,true);//BulletGlobals.SIMD_PI/2
		//添加前后方向的棍与左右方向远端的棍之间的约束
		originA = new Vector3f(0,0,0);
		originB = new Vector3f(0, 0, -Stick_Length);
		addSliderConstraint(1,stickLRFSliderBody.body,stickFBSliderBody.body,0,originA,originB,true);
		//添加FB方向的棍与LR方向近端的棍之间的约束
		originA = new Vector3f(0,0,0);
		originB = new Vector3f(0, 0, Stick_Length);
		addSliderConstraint(2,stickLRNSliderBody.body,stickFBSliderBody.body,0,originA,originB,true);
	}
	public void initRigidBody(){ //TODO
		cubeBody = new RigidBodyHelper(boxShape,1f,dynamicsWorld, 0.0f, 0.8f, new Vector3f(0,Ceiling_Height-5,0),true);
		stickFBSliderBody = new RigidBodyHelper(stickFBSliderShape, 1f, dynamicsWorld, 0.0f, 0.8f, new Vector3f(0,Ceiling_Height-5,0),true);
		stickLRFSliderBody = new RigidBodyHelper(stickLRSliderShape, 0, dynamicsWorld, 0.0f, 0.8f, new Vector3f(0,Ceiling_Height-5,-Stick_Length),true);
		stickLRNSliderBody = new RigidBodyHelper(stickLRSliderShape, 0, dynamicsWorld, 0.0f, 0.8f, new Vector3f(0,Ceiling_Height-5,Stick_Length),true);
	}
	public void addSliderConstraint(int index,RigidBody ra,RigidBody rb,float angle,Vector3f originA,Vector3f originB,boolean force){
		Transform localA = new Transform();
		Transform localB = new Transform();
		localA.setIdentity();
		localB.setIdentity();
		MatrixUtil.setEulerZYX(localA.basis, 0,angle, 0 );
		localA.origin.set(originA);
		MatrixUtil.setEulerZYX(localB.basis, 0, angle, 0);
		localB.origin.set(originB);	
		if(index==0){
			sliderFB = new SliderConstraint(ra, rb, localA, localB, force);
			//设置初始的limit
			sliderFB.setLowerLinLimit(-Stick_Length);//控制滑动的最小距离
			sliderFB.setUpperLinLimit(Stick_Length);//控制滑动的最大距离
			sliderFB.setLowerAngLimit(0);
			sliderFB.setUpperAngLimit(0);
			sliderFB.setDampingDirLin(0.05f); //设置线性阻尼
			dynamicsWorld.addConstraint(sliderFB,true);
			sliders[index]=sliderFB;
		}
		if(index==1){
			sliderLRF = new SliderConstraint(ra, rb, localA, localB, force);
			//设置初始的limit
			sliderLRF.setLowerLinLimit(-Stick_Length);//控制滑动的最小距离
			sliderLRF.setUpperLinLimit(Stick_Length);//控制滑动的最大距离
			sliderLRF.setLowerAngLimit(0);
			sliderLRF.setUpperAngLimit(0);
			sliderLRF.setDampingDirLin(0.5f); //设置线性阻尼
			dynamicsWorld.addConstraint(sliderLRF,true);
			sliders[index]=sliderLRF;
		}
		if(index==2){
			sliderLRN = new SliderConstraint(ra, rb, localA, localB, force);
			//设置初始的limit
			sliderLRN.setLowerLinLimit(-Stick_Length);//控制滑动的最小距离
			sliderLRN.setUpperLinLimit(Stick_Length);//控制滑动的最大距离
			sliderLRN.setLowerAngLimit(0);
			sliderLRN.setUpperAngLimit(0);
			sliderLRN.setDampingDirLin(0.5f); //设置线性阻尼
			dynamicsWorld.addConstraint(sliderLRN,true);
			sliders[index]=sliderLRN;
		}
	}
	public void slideFB(float mulFactor){
		sliding=true;
		sliderFB.getRigidBodyB().activate();
		currIndex=0;
		sliderFB.setPoweredLinMotor(true);//设置motor可用
		sliderFB.setMaxLinMotorForce(1.0f);//设置线性运动力的大小
		sliderFB.setTargetLinMotorVelocity(5.0f*mulFactor);//设置线性运动的速度
	}
	public void slideLR(float mulFactor){
		sliding=true;
		sliderLRF.getRigidBodyB().activate();
		currIndex=1;
		sliderLRF.setPoweredLinMotor(true);//设置motor可用
		sliderLRF.setMaxLinMotorForce(5.0f);//设置线性运动力的大小
		sliderLRF.setTargetLinMotorVelocity(5.0f*mulFactor);//设置线性运动的速度
		
		sliderLRN.setPoweredLinMotor(true);//设置motor可用
		sliderLRN.setMaxLinMotorForce(5.0f);//设置线性运动力的大小
		sliderLRN.setTargetLinMotorVelocity(5.0f*mulFactor);//设置线性运动的速度
	}
	public void stopSlide(){
		sliding=false;
		sliders[currIndex].setPoweredLinMotor(false);
		sliders[currIndex].setMaxLinMotorForce(0.0f);//设置线性运动力的大小
		sliders[currIndex].setTargetLinMotorVelocity(0.0f);//设置线性运动的速度
		if(currIndex==1){
			sliders[currIndex+1].setPoweredLinMotor(false);
			sliders[currIndex+1].setMaxLinMotorForce(0.0f);//设置线性运动力的大小
			sliders[currIndex+1].setTargetLinMotorVelocity(0.0f);//设置线性运动的速度
		}
	}
	private class MyRenderer implements GLSurfaceView.Renderer{
		float ratio;
		Cube cube;
		Stick stickFBSliderAxis;
		Stick stickLRFSliderAxis;
		Stick stickLRNSliderAxis;
		int activeTexId;
		int deactiveTexId;
	    int textureArrow;//系统分配的游戏前进虚拟按钮纹理id
		int[] texIds = new int[2];
		TexRect button;//虚拟按钮 
		@Override public void onDrawFrame(GL10 gl) {
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
			//调用此方法计算产生透视投影矩阵   
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1.5f, 100);
			//调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(2,14,2f,0,0,0,0f,0f,-1f);   
            MatrixState.setLightLocationRed(5, 50, 15);
            
			MatrixState.pushMatrix();
			cube.drawSelf(texIds, 1);
			stickFBSliderAxis.drawSelf(90,0,1,0);//绘制纵向轴
			stickLRFSliderAxis.drawSelf(0,1, 0, 0);//绘制远端横向轴
			stickLRNSliderAxis.drawSelf(0,1, 0, 0);//绘制近端横向轴
			MatrixState.popMatrix();
			 //绘制虚拟按钮
            //开启混合
			MatrixState.setCamera(0,0,10,0,0,0,0f,1.0f,0.0f); 
			MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1.5f, 100);
			MatrixState.pushMatrix();
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_BLEND);
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            //绘制按钮
            MatrixState.pushMatrix();
            MatrixState.translate(ratio-0.5f,-(1-0.5f),0);  
            button.drawSelf(textureArrow);
            MatrixState.popMatrix();
            //关闭混合
            GLES20.glDisable(GLES20.GL_BLEND);
            MatrixState.popMatrix();
		}
		@Override public void onSurfaceChanged(GL10 gl, int width, int height) {
			 //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            ratio= (float) width / height;   
            screenWidth= width;
            screenHeight=height;
            buttonPixels=screenHeight/2;
		}
		@Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {//TODO
			  //设置屏幕背景色RGBA
            GLES20.glClearColor(0.0f,0.0f,0.0f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            MatrixState.setInitStack();
            activeTexId = initTexture(R.drawable.wood_bin1);
            textureArrow = initTexture(R.drawable.arrow_small);
            texIds[0]=deactiveTexId;
            texIds[1]=activeTexId;
            cube = new Cube(MySurfaceView.this,cubeSize,cubeBody.body);
            stickFBSliderAxis = new Stick(MySurfaceView.this,Stick_Length,Stick_R,11.25f,
					new float[]{1,0,0,1},
					stickFBSliderBody.body);
            stickLRFSliderAxis = new Stick(MySurfaceView.this,Stick_Length,Stick_R,11.25f,
					new float[]{1,0,0,1},
					stickLRFSliderBody.body);
            stickLRNSliderAxis = new Stick(MySurfaceView.this,Stick_Length,Stick_R,11.25f,
					new float[]{1,0,0,1},
					stickLRNSliderBody.body);
            button = new TexRect(MySurfaceView.this, 0.5f,1f,1f);
            
            new Thread(){
            	public void run(){
            		while(flag){            			
            			try{
            				//模拟
                			dynamicsWorld.stepSimulation(1f/60.f, 5);
							Thread.sleep(20);
						} catch (Exception e){
							e.printStackTrace();
						}
            		}
            	}
            }.start();
            new KeyThread(MySurfaceView.this).start();
		}
	}
	public int initTexture(int drawableId){//textureId
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
}
