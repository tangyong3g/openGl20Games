precision mediump float;
varying vec2 mcLongLat;//接收从顶点着色器过来的参数
void main()                         
{                       
   vec3 bColor=vec3(0.678,0.231,0.129);//砖块的颜色
   vec3 mColor=vec3(0.763,0.657,0.614);//间隔的颜色
   vec3 color;
   
   //计算当前位于奇数还是偶数行
   int row=int(mod((mcLongLat.y+90.0)/12.0,2.0));
   //计算当前是否在此行的砖块垂直区间中的辅助变量
   float ny=mod(mcLongLat.y+90.0,12.0);
   //奇偶数行x偏移
   float oeoffset=0.0;
   //计算当前是否在此列的砖块水平区间中的辅助变量
   float nx;
   
   if(ny>10.0)
   {//不在此行的砖块垂直区间中
     color=mColor;
   }
   else
   {//在此行的砖块垂直区间中
     if(row==1)
     {//若为奇数行则加上列偏移
        oeoffset=11.0;
     }
     //计算当前是否在此列的砖块水平区间中的辅助变量
     nx=mod(mcLongLat.x+oeoffset,22.0);
     if(nx>20.0)
     {//不在此列的砖块水平区间中
        color=mColor;
     }
     else
     {//在此列的砖块水平区间中
        color=bColor;
     }
   } 
   //将计算出的颜色给此片元
   gl_FragColor=vec4(color,0);
}     