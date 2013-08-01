precision mediump float;
varying vec4 vPosition;  //接收从顶点着色器过来的参数
uniform highp vec3 uLightLocation;	//光源位置
void main()                         
{   
   float dis=distance(vPosition.xyz,uLightLocation);
   float zsbf=floor(dis);
   float xsbf=fract(dis);
   xsbf=floor(xsbf*1024.0);
   
   float hzsbf=floor(zsbf/256.0);
   float lzsbf=mod(zsbf,256.0);
   float hxsbf=floor(xsbf/32.0);
   float lxsbf=mod(xsbf,32.0);   
   
   float r=hzsbf/256.0;
   float g=lzsbf/256.0;
   float b=hxsbf/32.0;
   float a=lxsbf/32.0;
   
   gl_FragColor=vec4(r,g,b,a);
}    