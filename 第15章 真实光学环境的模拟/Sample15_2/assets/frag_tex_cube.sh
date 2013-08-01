precision mediump float;
uniform samplerCube sTexture;//纹理内容数据
varying vec3 vTextureCoord; //接收从顶点着色器过来的参数
void main() {
   //将计算出的颜色给此片元
   gl_FragColor=textureCube(sTexture, vTextureCoord);    
}   
