package com.bn.pp1;

import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Sample2_1_Activity extends Activity {
	SoundPool sp; // 声明SoundPool的引用
	HashMap<Integer, Integer> hm; // 声明一个HashMap来存放声音文件
	int currStreamId;// 当前正播放的streamId

	@Override
	// 重写onCreate方法
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); // 设置layout
		initSoundPool(); // 初始化声音池的方法
		Button b1 = (Button) this.findViewById(R.id.Button01); // 获取播放按钮
		b1.setOnClickListener // 为播放按钮添加监听器
		(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playSound(1, 0); // 播放1号声音资源，且播放一次
				// 提示播放即时音效
				Toast.makeText(getBaseContext(), "播放即时音效", Toast.LENGTH_SHORT)
						.show();
			}
		});
		Button b2 = (Button) this.findViewById(R.id.Button02); // 获取停止按钮
		b2.setOnClickListener // 为停止按钮添加监听器
		(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sp.stop(currStreamId); // 停止正在播放的某个声音
				// 提示停止播放
				Toast.makeText(getBaseContext(), "停止播放即时音效", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	// 初始化声音池的方法
	public void initSoundPool() {
		sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); // 创建SoundPool对象
		hm = new HashMap<Integer, Integer>(); // 创建HashMap对象
		hm.put(1, sp.load(this, R.raw.musictest, 1)); // 加载声音文件musictest并且设置为1号声音放入hm中
	}

	// 播放声音的方法
	public void playSound(int sound, int loop) { // 获取AudioManager引用
		AudioManager am = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		// 获取当前音量
		float streamVolumeCurrent = am
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		// 获取系统最大音量
		float streamVolumeMax = am
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// 计算得到播放音量
		float volume = streamVolumeCurrent / streamVolumeMax;
		// 调用SoundPool的play方法来播放声音文件
		currStreamId = sp.play(hm.get(sound), volume, volume, 1, loop, 1.0f);
	}
}