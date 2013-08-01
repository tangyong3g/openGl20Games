package com.bn.Sample11_9;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class FontUtil 
{
	static int cIndex=0;
	static final float textSize=40;
	static int R=255;
	static int G=255;
	static int B=255;
	public static Bitmap generateWLT(String[] str,int width,int height)
	{
		Paint paint=new Paint();
		paint.setARGB(255, R, G, B);
		paint.setTextSize(textSize);
		paint.setTypeface(null);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		Bitmap bmTemp=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bmTemp);
		for(int i=0;i<str.length;i++)
		{
			canvasTemp.drawText(str[i], 0, textSize*i+(i-1)*5, paint);
		}
		return bmTemp;
	}
	static String[] content=
	{
		"赵客缦胡缨，吴钩霜雪明。",
		"银鞍照白马，飒沓如流星。",
		"十步杀一人，千里不留行。",
		"事了拂衣去，深藏身与名。",
		"闲过信陵饮，脱剑膝前横。",
		"将炙啖朱亥，持觞劝侯嬴。",
		"三杯吐然诺，五岳倒为轻。",
		"眼花耳热后，意气素霓生。",
		"救赵挥金槌，邯郸先震惊。",
		"千秋二壮士，煊赫大梁城。",
		"纵死侠骨香，不惭世上英。",
		"谁能书閤下，白首太玄经。",
	};
	//获得数组的方法
	public static String[] getContent(int length,String[] content)
	{
		String[] result=new String[length+1];
		for(int i=0;i<=length;i++)
		{
			result[i]=content[i];
		}
		return result;
	}
	//更新颜色的方法
	public static void updateRGB()
	{
		R=(int)(255*Math.random());
		G=(int)(255*Math.random());
		B=(int)(255*Math.random());
	}
}