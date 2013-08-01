package com.bn.Sample17_6;
import javax.vecmath.Vector3f;

public class Constant {

	public static final float cubeSize=1f;//长方体的半高度
	
	public static final float Ball_R=1f;//长方体的半长度
	public static final float Ball_Height=0f;//长方体的半长度
	
	public static final float Stick_Length=5f;//长方体的半长度
	public static final float Stick_R=0.2f;//长方体的半长度
	public static final float Stick_Height=4f;//长方体的半长度
	
	public static final float Ceiling_Height=8f;//长方体的半长度
	public static final float Floor_Height=-2f;//长方体的半长度
	
	public static final float LEG_MASS=1f;//腿的质量
	
	public static final Vector3f boxPos = new Vector3f(-3,5,5); 
	
	public  static boolean keyFlag=true;
	
	
	public static boolean isNumber(String str){
		if(str.equals("NaN") || Float.isInfinite(Float.parseFloat(str))){
			return false;
		}
		return true;
	}
}
