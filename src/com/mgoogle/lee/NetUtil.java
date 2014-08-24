package com.mgoogle.lee;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import android.os.Handler;

public class NetUtil {
	
	/**
	 * ���255.255.255.0��������,����ָ��ip��ַ��������.
	 * @param ������һ��ip��ַ.
	 * @return InetAddress���͵�������ַ
	 */
	static public InetAddress getNetworkSegmentAddressFromInetAddress(InetAddress i){
		String s = i.getHostAddress();
		InetAddress j = null;
		try {
			j = InetAddress.getByName(s.substring(0, s.lastIndexOf(".")+1)+"0" );	//��������ĩβΪ0�����,�����Ϊ0������byte[] ip
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return j;
	}
	
	/**
	 * ��ָ�������ݹ㲥����������������
	 * @param msg�㲥������
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
	 * ��ָ����HandleTcp�ӿڴ���ͻ��˷�����InputStreamm,�ͻ���Ҫ��UDP�㲥���ҵ�����˶˿�.
	 * @param ht
	 * @param handler����ִ��
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
