uniform mat4 uMVPMatrix; //总变换矩阵
attribute vec3 aPosition;  //顶点位置
attribute vec2 aTexCoor;    //顶点纹理坐标
varying vec2 vTextureCoord;  //用于传递给片元着色器的变量
uniform float angleSpan;//扭曲总角度跨度
uniform float yStart;//Y坐标起始点
uniform float ySpan;//Y坐标跨度
void main()     
{
   //计算当前顶点角度跨度
   float tempAS= angleSpan*(aPosition.y-yStart)/ySpan;
   vec3 tPosition=aPosition;
   //若不是最下面一排顶点计算XZ位置
   if(aPosition.y>yStart)
   {
     tPosition.x=(cos(tempAS)*aPosition.x-sin(tempAS)*aPosition.z);
     tPosition.z=(sin(tempAS)*aPosition.x+cos(tempAS)*aPosition.z);
   }
   gl_Position = uMVPMatrix * vec4(tPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      