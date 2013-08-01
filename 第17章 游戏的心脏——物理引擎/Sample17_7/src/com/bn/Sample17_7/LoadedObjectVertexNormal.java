package com.bn.Sample17_7;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import android.opengl.GLES20;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;
import com.bulletphysics.linearmath.Transform;

//加载后的物体——仅携带顶点信息，颜色随机
public class LoadedObjectVertexNormal 
{
	int mProgram;//自定义渲染管线程序id 
    int muMVPMatrixHandle;//总变换矩阵引用id   
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maCameraHandle; //摄像机位置属性引用id  
    int maPositionHandle; //顶点位置属性引用id  
    int muColorHandle;
    int maNormalHandle; //顶点法向量属性引用  
    int maLightLocationHandle;//光源位置属性引用  
    
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
	private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer[]   mColorBuffer=new FloatBuffer[2];//顶点着色数据缓冲
    private FloatBuffer mNormalBuffer;
    int vCount=0;
    CollisionShape loadShape;
    float[] vertices;
    float[] normals;
    MySurfaceView mv;
    //==========拾取================
	float midX;//中心点坐标
	float midY;
	float midZ;
	RigidBody body;
    Point2PointConstraint p2p;  
    boolean isPicked=false;
    AABB3 preBox;//仿射变换之前的包围盒
    float[] m = new float[16];//仿射变换的矩阵  
	float[] color=new float[]{1,1,1,1};//顶点颜色
    public LoadedObjectVertexNormal(MySurfaceView mv,float[] vertices,float[] normals) 
    {
    	this.mv=mv;
    	this.vertices=vertices;
    	this.normals=normals;
    	//顶点坐标数据的初始化================begin============================
        vCount=vertices.length/3; 
        
        //初始化包围盒
    	preBox = new AABB3(vertices);
    	
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        
        //顶点着色数据的初始化================begin============================
        float colors[]=new float[vCount*4];//顶点颜色值数组，每个顶点4个色彩值RGBA
        for(int i=0;i<vCount;i++)
        {
        	colors[i*4]=1;//(float)(1*Math.random()); 	
        	colors[i*4+1]=0;//(float)(1*Math.random()); 
        	colors[i*4+2]=0;//(float)(1*Math.random()); 
        	colors[i*4+3]=1; 
        };
        
        //创建顶点着色数据缓冲
        //vertices.length*4是因为一个int型整数四个字节
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer[0] = cbb.asFloatBuffer();//转换为int型缓冲
        mColorBuffer[0].put(colors);//向缓冲区中放入顶点着色数据
        mColorBuffer[0].position(0);//设置缓冲区起始位置
        
        for(int i=0;i<vCount;i++)
        {
        	colors[i*4]=color[0]; 	
        	colors[i*4+1]=color[1]; 
        	colors[i*4+2]=color[2]; 
        	colors[i*4+3]=color[3]; 
        };
        
        //创建顶点着色数据缓冲
        //vertices.length*4是因为一个int型整数四个字节
        cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer[1] = cbb.asFloatBuffer();//转换为int型缓冲
        mColorBuffer[1].put(colors);//向缓冲区中放入顶点着色数据
        mColorBuffer[1].position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================
      //顶点法向量数据的初始化================begin============================  
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        
        //刚体顶点缓冲
    	ByteBuffer gVertices=ByteBuffer.allocateDirect(vCount*3*4).order(ByteOrder.nativeOrder()); 
    	for(int i=0;i<vertices.length;i++)
    	{
    		gVertices.putFloat(i*4,vertices[i]);
    	} 
    	gVertices.position(0);
    	//刚体索引缓冲
    	ByteBuffer gIndices=ByteBuffer.allocateDirect(vCount*4).order(ByteOrder.nativeOrder());
    	for(int i=0;i<vCount;i++)
    	{
    		gIndices.putInt(i);
    	}
    	gIndices.position(0);
    	//创建碰撞三角形组
    	int vertStride = 4*3;
		int indexStride = 4*3;
    	TriangleIndexVertexArray indexVertexArrays= 
		new TriangleIndexVertexArray
		(
			vCount/3,
			gIndices,
			indexStride,
			vCount, 
			gVertices, 
			vertStride
		);
    	//创建曲面形状    	
    	GImpactMeshShape trimesh = new GImpactMeshShape(indexVertexArrays);   
    	trimesh.updateBound();
    	loadShape =trimesh;
    	intShader(mv);
    }
    
