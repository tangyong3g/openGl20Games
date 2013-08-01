package com.bn.Sample15_9;
//一个交点的信息
public class HitInfo{

	double hitTime;//相交时间
	HitObject hitObject;//相交的物体
	boolean isEntering;//光线是进入还是出来
	int surface;//相交于哪个表面
	Point3 hitPoint;//交点的坐标，变换后的
	Vector3 hitNormal;//交点处的法向量，变换前的
	
	public HitInfo(){
		hitPoint = new Point3();
		hitNormal = new Vector3();
	}
	/* 
	 * 此方法可能会不对，复制问题可能出现
	 * 如果有解决不了的问题可以回来看
	 */
	public void set(HitInfo hit){
		this.hitTime=hit.hitTime;
		this.hitObject=hit.hitObject;//指向的物体不用复制
		this.isEntering=hit.isEntering;
		this.surface=hit.surface;
		this.hitPoint.set(hit.hitPoint);
		this.hitNormal.set(hit.hitNormal);
	}
	@Override
	public String toString() {
		return "hitTime"+hitTime+",hitPoint"+hitPoint;
	}
}
