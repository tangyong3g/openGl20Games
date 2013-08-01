precision mediump float;				//给出默认浮点精度
uniform sampler2D sTexture;			//纹理内容数据
uniform highp vec3 uLightLocation;		//光源位置
uniform highp mat4 uMVPMatrixGY; 	//总变换矩阵(光源)
varying vec4 ambient;				//接收从顶点着色器传来的环境光最终强度
varying vec4 diffuse; 				//接收从顶点着色器传来的散射光最终强度
varying vec4 specular; 				//接收从顶点着色器传来的镜面光最终强度
varying vec4 vPosition;  				//接收从顶点着色器传来的变换后顶点位置
void main(){   
   //将片元的位置投影到光源处虚拟摄像机的近平面上
   vec4 gytyPosition=uMVPMatrixGY * vec4(vPosition.xyz,1);
   gytyPosition=gytyPosition/gytyPosition.w;  		//进行透视除法
   float s=(gytyPosition.s+1.0)/2.0; 				//将投影后的坐标换算为纹理坐标
   float t=(gytyPosition.t+1.0)/2.0; 
   vec4 depth4=texture2D(sTexture, vec2(s,t)); 	//对投影纹理(距离纹理)图进行采样
   //将采样出的颜色值换算成最小距离值ZA
   float minDis=depth4.r*256.0*256.0+depth4.g*256.0+depth4.b+depth4.a/32.0;   
   float currDis=distance(vPosition.xyz,uLightLocation);	//计算光源到此片元的距离ZB
   vec4 finalColor=vec4(0.95,0.95,0.95,1.0); 			//物体的颜色
   if(s>=0.0&&s<=1.0&&t>=0.0&&t<=1.0) { 		//若纹理坐标在合法范围内则考虑投影贴图
   		if(minDis<=currDis-3.0) {				//若实际距离大于最小距离则在阴影中
   			gl_FragColor= finalColor*ambient*1.2;	//仅用环境光着色
   		} else{//不在阴影中用三个通道光照着色
   			gl_FragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
   		}
   } else{ 	//不在阴影中用三个通道光照着色
         gl_FragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
   }
}        
