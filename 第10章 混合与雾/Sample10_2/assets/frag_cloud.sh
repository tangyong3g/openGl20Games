//云层着色器
precision mediump float;
varying vec2 vTextureCoord;//接收从顶点着色器过来的参数
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
uniform sampler2D sTexture;//纹理内容数据
void main()                         
{  
  //给此片元从纹理中采样出颜色值            
  vec4 finalColor = texture2D(sTexture, vTextureCoord); 
  //根据颜色值计算透明度
  finalColor.a=(finalColor.r+finalColor.g+finalColor.b)/3.0;
  //计算光照因素
  finalColor=finalColor*ambient+finalColor*specular+finalColor*diffuse;
  //给此片元颜色值 
  gl_FragColor = finalColor;
}              