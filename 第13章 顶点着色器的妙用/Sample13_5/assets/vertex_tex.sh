uniform mat4 uMVPMatrix; //总变换矩阵
attribute vec3 aPosition;  //顶点位置
attribute vec2 aTexCoor;    //顶点纹理坐标
varying vec2 vTextureCoord;  //用于传递给片元着色器的变量
uniform float ratio;
void main()     
{       
	float pi = 3.1415926; 
	//-----------------------这里要进行二维扭动----------------
	//中心点的X坐标和Y坐标
	float centerX=0.0;
	float centerY=-5.0;
	//获取当前点的X坐标和Y坐标
	float currX = aPosition.x;
	float currY = aPosition.y;
	//计算X和Y的偏移量
	float spanX = currX - centerX;
	float spanY = currY - centerY;
	//计算极径
	float currRadius = sqrt(spanX * spanX + spanY * spanY);
	//计算当前点的极角
	float currRadians;//用弧度表示
	if(spanX != 0.0)
	{
		currRadians = atan(spanY , spanX);
	}
	else
	{
		currRadians = spanY > 0.0 ? pi/2.0 : 3.0*pi/2.0; 
	}
	//进行扭曲
	float resultRadians = currRadians + ratio*currRadius;
	//计算结果点
	float resultX = centerX + currRadius * cos(resultRadians);
	float resultY = centerY + currRadius * sin(resultRadians);
	//构造结果点
    gl_Position = uMVPMatrix * vec4(resultX,resultY,0.0,1); //根据总变换矩阵计算此次绘制此顶点位置
    vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                       