package com.bn.Sample17_7;
import javax.vecmath.Vector3f;
import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CapsuleShapeX;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
public class Doll {

	MySurfaceView mv;//MySurfaceView对象的引用
	DynamicsWorld dynamicsWorld;//物理世界引用
	CollisionShape[] bodyShapes= new CollisionShape[BodyPartIndex.BODYPART_COUNT.ordinal()];;//碰撞形状数组 
	RigidBody[] rigidBodies = new RigidBody[BodyPartIndex.BODYPART_COUNT.ordinal()];//刚体数组
	LoadedObjectVertexNormal[] bodyForDraws;//用于绘制的物体的引用
	//========属性值===========
	float mass=1;//刚体的质量
	//=========位置==========
	float bodyCenterH=7;//整个人偶中心的高度
	//===================胶囊的尺寸=============
	//头部
	float headR=0.5f;//头部胶囊两端半球的半径
	float headH=1.25f-2*headR;//头部胶囊中间圆柱的长度
	float headTH=1.25f;//头部胶囊总长度
	//脊椎
	float spineR=0.7f;//脊椎胶囊两端半球的半径
	float spineH=2.0f-2*spineR;//脊椎胶囊中间圆柱的长度
	float spineTH=2.0f;//脊椎胶囊总长度
	//右大臂
	float rightUpperArmR=0.3f;//右大臂胶囊两端半球的半径
	float rightUpperArmH=2.0f-2*rightUpperArmR;//右大臂胶囊中间圆柱的长度
	float rightUpperArmTH=2.0f;//右大臂胶囊总长度
	//右小臂
	float rightLowerArmR=0.3f;//右小臂胶囊两端半球的半径
	float rightLowerArmH=1.7f-2*rightLowerArmR;//右小臂胶囊中间圆柱的长度
	float rightLowerArmTH=1.7f;//右小臂胶囊总长度
	//左大臂
	float leftUpperArmR=0.3f;//左大臂胶囊两端半球的半径
	float leftUpperArmH=2.0f-2*leftUpperArmR;//左大臂胶囊中间圆柱的长度
	float leftUpperArmTH=2.0f;//左大臂胶囊总长度
	//左小臂
	float leftLowerArmR=0.3f;//左小臂胶囊两端半球的半径
	float leftLowerArmH=1.7f-2*leftLowerArmR;//左小臂胶囊中间圆柱的长度
	float leftLowerArmTH=1.7f;//左小臂胶囊总长度
	//骨盆
	float pelvisR=0.8f;//骨盆胶囊两端半球的半径
	float pelvisH=2.5f-pelvisR*2;//骨盆胶囊中间圆柱的长度
	float pelvisTH=2.5f;//骨盆胶囊总长度
	//右大腿
	float rightUpperLegR=0.3f;//右大腿胶囊两端半球的半径
	float rightUpperLegH=1.7f-rightUpperLegR*2;//右大腿胶囊中间圆柱的长度
	float rightUpperLegTH=1.7f;//右大腿胶囊总长度
	//左大腿
	float leftUpperLegR=0.3f;//左大腿胶囊两端半球的半径
	float leftUpperLegH=1.7f-leftUpperLegR*2;//左大腿胶囊中间圆柱的长度
	float leftUpperLegTH=1.7f;//左大腿胶囊总长度
	//左小腿
	float leftLowerLegR=0.3f;//左小腿胶囊两端半球的半径
	float leftLowerLegH=2.0f-2*leftLowerLegR;//左小腿胶囊中间圆柱的长度
	float leftLowerLegTH=2.0f;//左小腿胶囊总长度
	//右小腿
	float rightLowerLegR=0.3f;//右小腿胶囊两端半球的半径
	float rightLowerLegH=2.0f-2*rightLowerLegR;//右小腿胶囊中间圆柱的长度
	float rightLowerLegTH=2.0f;//右小腿胶囊总长度
	
	public Doll(MySurfaceView mv,DynamicsWorld dynamicsWorld,LoadedObjectVertexNormal[] bodyForDraws){
		this.mv=mv;
		this.dynamicsWorld=dynamicsWorld;
		this.bodyForDraws=bodyForDraws;
		initShapes();
		initRigidBodys();
	}
	
