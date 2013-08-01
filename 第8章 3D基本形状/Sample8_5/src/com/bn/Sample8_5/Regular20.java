package com.bn.Sample8_5;

import static com.bn.Sample8_5.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import android.opengl.GLES20;

/*
 * 正二十面体
 * 基于三个互相垂直的黄金长方形
 */
public class Regular20 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用
    int muMMatrixHandle;
    
    int maCameraHandle; //摄像机位置属性引用 
    int maNormalHandle; //顶点法向量属性引用 
    int maLightLocationHandle;//光源位置属性引用  
    
    
    String mVertexShader;//顶点着色器代码脚本  	 
    String mFragmentShader;//片元着色器代码脚本
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    int vCount=0;   
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    
    
    float bHalf=0;//黄金长方形的宽
    float r=0;//球的半径
    
    public Regular20(MySurfaceView mv,float scale,float aHalf,int n)
    {
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(scale,aHalf,n);
    	//调用初始化着色器的intShader方法
    	initShader(mv);
    }
    
    //自定义的初始化顶点数据的方法
    public void initVertexData(float scale, float aHalf, int n) //大小，黄金长方形长边的一半，分段数
	{
		aHalf*=scale;		//长边的一半
		bHalf=aHalf*0.618034f;		//短边的一半
		r=(float) Math.sqrt(aHalf*aHalf+bHalf*bHalf);
		vCount=3*20*n*n;//顶点个数，共有20个三角形，每个三角形都有三个顶点
		//正20面体坐标数据初始化
		ArrayList<Float> alVertix20=new ArrayList<Float>();//正20面体的顶点列表（未卷绕）
		ArrayList<Integer> alFaceIndex20=new ArrayList<Integer>();//正20面体组织成面的顶点的索引值列表（按逆时针卷绕）
		//正20面体顶点
		initAlVertix20(alVertix20,aHalf,bHalf);
		//正20面体索引
		initAlFaceIndex20(alFaceIndex20);
		//计算卷绕顶点
		float[] vertices20=VectorUtil.cullVertex(alVertix20, alFaceIndex20);//只计算顶点

		//坐标数据初始化
		ArrayList<Float> alVertix=new ArrayList<Float>();//原顶点列表（未卷绕）
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();//组织成面的顶点的索引值列表（按逆时针卷绕）
		int vnCount=0;//前i-1行前所有顶点数的和
		for(int k=0;k<vertices20.length;k+=9)//对正20面体每个大三角形循环
		{
			float [] v1=new float[]{vertices20[k+0], vertices20[k+1], vertices20[k+2]};
			float [] v2=new float[]{vertices20[k+3], vertices20[k+4], vertices20[k+5]};
			float [] v3=new float[]{vertices20[k+6], vertices20[k+7], vertices20[k+8]};
			//顶点
			for(int i=0;i<=n;i++)
			{
				float[] viStart=VectorUtil.devideBall(r, v1, v2, n, i);
				float[] viEnd=VectorUtil.devideBall(r, v1, v3, n, i);
				for(int j=0;j<=i;j++)
				{
					float[] vi=VectorUtil.devideBall(r, viStart, viEnd, i, j);
					alVertix.add(vi[0]); alVertix.add(vi[1]); alVertix.add(vi[2]);
				}
			}
			//索引
			for(int i=0;i<n;i++)
			{
				if(i==0){//若是第0行，直接加入卷绕后顶点索引012
					alFaceIndex.add(vnCount+0); alFaceIndex.add(vnCount+1);alFaceIndex.add(vnCount+2);
					vnCount+=1;
					if(i==n-1){//如果是每个大三角形的最后一次循环，将下一列的顶点个数也加上
						vnCount+=2;
					}
					continue;
				}
				int iStart=vnCount;//第i行开始的索引
				int viCount=i+1;//第i行顶点数
				int iEnd=iStart+viCount-1;//第i行结束索引
				
				int iStartNext=iStart+viCount;//第i+1行开始的索引
				int viCountNext=viCount+1;//第i+1行顶点数
				int iEndNext=iStartNext+viCountNext-1;//第i+1行结束的索引
				//前面的四边形
				for(int j=0;j<viCount-1;j++)
				{
					int index0=iStart+j;//四边形的四个顶点索引
					int index1=index0+1;
					int index2=iStartNext+j;
					int index3=index2+1;
					alFaceIndex.add(index0); alFaceIndex.add(index2);alFaceIndex.add(index3);//加入前面的四边形
					alFaceIndex.add(index0); alFaceIndex.add(index3);alFaceIndex.add(index1);				
				}// j
				alFaceIndex.add(iEnd); alFaceIndex.add(iEndNext-1);alFaceIndex.add(iEndNext); //最后一个三角形
				vnCount+=viCount;//第i行前所有顶点数的和
				if(i==n-1){//如果是每个大三角形的最后一次循环，将下一列的顶点个数也加上
					vnCount+=viCountNext;
				}
			}// i
		}// k
		
		//计算卷绕顶点
		float[] vertices=VectorUtil.cullVertex(alVertix, alFaceIndex);//只计算顶点
		float[] normals=vertices;//顶点就是法向量
		
		//纹理
		//正20面体纹理坐标数据初始化
		ArrayList<Float> alST20=new ArrayList<Float>();//正20面体的纹理坐标列表（未卷绕）
		ArrayList<Integer> alTexIndex20=new ArrayList<Integer>();//正20面体组织成面的纹理坐标的索引值列表（按逆时针卷绕）
		//正20面体纹理坐标
		float sSpan=1/5.5f;//每个纹理三角形的边长
		float tSpan=1/3.0f;//每个纹理三角形的高
		//按正二十面体的平面展开图计算纹理坐标
		for(int i=0;i<5;i++){
			alST20.add(sSpan+sSpan*i); alST20.add(0f);
		}
		for(int i=0;i<6;i++){
			alST20.add(sSpan/2+sSpan*i); alST20.add(tSpan);
		}
		for(int i=0;i<6;i++){
			alST20.add(sSpan*i); alST20.add(tSpan*2);
		}
		for(int i=0;i<5;i++){
			alST20.add(sSpan/2+sSpan*i); alST20.add(tSpan*3);
		}
		//正20面体索引
		initAlTexIndex20(alTexIndex20);

		//计算卷绕纹理坐标
		float[] st20=VectorUtil.cullTexCoor(alST20, alTexIndex20);//只计算纹理坐标
		ArrayList<Float> alST=new ArrayList<Float>();//原纹理坐标列表（未卷绕）
		for(int k=0;k<st20.length;k+=6)
		{
			float [] st1=new float[]{st20[k+0], st20[k+1], 0};//三角形的纹理坐标
			float [] st2=new float[]{st20[k+2], st20[k+3], 0};
			float [] st3=new float[]{st20[k+4], st20[k+5], 0};
			for(int i=0;i<=n;i++)
			{
				float[] stiStart=VectorUtil.devideLine(st1, st2, n, i);
				float[] stiEnd=VectorUtil.devideLine(st1, st3, n, i);
				for(int j=0;j<=i;j++)
				{
					float[] sti=VectorUtil.devideLine(stiStart, stiEnd, i, j);
					//将纹理坐标加入列表
					alST.add(sti[0]); alST.add(sti[1]);
				}
			}
		}
		//计算卷绕后纹理坐标
		float[] textures=VectorUtil.cullTexCoor(alST, alFaceIndex);
		
		//顶点坐标数据初始化
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //法向量数据初始化  
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);//创建顶点法向量数据缓冲
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //st坐标数据初始化		
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);//创建顶点纹理数据缓冲
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTexCoorBuffer = tbb.asFloatBuffer();//转换为float型缓冲
        mTexCoorBuffer.put(textures);//向缓冲区中放入顶点纹理数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
	}
    
    public void initAlVertix20(ArrayList<Float> alVertix20,float aHalf,float bHalf){
    	
		alVertix20.add(0f); alVertix20.add(aHalf); alVertix20.add(-bHalf);//顶正棱锥顶点
		
		alVertix20.add(0f); alVertix20.add(aHalf); alVertix20.add(bHalf);//棱柱上的点
		alVertix20.add(aHalf); alVertix20.add(bHalf); alVertix20.add(0f);
		alVertix20.add(bHalf); alVertix20.add(0f); alVertix20.add(-aHalf);
		alVertix20.add(-bHalf); alVertix20.add(0f); alVertix20.add(-aHalf);
		alVertix20.add(-aHalf); alVertix20.add(bHalf); alVertix20.add(0f);
		
		alVertix20.add(-bHalf); alVertix20.add(0f); alVertix20.add(aHalf);
		alVertix20.add(bHalf); alVertix20.add(0f); alVertix20.add(aHalf);
		alVertix20.add(aHalf); alVertix20.add(-bHalf); alVertix20.add(0f);
		alVertix20.add(0f); alVertix20.add(-aHalf); alVertix20.add(-bHalf);
		alVertix20.add(-aHalf); alVertix20.add(-bHalf); alVertix20.add(0f);
		
		alVertix20.add(0f); alVertix20.add(-aHalf); alVertix20.add(bHalf);//底棱锥顶点
		
    }
    
    public void initAlFaceIndex20(ArrayList<Integer> alFaceIndex20){ //初始化正二十面体的顶点索引数据
    	
		alFaceIndex20.add(0); alFaceIndex20.add(1); alFaceIndex20.add(2);
		alFaceIndex20.add(0); alFaceIndex20.add(2); alFaceIndex20.add(3);
		alFaceIndex20.add(0); alFaceIndex20.add(3); alFaceIndex20.add(4);
		alFaceIndex20.add(0); alFaceIndex20.add(4); alFaceIndex20.add(5);
		alFaceIndex20.add(0); alFaceIndex20.add(5); alFaceIndex20.add(1);
		
		alFaceIndex20.add(1); alFaceIndex20.add(6); alFaceIndex20.add(7);
		alFaceIndex20.add(1); alFaceIndex20.add(7); alFaceIndex20.add(2);
		alFaceIndex20.add(2); alFaceIndex20.add(7); alFaceIndex20.add(8);
		alFaceIndex20.add(2); alFaceIndex20.add(8); alFaceIndex20.add(3);
		alFaceIndex20.add(3); alFaceIndex20.add(8); alFaceIndex20.add(9);
		alFaceIndex20.add(3); alFaceIndex20.add(9); alFaceIndex20.add(4);
		alFaceIndex20.add(4); alFaceIndex20.add(9); alFaceIndex20.add(10);
		alFaceIndex20.add(4); alFaceIndex20.add(10); alFaceIndex20.add(5);
		alFaceIndex20.add(5); alFaceIndex20.add(10); alFaceIndex20.add(6);
		alFaceIndex20.add(5); alFaceIndex20.add(6); alFaceIndex20.add(1);
		
		alFaceIndex20.add(6); alFaceIndex20.add(11); alFaceIndex20.add(7);
		alFaceIndex20.add(7); alFaceIndex20.add(11); alFaceIndex20.add(8);
		alFaceIndex20.add(8); alFaceIndex20.add(11); alFaceIndex20.add(9);
		alFaceIndex20.add(9); alFaceIndex20.add(11); alFaceIndex20.add(10);
		alFaceIndex20.add(10); alFaceIndex20.add(11); alFaceIndex20.add(6);
    }
    public void initAlTexIndex20(ArrayList<Integer> alTexIndex20) //初始化顶点纹理索引数据
    {
		alTexIndex20.add(0); alTexIndex20.add(5); alTexIndex20.add(6);
		alTexIndex20.add(1); alTexIndex20.add(6); alTexIndex20.add(7);
		alTexIndex20.add(2); alTexIndex20.add(7); alTexIndex20.add(8);
		alTexIndex20.add(3); alTexIndex20.add(8); alTexIndex20.add(9);
		alTexIndex20.add(4); alTexIndex20.add(9); alTexIndex20.add(10);
		
		alTexIndex20.add(5); alTexIndex20.add(11); alTexIndex20.add(12);
		alTexIndex20.add(5); alTexIndex20.add(12); alTexIndex20.add(6);
		alTexIndex20.add(6); alTexIndex20.add(12); alTexIndex20.add(13);
		alTexIndex20.add(6); alTexIndex20.add(13); alTexIndex20.add(7);
		alTexIndex20.add(7); alTexIndex20.add(13); alTexIndex20.add(14);
		alTexIndex20.add(7); alTexIndex20.add(14); alTexIndex20.add(8);
		alTexIndex20.add(8); alTexIndex20.add(14); alTexIndex20.add(15);
		alTexIndex20.add(8); alTexIndex20.add(15); alTexIndex20.add(9);
		alTexIndex20.add(9); alTexIndex20.add(15); alTexIndex20.add(16);
		alTexIndex20.add(9); alTexIndex20.add(16); alTexIndex20.add(10);
		
		alTexIndex20.add(11); alTexIndex20.add(17); alTexIndex20.add(12);
		alTexIndex20.add(12); alTexIndex20.add(18); alTexIndex20.add(13);
		alTexIndex20.add(13); alTexIndex20.add(19); alTexIndex20.add(14);
		alTexIndex20.add(14); alTexIndex20.add(20); alTexIndex20.add(15);
		alTexIndex20.add(15); alTexIndex20.add(21); alTexIndex20.add(16);
    	
    }
    
    
    
    
    
    

    //自定义初始化着色器initShader方法
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex_light.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex_light.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
        
        
        //获取程序中顶点法向量属性引用id  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal"); 
        //获取程序中摄像机位置引用id
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
        //获取程序中光源位置引用id
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation"); 
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
        
        
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
         
         //将位置、旋转变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
         //将摄像机位置传入shader程序   
         GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //将光源位置传入shader程序   
         GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         
         
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
         //传送顶点法向量数据
         GLES20.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		4, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         ); 
         
         //启用顶点位置数据
         GLES20.glEnableVertexAttribArray(maPositionHandle);
         //启用顶点纹理数据
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         //启用顶点法向量数据
         GLES20.glEnableVertexAttribArray(maNormalHandle);
         
         
         //绑定纹理
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
         
         //绘制纹理矩形
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
}
