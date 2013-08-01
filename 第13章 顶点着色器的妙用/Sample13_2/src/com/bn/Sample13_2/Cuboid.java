package com.bn.Sample13_2;
import static com.bn.Sample13_2.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
//代表软糖的长方体
public class Cuboid 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    int uAngleSpanHandle;//扭曲总角度跨度引用id  
    int uYStartHandle;//Y坐标起始引用id
    int uYSpanHandle;//Y坐标跨度引用id
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
    final float Y_MAX=1.5f;
    final float Y_MIN=-1.5f;
    final int FD=6;
    final float hw=0.575f;
    
    float angleSpan=0;
    float angleStep=2f;
    
    public Cuboid(MySurfaceView mv)
    {    	
    	//初始化顶点坐标与着色数据
    	initVertexData();
    	//初始化shader        
    	intShader(mv);
    }
    
    //初始化顶点坐标与着色数据的方法
    public void initVertexData()
    {
    	//顶点坐标数据的初始化================begin============================
        vCount=FD*4*6;

        float vertices[]=new float[vCount*3];
        float texCoor[]=new float[vCount*2];
        float yStart=Y_MIN;
        float ySpan=(Y_MAX-Y_MIN)/FD;
        int count=0;
        int tCount=0;
        for(int i=0;i<FD;i++)
        {
        	float x1=-hw;
        	float y1=yStart;
        	float z1=hw;
        	
        	float x2=hw;
        	float y2=yStart;
        	float z2=hw;
        	
        	float x3=hw;
        	float y3=yStart;
        	float z3=-hw;
        	
        	float x4=-hw;
        	float y4=yStart;
        	float z4=-hw;
        	
        	float x5=-hw;
        	float y5=yStart+ySpan;
        	float z5=hw;
        	
        	float x6=hw;
        	float y6=yStart+ySpan;
        	float z6=hw;
        	
        	float x7=hw;
        	float y7=yStart+ySpan;
        	float z7=-hw;
        	
        	float x8=-hw;
        	float y8=yStart+ySpan;
        	float z8=-hw;
        	//512
        	vertices[count++]=x5;
        	vertices[count++]=y5;
        	vertices[count++]=z5;
        	vertices[count++]=x1;
        	vertices[count++]=y1;
        	vertices[count++]=z1;
        	vertices[count++]=x2;
        	vertices[count++]=y2;
        	vertices[count++]=z2;
        	//526
        	vertices[count++]=x5;
        	vertices[count++]=y5;
        	vertices[count++]=z5;        	
        	vertices[count++]=x2;
        	vertices[count++]=y2;
        	vertices[count++]=z2;
        	vertices[count++]=x6;
        	vertices[count++]=y6;
        	vertices[count++]=z6;
        	//
        	vertices[count++]=x6;
        	vertices[count++]=y6;
        	vertices[count++]=z6;        	
        	vertices[count++]=x2;
        	vertices[count++]=y2;
        	vertices[count++]=z2;
        	vertices[count++]=x3;
        	vertices[count++]=y3;
        	vertices[count++]=z3;
        	
        	vertices[count++]=x6;
        	vertices[count++]=y6;
        	vertices[count++]=z6;        	
        	vertices[count++]=x3;
        	vertices[count++]=y3;
        	vertices[count++]=z3;
        	vertices[count++]=x7;
        	vertices[count++]=y7;
        	vertices[count++]=z7;
        	
        	vertices[count++]=x7;
        	vertices[count++]=y7;
        	vertices[count++]=z7;        	
        	vertices[count++]=x3;
        	vertices[count++]=y3;
        	vertices[count++]=z3;
        	vertices[count++]=x4;
        	vertices[count++]=y4;
        	vertices[count++]=z4;
        	
        	vertices[count++]=x7;
        	vertices[count++]=y7;
        	vertices[count++]=z7;   
        	vertices[count++]=x4;
        	vertices[count++]=y4;
        	vertices[count++]=z4;
        	vertices[count++]=x8;
        	vertices[count++]=y8;
        	vertices[count++]=z8;
        	
        	vertices[count++]=x8;
        	vertices[count++]=y8;
        	vertices[count++]=z8;   
        	vertices[count++]=x4;
        	vertices[count++]=y4;
        	vertices[count++]=z4;
        	vertices[count++]=x1;
        	vertices[count++]=y1;
        	vertices[count++]=z1;
        	
        	vertices[count++]=x8;
        	vertices[count++]=y8;
        	vertices[count++]=z8;           	
        	vertices[count++]=x1;
        	vertices[count++]=y1;
        	vertices[count++]=z1;
        	vertices[count++]=x5;
        	vertices[count++]=y5;
        	vertices[count++]=z5;

        	yStart=yStart+ySpan;
        	
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=0;
        	
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=0;
        	
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=0;
        	
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=0;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=1;
        	texCoor[tCount++]=0;
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
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理坐标数据的初始化================end============================

    }

    //初始化shader
    public void intShader(MySurfaceView mv)
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
        //获取程序中总扭曲角度跨度
        uAngleSpanHandle = GLES20.glGetUniformLocation(mProgram, "angleSpan"); 
        //获取程序中Y坐标起始引用id
        uYStartHandle = GLES20.glGetUniformLocation(mProgram, "yStart");
        //获取程序中Y坐标跨度引用id
        uYSpanHandle = GLES20.glGetUniformLocation(mProgram, "ySpan");
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
         
         angleSpan=(float) (angleSpan+Math.toRadians(angleStep));   
         if(Math.toDegrees(angleSpan)>90) 
         {
        	 angleStep=-2f;      
         }
         else if(Math.toDegrees(angleSpan)<-90)
         {
        	 angleStep=2f;           
         }
         GLES20.glUniform1f(uAngleSpanHandle , angleSpan);
         GLES20.glUniform1f(uYStartHandle , Y_MIN);
         GLES20.glUniform1f(uYSpanHandle , Y_MAX-Y_MIN); 
         try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         //绘制纹理矩形
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 

    }
}