	public void initShapes(){
		bodyShapes[BodyPartIndex.BODYPART_HEAD.ordinal()] = new CapsuleShape(headR,headH);
		bodyShapes[BodyPartIndex.BODYPART_SPINE.ordinal()] = new CapsuleShape(spineR,spineH);
		bodyShapes[BodyPartIndex.BODYPART_RIGHT_UPPER_ARM.ordinal()] = new CapsuleShapeX(rightUpperArmR,rightUpperArmH);
		bodyShapes[BodyPartIndex.BODYPART_RIGHT_LOWER_ARM.ordinal()] = new CapsuleShapeX(rightLowerArmR,rightLowerArmH);
		bodyShapes[BodyPartIndex.BODYPART_LEFT_UPPER_ARM.ordinal()] = new CapsuleShapeX(leftUpperArmR,leftUpperArmH);
		bodyShapes[BodyPartIndex.BODYPART_LEFT_LOWER_ARM.ordinal()] = new CapsuleShapeX(leftLowerArmR,leftLowerArmH);
		bodyShapes[BodyPartIndex.BODYPART_PELVIS.ordinal()] = new CapsuleShape(pelvisR,pelvisH);
		bodyShapes[BodyPartIndex.BODYPART_RIGHT_UPPER_LEG.ordinal()] = new CapsuleShape(rightUpperLegR,rightUpperLegH);
		bodyShapes[BodyPartIndex.BODYPART_LEFT_UPPER_LEG.ordinal()] = new CapsuleShape(leftUpperLegR,leftUpperLegH);
		bodyShapes[BodyPartIndex.BODYPART_LEFT_LOWER_LEG.ordinal()] = new CapsuleShape(leftLowerLegR,leftLowerLegH);
		bodyShapes[BodyPartIndex.BODYPART_RIGHT_LOWER_LEG.ordinal()] = new CapsuleShape(rightLowerLegR,rightLowerLegH);
	}
	
