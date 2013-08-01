package com.bn.Sample15_9;
/*
 * 球心在原点，半径为1的标准球
 * 注意：其变换成椭球后法向量也是对的
 */
public class Ball extends HitObject {
	public Ball(Camera cam, Color3f color){
		this.cam=cam;
		this.color=color;
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
		Ray genRay=new Ray();//变换后的光线		
		xfrmRay(genRay, getInvertMatrix(), r);//获取变换后的光线（将r按逆变换矩阵变换后赋值给genRay）

		double A,B,C;
		A = Vector3.dot(genRay.dir,genRay.dir);	
		B = Vector3.dot(genRay.start, genRay.dir);
		C = Vector3.dot(genRay.start, genRay.start)-1.0f;
		double discrim = B*B-A*C;                       //求判别式
		if(discrim<0.0){//没有交点
			return false;
		}
		int num=0;//目前的交点个数
		double discRoot = (float) Math.sqrt(discrim);
		double t1 = (-B-discRoot)/A;		//第一次相交时间
		if(t1>0.00001){
			inter.hit[0].hitTime=t1;
			inter.hit[0].hitObject=this;
			inter.hit[0].isEntering=true;
			inter.hit[0].surface=0;			
			Point3 P = rayPos(r,t1);//交点坐标(使用变换前的光线)
			inter.hit[0].hitPoint.set(P);//变换后的顶点位置			
			Point3 preP = xfrmPtoPreP(P);//通过变换后的点求变换前的点
			inter.hit[0].hitNormal.set(preP);//变换前的点就是变换前的法向量
			
			num=1;//有一个交点
		}
		double t2 = (-B+discRoot)/A;//第2个有效交点
		if(t2>0.00001){
			inter.hit[num].hitTime=t2;
			inter.hit[num].hitObject=this;
			inter.hit[num].isEntering=true;
			inter.hit[num].surface=0;			
			Point3 P = rayPos(r,t2);//交点坐标(使用变换前的光线)
			inter.hit[num].hitPoint.set(P);
			Point3 preP = xfrmPtoPreP(P);//通过变换后的点求变换前的点
			inter.hit[num].hitNormal.set(preP);//变换前的点就是变换前的法向量
			
			num++;//另一个有效交点
		}
		inter.numHits=num;
		return (num>0);//true或者false
	}

	@Override
	public boolean hit(Ray r) {
		Ray genRay=new Ray();//变换后的光线		
		xfrmRay(genRay, getInvertMatrix(), r);//获取变换后的光线（将r按逆变换矩阵变换后赋值给genRay）

		double A,B,C;
		A = Vector3.dot(genRay.dir,genRay.dir);	
		B = Vector3.dot(genRay.start, genRay.dir);
		C = Vector3.dot(genRay.start, genRay.start)-1.0f;
		double discrim = B*B-A*C;                       //求判别式
		if(discrim<0.0){//没有交点
			return false;
		}
		double discRoot = (float) Math.sqrt(discrim);
		double t1 = (-B-discRoot)/A;		//第一次相交时间
		//只接受从0到1之间的碰撞，因为在光源另外一侧不会产生阴影
		if(t1<0 || t1>1){
			return false;
		}
		return true;
	}
}
