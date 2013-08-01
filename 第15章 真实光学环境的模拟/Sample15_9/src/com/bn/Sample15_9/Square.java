package com.bn.Sample15_9;

/*
 * 位于xoy平面中心，边长为2的正方形
 */
public class Square extends HitObject {
	public Square(Camera cam, Color3f color){		
		this.cam = cam;
		this.color = color;
	}
	@Override
	public boolean hit(Ray r,Intersection inter) {
		/*
		 * 求解光线S+ct与变换后物体的交点需要以下步骤：
		 * 1、求出逆变换光线S'+c't
		 * 2、求出逆变换光线与通用物体的碰撞时间t
		 * 3、把碰撞时间t代入等式S+ct得到实际的交点坐标
		 * 
		 * 
		 * 因此，genRay只是变换后的光线，
		 * 只用于求解碰撞时间t，
		 * 用t求交点时用变换前的光线r
		 */
		Ray genRay=new Ray();//用于生产变换后的光线		
		xfrmRay(genRay, getInvertMatrix(), r);//获取变换后的光线（将r按逆变换矩阵变换后赋值给genRay）
		double denom = genRay.dir.z;//分母
		
		if(Math.abs(denom)<0.0001){//光线和平面平行:无交点
			return false;
		}
		double time=-genRay.start.z/denom;//相交时间
		if(time<=0.0){//交点落在视点后方
			return false;
		}
		
		
		
		double hx=genRay.start.x+genRay.dir.x*time;//交点的x坐标
		double hy=genRay.start.y+genRay.dir.y*time;//交点的y坐标
		if (hx > 1.0 || hx < -1.0) {//x不在范围内
			return false;
		}
		if (hy > 1.0 || hy < -1.0) {//y不在范围内
			return false;
		}
		
		inter.numHits=1;//有一个有效交点
		
		//将光线和物体的相交信息存入inter中
		inter.hit[0].hitTime=time;
		inter.hit[0].hitObject=this;
		inter.hit[0].isEntering=true;
		inter.hit[0].surface=0;
		Point3 P = rayPos(r,time);//交点坐标(使用变换前的光线)
		inter.hit[0].hitPoint.set(P);//变换后的顶点位置
		inter.hit[0].hitNormal.set(0,0,1);//变换前的法向量
		return true;
	}
	@Override
	public boolean hit(Ray r) {
		Ray genRay=new Ray();//用于生产变换后的光线		
		xfrmRay(genRay, getInvertMatrix(), r);//获取变换后的光线（将r按逆变换矩阵变换后赋值给genRay）
		double denom = genRay.dir.z;//分母
		
		if(Math.abs(denom)<0.0001){//光线和平面平行:无交点
			return false;
		}
		double time=-genRay.start.z/denom;//相交时间
		//只接受从0到1之间的碰撞，因为在光源另外一侧不会产生阴影
		if(time<0.0 ||time>1){//交点落在视点后方
			return false;
		}
		
		double hx=genRay.start.x+genRay.dir.x*time;//交点的x坐标
		double hy=genRay.start.y+genRay.dir.y*time;//交点的y坐标
		if (hx > 1.0 || hx < -1.0) {//x不在范围内
			return false;
		}
		if (hy > 1.0 || hy < -1.0) {//y不在范围内
			return false;
		}
		return true;
	}
}
