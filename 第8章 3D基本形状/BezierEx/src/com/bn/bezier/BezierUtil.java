package com.bn.bezier;

import java.util.ArrayList;

public class BezierUtil 
{
   static ArrayList<BNPosition> al=new ArrayList<BNPosition>();	//贝塞尔曲线基于的点的列表
   
   public static ArrayList<BNPosition> getBezierData(float span)//求贝塞尔曲线上点的类
   {
	   ArrayList<BNPosition> result=new ArrayList<BNPosition>(); //存放贝塞尔曲线上点的结果列表
	   
	   int n=al.size()-1;	
	   
	   if(n<1)	//基于的点的数少于1，无贝塞尔曲线
	   {
		   return result;
	   }
	   
	   int steps=(int) (1.0f/span);	//总得步进数
	   long[] jiechengNA=new long[n+1];	//声明一个长度为n+1的阶乘数组
	   
	   for(int i=0;i<=n;i++){	//求0到n的阶乘
		   jiechengNA[i]=jiecheng(i);
	   }
	   
	   for(int i=0;i<=steps;i++)
	   {
		   float t=i*span;
		   if(t>1)		//t的值在0-1之间
		   {
			   t=1;
		   }
		   float xf=0;
		   float yf=0;
		   
		   float[] tka=new float[n+1];
		   float[] otka=new float[n+1];
		   for(int j=0;j<=n;j++)
		   {
			   tka[j]=(float) Math.pow(t, j); //计算t的j次幂
			   otka[j]=(float) Math.pow(1-t, j); //计算1-t的j次幂
		   }
		   
		   for(int k=0;k<=n;k++)
		   {
			   float xs=(jiechengNA[n]/(jiechengNA[k]*jiechengNA[n-k]))*tka[k]*otka[n-k];
			   xf=xf+al.get(k).x*xs;
			   yf=yf+al.get(k).y*xs;
		   }
		   result.add(new BNPosition(xf,yf));
	   }
	   
	   return result;
   }
   
   //求阶乘的方法
   public  static long jiecheng(int n){
	   long result=1;	//声明一个long型的变量
	   if(n==0)			//0的阶乘为1
	   {
		   return 1;
	   }
	   
	   for(int i=2;i<=n;i++){	//求大于等于2的数的阶乘
		   result=result*i;
	   }
	   
	   return result;	//返回结果
   }
}
