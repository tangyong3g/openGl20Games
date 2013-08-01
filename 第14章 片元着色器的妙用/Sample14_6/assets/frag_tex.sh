precision mediump float;
varying vec2 vTextureCoord;//接收从顶点着色器过来的参数
uniform sampler2D sTexture1;//纹理内容数据1
uniform sampler2D sTexture2;//纹理内容数据2
uniform float uT;
void main() {           
    vec4 color1 = texture2D(sTexture1, vTextureCoord); 	//从纹理中采样出颜色值1  
    vec4 color2 = texture2D(sTexture2, vTextureCoord); 	//从纹理中采样出颜色值2
    gl_FragColor = color1*(1.0-uT) + color2*uT;// 混合两个颜色值
}              