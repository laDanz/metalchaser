package main;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import sock.DedicatedServer;
import Classes.GameState;
import Classes.OGL;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.myAudio;
import Classes.myButton;
import Classes.myColor;
import Classes.myText;
import Classes.v3;

/**
 * Startmenue with choice beween Editor and Levelplay
 * 
 * @author laDanz
 * 
 */
public class Starter implements GameState {

	Texture logo;
	myButton play;
	myButton play_now;
	myButton mainmenu;
	myButton editor;
	myButton anim;
	
	// myButton netz;
	// myButton auftragsed;
	// myButton auftragsview;
	String state = "0";

	/**
	 * Constructor
	 */
	public Starter() {
		// myAudio.play(new myClip(SuperMain.ordner+ "sound/menutheme.ogg"));
		SuperMain.out("Starter gestartet, lol");
		once();
	}

	public String getState() {
		// TODO Auto-generated method stub
		return state;
	}

	public void logic() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			// OGL.finished = true;
			state = "EndState";
		}
		main.SuperMain.selection = OGL.selection(Mouse.getX(), Mouse.getY(), this);

	}

	@Override
	public void doFinalizeActions() {
		// TODO Auto-generated method stub

	}

	/**
	 * Singular Configurations
	 */
	public void once() {

		myAudio.print_supported();

		// myClip
		// cl=myAudio.loadClipFromFile(SuperMain.ordner+"sound/dedededeDEDE.wav");

		// myClip
		// cl2=myAudio.loadClipFromFile(SuperMain.ordner+"sound/dedededeDEDE.wav");

		// cl.start();
		/*
		 * try { Thread.sleep(500); } catch (InterruptedException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace();SuperMain.out(e); }
		 * cl2.start();
		 */

		try {
			logo = SuperMain.loadTex("img/menu/mainmenu.jpg");

		} catch (Exception e) {
			e.printStackTrace();
			SuperMain.out(e);
		}
		// OGL.viereck(new v3(3, 4, -14), new v3(4, 1, 0), editor, 1);

		myText.out("Development Modus", 1);
		
		
		editor = new myButton("img/button/button"	, -2, 3, 5, 1, onEditor(), "Editor");
		anim = new myButton("img/button/button"		, -2, 2, 5, 1, onAnim(), "Animator");
		play = new myButton("img/button/button"		, -2, 1, 5, 1, onPlay(), "Play Level");
		play_now = new myButton("img/button/button"	, -2, 0, 5, 1, onPlayNow(), "Play Now");
		mainmenu = new myButton("img/button/button"	, -2,-1, 5, 1, onMM(), "Mainmenu");
		
		//netz = new myButton("img/button/button", -7, 0, 4, 1, onNetz(), "beitreten");
		
		
		//editor.setTooltip("Editor starten");
		//anim.setTooltip("AnimateIt");
		//play.setTooltip("dirrekt auf eine karte ihrer\n wahl es oeffnet sich ein\ndatei-auswahl dialog");
		//mainmenu.setTooltip("Der orginal spielablauf");
		//netz.setTooltip("NetzSpiel(Client)");

		// auftragsed = new myButton("img/button/button", -2, 1, 4, 1,
		// onAuftragsEditor(), "AuftragEditor");
		// auftragsed.setTooltip("Das ist der\nTOOLTIP tEST1");
		// auftragsview = new myButton("img/button/button", -2, 0, 4, 1,
		// onAuftragsView(), "AuftragsView");

	}

	private Runnable onNetz() {
		// TODO Auto-generated method stub
		return new Runnable() {
			@Override
			public void run() {
				String serv = JOptionPane.showInputDialog("Server");
				if (serv == null || serv.equals("")) {
					return;
				}

				state = "LevelPlay,NETZ" + serv;

			}
		};
	}

	private Runnable onAnim() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				state = "AnimateIt";
			}

		};
	}

	private Runnable onPlayNow() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				state = "LevelPlay,temp3d.xml";
				if (!true)
					SuperMain.server = new DedicatedServer();
			}

		};
	}

	private Runnable onAuftragsView() {
		return new Runnable() {

			public void run() {
				state = "AuftragsViewer";

			}

		};
	}

	private Runnable onPlay() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {

				final File startvz = new File(SuperMain.ordner + "level");
				final JFileChooser f_ = new JFileChooser(startvz) {

					public boolean accept(File f) {
						String s = SuperMain.ordner + "level";
						if (!f.getAbsolutePath().toLowerCase().startsWith(s.toLowerCase())) {
							this.setCurrentDirectory(startvz);
						}

						return (f.getAbsolutePath().toLowerCase().endsWith("xml"));
					}

				};
				String s = "";
				if (f_.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					int beginIndex = f_.getSelectedFile().getAbsolutePath().lastIndexOf(
							SuperMain.seperator + "level" + SuperMain.seperator) + 1;
					s = f_.getSelectedFile().getAbsolutePath().substring(beginIndex);
				}

				state = "LevelPlay," + s;

			}

		};
	}

	private Runnable onEditor() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				state = "Editor";

			}

		};
	}

	private Runnable onMM() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				state = "MainMenu";

			}

		};
	}

	public void render() {
		OGL.setColor(myColor.WHITE);
		OGL.Hintergrund(logo);

		// etwas weg
		Vektor3D versch = new v3(0, 0, -14);
		OGL.verschieb(versch);
		
		

		mainmenu.render();
		editor.render();
		anim.render();
		play.render();
		play_now.render();
		
		//netz.render();
		// auftragsed.render();
		// auftragsview.render();
		v3 ver = new v3(-7, 0, 0);
		OGL.verschieb(ver);
		
		/* Seltsamer Danzi Test :P
		myText.out("a bazy", 1);

		OGL.verschieb(new v3(0, -1, 0));
		myText.out("<@?+*~>", 1);
		OGL.verschieb(new v3(0, -1, 0).negiere());
		*/

		OGL.verschieb(ver.negiere());
		OGL.verschieb(versch.negiere());

	}

}
