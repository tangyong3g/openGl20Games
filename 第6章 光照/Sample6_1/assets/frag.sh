precision mediump float;
uniform float uR;
varying vec2 mcLongLat;//接收从顶点着色器过来的参数
varying vec3 vPosition;//接收从顶点着色器过来的顶点位置
void main()                         
{
   vec3 color;
   float n = 8.0;//一个坐标分量分的总份数
   float span = 2.0*uR/n;//每一份的长度
   //每一维在立方体内的行列数
   int i = int((vPosition.x + uR)/span);
   int j = int((vPosition.y + uR)/span);
   int k = int((vPosition.z + uR)/span);
   //计算当点应位于白色块还是黑色块中
   int whichColor = int(mod(float(i+j+k),2.0));
   if(whichColor == 1) {//奇数时为红色
   		color = vec3(0.678,0.231,0.129);//红色
   }
   else {//偶数时为白色
   		color = vec3(1.0,1.0,1.0);//白色
   }
   //将计算出的颜色给此片元
   gl_FragColor=vec4(color,0);
}     