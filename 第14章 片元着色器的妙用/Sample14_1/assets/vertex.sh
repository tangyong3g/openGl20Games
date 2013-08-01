uniform mat4 uMVPMatrix; //总变换矩阵
attribute vec3 aPosition;  //顶点位置
attribute vec2 aLongLat;   //顶点经纬度
varying vec2 mcLongLat;
void main()     
{                   
   //根据总变换矩阵计算此次绘制此顶点位置         		
   gl_Position = uMVPMatrix * vec4(aPosition,1); 
   //将顶点的经纬度传给片元着色器
   mcLongLat=aLongLat;
}                      