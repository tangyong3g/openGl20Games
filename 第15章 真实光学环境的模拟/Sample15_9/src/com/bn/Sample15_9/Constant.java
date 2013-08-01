package com.bn.Sample15_9;

//常量类 
public class Constant {
	// 关于屏幕的量
	public static final float SCREEN_WIDTH = 800;// 屏幕的宽度
	public static final float SCREEN_HEIGHT = 480;// 屏幕的高度
	// 关于渲染时的量
	public static final float blockSize = 8f;//基本块的尺寸
	public static final float W = SCREEN_WIDTH / 2.0f;// 视口半宽高
	public static final float H = SCREEN_HEIGHT / 2.0f;
	public static final float ratio = W / H;//视口宽高比
	public static final float nRows = SCREEN_HEIGHT;// 像素总行列数
	public static final float nCols = SCREEN_WIDTH;
	//关于真实世界中近平面的量
	public static final float N_3D = 24;// 近平面到摄像机的距离
	public static final float W_3D = ratio;//近平面半宽
	public static final float H_3D = 1.0f;//近平面半高
	//关于真实世界中各物体的量
	public static final float R = 0.6f;//球的半径
	public static final float CENTER_DIS = 0.7f;//球与中心的距离
	public static final float PLANE_WIDTH = 3.5f;//平面宽度
	public static final float PLANE_HEIGHT = 4f;//平面高度
	
	public static final float[] BALL1_COLOR = {0.8f,0.2f,0.2f};//球1的颜色
	public static final float[] BALL2_COLOR = {0.2f,0.2f,0.8f};//球2的颜色
	public static final float[] PLANE_COLOR = {0.2f,0.8f,0.2f};//平面的颜色
	//关于摄像机的参数
	public static final float CAM_X = 15;
	public static final float CAM_Y = 7;
	public static final float CAM_Z = 32;
	//关于光源的参数
	public static final float LIGHT_X = 100;
	public static final float LIGHT_Y = 80;
	public static final float LIGHT_Z = 0;
	//计算阴影时用的极小的正数
	public static final float MNIMUM = 0.00001f;
}