package sock;


public interface ConnectionListener {

	void onNewClient(java.net.Socket client);

	void onClientDisconnected(java.net.Socket client);

	void onReceivedFromClient(java.net.Socket client, Object obj);

}
