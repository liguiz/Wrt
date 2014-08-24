package com.mgoogle.lee;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import android.os.Handler;

public class NetUtil {
	
	/**
	 * 针对255.255.255.0掩码的情况,生成指定ip地址的子网号.
	 * @param 子网的一个ip地址.
	 * @return InetAddress类型的子网地址
	 */
	static public InetAddress getNetworkSegmentAddressFromInetAddress(InetAddress i){
		String s = i.getHostAddress();
		InetAddress j = null;
		try {
			j = InetAddress.getByName(s.substring(0, s.lastIndexOf(".")+1)+"0" );	//针对网络号末尾为0的情况,如果不为0还是用byte[] ip
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return j;
	}
	
	/**
	 * 把指定的内容广播到子网的所有主机
	 * @param msg广播的内容
	 * @return
	 */
	static public boolean sendNetBroadCast(String msg){
		MulticastSocket s=null;
		DatagramPacket packet = null;
		try {
			s = new MulticastSocket();
			s.setTimeToLive(1);
			InetAddress  ip=null;
			ip= InetAddress.getLocalHost();
			s.joinGroup(NetUtil.getNetworkSegmentAddressFromInetAddress(ip));
			byte[] data = msg.getBytes();
			packet = new DatagramPacket(data,data.length);
			s.send(packet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			s.close();
		}
		return true;
	}
	
	
	static public boolean handleDataFromLan(HandleTcp ht){
		ServerSocket ss=null;
		try{
			ss = new ServerSocket();
			ss.bind(null);
			sendNetBroadCast(ss.getLocalPort()+"");
			ht.handleTcpFromStream(ss.accept().getInputStream());
			return true;
		}catch(Exception e){
			return false;
		}finally{
			try{
				ss.close();
			}catch(Exception e){
				return false;
			}
		}
	}
	
	/**
	 * 用指定的HandleTcp接口处理客户端发过的InputStreamm,客户端要从UDP广播里找到服务端端口.
	 * @param ht
	 * @param handler负责执行
	 */
	static public void handleDataFromLan(final HandleTcp ht,Handler handler){
		handler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handleDataFromLan(ht);
			}
			
		});
	}
}
