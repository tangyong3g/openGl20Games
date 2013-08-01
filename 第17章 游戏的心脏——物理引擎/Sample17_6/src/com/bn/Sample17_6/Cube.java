package com.bn.Sample17_6;

import static com.bn.Sample17_6.Constant.isNumber;

import javax.vecmath.Quat4f;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public class Cube {
	
	TexRect rect;//正方形
	float halfSize;
	
	RigidBody body;
	MySurfaceView mv;
	
	public Cube
		(
			MySurfaceView mv,
			float halfSize,
			RigidBody body
		){
		this.mv=mv;
		this.halfSize=halfSize;
		rect = new TexRect(mv,1,halfSize,halfSize);
		this.body=body;
	}
  
	public void drawSelf(int[] texIds,int index){
		int texId = texIds[index];
		 
		MatrixState.pushMatrix();
			MySurfaceView.init=false;
			Transform trans = body.getMotionState().getWorldTransform(new Transform());
			MatrixState.translate(trans.origin.x,trans.origin.y, trans.origin.z);
			Quat4f ro=trans.getRotation(new Quat4f());
			if(ro.x!=0||ro.y!=0||ro.z!=0)
			{
				float[] fa=SYSUtil.fromSYStoAXYZ(ro);
				if(isNumber(fa[0]+"") && isNumber(fa[1]+"") && isNumber(fa[2]+"")){
	 	 			MatrixState.rotate(fa[0], fa[1], fa[2], fa[3]);
	 			}
			}
		
		
		//绘制上面
		MatrixState.pushMatrix();
	    MatrixState.translate(0, halfSize, 0);
	    MatrixState.rotate(-90, 1, 0, 0);
	    rect.drawSelf(texId);
		MatrixState.popMatrix();
		
		//绘制下面
		MatrixState.pushMatrix();
	    MatrixState.translate(0, -halfSize, 0);
	    MatrixState.rotate(90, 1, 0, 0);
	    rect.drawSelf(texId);
		MatrixState.popMatrix();
		
		//绘制左面
		MatrixState.pushMatrix();
	    MatrixState.translate(-halfSize, 0, 0);
	    MatrixState.rotate(-90, 0, 1, 0);
	    rect.drawSelf(texId);
		MatrixState.popMatrix();
		
		//绘制右面
		MatrixState.pushMatrix();
	    MatrixState.translate(halfSize, 0, 0);
	    MatrixState.rotate(90, 0, 1, 0);
	    rect.drawSelf(texId);
		MatrixState.popMatrix();
		 
		//绘制前面
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, halfSize);
		rect.drawSelf(texId);
		MatrixState.popMatrix();
		
		//绘制后面
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, -halfSize);
		MatrixState.rotate(180, 0, 1, 0);
		rect.drawSelf(texId);
		MatrixState.popMatrix();
		
		MatrixState.popMatrix();
	}
}
