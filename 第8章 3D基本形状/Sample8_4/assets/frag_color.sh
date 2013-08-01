precision mediump float;
varying  vec4 vaColor; //接收从顶点着色器过来的参数
void main()                         
{
	vec4 finalColor =vaColor;
	gl_FragColor = finalColor;//给此片元颜色值
}              