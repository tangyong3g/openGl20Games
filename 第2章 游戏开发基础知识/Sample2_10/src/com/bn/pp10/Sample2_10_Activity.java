package com.bn.pp10;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 用于显示对话的主Activity
 */
public class Sample2_10_Activity extends Activity {
	private EditText outEt;// 布局中的控件引用
	private Button sendBtn;
	private String connectedNameStr = null;// 已连接的设备名称
	private StringBuffer outSb;// 发送的字符信息
	private BluetoothAdapter btAdapter = null;// 本地蓝牙适配器
	private MyService myService = null;// Service引用
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 获取本地蓝牙适配器
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	@Override
	public void onStart() {
		super.onStart();
		// 如果蓝牙没有开启，提示开启蓝牙，并退出Activity
		if (!btAdapter.isEnabled()) {
			Toast.makeText(this, "请先开启蓝牙！", Toast.LENGTH_LONG).show();
			finish();
		} else {// 否则初始化聊天的控件
			if (myService == null)
				initChat();
		}
	}
	@Override
	public synchronized void onResume() {
		super.onResume();		
		if (myService != null) {// 创建并开启Service
			// 如果Service为空状态
			if (myService.getState() == MyService.STATE_NONE) {
				myService.start();// 开启Service
			}
		}
	}
	private void initChat() {
		outEt = (EditText) findViewById(R.id.edit_text_out);// 获取编辑文本框的引用
		// 获取发送按钮引用，并为其添加监听
		sendBtn = (Button) findViewById(R.id.button_send);
		sendBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 获取编辑文本框中的文本内容，并发送消息
				TextView view = (TextView) findViewById(R.id.edit_text_out);
				String message = view.getText().toString();
				sendMessage(message);
			}
		});
		myService = new MyService(this, mHandler);// 创建Service对象
		// 初始化存储发送消息的StringBuffer
		outSb = new StringBuffer("");
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (myService != null) {// 停止Service
			myService.stop();
		}
	}
	// 发送消息的方法
	private void sendMessage(String message) {
		// 先检查是否已经连接到设备
		if (myService.getState() != MyService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (message.length() > 0) {// 如果消息不为空再发送消息
			byte[] send = message.getBytes();// 获取发送消息的字节数组，并发送
			myService.write(send);
			// 消除StringBuffer和编辑文本框的内容
			outSb.setLength(0);
			outEt.setText(outSb);
		}
	}
	// 处理从Service发来的消息的Handler
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.MSG_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// 创建要发送的信息的字符串
				String readMessage = new String(readBuf, 0, msg.arg1);
				Toast.makeText(Sample2_10_Activity.this,
						connectedNameStr + ":  " + readMessage,
						Toast.LENGTH_LONG).show();
				break;
			case Constant.MSG_DEVICE_NAME:
				// 获取已连接的设备名称，并弹出提示信息
				connectedNameStr = msg.getData().getString(
						Constant.DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"已连接到 " + connectedNameStr, Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			// 如果设备列表Activity返回一个连接的设备
			if (resultCode == Activity.RESULT_OK) {
				// 获取设备的MAC地址
				String address = data.getExtras().getString(
						MyDeviceListActivity.EXTRA_DEVICE_ADDR);
				// 获取BLuetoothDevice对象
				BluetoothDevice device = btAdapter
						.getRemoteDevice(address);
				myService.connect(device);// 连接该设备
			}
			break;
		}
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// 启动设备列表Activity搜索设备
		Intent serverIntent = new Intent(this, MyDeviceListActivity.class);
		startActivityForResult(serverIntent, 1);
		return true;
	}
}