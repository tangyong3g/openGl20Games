package com.bn.Sample17_2;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
public class CubeCylinder 
{
	Cylinder cl;//用于组合的圆柱
	TexCube tc;//用于组合的立方体
	float halfSize;
	RigidBody body;//对应的刚体对象
	MySurfaceView mv;//MySurfaceView的引用
	
	public CubeCylinder(MySurfaceView mv,float halfSize,CollisionShape[] csa,
			DiscreteDynamicsWorld dynamicsWorld,float mass,float cx,float cy,float cz,int[] mProgram)
	{	
		CompoundShape comShape=new CompoundShape(); //创建组合形状
		Transform localTransform = new Transform();//创建变换对象
		localTransform.setIdentity();//初始化变换 
		localTransform.origin.set(new Vector3f(0, 0, 0));//设置变换的起点
		comShape.addChildShape(localTransform, csa[0]);//添加子形状
		comShape.addChildShape(localTransform, csa[1]);//添加子形状	
		localTransform = new Transform();//创建变换对象
		localTransform.basis.rotX((float)Math.toRadians(90));//绕x旋转90
		comShape.addChildShape(localTransform, csa[2]);//添加子形状
		boolean isDynamic = (mass != 0f);//判断刚体是否可运动
		Vector3f localInertia = new Vector3f(0, 0, 0);//创建惯性向量
		if (isDynamic) //如果刚体可以运动
		{
			comShape.calculateLocalInertia(mass, localInertia);//计算刚体的惯性
		}
		Transform startTransform = new Transform();//创建刚体的初始变换对象
		startTransform.setIdentity();//初始化变换对象
		startTransform.origin.set(new Vector3f(cx, cy, cz));//设置变换的起点
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);//创建刚体的运动状态对象
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo//创建刚体信息对象
		(
				mass, myMotionState, comShape, localInertia
		);
		body = new RigidBody(rbInfo);//创建刚体对象
		body.setRestitution(0.6f);//设置反弹系数
		body.setFriction(0.8f); //设置摩擦系数
		dynamicsWorld.addRigidBody(body);//将刚体添加进物理世界
		tc=new TexCube(halfSize,mProgram[0]);//创建立方体
		cl=new Cylinder(halfSize/2,halfSize*3.6f,16,mProgram[0],mv);//创建圆柱		
		this.halfSize=halfSize;//保存半长
		this.mv=mv;//保存MySurfaceView的引用
	}
	public void drawSelf(int[] texIda,int[] texIdb)//绘制方法
	{
		int texId1=texIda[0];//立方体运动时的纹理
		int texId2=texIdb[1];//圆柱运动时的纹理
		if(!body.isActive())
		{
			texId1=texIda[1];//立方体静止时的纹理
			texId2=texIdb[0];//圆柱静止时的纹理
		}
		MatrixState.pushMatrix();//保存现场
		Transform trans=body.getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
		MatrixState.translate(trans.origin.x,trans.origin.y, trans.origin.z);//进行移位变换
		Quat4f ro=trans.getRotation(new Quat4f());//进行旋转变换
		if(ro.x!=0||ro.y!=0||ro.z!=0)
		{
			if(!Float.isNaN(ro.x)&&!Float.isNaN(ro.x)&&!Float.isNaN(ro.x)
					&&!Float.isInfinite(ro.x)&&!Float.isInfinite(ro.x)&&!Float.isInfinite(ro.x)){
				float[] fa=SYSUtil.fromSYStoAXYZ(ro);
				MatrixState.rotate(fa[0], fa[1], fa[2], fa[3]);
			}
		}
		tc.drawSelf( texId1);//绘制立方体
		MatrixState.pushMatrix();//保存现场
		MatrixState.rotate(90,0, 0, 1);//执行旋转
		MatrixState.translate(0, -halfSize*1.8f, 0);//执行平移
		cl.drawSelf(texId2,  texId2,  texId2);//绘制X水平圆柱	
		MatrixState.popMatrix();//恢复现场
		MatrixState.pushMatrix();//保存现场
		MatrixState.translate(0, -halfSize*1.8f, 0);//执行平移
		cl.drawSelf(texId2,  texId2,  texId2);//绘制Y垂直圆柱
		MatrixState.popMatrix();//恢复现场
		MatrixState.popMatrix();//恢复现场
	}
}
