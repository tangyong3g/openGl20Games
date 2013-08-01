precision mediump float;
uniform vec3 uColor;    //顶点颜色
uniform vec3 uPosition;//顶点位置：变换之后的
uniform vec3 uNormal;//法向量：变换之后的
uniform vec3 uLightLocation;//光源位置
uniform vec3 uCamera;	//摄像机位置
uniform int isShadow;			//阴影绘制标志

//定位光光照计算的方法
void pointLight
(
  inout vec4 ambient,//环境光分量
  inout vec4 diffuse,//散射光分量
  inout vec4 specular,//镜面反射光分量  
  in vec4 lightAmbient,//光的环境光分量
  in vec4 lightDiffuse,//光的散射光分量
  in vec4 lightSpecular//光的镜面反射光分量
)
{
  ambient=lightAmbient;			//直接得出环境光的最终强度  
  vec3 newNormal=normalize(uNormal);
  //计算从表面点到摄像机的矢量
  vec3 eye= normalize(uCamera-uPosition);  
  //计算从表面点到光源位置的矢量
  vec3 vp = normalize(uLightLocation-uPosition);
  vec3 halfVector=normalize(vp+eye);	//求视线与光线的半向量    
  float shininess=50.0;				//粗糙度，越小越光滑
  float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//求法向量与vp的点积与0的最大值
  diffuse=lightDiffuse*nDotViewPosition;				//计算散射光的最终强度
  float nDotViewHalfVector=dot(newNormal,halfVector);	//法线与半向量的点积 
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	//镜面反射光强度因子
  specular=lightSpecular*powerFactor;    			//计算镜面光的最终强度
}

void main()
{
   //基本颜色
   vec4 baseColor = vec4(uColor,1.0);
   if(isShadow == 0){ 
   		vec4 ambient, diffuse, specular;
   		pointLight(ambient,diffuse,specular,
   				vec4(0.15,0.15,0.15,1.0),vec4(0.9,0.9,0.9,1.0),vec4(0.7,0.7,0.7,1.0));
   		//给此片元颜色值
   		gl_FragColor=baseColor*ambient + baseColor*diffuse + baseColor*specular;   		
   } else {//如果在阴影中只加入环境光
   		gl_FragColor=baseColor*vec4(0.15,0.15,0.15,1.0);
   }
}