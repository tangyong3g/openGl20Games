//焰火粒子着色器
precision mediump float;
uniform vec3 uColor;//粒子颜色
void main()                         
{
  //给此片元颜色值 
  gl_FragColor = vec4(uColor,1.0);
}              