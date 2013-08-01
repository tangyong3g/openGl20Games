package com.bn.pp8;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * 2D SurfaceView
 * 
 */
public class MySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	Sample2_8_Activity activity;// activity的引用
	Paint paint;// 画笔引用
	DrawThread drawThread;// 绘制线程引用
	Bitmap bgBmp;//背景图片
	Bitmap bulletBmp;// 子弹位图
	Bitmap[] explodeBmps;//爆炸位图数组
	Bullet bullet;//子弹对象引用
	public MySurfaceView(Sample2_8_Activity activity) {//构造器
		super(activity);
		this.activity = activity;
		// 获得焦点并设置为可触控
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		getHolder().addCallback(this);// 注册回调接口
	}

	@Override
	protected void onDraw(Canvas canvas) {//绘制界面的方法
		super.onDraw(canvas);
		canvas.drawBitmap(bgBmp, 0, 0, paint);//绘制背景
		bullet.drawSelf(canvas, paint);//绘制子弹
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {//界面变化时调用的方法
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		paint = new Paint();// 创建画笔
		paint.setAntiAlias(true);// 打开抗锯齿
		//加载图片资源
		bulletBmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.bullet);
		bgBmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg);
		explodeBmps=new Bitmap[]{
				BitmapFactory.decodeResource(this.getResources(), R.drawable.explode0),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.explode1),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.explode2),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.explode3),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.explode4),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.explode5),
		};
		bullet = new Bullet(this, bulletBmp,explodeBmps,0,290,1.3f,-5.9f);//创建子弹对象
		drawThread = new DrawThread(this);//创建绘制线程
		drawThread.start();//启动绘制线程
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {//界面销毁时调用的方法
		drawThread.setFlag(false);//停止绘制线程
	}
}
