package com.bn.Sample19_1;
import java.util.ArrayList;

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
	//关于摄像机的变量
	float cx=0;//摄像机x位置
	float cy=0;//摄像机y位置
	float cz=60;//摄像机z位置
	
	float tx=0;//目标点x位置
	float ty=0;//目标点y位置
	float tz=0;//目标点z位置
	public float currSightDis=100;//摄像机和目标的距离
	float angdegElevation=30;//仰角
	public float angdegAzimuth=180;//方位角	
	float left;
    float right;
	float top;
	float bottom;
	float near;
	float far;
	
	//可触控物体列表
	ArrayList<TouchableObject> lovnList=new ArrayList<TouchableObject>();
	//被选中物体的索引值，即id，没有被选中时索引值为-1
	int checkedIndex=-1;
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
			//计算仿射变换后AB两点的位置
			float[] AB=IntersectantUtil.calculateABPosition
			(
				x, //触控点X坐标
				y, //触控点Y坐标
				Sample19_1_Activity.screenWidth, //屏幕宽度
				Sample19_1_Activity.screenHeight, //屏幕长度
				left, //视角left、top值
				top,
				near, //视角near、far值
				far
			);
			//射线AB
			Vector3f start = new Vector3f(AB[0], AB[1], AB[2]);//起点
			Vector3f end = new Vector3f(AB[3], AB[4], AB[5]);//终点
			Vector3f dir = end.minus(start);//长度和方向
			/*
			 * 计算AB线段与每个物体包围盒的最佳交点(与A点最近的交点)，
			 * 并记录有最佳交点的物体在列表中的索引值
			 */
			//记录列表中时间最小的索引值
    		checkedIndex = -1;//标记为没有选中任何物体
    		int tmpIndex=-1;//记录与A点最近物体索引的临时值
    		float minTime=1;//记录列表中所有物体与AB相交的最短时间
    		for(int i=0;i<lovnList.size();i++){//遍历列表中的物体
    			AABB3 box = lovnList.get(i).getCurrBox(); //获得物体AABB包围盒   
				float t = box.rayIntersect(start, dir, null);//计算相交时间
    			if (t <= minTime) {
					minTime = t;//记录最小值
					tmpIndex = i;//记录最小值索引
				}
    		}
    		checkedIndex=tmpIndex;//将索引保存在checkedIndex中    		
    		changeObj(checkedIndex);//改变被选中物体	
       	break;
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            //不超过阈值不移动摄像机
            if(Math.abs(dx)<7f && Math.abs(dy)<7f){
            	break;
            }            
            angdegAzimuth += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
            angdegElevation += dy * TOUCH_SCALE_FACTOR;//设置沿z轴旋转角度
            //将仰角限制在5～90度范围内
            angdegElevation = Math.max(angdegElevation, 5);
            angdegElevation = Math.min(angdegElevation, 90);
            //设置摄像机的位置
            setCameraPostion();
        break;
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
    //设置摄像机位置的方法
	public void setCameraPostion() {
		//计算摄像机的位置
		double angradElevation = Math.toRadians(angdegElevation);//仰角（弧度）
		double angradAzimuth = Math.toRadians(angdegAzimuth);//方位角
		cx = (float) (tx - currSightDis * Math.cos(angradElevation)	* Math.sin(angradAzimuth));
		cy = (float) (ty + currSightDis * Math.sin(angradElevation));
		cz = (float) (tz - currSightDis * Math.cos(angradElevation) * Math.cos(angradAzimuth));
	}
	//改变列表中下标为index的物体
	public void changeObj(int index){
		if(index != -1){//如果有物体被选中
    		for(int i=0;i<lovnList.size();i++){
    			if(i==index){//改变选中的物体
    				lovnList.get(i).changeOnTouch(true);
    			}
    			else{//恢复其他物体
    				lovnList.get(i).changeOnTouch(false);
    			}
    		}
        }
    	else{//如果没有物体被选中
    		for(int i=0;i<lovnList.size();i++){//恢复其他物体			
    			lovnList.get(i).changeOnTouch(false);
    		}
    	}
	}
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormalFace pm;
		LoadedObjectVertexNormalFace cft;
		LoadedObjectVertexNormalAverage qt;
		LoadedObjectVertexNormalAverage yh;
		LoadedObjectVertexNormalAverage ch;
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
			//设置camera位置
			MatrixState.setCamera(cx, cy, cz, tx, ty, tz, 0, 1, 0);
            //初始化光源位置
            MatrixState.setLightLocation(100, 100, 100);                    
            //绘制物体            
            pm.drawSelf();//平面
        	
            //绘制长方体
            MatrixState.pushMatrix();
            MatrixState.translate(-30f, 0f, 0);
            MatrixState.scale(cft.size, cft.size, cft.size);
            cft.drawSelf();
            MatrixState.popMatrix();   
            //绘制球体
            MatrixState.pushMatrix();
            MatrixState.translate(30f, 0f, 0);
            MatrixState.scale(qt.size, qt.size, qt.size);
            qt.drawSelf();
            MatrixState.popMatrix();  
            //绘制圆环
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -30f);
            MatrixState.scale(yh.size, yh.size, yh.size);
            MatrixState.rotate(45, 0, 1, 0);
            yh.drawSelf();
            MatrixState.popMatrix();  
            //绘制茶壶
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, 30f);
            MatrixState.scale(ch.size, ch.size, ch.size);
            MatrixState.rotate(30, 0, 1, 0);
            ch.drawSelf();
            MatrixState.popMatrix(); 
        } 

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            left=right=ratio;
            top=bottom=1;
            near=2;
            far=500;
            MatrixState.setProjectFrustum(-left, right, -bottom, top, near, far);
            //计算摄像机的位置
            setCameraPostion();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.3f,0.3f,0.3f,1.0f);    
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);  
            //关闭背面剪裁
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //加载要绘制的物体
            pm=LoadUtil.loadFromFileVertexOnlyFace("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
                        
            ch=LoadUtil.loadFromFileVertexOnlyAverage("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		cft=LoadUtil.loadFromFileVertexOnlyFace("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		qt=LoadUtil.loadFromFileVertexOnlyAverage("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		yh=LoadUtil.loadFromFileVertexOnlyAverage("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		lovnList.add(ch);
            lovnList.add(cft);
            lovnList.add(qt);
            lovnList.add(yh);
        }
    }
}
