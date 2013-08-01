package com.bn.Sample8_7;

/*
 * 整体建筑物
 */

public class Building {
	float scale;//建筑的大小
	
	Top top;//穹顶	
	Tower tower;//塔
	TowerTop towertop;
	Cube cube;//立方体
	Cube cube2;//立方体
	Cube cube3;//立方体
	Cube cube4;//立方体
	
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
	
	Building( MySurfaceView mv,
			float scale,//建筑的大小
			int nCol,int nRow)
	{
		this.scale=scale;
		//创建对象
		top=new Top(mv,0.5f*scale,nCol,nRow);//穹顶
		tower=new Tower(mv,1*scale,nCol,nRow);//塔
		
		towertop=new TowerTop(mv,1*scale,nCol,nRow);//塔
		
		
        cube=new Cube(mv,1.4f*scale,new float[]{2,3f,3f} );//立方体
        cube2=new Cube(mv,1.4f*scale,new float[]{2f,3f,2f} );//立方体
        cube3=new Cube(mv,1.4f*scale,new float[]{1.4f,3f,1.4f} );//立方体
        
        cube4=new Cube(mv,1.4f*scale,new float[]{10f,1f,10f} );//立方体

	}
	public void drawSelf(int texId)
	{
		
   	 	MatrixState.rotate(xAngle, 1, 0, 0);
   	 	MatrixState.rotate(yAngle, 0, 1, 0);
   	 	MatrixState.rotate(zAngle, 0, 0, 1);
		
		MatrixState.pushMatrix();
		
		//穹顶
		MatrixState.pushMatrix();
        MatrixState.translate(0f, 0f*scale, 0f);
        top.drawSelf(texId);//穹顶
        MatrixState.popMatrix();
        
		//****************************塔********************************
		MatrixState.pushMatrix();
        MatrixState.translate(6f*scale, -1.6f*scale, 6f*scale);
        tower.drawSelf(texId);//穹顶
        MatrixState.popMatrix();
        
		//塔
		MatrixState.pushMatrix();
        MatrixState.translate(-6f*scale, -1.6f*scale, 6f*scale);
        tower.drawSelf(texId);//穹顶
        MatrixState.popMatrix();
        
		//塔
		MatrixState.pushMatrix();
        MatrixState.translate(6f*scale, -1.6f*scale, -6f*scale);
        tower.drawSelf(texId);//穹顶
        MatrixState.popMatrix();
        
		//塔
		MatrixState.pushMatrix();
        MatrixState.translate(-6f*scale, -1.6f*scale, -6f*scale);
        tower.drawSelf(texId);//穹顶
        MatrixState.popMatrix();
        //****************************塔********************************
        
        //*******************塔顶************************
		MatrixState.pushMatrix();
        MatrixState.translate(-2.5f*scale, -3.8f*scale, 0f);
        towertop.drawSelf(texId);//穹顶
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
        MatrixState.translate(2.5f*scale, -3.8f*scale, 0f);
        towertop.drawSelf(texId);//穹顶
        MatrixState.popMatrix();
        
        //*******************塔顶************************
        
        //*********************立方体*********************************
		MatrixState.pushMatrix();
        MatrixState.translate(0f, -2.7f*scale, 0f);
        MatrixState.translate(0, 0.05f, 0f);
        cube.drawSelf(texId);
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
        MatrixState.translate(-2.0f*scale, -2.7f*scale, 0f);
        cube2.drawSelf(texId);
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
        MatrixState.translate(2.0f*scale, -2.7f*scale, 0f);
        cube2.drawSelf(texId);
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
        MatrixState.translate(-3.43f*scale, -2.7f*scale, 0f);
        MatrixState.translate(0, -0.05f, 0f);
        MatrixState.rotate(45, 0, 1, 0);
        cube3.drawSelf(texId);
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
        MatrixState.translate(3.43f*scale, -2.7f*scale, 0f);
        MatrixState.translate(0, -0.05f, 0f);
        MatrixState.rotate(45, 0, 1, 0);
        cube3.drawSelf(texId);
        MatrixState.popMatrix();
        
      //*********************立方体*********************************
        
		MatrixState.pushMatrix();
        MatrixState.translate(0f, -5f*scale, 0f);
        cube4.drawSelf(texId);
        MatrixState.popMatrix(); 
        
        MatrixState.popMatrix();
	}
}
