//月球着色器
precision mediump float;
varying vec2 vTextureCoord;//接收从顶点着色器过来的参数
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
uniform sampler2D sTexture;//纹理内容数据
void main()                         
{  
  //给此片元从纹理中采样出颜色值            
  vec4 finalColor = texture2D(sTexture, vTextureCoord); 
  //给此片元颜色值 
  gl_FragColor = finalColor*vAmbient+finalColor*vSpecular+finalColor*vDiffuse;
}              