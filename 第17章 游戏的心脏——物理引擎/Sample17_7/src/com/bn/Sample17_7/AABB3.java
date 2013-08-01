package com.bn.Sample17_7;
import android.opengl.Matrix;
//物体的AABB包围盒
public class AABB3
{
	MyVector3f min;
	MyVector3f max;
	//空参构造器
	AABB3(){
		min = new MyVector3f();
		max = new MyVector3f();
		empty();
	}
	//参数为顶点数组的构造器  
	AABB3(float[] vertices){
		min = new MyVector3f();
		max = new MyVector3f();
		empty();
		//将所有的点加入包围盒
		for(int i=0; i<vertices.length; i+=3){
			this.add(vertices[i], vertices[i+1], vertices[i+2]);
		}
	}
	//清空AABB
	public void empty(){
		min.x = min.y = min.z = Float.POSITIVE_INFINITY;//将最小点设为最大值
		max.x = max.y = max.z = Float.NEGATIVE_INFINITY;//将最大点设为最小值
	}
	//将单个点加入到AABB中，并在必要的时候扩展AABB以包含每个点
	public void add(MyVector3f p){
		if (p.x < min.x) { min.x = p.x; }
		if (p.x > max.x) { max.x = p.x; }
		if (p.y < min.y) { min.y = p.y; }
		if (p.y > max.y) { max.y = p.y; }
		if (p.z < min.z) { min.z = p.z; }
		if (p.z > max.z) { max.z = p.z; }
	}
	public void add(float x, float y, float z){
		if (x < min.x) { min.x = x; }
		if (x > max.x) { max.x = x; }
		if (y < min.y) { min.y = y; }
		if (y > max.y) { max.y = y; }
		if (z < min.z) { min.z = z; }
		if (z > max.z) { max.z = z; }
	}
	//获取AABB所有顶点坐标的方法
	public MyVector3f[] getAllCorners(){
		MyVector3f[] result = new MyVector3f[8];
		for(int i=0; i<8; i++){
			result[i] = getCorner(i);
		}
		return result;
	}
	//获取AABB第i个顶点坐标的方法
	public MyVector3f getCorner(int i){		
		if(i<0||i>7){//检查i是否合法
			return null;
		}
		return new MyVector3f(
				((i & 1) == 0) ? max.x : min.x,
				((i & 2) == 0) ? max.y : min.y, 
				((i & 4) == 0) ? max.z : min.z
				);
	}
	//通过当前仿射变换矩阵求得仿射变换后的AABB包围盒的方法
	public AABB3 setToTransformedBox(float[] m)
	{
		//获取所有顶点的坐标
		MyVector3f[] va = this.getAllCorners();
		//用于存放仿射变换后的顶点数组
	    float[] transformedCorners=new float[24];
	    //将变换前的AABB包围盒的8个顶点与仿射变换矩阵m相乘，得到仿射变换后的OBB包围盒的所有顶点
		float[] tmpResult=new float[4];
	    int count=0;
		for(int i=0;i<va.length;i++){
			float[] point=new float[]{va[i].x,va[i].y,va[i].z,1};//将顶点转换成齐次坐标
			Matrix.multiplyMV(tmpResult, 0, m, 0, point, 0);
			transformedCorners[count++]=tmpResult[0];
			transformedCorners[count++]=tmpResult[1];
			transformedCorners[count++]=tmpResult[2];
		}
		//通过构造器将OBB包围盒转换成AABB包围盒，并返回
		return new AABB3(transformedCorners);
	}
	public float getXSize(){//获取x方向大小
		return max.x - min.x;
	}
	public float getYSize(){//获取y方向大小
		return max.y - min.y;
	}
	public float getZSize(){//获取z方向大小
		return max.z - min.z;
	}
	public MyVector3f getSize(){//获取对角线向量
		return max.minus(min);
	}
	//获取包围盒的中心点坐标的方法
	public MyVector3f getCenter(){
		return (min.add(max)).multiK(0.5f);
	}
	
