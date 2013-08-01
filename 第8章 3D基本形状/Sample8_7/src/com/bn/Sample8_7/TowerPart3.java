package com.bn.Sample8_7;

import static com.bn.Sample8_7.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;

/*
 * 泰姬陵顶部组建3
 */
public class TowerPart3 {	
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
    
    float scale;
    
    public TowerPart3(MySurfaceView mv,float scale, int nCol ,int nRow)
    {
    	this.scale=scale;
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(scale,nCol,nRow);
    	//调用初始化着色器的intShader方法
    	initShader(mv);
    }
    
    //自定义的初始化顶点数据的方法
    public void initVertexData(float scale, int nCol ,int nRow //大小，列数，行数（行数要保证可以被1除尽）
			){
		//成员变量初始化
		float angdegSpan=360.0f/nCol;
		vCount=3*nCol*nRow*2;//顶点个数，共有nColumn*nRow*2个三角形，每个三角形都有三个顶点
		//坐标数据初始化
		ArrayList<Float> alVertix=new ArrayList<Float>();//原顶点列表（未卷绕）
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();//组织成面的顶点的索引值列表（按逆时针卷绕）
		
		//以下是贝赛尔曲线的实现代码
		BezierUtil.al.clear();//清空数据点列表

		//加入数据点
		BezierUtil.al.add(new BNPosition(87, 22));
		BezierUtil.al.add(new BNPosition(83, 229));
		BezierUtil.al.add(new BNPosition(77, 226));
		BezierUtil.al.add(new BNPosition(72, 205));
		BezierUtil.al.add(new BNPosition(75, 233));
		BezierUtil.al.add(new BNPosition(137, 240));
		BezierUtil.al.add(new BNPosition(94, 212));
		BezierUtil.al.add(new BNPosition(65, 248));
		BezierUtil.al.add(new BNPosition(78, 245));
		
		
		//通过数据点，获取贝赛尔曲线上的点的列表
		ArrayList<BNPosition> alCurve=BezierUtil.getBezierData(1.0f/nRow);
		//顶点
		for(int i=0;i<nRow+1;i++)
		{
			double r=alCurve.get(i).x*Constant.DATA_RATIO*scale;//当前圆的半径
			float y=alCurve.get(i).y*Constant.DATA_RATIO*scale;//当前y值
			for(float angdeg=0;Math.ceil(angdeg)<360+angdegSpan;angdeg+=angdegSpan)//重复了一列顶点，方便了索引的计算
			{
				double angrad=Math.toRadians(angdeg);//当前列弧度
				float x=(float) (-r*Math.sin(angrad));
				float z=(float) (-r*Math.cos(angrad));
				//将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
				alVertix.add(x); alVertix.add(y); alVertix.add(z);
			}
		}
		//索引
		for(int i=0;i<nRow;i++){
			for(int j=0;j<nCol;j++){
				int index=i*(nCol+1)+j;//当前索引
				//卷绕索引
				alFaceIndex.add(index+1);//下一列---1
				alFaceIndex.add(index+nCol+2);//下一行下一列---3
				alFaceIndex.add(index+nCol+1);//下一列---2
				
				alFaceIndex.add(index+1);//下一列---1
				alFaceIndex.add(index+nCol+1);//下一列---2
				alFaceIndex.add(index);//当前---0
			}
		}
		//计算卷绕顶点
		float[] vertices=new float[vCount*3];
		vertices=VectorUtil.calVertices(alVertix, alFaceIndex);
		
		//顶点坐标数据初始化
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//创建顶点坐标数据缓冲
		vbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
		mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);//设置缓冲区起始位置
        
		//纹理
		ArrayList<Float> alST=new ArrayList<Float>();//原顶点列表（未卷绕）

		float yMin=999999999;//y最小值
		float yMax=0;//y最大值
		for(BNPosition pos:alCurve){
			yMin=Math.min(yMin, pos.y);//y最小值
			yMax=Math.max(yMax, pos.y);//y最大值
		}
		for(int i=0;i<nRow+1;i++)
		{
			float y=alCurve.get(i).y;//当前y值
			float t=1-(y-yMin)/(yMax-yMin);//t坐标
			for(float angdeg=0;Math.ceil(angdeg)<360+angdegSpan;angdeg+=angdegSpan)//重复了一列纹理坐标，以索引的计算
			{
				float s=angdeg/360;//s坐标
				//将计算出来的ST坐标加入存放顶点坐标的ArrayList
				alST.add(s); alST.add(t);
			}
		}
		//计算卷绕后纹理坐标
		float[] textures=VectorUtil.calTextures(alST, alFaceIndex);
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);//创建顶点纹理数据缓冲
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = tbb.asFloatBuffer();//转换为float型缓冲
        mTexCoorBuffer.put(textures);//向缓冲区中放入顶点纹理数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
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
    }
}
