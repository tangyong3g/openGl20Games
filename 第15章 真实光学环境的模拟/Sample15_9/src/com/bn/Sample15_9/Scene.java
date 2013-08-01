package com.bn.Sample15_9;
import java.util.ArrayList;
import java.util.List;
import static com.bn.Sample15_9.Constant.*;
//场景类
public class Scene {
	Camera cam;
	Light light;
	Ray feeler = new Ray();//阴影探测器
	List<HitObject> hitObjects;//物体列表
	
	Ball ball1;//球1
	Ball ball2;//球2
	Square sqare;//矩形平面
	
	public Scene(Camera cam, Light light){
		this.cam=cam;
		this.light = light;
		hitObjects = new ArrayList<HitObject>();
		//创建一个红色的球
		ball1 = new Ball(cam, new Color3f(BALL1_COLOR));
		//创建一个蓝色的球
		ball2 = new Ball(cam, new Color3f(BALL2_COLOR));
		//创建一个绿色平面
		sqare = new Square(cam, new Color3f(PLANE_COLOR));
		
		//将物体加入场景中
		hitObjects.add(ball1);
		hitObjects.add(ball2);
		hitObjects.add(sqare);
		
	}
	
	//场景中的物体进行变换的方法
	public void transform(){	
		//为所有物体初始化变换矩阵
		for(HitObject pObj:hitObjects){
			pObj.initMyMatrix();
		}
		
		//设置平面的变换
		sqare.rotate(-90, 1, 0, 0);
		sqare.scale(PLANE_WIDTH/2.0f, PLANE_HEIGHT/2.0f, 1);
		
		//设置球1的变换
		ball1.translate(-CENTER_DIS, R, 0);
		ball1.scale(R, R, R);
		
		//设置球2的变换
		ball2.translate(CENTER_DIS, R, 0);
		ball2.scale(R, R, R);
	}
	
	/*
	 * 返回光线对应的像素各信息,
	 * 
	 * 返回值：
	 * -1表示没有交点，
	 * 0表示有交点，且最佳碰撞点不在阴影中
	 * 1表示有交点，且最佳碰撞点在阴影中
	 */
	public int shade(
			Ray ray, //光线
			Color3f color, //物体颜色
			Point3 vetex, //变换后的顶点位置
			Vector3 normal//变换后的法向量
	){
		Intersection best = new Intersection();//用于保存目前为止最佳的碰撞记录		
		getFirstHit(ray, best);//填充最佳碰撞记录
		if(best.numHits==0){//如果没有物体与光线相交
			return -1;
		}		
		//如果有物体与光线相交返回碰撞点的各信息
		color.set(best.hit[0].hitObject.getColor());//物体颜色
		vetex.set(best.hit[0].hitPoint);//顶点位置
		//通过逆转置变换，求变换之后的法向量
		float[] inverTranspM = best.hit[0].hitObject.getInvertTransposeMatrix();//逆转置矩阵		
		Vector3 preN = best.hit[0].hitNormal;//变换前的法向量
		best.hit[0].hitObject.xfrmNormal(normal, inverTranspM, preN);//求变换后的法向量
		
		//探测是否在阴影中
		Point3 hitPoint = best.hit[0].hitPoint;
		//阴影探测器的起点为，碰撞点朝人眼方向移动一个微小的距离
		feeler.start.set(hitPoint.minus(ray.dir.multiConst(MNIMUM)));
		//阴影探测器的方向，从碰撞点指向光源
		feeler.dir = light.pos.minus(hitPoint);
		if(isInShadow(feeler)){
			return 1;//有交点，且最佳碰撞点在阴影中
		}
		return 0;//有交点，且最佳碰撞点不在阴影中
	}
	
	public void getFirstHit(Ray ray, Intersection best){
		Intersection inter = new Intersection();//实例化相交记录
		best.numHits=0;//还没有交点
		/*
		 * 此处检测光线与每个物体是否相交，
		 * 与每个物体相交的信息都会存储在best中。
		 * 由于光线与单个物体相交时，
		 * 总会将光线与该物体的最近相交点保存在best.hit[0]中(由每个物体的hit方法决定)，
		 * 因此只要将所有物体的“最近点”信息做比较，并将最终结果存入best.hit[0]中，
		 * 即可得出光线与所有物体的最近的交点信息
		 */
		for(HitObject pObj:hitObjects){//检查场景中的每一个物体
			if(!pObj.hit(ray, inter)){//光线是否和pObj相交？（此处已经调用hit方法，如果有相交，会将相交信息记录在inter中）
				continue;//无交点：检测下一个物体
			}
			if(best.numHits==0 || //best中还没有交点信息，或best中的交点不是最近点
					inter.hit[0].hitTime<best.hit[0].hitTime){
				/*
				 * 注意这里一定是复制一份，而不能直接给其引用，
				 * 否则里面的值一变会导致best的值也变！
				 */
				best.set(inter);//复制inter到best
			}
		}
	}
	//检测是否在阴影中的方法，参数为光线跟踪器
	public boolean isInShadow(Ray feeler){
		for(HitObject pObj:hitObjects){
			if(pObj.hit(feeler)){//光线与任何物体相交，在阴影中
				return true;
			}
		}
		return false;//没有相交的物化，不在阴影中
	}
}