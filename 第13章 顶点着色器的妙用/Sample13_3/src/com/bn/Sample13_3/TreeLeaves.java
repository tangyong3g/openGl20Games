package com.bn.Sample13_3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
/*
 * 用于绘制树叶矩形
 */
public class TreeLeaves 
{	
	int mProgram;//自定义渲染管线着色器程序id   
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用  
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
    
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
    float centerX;//树叶中心点X坐标
    float centerZ;//树叶中心点Z坐标
    int index;//当前树的索引
    
    public TreeLeaves(int mProgram,float width,float height,float absolute_height,int index)
    {    	
    	this.mProgram=mProgram;
    	//初始化顶点坐标与着色数据
    	initVertexData(width,height,absolute_height,index);
    	//初始化shader        
    	intShader();
    	this.index=index;
    }
    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float width,float height,float absolute_height,int index)
    {
        vCount=6;
        float vertices[]=null;//顶点坐标
        float texCoor[]=null;//纹理坐标
        switch(index)//自动生成每片树叶
        {
        case 0://平行于X轴正方向开始,逆时针旋转,每个60度,增加一片树叶
            vertices=new float[]
            {
        		0,height+absolute_height,0,
        		0,absolute_height,0,
        		width,height+absolute_height,0,
            	
        		width,height+absolute_height,0,
        		0,absolute_height,0,
        		width,absolute_height,0,
            };
            texCoor=new float[]
            {
            	1,0, 1,1, 0,0,
            	0,0, 1,1, 0,1
            };
            //确定中心点坐标
            centerX=width/2;
            centerZ=0;
        	break;
        case 1://60度角的树叶
           vertices=new float[]
           {
	       		0,height+absolute_height,0,
	       		0,absolute_height,0,
	       		width/2,height+absolute_height,(float) (-width*Math.sin(Math.PI/3)),
	           	
	       		width/2,height+absolute_height,(float) (-width*Math.sin(Math.PI/3)),
	       		0,absolute_height,0,
	       		width/2,absolute_height,(float) (-width*Math.sin(Math.PI/3))
           };
           texCoor=new float[]
           {
	           	1,0, 1,1, 0,0,
	           	0,0, 1,1, 0,1
           };
           //确定中心点坐标
           centerX=width/4;
           centerZ=(float) (-width*Math.sin(Math.PI/3))/2;
        	break;
        case 2:
        	vertices=new float[]
            {
        		-width/2,height+absolute_height,(float) (-width*Math.sin(Math.PI/3)),
        		-width/2,absolute_height,(float) (-width*Math.sin(Math.PI/3)),
        		0,height+absolute_height,0,
            	
        		0,height+absolute_height,0,
        		-width/2,absolute_height,(float) (-width*Math.sin(Math.PI/3)),
        		0,absolute_height,0,
            };
            texCoor=new float[]
            {
        		0,0, 0,1, 1,0,
            	1,0, 0,1, 1,1
            };
            //确定中心点坐标
            centerX=-width/4;
            centerZ=(float) (-width*Math.sin(Math.PI/3))/2;
        	break;
        case 3:
           vertices=new float[]
           {
	       		-width,height+absolute_height,0,
	       		-width,absolute_height,0,
	       		0,height+absolute_height,0,
	           	
	       		0,height+absolute_height,0,
	       		-width,absolute_height,0,
	       		0,absolute_height,0,
           };
           texCoor=new float[]
           {
	       		0,0, 0,1, 1,0,
	           	1,0, 0,1, 1,1
           };
           //确定中心点坐标
           centerX=-width/2;
           centerZ=0;
        	break;
        case 4:
           vertices=new float[]
           {
	       		-width/2,height+absolute_height,(float) (width*Math.sin(Math.PI/3)),
	       		-width/2,absolute_height,(float) (width*Math.sin(Math.PI/3)),
	       		0,height+absolute_height,0,
	           	
	       		0,height+absolute_height,0,
	       		-width/2,absolute_height,(float) (width*Math.sin(Math.PI/3)),
	       		0,absolute_height,0,
           };
           texCoor=new float[]
           {
	       		0,0, 0,1, 1,0,
	           	1,0, 0,1, 1,1
           };
           //确定中心点坐标
           centerX=-width/4;
           centerZ=(float) (width*Math.sin(Math.PI/3))/2;
           break;
        case 5:
           vertices=new float[]
	       {
		   		0,height+absolute_height,0,
		   		0,absolute_height,0,
		   		width/2,height+absolute_height,(float) (width*Math.sin(Math.PI/3)),
		       	
		   		width/2,height+absolute_height,(float) (width*Math.sin(Math.PI/3)),
		   		0,absolute_height,0,
		   		width/2,absolute_height,(float) (width*Math.sin(Math.PI/3))
	       };
	       texCoor=new float[]
	       {
		       	1,0, 1,1, 0,0,
		       	0,0, 1,1, 0,1
	       };
	       //确定中心点坐标
           centerX=width/4;
           centerZ=(float) (width*Math.sin(Math.PI/3))/2;
        	break;
        }
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
        
        //顶点纹理坐标数据的初始化================begin============================
        //创建顶点纹理坐标数据缓冲
        
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }
    //初始化shader
    public void intShader()
    {
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    public void drawSelf(int texId)
    {        
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mProgram); 
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         //将顶点位置数据传入渲染管线
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //将顶点纹理坐标数据传入渲染管线
         GLES20.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   
         //启用顶点位置、纹理坐标数据
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         
         //绑定纹理
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
         
         //绘制纹理矩形
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
}
