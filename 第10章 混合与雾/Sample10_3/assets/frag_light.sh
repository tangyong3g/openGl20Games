precision mediump float;
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
varying float vFogFactor; //雾因子
void main()                         
{
	vec4 objectColor=vec4(0.95,0.95,0.95,1.0);//物体颜色	
	vec4 fogColor = vec4(0.97,0.76,0.03,1.0);//雾的颜色	
 	if(vFogFactor != 0.0){//如果雾因子为0，不必计算光照
		objectColor = objectColor*ambient+objectColor*specular+objectColor*diffuse;//计算光照之后物体颜色
		gl_FragColor = objectColor*vFogFactor + fogColor*(1.0-vFogFactor);//物体颜色和雾颜色插值计算最终颜色
	}else{
 	    gl_FragColor=fogColor;
 	}
}