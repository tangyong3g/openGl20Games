uniform mat4 uMVPMatrix; //总变换矩阵
uniform float uStartAngle;//本帧起始角度
uniform float uWidthSpan;//横向长度总跨度
attribute vec3 aPosition;  //顶点位置
attribute vec2 aTexCoor;    //顶点纹理坐标
varying vec2 vTextureCoord;  //用于传递给片元着色器的变量
void main()     
{                  
   //计算X向角度          		
   float angleSpanH=4.0*3.14159265;//横向角度总跨度   
   float startX=-uWidthSpan/2.0;//起始X坐标
   //根据横向角度总跨度、横向长度总跨度及当前点X坐标折算出当前点X坐标对应的角度
   float currAngleH=uStartAngle+((aPosition.x-startX)/uWidthSpan)*angleSpanH;
   
   //计算出随Y向发展起始角度的扰动值
   float angleSpanZ=4.0*3.14159265;//纵向角度总跨度 
   float uHeightSpan=0.75*uWidthSpan;//纵向长度总跨度
   float startY=-uHeightSpan/2.0;//起始Y坐标
   //根据纵向角度总跨度、纵向长度总跨度及当前点Y坐标折算出当前点Y坐标对应的角度
   float currAngleZ=((aPosition.y-startY)/uHeightSpan)*angleSpanZ;
      
   //计算斜向波浪
   float tzH=sin(currAngleH-currAngleZ)*0.1;   
   //根据总变换矩阵计算此次绘制此顶点位置
   gl_Position = uMVPMatrix * vec4(aPosition.x,aPosition.y,tzH,1); 
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      