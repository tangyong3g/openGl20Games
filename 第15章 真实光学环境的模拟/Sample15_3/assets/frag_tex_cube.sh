precision mediump float;
uniform samplerCube sTexture;//纹理内容数据
varying vec3 eyeVary;		//接收从顶点着色器过来的视线向量
varying vec3 newNormalVary;	//接收从顶点着色器过来的变换后法向量
vec4 zs(					//根据法向量、视线向量及斯涅尔定律计算立方图纹理采样的方法
  in float zsl				//折射率
){  
  vec3 vTextureCoord=refract(-eyeVary,newNormalVary,zsl);//根据斯涅尔定律计算
  vec4 finalColor=textureCube(sTexture, vTextureCoord);     
  return finalColor;
}
void main(){
   gl_FragColor=zs(0.94); 
}    
