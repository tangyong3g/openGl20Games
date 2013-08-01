package com.bn.Sample8_4;

import static com.bn.Sample8_4.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import android.opengl.GLES20;

/*
 * 圆环
 */
public class Spring 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用
    
    String mVertexShader;//顶点着色器代码脚本  	 
    String mFragmentShader;//片元着色器代码脚本
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲

    int vCount=0;   
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度

    float h;
    
    public Spring(MySurfaceView mv,
    		float rBig, float rSmall,
    		float h, float nCirclef, //高度，圈数
    		int nCol ,int nRow)
    {
    	this.h=h;
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(rBig,rSmall,h,nCirclef,nCol,nRow);
    	//调用初始化着色器的intShader方法
    	initShader(mv);
    }
    
    //自定义的初始化顶点数据的方法
    public void initVertexData(
			float rBig, float rSmall,//螺旋管外径、螺旋管内径
			float h, float nCirclef, //螺旋管高度，螺旋管圈数
			int nCol ,int nRow) {//小圆周和大圆周切分的份数
		float angdegTotal=nCirclef*360.0f;//大圆周总度数
		float angdegColSpan=360.0f/nCol;//小圆周每份的角度跨度
		float angdegRowSpan=angdegTotal/nRow;//大圆周每份的角度跨度
		float A=(rBig-rSmall)/2;//用于旋转的小圆半径
		float D=rSmall+A;//旋转轨迹形成的大圆周半径
		vCount=3*nCol*nRow*2;//顶点个数，共有nColumn*nRow*2个三角形，每个三角形都有三个顶点
		//坐标数据初始化
		ArrayList<Float> alVertix=new ArrayList<Float>();//原顶点列表（未卷绕）
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();//组织成面的顶点的索引值列表（按逆时针卷绕）		
		//顶点
		for(float angdegCol=0;Math.ceil(angdegCol)<360+angdegColSpan;angdegCol+=angdegColSpan)
		{
			double a=Math.toRadians(angdegCol);//当前小圆周弧度
			for(float angdegRow=0;Math.ceil(angdegRow)<angdegTotal+angdegRowSpan;angdegRow+=angdegRowSpan)//重复了一列顶点，方便了索引的计算
			{
				float yVec=(angdegRow/angdegTotal)*h;//根据旋转角度增加y的值
				double u=Math.toRadians(angdegRow);//当前大圆周弧度
				float y=(float) (A*Math.cos(a));
				float x=(float) ((D+A*Math.sin(a))*Math.sin(u));
				float z=(float) ((D+A*Math.sin(a))*Math.cos(u));
				//将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
        		alVertix.add(x); alVertix.add(y+yVec); alVertix.add(z);
			}
		}
		//索引
		for(int i=0;i<nCol;i++){
			for(int j=0;j<nRow;j++){
				int index=i*(nRow+1)+j;//当前索引
				//卷绕索引
				alFaceIndex.add(index+1);//下一列---1
				alFaceIndex.add(index+nRow+1);//下一列---2
				alFaceIndex.add(index+nRow+2);//下一行下一列---3
				
				alFaceIndex.add(index+1);//下一列---1
				alFaceIndex.add(index);//当前---0
				alFaceIndex.add(index+nRow+1);//下一列---2
			}
		}
		//计算卷绕顶点和平均法向量
		float[] vertices=new float[vCount*3];
		cullVertex(alVertix, alFaceIndex, vertices);
		
		//纹理
		ArrayList<Float> alST=new ArrayList<Float>();//原纹理坐标列表（未卷绕）
		for(float angdegCol=0;Math.ceil(angdegCol)<360+angdegColSpan;angdegCol+=angdegColSpan)
		{
			float t=angdegCol/360;//t坐标
			for(float angdegRow=0;Math.ceil(angdegRow)<angdegTotal+angdegRowSpan;angdegRow+=angdegRowSpan)//重复了一列纹理坐标，以索引的计算
			{
				float s=angdegRow/angdegTotal;//s坐标
				//将计算出来的ST坐标加入存放顶点坐标的ArrayList
				alST.add(s); alST.add(t);
			}
		}
		//计算卷绕后纹理坐标
		float[] textures=cullTexCoor(alST, alFaceIndex);
		
		//顶点坐标数据初始化
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        //纹理坐标数据初始化		
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);//创建顶点纹理数据缓冲
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = tbb.asFloatBuffer();//转换为float型缓冲
        mTexCoorBuffer.put(textures);//向缓冲区中放入顶点纹理数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
	}
    
	//通过原顶点和面的索引值，得到用顶点卷绕的数组
	public static void cullVertex(
			ArrayList<Float> alv,//原顶点列表（未卷绕）
			ArrayList<Integer> alFaceIndex,//组织成面的顶点的索引值列表（按逆时针卷绕）
			float[] vertices//用顶点卷绕的数组（顶点结果放入该数组中，数组长度应等于索引列表长度的3倍）
		){
		//生成顶点的数组
		int vCount=0;
		for(int i:alFaceIndex){
			vertices[vCount++]=alv.get(3*i);
			vertices[vCount++]=alv.get(3*i+1);
			vertices[vCount++]=alv.get(3*i+2);
		}
	}
	//根据原纹理坐标和索引，计算卷绕后的纹理的方法
	public static float[] cullTexCoor(
			ArrayList<Float> alST,//原纹理坐标列表（未卷绕）
			ArrayList<Integer> alTexIndex//组织成面的纹理坐标的索引值列表（按逆时针卷绕）
			)
	{
		float[] textures=new float[alTexIndex.size()*2];
		//生成顶点的数组
		int stCount=0;
		for(int i:alTexIndex){
			textures[stCount++]=alST.get(2*i);
			textures[stCount++]=alST.get(2*i+1);
		}
		return textures;
	}

    //自定义初始化着色器initShader方法
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
    }
    
    public void drawSelf(int texId)
    {
	   	 MatrixState.rotate(xAngle, 1, 0, 0);
		 MatrixState.rotate(yAngle, 0, 1, 0);
		 MatrixState.rotate(zAngle, 0, 0, 1);
		 
		 MatrixState.pushMatrix();
		 MatrixState.translate(0, -h/2, 0);   	 
    	
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mProgram);        
         
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         
         //传送顶点位置数据
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //传送顶点纹理坐标数据
         GLES20.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         ); 
         
         //启用顶点位置数据
         GLES20.glEnableVertexAttribArray(maPositionHandle);
         //启用顶点纹理数据
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         
         //绑定纹理
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
         
         //绘制纹理矩形
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
         
         MatrixState.popMatrix();
         
    }
}
