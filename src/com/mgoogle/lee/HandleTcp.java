package com.mgoogle.lee;

import java.io.InputStream;
import java.net.ServerSocket;

public interface HandleTcp {
	public void handleTcpFromStream(InputStream is);
	public void openConnection(ServerSocket ss);
}
