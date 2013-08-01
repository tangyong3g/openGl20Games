package com.bn.pp8;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {
	private boolean flag = true;//线程工作标志位
	private int sleepSpan = 100;//线程休眠时间
	MySurfaceView gameView;//父界面引用
	SurfaceHolder surfaceHolder;//surfaceHolder引用

	public DrawThread(MySurfaceView gameView) {//构造器
		this.gameView = gameView;
		this.surfaceHolder = gameView.getHolder();
	}

	public void run() {
		Canvas c;//声明画布
		while (this.flag) {
			c = null;
			try {
				// 锁定整个画布，在内存要求比较高的情况下，建议参数不要为null
				c = this.surfaceHolder.lockCanvas(null);
				synchronized (this.surfaceHolder) {
					gameView.onDraw(c);// 绘制
				}
			} finally {
				if (c != null) {// 并释放锁
					this.surfaceHolder.unlockCanvasAndPost(c);
				}
			}
			try {
				Thread.sleep(sleepSpan);// 睡眠指定毫秒数
			} catch (Exception e) {
				e.printStackTrace();// 打印堆栈信息
			}
		}
	}

	public void setFlag(boolean flag) {//设置标志位的方法
		this.flag = flag;
	}
}
