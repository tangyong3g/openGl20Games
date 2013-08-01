precision mediump float;
//接收从顶点着色器过来的参数
varying vec4 diffuse;
varying vec4 specular;
varying float vEdge;
void main()                         
{
   float averageDiffuse = (diffuse.x + diffuse.y + diffuse.z)/3.0;
   if(averageDiffuse<0.5){
      averageDiffuse=0.2;
   } else {
      averageDiffuse=0.8;
   }
   vec4 diffuseFinal = vec4(averageDiffuse, averageDiffuse, averageDiffuse, 1.0);
   
   float averageSpecular = (specular.x + specular.y + specular.z)/3.0;
   if(averageSpecular<0.18){
      averageSpecular=0.0;
   } else {
      averageSpecular=1.0;
   }
   vec4 specularFinal = vec4(averageSpecular, averageSpecular, averageSpecular, 1.0);
   
   float edgeFinal = 1.0;
   if(vEdge<0.2){//如果为边缘像素，用黑色描边
       edgeFinal = 0.0;
   }
   gl_FragColor = edgeFinal*(specularFinal+diffuseFinal);//给此片元颜色值
}