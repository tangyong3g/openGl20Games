package com.bn.Sample13_3;
/*
 * 叶子的控制类 
 */
public class TreeLeavesControl implements Comparable<TreeLeavesControl>
{
	//叶子的位置
	float positionX;
	float positionY;
	float positionZ;
	//叶子
	TreeLeaves treeLeaves;
	public TreeLeavesControl(float positionX,float positionY,float positionZ,TreeLeaves treeLeaves)
	{
		this.positionX=positionX;
		this.positionY=positionY;
		this.positionZ=positionZ;
		this.treeLeaves=treeLeaves;
	}
	public void drawSelf(int tex_leavesId,float bend_R,float wind_direction)
	{
		MatrixState.pushMatrix();
		MatrixState.translate(positionX, positionY, positionZ);
		//在这里需要计算叶子相对于树干来说的偏移位置和旋转方向
		//---------------------------
		float curr_height=Constant.leaves_absolute_height;
		//树干最高点的位置 
		float result[]=resultPoint(wind_direction,bend_R,0,curr_height,0);
		//需要的旋转轴
		//需要旋转的角度
		MatrixState.translate(result[0], result[1], result[2]);
		MatrixState.rotate(result[5], result[3],0,-result[4]);
		//---------------------------------
		treeLeaves.drawSelf(tex_leavesId);
		MatrixState.popMatrix();
	}
	 //这里对每片树叶距离摄像机的远近距离进行 排序, 从大到小排序
	@Override
	public int compareTo(TreeLeavesControl another) 
	{
		//当前树叶距离摄像机的距离
		float distanceX=(this.positionX+this.treeLeaves.centerX-GameSurfaceView.cx)*(this.positionX+this.treeLeaves.centerX-GameSurfaceView.cx);
		float distanceZ=(this.positionZ+this.treeLeaves.centerZ-GameSurfaceView.cz)*(this.positionZ+this.treeLeaves.centerZ-GameSurfaceView.cz);
		//比较点距离摄像机的距离
		float distanceOX=(another.positionX+another.treeLeaves.centerX-GameSurfaceView.cx)*(another.positionX+another.treeLeaves.centerX-GameSurfaceView.cx);
		float distanceOZ=(another.positionZ+another.treeLeaves.centerZ-GameSurfaceView.cz)*(another.positionZ+another.treeLeaves.centerZ-GameSurfaceView.cz);
		return (distanceX+distanceZ)>(distanceOX+distanceOZ)?-1:1;
	}
	//生成最后顶点的位置
	public float[] resultPoint(float direction_degree,float currBend_R,float pointX,float pointY,float pointZ)//currBend_R代表当前的风向,pointHeight当前点的高度
	{
		float []position=new float[6];
		//计算当前的弧度
		float curr_radian=pointY/currBend_R;
		//计算当前点的结果高度
		float result_Y=(float) (currBend_R*Math.sin(curr_radian));
		//计算当前点的增加的长度
		float increase=(float) (currBend_R-currBend_R*Math.cos(curr_radian));
		//计算当前点最后的x坐标
		float result_X=(float) (pointX+increase*Math.sin(Math.toRadians(direction_degree)));
		//计算当前点最后的z坐标
		float result_Z=(float) (pointZ+increase*Math.cos(Math.toRadians(direction_degree)));
		//最后结果
		position[0]=result_X;
		position[1]=result_Y;
		position[2]=result_Z;
		//x向旋转轴
		position[3]=(float) Math.cos(Math.toRadians(direction_degree));
		//z向旋转轴
		position[4]=(float) Math.sin(Math.toRadians(direction_degree));
		//旋转角度
		position[5]= (float) Math.toDegrees(curr_radian);
		return position;
	}

}
