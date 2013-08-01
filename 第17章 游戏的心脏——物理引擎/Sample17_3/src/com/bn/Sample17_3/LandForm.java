package com.bn.Sample17_3;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;

public class LandForm {
	
	int mProgram;//自定义渲染管线程序id 
    int muMVPMatrixHandle;//总变换矩阵引用id   
    int muMMatrixHandle;//位置、旋转变换矩阵
    int uTexHandle;//外观纹理属性引用id
    
    int maCameraHandle; //摄像机位置属性引用id  
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
	private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer   mTextureBuffer;//顶点着色数据缓冲
    
    int vCount;
    float yOffset;
    
    public LandForm(MySurfaceView mv,final float UNIT_SIZE, float yOffset,
    		DiscreteDynamicsWorld dynamicsWorld,
    		float[][] yArray,int rows,int cols,int mProgram)
    {	
    	this.mProgram=mProgram;
		//顶点坐标数据的初始化================begin============================
        vCount=cols*rows*2*3;//每个格子两个三角形，每个三角形3个顶点        
        float vertices[]=new float[vCount*3];//每个顶点xyz三个坐标
        int count=0;//顶点计数器
        for(int j=0;j<rows;j++)
        {
        	for(int i=0;i<cols;i++)
        	{        		
        		//计算当前格子左上侧点坐标 
        		float zsx=-UNIT_SIZE*cols/2+i*UNIT_SIZE;
        		float zsz=-UNIT_SIZE*rows/2+j*UNIT_SIZE;        		
        		
        		vertices[count++]=zsx;
        		vertices[count++]=yArray[j][i]+yOffset;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx;
        		vertices[count++]=yArray[j+1][i]+yOffset;
        		vertices[count++]=zsz+UNIT_SIZE;
        		
        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=yArray[j][i+1]+yOffset;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=yArray[j][i+1]+yOffset;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx;
        		vertices[count++]=yArray[j+1][i]+yOffset;
        		vertices[count++]=zsz+UNIT_SIZE;
        		
        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=yArray[j+1][i+1]+yOffset;
        		vertices[count++]=zsz+UNIT_SIZE;
        	}
        }
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        
        //顶点纹理数据的初始化================begin============================
    	//自动生成纹理数组，20列15行
    	float textures[]=generateTexCoor(cols,rows);
        
        //创建顶点纹理数据缓冲
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理数据的初始化================end============================
        
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
    	CollisionShape groundShape=new BvhTriangleMeshShape(indexVertexArrays,true,true);
        
        //创建刚体的初始变换对象
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(0.f, 0.f, 0.f));		
		Vector3f localInertia = new Vector3f(0, 0, 0);		
		//创建刚体的运动状态对象
		DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
		//创建刚体信息对象
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, myMotionState, groundShape, localInertia);
		//创建刚体
		RigidBody body = new RigidBody(rbInfo);
		//设置反弹系数
		body.setRestitution(0.4f);
		//设置摩擦系数
		body.setFriction(0.8f);
		body.setCollisionFlags(body.getCollisionFlags() & ~CollisionFlags.KINEMATIC_OBJECT);
		body.forceActivationState(CollisionObject.ACTIVE_TAG);
		//将刚体添加进物理世界
		dynamicsWorld.addRigidBody(body);
		
		initShader(mv);
    }

  //初始化shader
    public void initShader(MySurfaceView mv)
    {
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点经纬度属性引用id   
        maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");  
        //获取程序中总变换矩阵引用id 
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");   
        uTexHandle=GLES20.glGetUniformLocation(mProgram, "sTexture"); 
    }
    
    public void drawSelf(int texId) 
    {        
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mProgram);
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         
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
         //为画笔指定顶点纹理坐标数据
         GLES20.glVertexAttribPointer  
         (  
        		maTexCoorHandle,  
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTextureBuffer
         );   
         //允许顶点位置数据数组
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         //绑定纹理
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);    
         GLES20.glUniform1i(uTexHandle, 0);
           
         //绘制三角形
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
    
    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=16.0f/bw;//列数
    	float sizeh=16.0f/bh;//行数
    	int c=0;
    	for(int i=0;i<bh;i++)
    	{
    		for(int j=0;j<bw;j++)
    		{
    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
    			float s=j*sizew;
    			float t=i*sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;    			
    		}
    	}
    	return result;
    }
}
