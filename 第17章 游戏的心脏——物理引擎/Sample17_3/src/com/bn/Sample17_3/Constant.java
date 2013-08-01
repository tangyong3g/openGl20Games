package com.bn.Sample17_3;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.bn.Sample17_3.R;

public class Constant 
{
	 final static float TIME_STEP=1.0f/60;					//模拟的频率
	  final static int MAX_SUB_STEPS=5;					//最大的子步数
	  final static float EYE_X=-5;							//观察者的位置x
	  final static float EYE_Y=4;							//观察者的位置y
	  final static float EYE_Z=5;							//观察者的位置z
	  final static float TARGET_X=0;						//目标的位置x
	  final static float TARGET_Y=0;						//目的位置Y
	  final static float TARGET_Z=0;						//目标的位置Z
	//刚体基本尺寸单元
    final static float GT_UNIT_SIZE=0.6f;
    
	public static final float UNIT_SIZE=0.5f;//每格的单位长度	
	public static final float LAND_HIGHEST=1.5f;//陆地最大高差
	
	public static float[][] yArray;//陆地上每个顶点的高度数组
	public static int COLS;//陆地列数
	public static int ROWS;//陆地行数

	public static void initConstant(Resources r)
	{
		//从灰度图片中加载陆地上每个顶点的高度
		yArray=loadLandforms(r);
		//根据数组大小计算陆地的行数及列数
		COLS=yArray[0].length-1;
		ROWS=yArray.length-1;		 
	}
	
	//从灰度图片中加载陆地上每个顶点的高度
	public static float[][] loadLandforms(Resources resources)
	{
		Bitmap bt=BitmapFactory.decodeResource(resources, R.drawable.landform);
		int colsPlusOne=bt.getWidth();
		int rowsPlusOne=bt.getHeight(); 
		float[][] result=new float[rowsPlusOne][colsPlusOne];
		for(int i=0;i<rowsPlusOne;i++)
		{
			for(int j=0;j<colsPlusOne;j++)
			{
				int color=bt.getPixel(j,i);
				int r=Color.red(color);
				int g=Color.green(color); 
				int b=Color.blue(color);
				int h=(r+g+b)/3;
				result[i][j]=h*LAND_HIGHEST/255;  
			}
		}		
		return result;
	}
}
