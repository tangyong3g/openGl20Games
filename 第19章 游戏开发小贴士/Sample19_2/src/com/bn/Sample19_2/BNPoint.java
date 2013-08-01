package com.bn.Sample19_2;

//用于记录触控点坐标、及绘制触控点的类
public class BNPoint   
{
   //上一次位置的X、Y坐标
   float oldX;
   float oldY;	
   //是否已经有上一次位置的标志位
   boolean hasOld=false;
   //触控点X、Y坐标
   float x;
   float y;    
   
   public BNPoint(float x,float y)
   {
	   this.x=x;
	   this.y=y;
   }
   
   public void setLocation(float x,float y)
   {
	   //把原来位置记录为旧位置
	   oldX=this.x;
	   oldY=this.y;
	   //设置是否已经有上一次位置的标志位
	   hasOld=true;
	   //设置新位置
	   this.x=x;
	   this.y=y;
   }
   
   //计算两个点距离的标志位
   public static float calDistance(BNPoint a,BNPoint b)
   {
	   float result=0;
	   
	   result=(float)Math.sqrt(
	     (a.x-b.x)*(a.x-b.x)+
	     (a.y-b.y)*(a.y-b.y)
	   );	   
	   return result;
   }
}
