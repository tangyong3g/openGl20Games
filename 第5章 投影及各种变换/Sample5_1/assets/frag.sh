precision mediump float;
varying  vec4 aaColor; //接收从顶点着色器过来的参数
void main()                         
{                       
   gl_FragColor = aaColor;//给此片元颜色值
}              