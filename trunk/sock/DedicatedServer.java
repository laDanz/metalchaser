package sock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Vector;

import main.LevelPlay;
import main.SuperMain;
import Classes.Geschoss;
import Classes.Objekt;
import Classes.Player;
import drops.SchrottDrop;

public class DedicatedServer implements ConnectionListener {

	public SimplServer server;

	public DedicatedServer() {
		// server starten
		server = new SimplServer();
		server.addConnectionListener(this);
	}

	//@override
	public void onClientDisconnected(Socket client) {
		LevelPlay.InGameConsole.addText("Mecha weg");
	}

	//@override
	public void onNewClient(Socket client) {
		LevelPlay.InGameConsole.addText("neuer Mecha");

	}

	//@override
	public void onReceivedFromClient(Socket client, Object obj) {

		String message = null;
		if (obj instanceof Geschoss) {
			LevelPlay.bullets.add((Geschoss) obj);
		}
		if (obj instanceof Data) {
			Data d = ((Data) obj);
			Player enem = null;
			for (Player e : LevelPlay.enemy) {
				if (e.getID() == d.id) {
					enem = e;
				}
			}
			if (enem == null) {
				enem = new Player(d.id);
				LevelPlay.enemy.add(enem);
			}
			enem.setPosition(d.pos);
			enem.huftwinkel = d.huftwinkel;
			enem.blickrichtung = d.drehwinkel;
		}
		if (obj instanceof SchrottDrop) {
			// client picked up a drop so we must remove it!

			SuperMain.removeDrop(((SchrottDrop) obj));
		}
		if (obj instanceof String) {
			message = obj.toString();
		}
		if (message == null) {
			return;
		}
		if (message.equals("gimmeLevel")) {
			try {
				// FIXME level als object schreiben!!!!
				// Scanner sc = new Scanner(new File(SuperMain.ordner +
				// LevelPlay.geladenes_level));
				// String s;
				// DataOutputStream out = new
				// DataOutputStream(client.getOutputStream());
				// System.out.println("übermittle level daten");
				//
				// while (sc.hasNext() && (s = sc.nextLine()) != null) {
				// out.writeUTF("yourMap:" + s);
				// out.flush();
				// }
				// out.writeUTF("EOF");
				// out.flush();
				LevelString l = new LevelString(LevelPlay.geladenes_level, 60000 + LevelPlay.enemy.size());
				for (Vector<Object> v : server.clients) {
					if (((Socket) v.get(0)).equals(client)) {
						((ObjectOutputStream) v.get(1)).writeObject(l);
					}
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			}
		}
	}

	LinkedList<Data> lastsend = new LinkedList<Data>();

	public void sendDataAboutAllObjects() {

		LinkedList<Data> tosend = new LinkedList<Data>();
		for (Objekt o : SuperMain.level.getObjekte()) {
			Data d = new Data(o);
			// nur senden wenns nicht (in gleicher form) schon beim letzten mal
			// gesendet wurde
			// weil keine aenderung
			if (!lastsend.contains(d)) {
				tosend.add(d);
			}
		}
		// Jetzt noch alle Player
		for (Player e : LevelPlay.enemy) {
			Data d = new Data(e);
			tosend.add(d);
		}
		// Und meinen eigenen Standpunkt!
		Data d = new Data(LevelPlay.p, 59999);
		tosend.add(d);

		server.broadCastL(tosend);
		lastsend = tosend;
	}

}
