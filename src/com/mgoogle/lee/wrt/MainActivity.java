package com.mgoogle.lee.wrt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.http.conn.util.InetAddressUtils;

import com.mgoogle.lee.NetUtil;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final String REQUIRE_ROUTER_IP = "Require Router Ip.";
	protected static final long CAST_INTERVAL = 200;
	/**
	 * 先判断拿到网段ip
	 * 发广播
	 * 接受回函
	 * 通信
	 */
	Runnable mRun = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			WifiManager wm =  (WifiManager) getSystemService(Context.WIFI_SERVICE);
			NetworkInfo mNetInfo;
			WifiInfo cInfo;
			MulticastSocket s=null;
			DatagramPacket packet = null;
			try {
				s = new MulticastSocket();
				s.setTimeToLive(1);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for(int i = 0;i<4;i++){
				mNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				cInfo = wm.getConnectionInfo();
				if(mNetInfo!=null&&mNetInfo.isAvailable()&&mNetInfo.isConnected()){
					InetAddress  ip=null;
					try {
						ip= InetAddress.getLocalHost();
						s.joinGroup(NetUtil.getNetworkSegmentAddressFromInetAddress(ip));
						byte[] data = REQUIRE_ROUTER_IP.getBytes();
						packet = new DatagramPacket(data,data.length);
						s.send(packet);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(CAST_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView lv = (ListView)findViewById(R.id.listView1);
		View framelayout = View.inflate(this, R.layout.com, null);
		TextView footer = (TextView) framelayout.findViewById(R.id.footerview);
		lv.setFooterDividersEnabled(true);
		lv.addFooterView(footer);
		
		lv.setClickable(true);
		footer.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
