precision mediump float;//给出默认的浮点精度
varying vec2 vTextureCoord;//从顶点着色器传递过来的纹理坐标
uniform sampler2D sTexture;//纹理内容数据
void main() {           
	//给出卷积内核中各个元素对应像素相对于待处理像素的纹理坐标偏移量
	vec2 offset0=vec2(-1.0,-1.0); vec2 offset1=vec2(0.0,-1.0); vec2 offset2=vec2(1.0,-1.0);
	vec2 offset3=vec2(-1.0,0.0); vec2 offset4=vec2(0.0,0.0); vec2 offset5=vec2(1.0,0.0);
	vec2 offset6=vec2(-1.0,1.0); vec2 offset7=vec2(0.0,1.0); vec2 offset8=vec2(1.0,1.0); 
	const float scaleFactor = 1.0/9.0;//给出最终求和时的加权因子(为调整亮度)
	//卷积内核中各个位置的值
	float kernelValue0 = 1.0; float kernelValue1 = 1.0; float kernelValue2 = 1.0;
	float kernelValue3 = 1.0; float kernelValue4 = 1.0; float kernelValue5 = 1.0;
	float kernelValue6 = 1.0; float kernelValue7 = 1.0; float kernelValue8 = 1.0;
	vec4 sum;//最终的颜色和
	//获取卷积内核中各个元素对应像素的颜色值
	vec4 cTemp0,cTemp1,cTemp2,cTemp3,cTemp4,cTemp5,cTemp6,cTemp7,cTemp8;	
	cTemp0=texture2D(sTexture, vTextureCoord.st + offset0.xy/512.0);
	cTemp1=texture2D(sTexture, vTextureCoord.st + offset1.xy/512.0);
	cTemp2=texture2D(sTexture, vTextureCoord.st + offset2.xy/512.0);
	cTemp3=texture2D(sTexture, vTextureCoord.st + offset3.xy/512.0);
	cTemp4=texture2D(sTexture, vTextureCoord.st + offset4.xy/512.0);
	cTemp5=texture2D(sTexture, vTextureCoord.st + offset5.xy/512.0);
	cTemp6=texture2D(sTexture, vTextureCoord.st + offset6.xy/512.0);
	cTemp7=texture2D(sTexture, vTextureCoord.st + offset7.xy/512.0);
	cTemp8=texture2D(sTexture, vTextureCoord.st + offset8.xy/512.0);
	//颜色求和
	sum =kernelValue0*cTemp0+kernelValue1*cTemp1+kernelValue2*cTemp2+
		 kernelValue3*cTemp3+kernelValue4*cTemp4+kernelValue5*cTemp5+
	     kernelValue6*cTemp6+kernelValue7*cTemp7+kernelValue8*cTemp8; 
  	gl_FragColor = sum * scaleFactor; //进行亮度加权后将最终颜色传递给管线
}         