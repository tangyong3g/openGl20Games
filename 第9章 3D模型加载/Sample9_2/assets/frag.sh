precision mediump float;
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
void main()                         
{    
   //将计算出的颜色给此片元
   vec4 finalColor=vec4(0.9,0.9,0.9,1.0);   
   gl_FragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;//给此片元颜色值

}   