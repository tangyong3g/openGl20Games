package com.bn.Sample15_9;
import android.opengl.Matrix;

public abstract class HitObject {
	Color3f color;
	private float[] myMatrix;//变换矩阵

	Camera cam;

	public abstract boolean hit(Ray ray,Intersection inter);	//判断交点的方法
	public abstract boolean hit(Ray ray);	//只判断是否相交的方法
	public Color3f getColor() {
		return color;
	}
	//返回光线在t时刻的位置的方法(使用变换前的光线)
	public Point3 rayPos(Ray r,double t){
		return cam.eye.addVec(r.dir.multiConst((float)t));	//eye+dir*t  得到当前点的坐标	
	}
	//获取变换后的光线（将r按逆变换矩阵变换后赋值给genRay，(左乘变换矩阵的逆矩阵)）
	public void xfrmRay(Ray genRay, float[] invTransf, Ray r){		
		//求变换后的start
		float[] genStart = new float[4];
		Matrix.multiplyMV(genStart, 0, invTransf, 0, r.start.toQici4(), 0);
		genRay.start.set(genStart);//只取齐次坐标和前三个分量作为start
		
		//求变换后的dir
		float[] genDir = new float[4];
		Matrix.multiplyMV(genDir, 0, invTransf, 0, r.dir.toQici4(), 0);
		genRay.dir.set(genDir);//只取齐次坐标和前三个分量作为dir
	}
	//获取变换后的法向量(左乘变换矩阵的逆转置矩阵)
	public void xfrmNormal(Vector3 genNormal, float[] invTranspM, Vector3 normal){
		//求变换后的Normal
		float[] tmpNormal = new float[4];
		Matrix.multiplyMV(tmpNormal, 0, invTranspM, 0, normal.toQici4(), 0);
		genNormal.set(tmpNormal);//只取齐次坐标和前三个分量作为normal
	}

	//通过变换后的点求变换前的点的方法：乘以逆矩阵
	public Point3 xfrmPtoPreP(Point3 P){
		//通过逆变换，得到变换之前的点
		float[] inverM = getInvertMatrix();//获取逆变换矩阵
		float[] preP = new float[4];
		Matrix.multiplyMV(preP, 0, inverM, 0, P.toQici4(), 0);//求变换前的点
		return new Point3(preP);//变换前的点就是变换之前的法向量
	}
	//初始化变换矩阵的方法
	public void initMyMatrix() {
		myMatrix = new float[16];
	    Matrix.setIdentityM(myMatrix, 0);	    
	}
	
	//获取对象的变换矩阵的方法
	public float[] getMatrix(){		
		return myMatrix;
	}
	//获取对象的逆矩阵的方法
	public float[] getInvertMatrix(){
		float[] invM = new float[16];
		Matrix.invertM(invM, 0, myMatrix, 0);//求逆矩阵
		return invM;
	}
	//获取对象的逆转置矩阵的方法
	public float[] getInvertTransposeMatrix(){
		float[] invTranspM = new float[16];
		Matrix.transposeM(invTranspM, 0, myMatrix, 0);//求转置矩阵
		Matrix.invertM(invTranspM, 0, invTranspM, 0);//求逆转置矩阵，两个参数要相同！
		return invTranspM;
	}
	// 设置沿xyz轴移动
	public void translate(float x, float y, float z) {
		Matrix.translateM(myMatrix, 0, x, y, z);
	}
	// 设置绕xyz轴旋转
	public void rotate(float angle, float x, float y, float z) {
		Matrix.rotateM(myMatrix, 0, angle, x, y, z);
	}
	// 设置沿xyz轴缩放
	public void scale(float x, float y, float z) {
		Matrix.scaleM(myMatrix, 0, x, y, z);
	}
}