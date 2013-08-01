package com.bn.Sample16_6;

import java.util.ArrayList;
import java.util.List;

/*
 * 由所有质点组成的一个模拟绳子运动的抽象绳子类
 */

public abstract class RopeSimulation {
	List<Mass> massList;	//存放所有质点对象的列表
	int massCount;	//质点数量
	
	
	public RopeSimulation(int massCount,float m){	//参数为质点的数量与每个质点的质量
		this.massCount=massCount;	//质点数量赋值
		massList = new ArrayList<Mass>();	//创建存放所有质点对象的列表
		
		for(int i=0;i<massCount;i++){
			Mass mass = new Mass(m);	//创建massCount个相同的质量为m的质点
			massList.add(mass);			//加入到质点的列表中	
		}
	}
	//获取质点对象列表中某个质点的对象
	public Mass getMass(int index){
		if(index<0||index>=massCount){
			return null;
		}
		return massList.get(index);
	}
	//初始化质点列表中每一个质点对象受到的力的情况（初始化时每个质点，受力为0）
	public void init(){
		for(int i=0;i<massList.size();i++){
			massList.get(i).initForce();	//初始化质点的受力情况
		}
	}
	//计算绳子当前运动轨迹的抽象方法
	public abstract void solve();
	
	//更新质点列表中，每一个质点当前位置与当前速度的方法，使得所有的质点均模拟绳子运动
	public void simulateRope(float dt){
		for(int i=0;i<massList.size();i++){
			massList.get(i).calculateCurrPosAndVel(dt);	//更新该质点的速度和质量
		}
	}
	//不断更新组成绳子的每个质点位置与速度，从而使得绳子运动起来
	public void operate(float dt){
		init();								
		solve();
		simulateRope(dt);
	}
	
}
