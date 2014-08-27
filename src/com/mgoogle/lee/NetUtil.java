package com.mgoogle.lee;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ListIterator;

import android.os.Handler;

public class NetUtil {
	
	/**
	 * 针对255.255.255.0掩码的情况,生成指定ip地址的子网号.
	 * @param 子网的一个ip地址.
	 * @return InetAddress类型的子网地址
	 */
	final static int[] portCan={1531,4862,1615,4628,1369};
	final static int udp_port=6758;
	static public InetAddress getNetworkSegmentAddressFromInetAddress(InetAddress i){
		String s = i.getHostAddress();
		InetAddress j = null;
		try {
			j = InetAddress.getByName(s.substring(0, s.lastIndexOf(".")+1)+"255" );	//针对网络号末尾为0的情况,如果不为0还是用byte[] ip
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(j.getHostAddress());
		return j;
	}
	
	/**
	 * 把指定的内容广播到子网的所有主机224.0.0.1
	 * @param msg广播的内容,clientPort指定接收广播的端口,负值使用候选端口
	 * @return
	 */
	static public boolean sendNetBroadCast(String msg,int clientPort){
		MulticastSocket s=null;
		DatagramPacket packet = null;
		try {
			for(int i=1024;i<5001;++i){
				try{
					s = new MulticastSocket(i);
				}catch(Exception e){
					
				}
			}
			s.setTimeToLive(2);

//			s.joinGroup(NetUtil.getNetworkSegmentAddressFromInsetAddress(ip));
			s.joinGroup(InetAddress.getByName("224.0.0.1"));
			byte[] data = msg.getBytes();
			if(clientPort>0){
				packet = new DatagramPacket(data,data.length,InetAddress.getByName("224.0.0.1"),clientPort);
				s.send(packet);
			}
			else{
				for(int i=0;i<portCan.length;i++){
					packet = new DatagramPacket(data,data.length,InetAddress.getByName("224.0.0.1"),portCan[i]);
					s.send(packet);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			s.close();
		}
		return true;
	}

	
	public static ArrayList<InetAddress> getLanIps() throws UnknownHostException {
		InetAddress  ips[]=null;
		ips= InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
		ArrayList<InetAddress> ip=new ArrayList<InetAddress>();
		for(int i = 0;i<ips.length;i++){
			String sip=ips[i].getHostAddress();
			
			if(sip.contains("192.168.")||		//c类私有地址
					sip.startsWith("10.")||		//A类私有地址
					(sip.startsWith("172.")&&(
							Integer.parseInt(sip.substring(3, 5))>15||
							Integer.parseInt(sip.substring(3, 5))<33))){
				ip.add(ips[i]);

				continue;
			}
		}
		return ip;
	}
	
	
	static public boolean handleDataFromLan(final HandleTcp ht) throws Exception{
//		final int  routerPort=6751;
		ServerSocket ss=null;
		try{
			ss = new ServerSocket();
			ss.bind(null);
			sendNetBroadCast(ss.getLocalPort()+"",udp_port);
			while(true){
				Socket s = ss.accept();
				ht.handleTcpFromStream(s.getInputStream());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ss.close();
		}

		return true;
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
				try {
					handleDataFromLan(ht);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
	}
	
	static public String getMessageFromBroadcast() throws Exception{
		String msg=null;
		byte[] buf = new byte[1024];
		MulticastSocket ms = new MulticastSocket(udp_port);
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		ms.joinGroup(InetAddress.getByName("224.0.0.1"));
		ms.receive(dp);
		System.out.println("dp.getSocketAddress():"+dp.getSocketAddress());
		msg=new String(dp.getData());
		ms.close();
		return msg;
	}
	
	static public boolean sendNetBroadCastInBackground(final String s,final int clientport) {
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendNetBroadCast(s, clientport);
			}
			
		}).start();
		return true;
	}
}
