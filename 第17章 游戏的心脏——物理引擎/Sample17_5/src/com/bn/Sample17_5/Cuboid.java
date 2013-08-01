package com.bn.Sample17_5;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class Cuboid {
	
	TexRect frontBackRect;//前后的长方形
	TexRect topBottomRect;//上下的长方形
	TexRect leftRightRect;//左右的长方形
	float halfX;
	float halfY;
	float halfZ;
	
	RigidBody body;
	
	public Cuboid
		(
			DiscreteDynamicsWorld dynamicsWorld,CollisionShape boxShape,
			float mass,
			float cx,float cy,float cz,
			MySurfaceView mv,
			float halfX,	//x
			float halfY,	//y
			float halfZ	//z
		){
		this.halfX=halfX;
		this.halfY = halfY;
		this.halfZ = halfZ;
		frontBackRect = new TexRect(mv,1,halfX,halfY);
		topBottomRect = new TexRect(mv,1,halfX,halfZ);
		leftRightRect = new TexRect(mv,1,halfZ,halfY);
		boolean isDynamic = (mass!=0);
		Vector3f localInertia = new Vector3f(0,0,0);
		if(isDynamic){
			boxShape.calculateLocalInertia(mass, localInertia);
		}
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(new Vector3f(cx,cy,cz));
		DefaultMotionState myMotionState = new DefaultMotionState(transform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo
		(
				mass, myMotionState, boxShape, localInertia
		);
		body = new RigidBody(rbInfo);
		body.setRestitution(0.6f);
		body.setFriction(0.8f);
		dynamicsWorld.addRigidBody(body);
	}
  
	public void drawSelf(int texId){
		
		MatrixState.pushMatrix();
		Transform trans = body.getMotionState().getWorldTransform(new Transform());
		MatrixState.translate(trans.origin.x,trans.origin.y, trans.origin.z);
		Quat4f ro=trans.getRotation(new Quat4f());
		if(ro.x!=0||ro.y!=0||ro.z!=0)
		{
			float[] fa=SYSUtil.fromSYStoAXYZ(ro);
			MatrixState.rotate(fa[0], fa[1], fa[2], fa[3]);
		}
		
		//绘制上面
		MatrixState.pushMatrix();
	    MatrixState.translate(0, halfY, 0);
	    MatrixState.rotate(-90, 1, 0, 0);
	    topBottomRect.drawSelf(texId);
		MatrixState.popMatrix();
		
		//绘制下面
		MatrixState.pushMatrix();
	    MatrixState.translate(0, -halfY, 0);
	    MatrixState.rotate(90, 1, 0, 0);
	    topBottomRect.drawSelf(texId);
		MatrixState.popMatrix();
		
		//绘制左面
		MatrixState.pushMatrix();
	    MatrixState.translate(-halfX, 0, 0);
	    MatrixState.rotate(-90, 0, 1, 0);
	    leftRightRect.drawSelf(texId);
		MatrixState.popMatrix();
		
		//绘制右面
		MatrixState.pushMatrix();
	    MatrixState.translate(halfX, 0, 0);
	    MatrixState.rotate(90, 0, 1, 0);
	    leftRightRect.drawSelf(texId);
		MatrixState.popMatrix();
		 
		//绘制前面
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, halfZ);
		frontBackRect.drawSelf(texId);
		MatrixState.popMatrix();
		
		//绘制后面
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, -halfZ);
		MatrixState.rotate(180, 0, 1, 0);
		frontBackRect.drawSelf(texId);
		MatrixState.popMatrix();
		
		MatrixState.popMatrix();
	}
}
