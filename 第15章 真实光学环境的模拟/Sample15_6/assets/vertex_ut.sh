uniform mat4 uMVPMatrix; 		//总变换矩阵
attribute vec3 aPosition;  		//顶点位置
attribute vec2 aTexCoor;    		//顶点纹理坐标
attribute vec3 aNormal;   		//法向量
attribute vec3 tNormal;   			//切向量
varying vec2 vTextureCoord;  		//用于传递给片元着色器的纹理坐标
varying vec3 fNormal;    		//用于传递给片元着色器的法向量
varying vec3 ftNormal;    		//用于传递给片元着色器的切向量
varying vec3 vPosition;  			//用于传递给片元着色器的顶点位置
void main() {     
   gl_Position = uMVPMatrix * vec4(aPosition,1); 	//根据总变换矩阵计算此次绘制此顶点的位置
   vTextureCoord=aTexCoor;					//将顶点的纹理坐标传给片元着色器
   fNormal=aNormal;   						//将顶点的法向量传给片元着色器
   ftNormal=tNormal; 						//将顶点的切向量传给片元着色器
   vPosition=aPosition; 						//将顶点的位置传给片元着色器
}   
