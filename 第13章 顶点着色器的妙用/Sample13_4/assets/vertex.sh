uniform mat4 uMVPMatrix; //总变换矩阵
attribute vec3 aPosition;  //顶点位置
attribute vec3 bPosition;  //顶点位置
attribute vec3 cPosition;  //顶点位置
attribute vec2 aTexCoor;    //顶点纹理坐标
uniform float uBfb;//变化百分比
varying vec2 vTextureCoord;  

void main()     
{ 
 	vec3 tv;         		
   if(uBfb<=1.0)
   {
   		tv=mix(aPosition,bPosition,uBfb);
   }
   else
   {
   		tv=mix(bPosition,cPosition,uBfb-1.0);
   }
   gl_Position = uMVPMatrix * vec4(tv,1);
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      