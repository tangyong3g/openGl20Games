precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
//接收从顶点着色器过来的参数
varying vec2 vTextureCoord;
void main()                         
{    
   //给此片元颜色值
   gl_FragColor = texture2D(sTexture, vTextureCoord); 

}   