precision mediump float;
varying  vec4 vaaColor; //接收从顶点着色器过来的参数
varying vec4 vambient;
varying vec4 vdiffuse;
varying vec4 vspecular;
void main()                         
{
   //将颜色给此片元
	vec4 finalColor = vaaColor;
   //给此片元颜色值 
   gl_FragColor = finalColor*vambient+finalColor*vspecular+finalColor*vdiffuse;//给此片元颜色值
}              