package com.bn.pp8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Bullet {
	MySurfaceView gameView;
	private Bitmap bitmap;// 位图
	private Bitmap[] bitmaps;// 爆炸动画图组
	float x;// x方向位移
	float y;// y方向位移
	float vx;// x方向速度
	float vy;// y方向速度
	private float t = 0;// 时间
	private float timeSpan = 0.5f;// 时间间隔
	int size;// 子弹尺寸
	boolean explodeFlag = false;// 是否绘制子弹的标记
	Explosion mExplosion;// 爆炸对象引用

	// 构造器
	public Bullet(MySurfaceView gameView, Bitmap bitmap, Bitmap[] bitmaps,
			float x, float y, float vx, float vy) {
		this.gameView = gameView;// 成员变量赋值
		this.bitmap = bitmap;
		this.bitmaps = bitmaps;
		this.x = x;// 成员变量赋值
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		size = bitmap.getHeight();// 获得图片的高度
	}

	// 绘制子弹的方法
	public void drawSelf(Canvas canvas, Paint paint) {
		if (explodeFlag && mExplosion != null) {// 如果已经爆炸，绘制爆炸动画
			mExplosion.drawSelf(canvas, paint);
		} else {
			go();// 子弹前进
			canvas.drawBitmap(bitmap, x, y, paint);// 绘制子弹
		}
	}

	// 子弹前进的方法
	public void go() {
		x += vx * t;// 水平方向匀速直线运动
		y += vy * t + 0.5f * Constant.G * t * t;// 竖直方向匀加速直线运动
		if (x >= Constant.EXPLOSION_X || y >= Constant.SCREEN_HEIGHT) {// 子弹在特定位置爆炸
			mExplosion = new Explosion(gameView, bitmaps, x, y);// 创建爆炸对象
			explodeFlag = true;// 不再绘制子弹
			return;
		}
		t += timeSpan;// 时间间隔
	}
}
