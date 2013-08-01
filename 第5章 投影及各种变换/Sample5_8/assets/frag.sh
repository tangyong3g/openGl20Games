precision mediump float;
varying  vec4 vColor; //接收从顶点着色器过来的参数
varying vec3 vPosition;//接收从顶点着色器过来的顶点位置
void main() {  
   gl_FragColor = vColor;//给此片元颜色值
}