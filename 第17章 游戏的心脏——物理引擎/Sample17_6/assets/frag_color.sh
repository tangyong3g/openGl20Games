precision mediump float;
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
void main()                         
{
   vec4 mColor=vec4(0.763,0.657,0.614,0);
   gl_FragColor = mColor*ambient+mColor*diffuse+mColor*specular;//给此片元颜色值
}   