	/*
	 * Woo提出的方法，先判断矩形边界框的哪个面会相交，
	 * 再检测射线与包含这个面的平面的相交性。
	 * 如果交点在盒子中，那么射线与矩形边界框相交，
	 * 否则不存在相交 
	 */
	//和参数射线的相交性测试，如果不相交则返回值是一个非常大的数(大于1)
	//如果相交，返回相交时间t
	//t为0-1之间的值
	public float rayIntersect(
			MyVector3f rayStart,//射线起点
			MyVector3f rayDir,//射线长度和方向
			MyVector3f returnNormal//可选的，相交点处法向量
			){
		//如果未相交则返回这个大数
		final float kNoIntersection = Float.POSITIVE_INFINITY;
		//检查点在矩形边界内的情况，并计算到每个面的距离
		boolean inside = true;
		float xt, xn = 0.0f;
		if(rayStart.x<min.x){
			xt = min.x - rayStart.x;
			if(xt>rayDir.x){ return kNoIntersection; }
			xt /= rayDir.x;
			inside = false;
			xn = -1.0f;
		}
		else if(rayStart.x>max.x){
			xt = max.x - rayStart.x;
			if(xt<rayDir.x){ return kNoIntersection; }
			xt /= rayDir.x;
			inside = false;
			xn = 1.0f;
		}
		else{
			xt = -1.0f;
		}
		
		float yt, yn = 0.0f;
		if(rayStart.y<min.y){
			yt = min.y - rayStart.y;
			if(yt>rayDir.y){ return kNoIntersection; }
			yt /= rayDir.y;
			inside = false;
			yn = -1.0f;
		}
		else if(rayStart.y>max.y){
			yt = max.y - rayStart.y;
			if(yt<rayDir.y){ return kNoIntersection; }
			yt /= rayDir.y;
			inside = false;
			yn = 1.0f;
		}
		else{
			yt = -1.0f;
		}
		
		float zt, zn = 0.0f;
		if(rayStart.z<min.z){
			zt = min.z - rayStart.z;
			if(zt>rayDir.z){ return kNoIntersection; }
			zt /= rayDir.z;
			inside = false;
			zn = -1.0f;
		}
		else if(rayStart.z>max.z){
			zt = max.z - rayStart.z;
			if(zt<rayDir.z){ return kNoIntersection; }
			zt /= rayDir.z;
			inside = false;
			zn = 1.0f;
		}
		else{
			zt = -1.0f;
		}
		//是否在矩形边界框内？
		if(inside){
			if(returnNormal != null){
				returnNormal = rayDir.multiK(-1);
				returnNormal.normalize();
			}
			return 0.0f;
		}
		//选择最远的平面————发生相交的地方
		int which = 0;
		float t = xt;
		if(yt>t){
			which = 1;
			t=yt;
		}
		if(zt>t){
			which = 2;
			t=zt;
		}
		switch(which){
			case 0://和yz平面相交
			{
				float y=rayStart.y+rayDir.y*t;
				if(y<min.y||y>max.y){return kNoIntersection;}
				float z=rayStart.z+rayDir.z*t;
				if(z<min.z||z>max.z){return kNoIntersection;}
				if(returnNormal != null){
					returnNormal.x = xn;
					returnNormal.y = 0.0f;
					returnNormal.z = 0.0f;
				}				
			}
			break;
			case 1://和xz平面相交
			{
				float x=rayStart.x+rayDir.x*t;
				if(x<min.x||x>max.x){return kNoIntersection;}
				float z=rayStart.z+rayDir.z*t;
				if(z<min.z||z>max.z){return kNoIntersection;}
				if(returnNormal != null){
					returnNormal.x = 0.0f;
					returnNormal.y = yn;
					returnNormal.z = 0.0f;
				}				
			}
			break;
			case 2://和xy平面相交
			{
				float x=rayStart.x+rayDir.x*t;
				if(x<min.x||x>max.x){return kNoIntersection;}
				float y=rayStart.y+rayDir.y*t;
				if(y<min.y||y>max.y){return kNoIntersection;}
				if(returnNormal != null){
					returnNormal.x = 0.0f;
					returnNormal.y = 0.0f;
					returnNormal.z = zn;
				}				
			}
			break;
		}
		return t;//返回相交点参数值
	}
}
