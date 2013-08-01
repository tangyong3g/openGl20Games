precision mediump float;
uniform samplerCube sTexture;//纹理内容数据
varying vec3 eyeVary;		//接收从顶点着色器过来的视线向量
varying vec3 newNormalVary;	//接收从顶点着色器过来的变换后法向量
vec4 zs(					//根据法向量、视线向量及斯涅尔定律计算立方图纹理采样的方法
  in float zsl				//折射率
){  
  vec3 vTextureCoord=refract(-eyeVary,newNormalVary,zsl);//根据斯涅尔定律计算
  vec4 finalColor=textureCube(sTexture, vTextureCoord);  //进行立方图纹理采样     
  return finalColor;
}
void main(){
   vec4 finalColor=vec4(0.0,0.0,0.0,0.0);
   //由于有色散RGB三个色彩通道单独计算折射
   finalColor.r=zs(0.97).r;  
   finalColor.g=zs(0.955).g;  
   finalColor.b=zs(0.94).b;  
   gl_FragColor=finalColor; 
}    
