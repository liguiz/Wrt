package com.mgoogle.lee.test;

import com.mgoogle.lee.NetUtil;

public class MulticastTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		Runnable r=new Runnable(){
			String client="init";
			
			@Override
			public void run() {
				try {
					client= NetUtil.getMessageFromBroadcast();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(client);
			}

			public void setClient(String client) {
				this.client = client;
			}
		};


		NetUtil.sendNetBroadCast("form server!",6758);
		
	}

}
