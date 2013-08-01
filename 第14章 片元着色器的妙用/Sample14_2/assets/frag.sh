precision mediump float;
varying vec4 vAmbient;//接收从顶点着色器过来的环境光分量
varying vec4 vDiffuse;//接收从顶点着色器过来的散射光分量
varying vec4 vSpecular;//接收从顶点着色器过来的镜面反射光分量
varying vec2 mcLongLat;//接收从顶点着色器过来的参数
void main() {
   vec3 color;
   if(abs(mcLongLat.y)>75.0){
   	  color = vec3(1.0,1.0,1.0);//两边是白色
   }else{
      int colorNum = int(mcLongLat.x/45.0);//颜色号
      if(colorNum == 0){
      	color = vec3(1.0,0.0,0.0);//0号颜色
      }else if(colorNum == 1){
      	color = vec3(0.0,1.0,0.0);//1号颜色
      }else if(colorNum == 2){
      	color = vec3(0.0,0.0,1.0);//2号颜色
      }else if(colorNum == 3){
      	color = vec3(1.0,1.0,0.0);//3号颜色
      }else if(colorNum == 4){
      	color = vec3(1.0,0.0,1.0);//4号颜色
      }else if(colorNum == 5){
      	color = vec3(0.0,1.0,1.0);//5号颜色
      }else if(colorNum == 6){
      	color = vec3(0.3,0.4,0.7);//6号颜色
      }else if(colorNum == 7){
      	color = vec3(0.3,0.7,0.2);//7号颜色   
      }
   }
   vec4 finalColor = vec4(color,1.0);//将计算出的颜色给此片元
   //给此片元颜色值
   gl_FragColor=finalColor*vAmbient + finalColor*vDiffuse + finalColor*vSpecular;
}     