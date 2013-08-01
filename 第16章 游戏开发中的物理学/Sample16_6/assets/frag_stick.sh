precision mediump float;
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;

varying  vec4 aaColor; //接收从顶点着色器过来的参数
void main()                         
{  
  //给此片元从纹理中采样出颜色值            
   vec4 finalColor = vec4(0.0,1.0,0.0,0.0); 
  //给此片元颜色值 
  gl_FragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
}              