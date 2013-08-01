precision mediump float;
varying vec2 vTextureCoord; //接收纹理坐标参数
varying float vertexHeight;//接受顶点的高度值
uniform sampler2D sTextureSand;//纹理内容数据   ----沙滩
uniform sampler2D sTextureGrass;//纹理内容数据-----草地
void main()                         
{       
	float height1=15.0;  
	float height2=25.0;    
   vec4 finalSand=texture2D(sTextureSand, vTextureCoord);//沙滩
   vec4 finalGrass=texture2D(sTextureGrass, vTextureCoord);   //草地
   if(vertexHeight<height1)//绘制沙滩
   {
  	  gl_FragColor = finalSand;
   }
   else if(vertexHeight<height2)//绘制草地和沙滩混合层
   {
      float ratio=(vertexHeight-height1)/(height2-height1);	
      finalSand *=(1.0-ratio); 
   	  finalGrass *=ratio;
      gl_FragColor =finalGrass+ finalSand;
   }
   else//绘制草地
   {
      gl_FragColor = finalGrass;
   }
}              