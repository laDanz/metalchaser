package sock;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SimplClient {

	Socket socket;
	ConnectionListener connListener;
	ObjectOutputStream out;
	ObjectInputStream in;

	public SimplClient(String host) {
		// System.out.print("Hostname:");
		//
		// Scanner sc = new Scanner(System.in);
		//
		// String s = sc.next();
		try {
			socket = new Socket(host, 27128);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());

			// while (!(s = sc.next()).equals("q")) {
			// out.writeUTF(s);
			// out.flush();
			// }
			// socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startListening() {
		new newListenThread(socket);
	}

	public void send(Object what) {
		try {
			out.writeObject(what);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addConnectionListener(ConnectionListener cl) {
		connListener = cl;
	}

	public static void main(String[] args) {
		System.out.print("Hostname:");

		Scanner sc = new Scanner(System.in);

		String s = sc.nextLine();
		SimplClient scl = new SimplClient(s);

		while (sc.hasNext()) {
			scl.send(sc.nextLine());
		}
	}

	class newListenThread extends Thread {
		Socket client;

		public newListenThread(Socket client_) {
			this.client = client_;
			start();

		}

		@Override
		public void run() {
			try {

				while (client.isConnected()) {

					if (/* in.available() > 0 */true) {

						// Data packet?
						ObjectInputStream ois = in;
						try {
							Object obj = ois.readObject();
							connListener.onReceivedFromClient(client, obj);

						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (EOFException e) {
							// Server has closed
							connListener.onClientDisconnected(client);
							e.printStackTrace();
						}

					}

				}
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

}
