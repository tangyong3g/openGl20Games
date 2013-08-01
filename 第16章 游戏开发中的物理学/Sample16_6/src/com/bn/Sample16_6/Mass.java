package com.bn.Sample16_6;

/*
 * 质点类
 * 
 * 根据质点受到力的情况，计算质点当前的位置与速度
 * 
 */

public class Mass {
	float m;		//质点的质量
	Vector3 pos;	//质点的位置
	Vector3 vel;	//质点的速度
	Vector3 force;	//质点的受力
	public Mass(float m){//构造器
		this.m=m;		//指定质量
		pos = new Vector3();//初始化位置
		vel = new Vector3();//初始化速度
	}
	public Mass(float m,Vector3 pos,Vector3 vel){//构造器
		this.m=m;		//指定质量
		this.pos=pos;	//指定位置
		this.vel=vel;	//指定速度
	}
	
	//调用该方法，对质点施加力（包括重力、空气阻力等）
	public void applyForce(Vector3 force){	//施加力的方法
		this.force=this.force.add(force);	
	}
	
	//该方法初始化质点受到的力，初始时受到的力为0
	public void initForce(){	
		force = new Vector3(0,0,0);
	}
	//计算质点当前位置与速度的方法
	public void calculateCurrPosAndVel(float dt){
		Vector3 a = force.multiConstant(1/m);	// 计算加速度 a = F/m 
		Vector3 deltaV = a.multiConstant(dt); 	//计算速度增量 deltaV = at
		vel = vel.add(deltaV);					// 计算速度v = v + at
		pos = pos.add(vel.multiConstant(dt));	// 计算位置 pos = pos + vt
	}
}
