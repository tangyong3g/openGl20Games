package com.bn.Sample2_9_Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//服务器端
public class Sample2_9_Server
{
	static ServerSocket sSocket;
	public static void main(String[] args)
	{
		try
		{
			sSocket=new ServerSocket(8877);
			System.out.println("监听8877接口......");
			while(true)
			{
				Socket socket=sSocket.accept();
				DataInputStream diStream=new DataInputStream(socket.getInputStream());
				DataOutputStream dotStream=new DataOutputStream(socket.getOutputStream());
				System.out.println("客户端信息："+diStream.readUTF());
				dotStream.writeUTF("成功连接服务器端");
				diStream.close();
				dotStream.close();
				socket.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