	public void initRigidBodys(){
		//====================创建刚体=========================
		//头
		Transform tempTrans = new Transform();
		tempTrans.setIdentity();
		tempTrans.origin.set(0,bodyCenterH+spineTH+headTH/2,0);
		rigidBodies[BodyPartIndex.BODYPART_HEAD.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_HEAD.ordinal()]);
		//脊椎
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(0,bodyCenterH+spineTH/2,0));
		rigidBodies[BodyPartIndex.BODYPART_SPINE.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_SPINE.ordinal()]);
		//右大臂
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(spineR+rightUpperArmTH/2,bodyCenterH+spineTH-spineR,0));
		rigidBodies[BodyPartIndex.BODYPART_RIGHT_UPPER_ARM.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_RIGHT_UPPER_ARM.ordinal()]);
		//右小臂
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(spineR+rightUpperArmTH+rightLowerArmTH/2,bodyCenterH+spineTH-spineR,0));
		rigidBodies[BodyPartIndex.BODYPART_RIGHT_LOWER_ARM.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_RIGHT_LOWER_ARM.ordinal()]);
		//左大臂
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(-spineR-leftUpperArmTH/2,bodyCenterH+spineTH-spineR,0));
		rigidBodies[BodyPartIndex.BODYPART_LEFT_UPPER_ARM.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_LEFT_UPPER_ARM.ordinal()]);
		//左小臂
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(-spineR-leftUpperArmTH-leftLowerArmTH/2,bodyCenterH+spineTH-spineR,0));
		rigidBodies[BodyPartIndex.BODYPART_LEFT_LOWER_ARM.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_LEFT_LOWER_ARM.ordinal()]);
		//骨盆
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(0,bodyCenterH-pelvisTH/2,0));
		rigidBodies[BodyPartIndex.BODYPART_PELVIS.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_PELVIS.ordinal()]);
		//右大腿
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(pelvisR+rightUpperLegR,bodyCenterH-pelvisTH-rightUpperLegH/2+pelvisR,0));
		rigidBodies[BodyPartIndex.BODYPART_RIGHT_UPPER_LEG.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_RIGHT_UPPER_LEG.ordinal()]);
		//左大腿
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(-pelvisR-leftUpperLegR,bodyCenterH-pelvisTH-rightUpperLegH/2+pelvisR,0));
		rigidBodies[BodyPartIndex.BODYPART_LEFT_UPPER_LEG.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_LEFT_UPPER_LEG.ordinal()]);
		//左小腿
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(-pelvisR-leftUpperLegR,bodyCenterH-pelvisTH-leftUpperLegTH-leftLowerLegTH/2+pelvisR,0));
		rigidBodies[BodyPartIndex.BODYPART_LEFT_LOWER_LEG.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_LEFT_LOWER_LEG.ordinal()]);
		//右小腿
		tempTrans.setIdentity();
		tempTrans.origin.set(new Vector3f(pelvisR+rightUpperLegR,bodyCenterH-pelvisTH-rightUpperLegTH-rightLowerLegTH/2+pelvisR,0));
		rigidBodies[BodyPartIndex.BODYPART_RIGHT_LOWER_LEG.ordinal()] = addRigidBody(mass, tempTrans, bodyShapes[BodyPartIndex.BODYPART_RIGHT_LOWER_LEG.ordinal()]);
		//============添加约束=============
		Generic6DofConstraint joint6DOF;
		Transform localA = new Transform(); 
		Transform localB = new Transform();
		//头部和脊椎约束
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(0f, -headTH/2, 0f);
		localB.origin.set(0,spineTH/2,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_HEAD.ordinal()], rigidBodies[BodyPartIndex.BODYPART_SPINE.ordinal()], localA, localB, true);
		Vector3f limitTrans = new Vector3f();
		limitTrans.set(-BulletGlobals.SIMD_PI * 0.1f, -BulletGlobals.FLT_EPSILON, -BulletGlobals.SIMD_PI * 0.1f);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set(BulletGlobals.SIMD_PI * 0.2f, BulletGlobals.FLT_EPSILON, BulletGlobals.SIMD_PI * 0.2f);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
		//右大臂和脊椎
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(spineR,spineTH/2-rightUpperArmR*2, 0f);
		localB.origin.set(-rightUpperArmTH/2,0,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_SPINE.ordinal()], rigidBodies[BodyPartIndex.BODYPART_RIGHT_UPPER_ARM.ordinal()], localA, localB, true);
		limitTrans.set(-BulletGlobals.FLT_EPSILON,-BulletGlobals.SIMD_PI * 0.1f, -BulletGlobals.SIMD_PI * 0.4f);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set(BulletGlobals.FLT_EPSILON,BulletGlobals.SIMD_PI * 0.1f,  BulletGlobals.SIMD_PI * 0.4f);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
		//右大臂和右小臂
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(rightUpperArmTH/2,0, 0f);
		localB.origin.set(-rightLowerArmTH/2,0,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_RIGHT_UPPER_ARM.ordinal()], rigidBodies[BodyPartIndex.BODYPART_RIGHT_LOWER_ARM.ordinal()], localA, localB, true);
		limitTrans.set(-BulletGlobals.FLT_EPSILON,-BulletGlobals.SIMD_PI * 0.4f,  -BulletGlobals.SIMD_PI * 0.05f);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set(BulletGlobals.FLT_EPSILON,BulletGlobals.SIMD_PI * 0.4f,  BulletGlobals.SIMD_PI * 0.05f);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
		//左大臂和脊椎
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(-spineR,spineTH/2-leftUpperArmR*2, 0f);
		localB.origin.set(leftUpperArmTH/2,0,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_SPINE.ordinal()], rigidBodies[BodyPartIndex.BODYPART_LEFT_UPPER_ARM.ordinal()], localA, localB, true);
		limitTrans.set( -BulletGlobals.FLT_EPSILON,-BulletGlobals.SIMD_PI * 0.1f, -BulletGlobals.SIMD_PI * 0.4f);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set( BulletGlobals.FLT_EPSILON,BulletGlobals.SIMD_PI * 0.1f, BulletGlobals.SIMD_PI * 0.4f);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
		//左小臂和左大臂
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(-leftUpperArmTH/2,0, 0f);
		localB.origin.set(leftLowerArmTH/2,0,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_LEFT_UPPER_ARM.ordinal()], rigidBodies[BodyPartIndex.BODYPART_LEFT_LOWER_ARM.ordinal()], localA, localB, true);
		limitTrans.set(-BulletGlobals.FLT_EPSILON,-BulletGlobals.SIMD_PI * 0.4f, -BulletGlobals.SIMD_PI * 0.05f);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set(BulletGlobals.FLT_EPSILON,BulletGlobals.SIMD_PI * 0.4f,  BulletGlobals.SIMD_PI * 0.05f);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
		//脊椎和骨盆
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(0,-spineTH/2,0f);
		localB.origin.set(0,pelvisTH/2,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_SPINE.ordinal()], rigidBodies[BodyPartIndex.BODYPART_PELVIS.ordinal()], localA, localB, true);
		limitTrans.set(-BulletGlobals.SIMD_PI * 0.2f, -BulletGlobals.SIMD_PI/2, -BulletGlobals.SIMD_PI * 0.2f);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set(BulletGlobals.SIMD_PI * 0.2f, BulletGlobals.SIMD_PI/2, BulletGlobals.SIMD_PI * 0.2f);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
		//骨盆和右大腿
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(pelvisR+rightUpperLegR,-pelvisH/2,0f);
		localB.origin.set(0,rightUpperLegH/2,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_PELVIS.ordinal()], rigidBodies[BodyPartIndex.BODYPART_RIGHT_UPPER_LEG.ordinal()], localA, localB, true);
		limitTrans.set(-BulletGlobals.SIMD_PI * 0.1f, -BulletGlobals.SIMD_PI * 0.1f, -BulletGlobals.SIMD_PI * 0.2f);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set(BulletGlobals.SIMD_PI * 0.1f, BulletGlobals.SIMD_PI * 0.1f, BulletGlobals.SIMD_PI * 0.2f);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
		//盆骨和左大腿
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(-pelvisR-leftUpperLegR,-pelvisH/2,0f);
		localB.origin.set(0,rightUpperLegH/2,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_PELVIS.ordinal()], rigidBodies[BodyPartIndex.BODYPART_LEFT_UPPER_LEG.ordinal()], localA, localB, true);
		limitTrans.set(-BulletGlobals.SIMD_PI * 0.1f, -BulletGlobals.SIMD_PI * 0.1f, -BulletGlobals.SIMD_PI * 0.2f);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set(BulletGlobals.SIMD_PI * 0.1f, BulletGlobals.SIMD_PI * 0.1f, BulletGlobals.SIMD_PI * 0.2f);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
		//左大腿和左小腿
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(0,-leftUpperLegTH/2,0f);
		localB.origin.set(0,leftLowerLegTH/2,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_LEFT_UPPER_LEG.ordinal()], rigidBodies[BodyPartIndex.BODYPART_LEFT_LOWER_LEG.ordinal()], localA, localB, true);
		limitTrans.set(-BulletGlobals.SIMD_PI * 0.5f, -BulletGlobals.FLT_EPSILON, -BulletGlobals.FLT_EPSILON);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set(BulletGlobals.FLT_EPSILON, BulletGlobals.FLT_EPSILON, BulletGlobals.FLT_EPSILON);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
		//右大腿和右小腿
		localA.setIdentity();
		localB.setIdentity();
		localA.origin.set(0,-rightUpperLegTH/2,0f);
		localB.origin.set(0,rightLowerLegTH/2,0);
		joint6DOF = new Generic6DofConstraint(rigidBodies[BodyPartIndex.BODYPART_RIGHT_UPPER_LEG.ordinal()], rigidBodies[BodyPartIndex.BODYPART_RIGHT_LOWER_LEG.ordinal()], localA, localB, true);
		limitTrans.set(-BulletGlobals.SIMD_PI * 0.5f, -BulletGlobals.FLT_EPSILON, -BulletGlobals.FLT_EPSILON);
		joint6DOF.setAngularLowerLimit(limitTrans);
		limitTrans.set(BulletGlobals.FLT_EPSILON, BulletGlobals.FLT_EPSILON, BulletGlobals.FLT_EPSILON);
		joint6DOF.setAngularUpperLimit(limitTrans);
		dynamicsWorld.addConstraint(joint6DOF, true);
	}
	private RigidBody addRigidBody(float mass, Transform startTransform, CollisionShape shape) {
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f();
		localInertia.set(0f, 0f, 0f);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);
		rbInfo.additionalDamping = true;
		RigidBody body = new RigidBody(rbInfo);
		body.setRestitution(0.0f);
		body.setFriction(0.8f);
		dynamicsWorld.addRigidBody(body);
		return body;
	}
	
	public void drawSelf(int checkedIndex){
		MatrixState.pushMatrix();
		for(int i=0;i<bodyForDraws.length;i++){
			LoadedObjectVertexNormal lovo = bodyForDraws[i];
			if(i==checkedIndex){
				lovo.changeColor(true);
				lovo.drawSelf(rigidBodies[i]);
			}else{
				lovo.changeColor(false);
				lovo.drawSelf(rigidBodies[i]);
			}
		}
		MatrixState.popMatrix();
	}
}
