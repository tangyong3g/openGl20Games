package com.bn.Sample16_6;
/*
 * 弹簧类
 */
public class Spring {
	
	Mass mass1;//质点1
	Mass mass2;//质点2
	float springConstant;//弹性系数
	float springLength;//弹簧长度
	float frictionConstant;//摩擦系数
	
	public Spring(Mass mass1, Mass mass2) {//构造器
		this.mass1 = mass1;	//指定第一个质点	
		this.mass2 = mass2;	//指定第二个质点
		this.springConstant = Constant.springConstant;//指定弹性系数	
		this.springLength = Constant.springLength;//指定弹簧长度
		this.frictionConstant = Constant.frictionConstant;//指定弹簧摩擦系数
	}

	public void calculateSpringForce(){	//计算各个物体受力的方法
		Vector3 springVector = mass1.pos.add(mass2.pos.multiConstant(-1));//弹簧的伸长方向
		float distance = springVector.length();//两个质点间的距离
		Vector3 force;//作用力
		if(distance!=0){
			float deltaX = distance-this.springLength;//弹簧偏离平衡位置的距离
			
			Vector3 normalV =springVector.multiConstant(1/distance).multiConstant(-1);//将弹簧的方向向量规格化;
			
			force = normalV.multiConstant(deltaX).multiConstant(springConstant);//弹簧的拉力 = k*deltaX*dir
			
			force = force.add(mass1.vel.add(mass2.vel.multiConstant(-1)).multiConstant(-frictionConstant));//计算合力  
			
			mass1.applyForce(force);//对第一个质点施加力
			mass2.applyForce(force.multiConstant(-1));//对第二个质点施加力
		}	
	}
}
