package sock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;

import main.LevelPlay;
import main.SuperMain;
import Classes.Objekt;
import Classes.Player;
import drops.SchrottDrop;

public class MCClient implements ConnectionListener {
	public SimplClient client;
	/**
	 * The id for me provided by the server.
	 */
	public int id;

	// DEBUG
	int receive_stack = 0;

	public MCClient(String serv) {
		client = new SimplClient(serv);
		client.addConnectionListener(this);
	}

	@Override
	public void onClientDisconnected(Socket client) {
		// Server hat gestopt!
		LevelPlay.setState("MainMenu");
	}

	@Override
	public void onNewClient(Socket client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceivedFromClient(Socket client, final Object message) {
		if (message instanceof Data) {
			onReceivedFromClient(client, (Data) message);
			return;
		}
		if (message instanceof LevelString) {

			return;
		}
		if (message instanceof ObjRemover) {
			SuperMain.toRun.add(new Runnable() {
				public void run() {
					SuperMain.level.objekte.remove(((ObjRemover) message).obj);

				}
			});
		}
		if (message instanceof SchrottDrop) {
			System.out.println("received drop: " + message);
			SuperMain.addDrop((Objekt) message);
			return;
		}
		System.out.println(message.toString());
	}

	public HashMap<Integer, Data> receivedData = new HashMap<Integer, Data>();

	public void onReceivedFromClient(Socket client, Data data) {
		// Player data?
		if (data.id >= 59999) {
			if (data.id == this.id)
				return;

			Player enem = null;
			for (Player e : LevelPlay.enemy) {
				if (e.getID() == data.id) {
					enem = e;
				}
			}
			if (enem == null) {
				enem = new Player(data.id);
				LevelPlay.enemy.add(enem);
			}
			enem.setPosition(data.pos);
			enem.huftwinkel = data.huftwinkel;
			enem.blickrichtung = data.drehwinkel;
			return;
		}
		receivedData.put(data.id, data);
		SuperMain.data_rec++;
	}

	public String getLevel() {
		// Server zum senden auffordern
		client.send("gimmeLevel");

		// antwort einlesen
		try {
			// BufferedWriter bw = new BufferedWriter(new FileWriter(new
			// File(SuperMain.ordner + "level/multi.mp")));
			// String s;

			// while (!(s = client.in.readUTF()).equals("EOF")) {
			// if (s.startsWith("yourMap:")) {
			// s = s.substring(8);
			// } else {
			// continue;
			// }
			// bw.write(s);
			// bw.newLine();
			// }
			ObjectInputStream ois = (client.in);
			while (true) {
				Object obj;
				try {
					obj = ois.readObject();
					System.out.println("obj kommt an: " + obj);
					if (obj instanceof LevelString) {
						this.id = ((LevelString) obj).id;
						return ((LevelString) obj).s;
					} else if (obj instanceof Data) {
						// onReceivedFromClient(client.socket, ((Data) obj));
					} else {
						// onReceivedFromClient(client.socket, obj.toString());
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}

			// bw.close();
			// return "level/multi.mp";
			// return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}

		return null;
	}
}
