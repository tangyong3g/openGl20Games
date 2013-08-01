package com.bn.Sample16_6;

import static com.bn.Sample16_6.Constant.springLength;

import java.util.ArrayList;
import java.util.List;

/*
 * 绳子类
 */

public class Rope extends RopeSimulation {
	
	List<Spring> springList;//弹簧列表
	Vector3 gravity;//重力
	Vector3 connectionPos;//绳头结点的位置
	Vector3 connectionVel;//绳头结点的速度
	float groundRepulsionConstant;//地面的反弹系数
	float groundFrictionConstant;//地面的摩擦系数
	float groundAbsorptionConstant;//地面的缓冲系数
	float groundHeight;//地面的高度
	float airFrictionConstant;//空气的摩擦系数
	public Rope(
			int massCount,						//质点的数量 
			float m,							//质点的质量
			Vector3 gravity,					//重力
			float groundRepulsionConstant, 		//地面反弹系数
			float groundFrictionConstant,		//地面摩擦系数
			float groundAbsorptionConstant,		//地面缓冲系数 
			float groundHeight,					//地面高度
			float airFrictionConstant,			//空气阻力
			Vector3 connectionVel,				//绳头速度
			Vector3 connectionPos) {			//绳头位置	
		
		super(massCount, m);		//调用父类的构造器，创建massCount个质量为m的质点Mass的对象
		
		this.gravity = gravity;	//指定重力
		this.groundRepulsionConstant = groundRepulsionConstant;//指定地面反弹系数
		this.groundFrictionConstant = groundFrictionConstant;//指定地面摩擦系数
		this.groundAbsorptionConstant = groundAbsorptionConstant;//指定地面缓冲系数
		this.groundHeight = groundHeight;	//指定地面高度
		this.airFrictionConstant = airFrictionConstant;//指定空气阻力
		this.connectionVel=connectionVel;//绳头速度
		this.connectionPos=connectionPos;//绳头位置
		
		for(int i=0;i<massCount;i++){		//初始化所有质点的位置
			massList.get(i).pos.x = i*springLength;		//指定x坐标
			massList.get(i).pos.y = groundHeight;		//指定y坐标
			massList.get(i).pos.z = 0;					//指定z坐标
		}
		springList = new ArrayList<Spring>();		//创建存放弹簧对象的列表
		for(int i=0;i<massCount-1;i++){		//初始化质点之间的弹簧(弹簧的数量比质点的数量少一个)
			Spring temp = new Spring(massList.get(i),massList.get(i+1));
			springList.add(temp);		//加入到弹簧对象的列表
		}
	}
	//计算绳子当前运动轨迹的方法
	public void solve() {
		for(int i=0;i<massCount-1;i++){		
			springList.get(i).calculateSpringForce();	//该弹簧对其两端质点施加的力
		}
		for(int i=0;i<massCount;i++){	//物体受到的其他力
			Mass mass = massList.get(i);
			//施加万有引力
			mass.applyForce(gravity.multiConstant(mass.m));
			//施加空气阻力
			mass.applyForce(mass.vel.multiConstant(-1).multiConstant(airFrictionConstant));
			
			if(mass.pos.y<=groundHeight){
				Vector3 v = mass.vel.copy();
				v.y=0;
				//摩擦力
				mass.applyForce(v.multiConstant(-1).multiConstant(groundFrictionConstant));
				v = mass.vel.copy();
				v.x=0;
				v.z=0;
				if(v.y<0){
					mass.applyForce(v.multiConstant(-1).multiConstant(groundAbsorptionConstant));
				}
				//计算地面的反作用力
				Vector3 force = new Vector3(0,groundRepulsionConstant,0).multiConstant(groundHeight-mass.pos.y);
				mass.applyForce(force);
			}
		}
	}
	@Override
	public void simulateRope(float dt){ //模拟绳子运动的方法
		super.simulateRope(dt);			//调用父类的模拟方法
		Mass head = massList.get(0);	//获取绳头的质点
		Vector3[] result = calCentripetalForceAndVel();//计算向心力和速度
		head.applyForce(result[0]);	//施加向心力
		connectionVel = result[1];	//计算速度
		connectionPos = connectionPos.add(   //计算绳头质点的位置
								connectionVel.multiConstant(dt));
		if(connectionPos.y<groundHeight){ //保证绳头在地面的高度之上
			connectionPos.y=groundHeight;
			connectionVel.y=0;
		}
		head.pos=connectionPos; //设置绳头的位置
		head.vel=connectionVel;//设置绳头的速度
	}
	public Vector3[] calCentripetalForceAndVel(){
		Mass head = massList.get(0);  //获取绳头质点
		Vector3 center = new Vector3(0,head.pos.y,-2);//绕y轴旋转
		Vector3 forceDir = center.add(head.pos.multiConstant(-1)).normal();
		Vector3 force = forceDir.multiConstant(head.m); //向心力 F=m*v*v/r  此处令v=1,r=1;
		Vector3 velDir = Vector3.yRotate(
				Math.toRadians(90), new double[]{forceDir.x,forceDir.y,forceDir.z,1});
		Vector3 vel = velDir.multiConstant(10);//速度大小为10
		if(head.pos.y<1.5f){
			vel.y=1;//垂直速度为1
		}
		return new Vector3[]{force,vel};//返回向心力和速度
	}
}