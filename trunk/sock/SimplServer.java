package sock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Vector;

import main.SuperMain;

public class SimplServer {

	ServerSocket socket;
	// Socket ObjectOutputStream SchonLevelGekriegt ?????
	LinkedList<Vector<Object>> clients, zuRemovendeClients;
	ConnectionListener connListener;

	public SimplServer() {
		clients = new LinkedList<Vector<Object>>();
		zuRemovendeClients = new LinkedList<Vector<Object>>();

		try {
			socket = new ServerSocket(27128);
			startListenForClients();
			System.out.println("Server lauscht!");

			// Scanner sc = new Scanner(System.in);
			// String s = sc.next();
			// while (!(s = sc.next()).equals("q")) {
			// broadCast(s);
			// }

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void addConnectionListener(ConnectionListener cl) {
		connListener = cl;
	}

	private void startListenForClients() {
		new Thread() {
			//@override
			public void run() {
				while (!socket.isClosed()) {
					try {
						new newClientThread(socket.accept());
					} catch (IOException e) {

						e.printStackTrace();
					}
					try {
						this.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}.start();

	}

	private void ClientDisconnected(Socket client) {
		System.out.println("Client weg: " + client.getRemoteSocketAddress());
		clients.remove(client);
		System.out.println("noch " + clients.size() + " clients");
		connListener.onClientDisconnected(client);
	}

	private void onClientRead(Socket client, Object object) {
		// System.out.println(client.getRemoteSocketAddress() + ": " + readUTF);
		// broadCast(client.getRemoteSocketAddress() + ": " + readUTF);
		// System.out.println("received: " + object);
		connListener.onReceivedFromClient(client, object);
	}

	public static void main(String[] args) {
		new SimplServer();
	}

	public void broadCast(Object string) {
		// vor jedem broadcast nach zu entfernenden checken
		while (zuRemovendeClients.size() > 0) {
			Vector v = zuRemovendeClients.remove();
			clients.remove(v);
			ClientDisconnected((Socket) v.get(0));
		}

		for (Vector v : clients) {
			try {
				ObjectOutputStream s = (ObjectOutputStream) v.get(1);
				s.writeObject(string);
				s.flush();
			} catch (IOException e) {
				// e.printStackTrace();SuperMain.out(e);
				// Client wohl weg-> zum entfernen vorsehen
				System.err.println("Fehler beim schreiben an Client " + ((Socket) v.get(0)).getRemoteSocketAddress());
				zuRemovendeClients.add(v);
				continue;

			}
		}

	}

	class newClientThread extends Thread {
		Socket client;
		ObjectOutputStream oos;
		ObjectInputStream ois;

		public newClientThread(Socket client_) {
			this.client = client_;
			try {
				this.oos = new ObjectOutputStream(client.getOutputStream());
				this.ois = new ObjectInputStream(client.getInputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// System.out.println("neuer Client: " +
			// client.getRemoteSocketAddress());

			Vector v = new Vector<Object>();
			v.add(client);
			v.add(oos);
			clients.add(v);

			if (connListener != null)
				connListener.onNewClient(client);
			start();
			// broadCast("neuer Client: " + client.getRemoteSocketAddress());
		}

		//@override
		public void run() {
			try {
				ObjectInputStream is = ois;
				while (client.isConnected()) {

					if (/* is.available() > 0 */true) {
						try {
							onClientRead(client, is.readObject());
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				ClientDisconnected(client);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

	public void broadCastL(LinkedList<Data> tosend) {
		// vor jedem broadcast nach zu entfernenden checken
		while (zuRemovendeClients.size() > 0) {
			Vector v = zuRemovendeClients.remove();
			clients.remove(v);
			ClientDisconnected((Socket) v.get(0));
		}

		for (Vector v : clients) {
			try {
				ObjectOutputStream oos = (ObjectOutputStream) v.get(1);
				for (Data d : tosend) {
					oos.writeObject(d);
					SuperMain.data_send++;
				}
				oos.flush();
			} catch (IOException e) {
				// e.printStackTrace();SuperMain.out(e);
				// Client wohl weg-> zum entfernen vorsehen
				System.err.println("Fehler beim schreiben an Client " + ((Socket) v.get(0)).getRemoteSocketAddress());
				zuRemovendeClients.add(v);
				continue;

			}
		}

	}

}
