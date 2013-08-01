precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
varying vec2 vTextureCoord; //接收从顶点着色器过来的参数
varying vec4 vambient;
varying vec4 vdiffuse;
varying vec4 vspecular;
void main()                         
{
   //将计算出的颜色给此片元
   vec4 finalColor=texture2D(sTexture, vTextureCoord);
   //给此片元颜色值 
   gl_FragColor = finalColor*vambient+finalColor*vspecular+finalColor*vdiffuse;//给此片元颜色值
}              