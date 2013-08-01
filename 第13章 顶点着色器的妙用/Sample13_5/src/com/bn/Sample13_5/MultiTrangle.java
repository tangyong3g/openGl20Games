package com.bn.Sample13_5;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;

/*
 * 自动生成三角形组   等边三角形
 * 当前三角形组的最上边点位于原点,并且关于Y轴负方向轴对称
 */
public class MultiTrangle
{
	int program;//自定义渲染管线着色器程序id
    int maPositionHandle;//获取程序中顶点位置属性引用  
    int maTexCoorHandle;//获取程序中顶点纹理坐标属性引用  
    int muMVPMatrixHandle;//获取程序中总变换矩阵引用
    int fuRatioHandle;//三角形的缩放比例id
    
	FloatBuffer fVertexBuffer;//顶点数据buffer
	FloatBuffer fTextureBuffer;//纹理数据buffer
	int vCount;//顶点的个数
	public MultiTrangle(int program,float edgeLength,int levelNum)//程序id,三角形边长,三角形的层数
	{
		this.program=program;
		initVertexData(edgeLength,levelNum);
		initShader();
	}
	//初始化顶点信息
	public void initVertexData(float edgeLength,int levelNum)
	{
		ArrayList<Float> al_vertex=new ArrayList<Float>();//存储顶点信息
		ArrayList<Float> al_texture=new ArrayList<Float>();//存储纹理信息
		float perLength = edgeLength/levelNum;
		for(int i=0;i<levelNum;i++)//每层进行扫描
		{
			//当前层顶端边数
			int currTopEdgeNum=i;
			//当前层底端边数
			int currBottomEdgeNum=i+1;
			//每个三角形的高度
			float currTrangleHeight=(float) (perLength*Math.sin(Math.PI/3));
			//当前层顶端最左边点的坐标
			float topEdgeFirstPointX=-perLength*currTopEdgeNum/2;
			float topEdgeFirstPointY=-i*currTrangleHeight;
			float topEdgeFirstPointZ=0;
			
			//当前层底端最左边点的坐标
			float bottomEdgeFirstPointX=-perLength*currBottomEdgeNum/2;
			float bottomEdgeFirstPointY=-(i+1)*currTrangleHeight;
			float bottomEdgeFirstPointZ=0;
			//---------------纹理----------------
			float horSpan=1/(float)levelNum;//横向纹理的偏移量
			float verSpan=1/(float)levelNum;//纵向纹理的偏移量
			//当前层顶端第一个纹理坐标相关参数问题
			float topFirstS=0.5f-currTopEdgeNum*horSpan/2;
			float topFirstT=i*verSpan;
			//当前层底端第一个纹理坐标的相关参数
			float bottomFirstS=0.5f-currBottomEdgeNum*horSpan/2;
			float bottomFirstT=(i+1)*verSpan;
			//底层三角形卷绕建模
			for(int j=0;j<currBottomEdgeNum;j++)//对每个三角形进行卷绕
			{
				//顶点
				float topX=topEdgeFirstPointX+j*perLength;
				float topY=topEdgeFirstPointY;
				float topZ=topEdgeFirstPointZ;
				float topS=topFirstS+j*horSpan;
				float topT=topFirstT;
				//左下点
				float leftBottomX=bottomEdgeFirstPointX+j*perLength;
				float leftBottomY=bottomEdgeFirstPointY;
				float leftBottomZ=bottomEdgeFirstPointZ;
				float leftBottomS=bottomFirstS+j*horSpan;
				float leftBottomT=bottomFirstT;
				//右下点
				float rightBottomX=leftBottomX+perLength;
				float rightBottomY=bottomEdgeFirstPointY;
				float rightBottomZ=bottomEdgeFirstPointZ;
				float rightBottomS=leftBottomS+horSpan;
				float rightBottomT=leftBottomT;
				//逆时针卷绕----- 纹理绘制方式
				al_vertex.add(topX);al_vertex.add(topY);al_vertex.add(topZ);
				al_vertex.add(leftBottomX);al_vertex.add(leftBottomY);al_vertex.add(leftBottomZ);
				al_vertex.add(rightBottomX);al_vertex.add(rightBottomY);al_vertex.add(rightBottomZ);
				//-------纹理绘制方式
				al_texture.add(topS);al_texture.add(topT);
				al_texture.add(leftBottomS);al_texture.add(leftBottomT);
				al_texture.add(rightBottomS);al_texture.add(rightBottomT);
				
			}
			//顶层三角形卷绕建模
			for(int k=0;k<currTopEdgeNum;k++)
			{
				//左上点
				float leftTopX=topEdgeFirstPointX+k*perLength;
				float leftTopY=topEdgeFirstPointY;
				float leftTopZ=topEdgeFirstPointZ;
				float leftTopS=topFirstS+k*horSpan;
				float leftTopT=topFirstT;
				//底端点
				float bottomX=bottomEdgeFirstPointX+(k+1)*perLength;
				float bottomY=bottomEdgeFirstPointY;
				float bottomZ=bottomEdgeFirstPointZ;
				float bottomS=bottomFirstS+(k+1)*horSpan;
				float bottomT=bottomFirstT;
				//右上点
				float rightTopX=leftTopX+perLength;
				float rightTopY=leftTopY;
				float rightTopZ=leftTopZ;
				float rightTopS=leftTopS+horSpan;
				float rightTopT=topFirstT;
				//逆时针卷绕-----
				al_vertex.add(leftTopX);al_vertex.add(leftTopY);al_vertex.add(leftTopZ);
				al_vertex.add(bottomX);al_vertex.add(bottomY);al_vertex.add(bottomZ);
				al_vertex.add(rightTopX);al_vertex.add(rightTopY);al_vertex.add(rightTopZ);
				
				al_texture.add(leftTopS);al_texture.add(leftTopT);
				al_texture.add(bottomS);al_texture.add(bottomT);
				al_texture.add(rightTopS);al_texture.add(rightTopT);
			}
		}
		//加载进顶点缓冲
		int vertexSize=al_vertex.size();
		vCount=vertexSize/3;//确定顶点的个数
		float vertexs[]=new float[vertexSize];
		for(int i=0;i<vertexSize;i++)
		{
			vertexs[i]=al_vertex.get(i);
		}
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertexSize*4);
		vbb.order(ByteOrder.nativeOrder());
		fVertexBuffer=vbb.asFloatBuffer();
		fVertexBuffer.put(vertexs);
		fVertexBuffer.position(0);
		al_vertex=null;
		//加载进纹理缓冲
		int textureSize=al_texture.size();
		float textures[]=new float[textureSize];
		for(int i=0;i<textureSize;i++)
		{
			textures[i]=al_texture.get(i);
		}
		ByteBuffer tbb=ByteBuffer.allocateDirect(textureSize*4);
		tbb.order(ByteOrder.nativeOrder());
		fTextureBuffer=tbb.asFloatBuffer();
		fTextureBuffer.put(textures);
		fTextureBuffer.position(0);
		al_texture=null;
	}
	//初始化着色器
	public void initShader()
	{
		//获取程序中顶点位置属性引用  
        maPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle= GLES20.glGetAttribLocation(program, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");  
        //获取程序中三角形的缩放比例
        fuRatioHandle = GLES20.glGetUniformLocation(program, "ratio");
	}
	//绘制方法
	public void drawSelf(int texId,float twistingRatio)
	{
		//制定使用某套shader程序
   	 	GLES20.glUseProgram(program); 
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //将缩放比例传入shader程序
        GLES20.glUniform1f(fuRatioHandle, twistingRatio); 
        //将顶点位置数据传入渲染管线
		GLES20.glVertexAttribPointer
		(
			maPositionHandle, 
			3, 
			GLES20.GL_FLOAT, 
			false, 
			3*4, 
			fVertexBuffer
		);
		//将纹理坐标数据传入渲染管线
		GLES20.glVertexAttribPointer
		(
			maTexCoorHandle, 
			2, 
			GLES20.GL_FLOAT, 
			false, 
			2*4, 
			fTextureBuffer
		);
		//启用顶点位置数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}
}
