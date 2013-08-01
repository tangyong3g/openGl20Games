package com.bn.pp8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Explosion {
	MySurfaceView gameView;
	private Bitmap[] bitmaps;// 位图
	float x;// x方向位移
	float y;// y方向位移
	private int anmiIndex = 0;// 爆炸动画帧索引

	public Explosion(MySurfaceView gameView, Bitmap[] bitmaps, float x, float y) {
		this.gameView = gameView;
		this.bitmaps = bitmaps;
		this.x = x;
		this.y = y;
	}

	// 绘制背景的方法
	public void drawSelf(Canvas canvas, Paint paint) {
		if (anmiIndex >= bitmaps.length - 1) {// 如果动画播放完毕，不再绘制爆炸效果
			return;
		}
		canvas.drawBitmap(bitmaps[anmiIndex], x, y, paint);// 绘制数组中某一幅图
		anmiIndex++;// 当前下标加1
	}
}