    //初始化shader
    public void intShader(MySurfaceView mv)
    {
        //基于顶点着色器与片元着色器创建程序
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_color.sh", mv.getResources());
        ShaderUtil.checkGlError("==ss==");   
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_color.sh", mv.getResources());  
        ShaderUtil.checkGlError("==ss==");      
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中总变换矩阵引用id 
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix"); 
        muColorHandle=GLES20.glGetUniformLocation(mProgram, "aColor");
        //获取程序中顶点法向量属性引用  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中光源位置引用
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera");
    }

    public void drawSelf(RigidBody body) 
    {
    	this.body=body;
    	MatrixState.pushMatrix();
		Transform trans = body.getMotionState().getWorldTransform(new Transform());
		MatrixState.translate(trans.origin.x,trans.origin.y, trans.origin.z);
		Quat4f ro=trans.getRotation(new Quat4f());
		if(ro.x!=0||ro.y!=0||ro.z!=0)
		{
			float[] fa=SYSUtil.fromSYStoAXYZ(ro);
			if(!Float.isInfinite(fa[0])&&!Float.isInfinite(fa[1])&&!Float.isInfinite(fa[2])&&
					!Float.isNaN(fa[0])&&!Float.isNaN(fa[1])&&!Float.isNaN(fa[2])){
				MatrixState.rotate(fa[0],fa[1],fa[2],fa[3]);
			}
    	}
		
		copyM();
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mProgram);
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         //将位置、旋转变换矩阵传入着色器程序
         GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
         //将光源位置传入着色器程序   
         GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         //将摄像机位置传入着色器程序   
         GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         GLES20.glUniform4fv(muColorHandle, 1, color, 0);
         //为画笔指定顶点位置数据    
         GLES20.glVertexAttribPointer        
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4, 
                mVertexBuffer   
         );       
       //将顶点法向量数据传入渲染管线
         GLES20.glVertexAttribPointer 
         (
        		maNormalHandle, 
         		3,   
         		GLES20.GL_FLOAT,
         		false,
                3*4,   
                mNormalBuffer
         );
         //允许顶点位置数据数组
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maNormalHandle);  
           
         //绘制三角形
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
         MatrixState.popMatrix();
    }
    
    public LoadedObjectVertexNormal clone(){
    	return new LoadedObjectVertexNormal(mv,vertices,normals); 
    }
    
    //更改颜色的方法
    public void changeColor(boolean flag)
    {
    	if(body!=null && !body.isActive()&&!isPicked){
    		color=new float[]//蓝绿色
    		{
    			0,1,1
    		};  
    	}else{
    		if(flag){
        		color=new float[]{//绿色
        			0,1,0,1
        		};
        	}
        	else{
        		color=new float[]{//白色
        			1,1,1,1
        		};
        	}
    	}
    }
    public void addPickedConstraint(){
    	p2p = new Point2PointConstraint(body, new Vector3f(0,0,0));
    	mv.dynamicsWorld.addConstraint(p2p, true);
    	this.isPicked=true;
    	
    }
    public void removePickedConstraint(){    	
    	if(p2p!=null){
    		mv.dynamicsWorld.removeConstraint(p2p);
    	}
    	this.isPicked=false;
    }
	//获得中心点位置和长宽高的方法
    public AABB3 getCurrBox(){
    	return preBox.setToTransformedBox(m);//获取变换后的包围盒
    
    }
    //复制变换矩阵
    public void copyM(){
    	for(int i=0;i<16;i++){
    		m[i]=MatrixState.getMMatrix()[i];
    	}
    }
}
