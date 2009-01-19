package Fenster;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import main.Editor;
import main.SuperMain;
import other.StartPkt;
import Classes.Objekt;
import Classes.quad;

/**
 * Window for the Editor.<br>
 * Implements Terrain shape, texture - and Objectplacing Methods 
 * 
 * @author laDanz
 * 
 */
public class GelaendeFenster extends EditorFenster {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static public JCheckBox cb_Begrenzer = new JCheckBox("Begrenzer");
	static public JCheckBox cb_Gitter = new JCheckBox("Gitter");
	static public JCheckBox cb_andereMitZiehen = new JCheckBox("anliegende Mitbewegen");
	static public JCheckBox cb_Farbe = new JCheckBox("Farbe");
	static public JCheckBox cb_texture = new JCheckBox("Texturen");
	static public JCheckBox cb_light = new JCheckBox("Licht");

	static public JRadioButton click_none;
	static public JRadioButton click_tex;
	static public JRadioButton click_move;

	static JPanel gelaendeP;
	static JPanel objectsP;
	static JPanel optionP;
	static JTabbedPane mainP;
	ButtonGroup bg;
	// Texturen

	static public JTable tex_area;
	JButton addTex;
	JButton texAll;
	static JScrollPane scrollingArea;

	// Objektbeschreibungen
	static JLabel objektName;
	static JLabel objektDesc;
	static JLabel objektPos;
	static public JPopupMenu menu;
	JButton putB;
	static Class lastShown = null;

	/**
	 * Constructor with postion and size of the window
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public GelaendeFenster(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.addMouseListener(onMouseMove());
		this.setTitle("Aufgaben");
		initGelaendeComponents();
		initObjComps();

		initMainComp();
		repaint();
	}

	/**
	 * Simple Listener Method
	 * @return
	 */
	private MouseListener onMouseMove() {
		// TODO Auto-generated method stub
		return new MouseListener() {

			/**
			 * Simple Listener Method
			 * @return
			 */
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			/**
			 * Simple Listener Method
			 * @return
			 */
			public void mouseEntered(MouseEvent arg0) {
				main.SuperMain.selection = -1;
				main.Editor.allow_selection = false;
				if (Editor.putter == null)
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			/**
			 * Simple Listener Method
			 * @return
			 */
			public void mouseExited(MouseEvent arg0) {

			}

			/**
			 * Simple Listener Method
			 * @return
			 */
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			/**
			 * Simple Listener Method
			 * @return
			 */
			public void mouseReleased(MouseEvent arg0) {
				if (Editor.putter != null)
					setCursor(new Cursor(Cursor.MOVE_CURSOR));

			}

		};
	}

	/**
	 * Returns true if Editor is in Object-Mode
	 * @return
	 */
	public static boolean isInObjMode() {
		return mainP.getSelectedComponent().equals(objectsP);
	}

	private void initMainComp() {
		mainP = new JTabbedPane();
		mainP.setBounds(0, 0, 190, 390);
		mainP.addTab("Gelnde", gelaendeP);
		mainP.addTab("Objects", objectsP);
		mainP.addMouseListener(onMouseMove());
		add(mainP);

		mainP.addChangeListener(new ChangeListener() {
			Object last;

			/**
			 * Statechange of the Editor
			 */
			public void stateChanged(ChangeEvent arg0) {
				if (mainP.getSelectedComponent().equals(objectsP) && (last == null || !last.equals(objectsP))) {
					click_none.setSelected(true);
					mainP.removeChangeListener(this);
					mainP.setSelectedComponent(objectsP);
					mainP.setEnabled(false);
					mainP.repaint();
					SuperMain.level.calc_height();

					mainP.addChangeListener(this);
					mainP.setEnabled(true);
				}
				last = mainP.getSelectedComponent();
			}

		});

	}

	private void initTexComp() {
		String[] columnNames = { "Texture" };

		Object[][] data = {};

		tex_area = new JTable(data, columnNames);
		scrollingArea = new JScrollPane(tex_area);
		scrollingArea.setBounds(0, 180, 180, 100);
		gelaendeP.add(scrollingArea);
		tex_area.addMouseListener(onTexTable());

		addTex = new JButton("load texture");
		addTex.setBounds(5, 305, 150, 20);
		gelaendeP.add(addTex);
		addTex.addActionListener(onAddTex());

		texAll = new JButton("texture all");
		texAll.setBounds(5, 330, 150, 20);
		gelaendeP.add(texAll);
		texAll.addActionListener(onTexAll());
	}

	static private MouseListener onTexTable() {
		return new MouseListener() {

			/**
			 * Simple Listener Method
			 */
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			/**
			 * Simple Listener Method
			 */
			public void mouseEntered(MouseEvent arg0) {
				main.SuperMain.selection = -1;
				main.Editor.allow_selection = false;
			}

			/**
			 * Simple Listener Method
			 */
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			/**
			 * Simple Listener Method
			 */
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			/**
			 * Simple Listener Method
			 */
			public void mouseReleased(MouseEvent arg0) {
				GelaendeFenster.click_tex.setSelected(true);

			}

		};
	}

	/**
	 * Simple Listener Method
	 */
	private ActionListener onAddTex() {
		// TODO Auto-generated method stub
		return new ActionListener() {

			/**
			 * action on Editor is performed
			 */
			public void actionPerformed(ActionEvent e) {
				final File startvz = new File(SuperMain.ordner + "img/textures");
				final JFileChooser f_ = new JFileChooser(startvz) {

					private static final long serialVersionUID = 1L;

					/**
					 * Check if Texture File is supported by the Editor
					 */
					public boolean accept(File f) {
						String s = SuperMain.ordner + "img";
						if (!f.getAbsolutePath().toLowerCase().startsWith(s.toLowerCase())) {
							this.setCurrentDirectory(startvz);
						}

						return (f.getAbsolutePath().toLowerCase().endsWith("jpg")
								|| f.getAbsolutePath().toLowerCase().endsWith("png")
								|| f.getAbsolutePath().toLowerCase().endsWith("bmp") || f.isDirectory());
					}

				};

				File fi = null;
				if (f_.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					fi = f_.getSelectedFile();
				}
				f_.setCurrentDirectory(new File(SuperMain.ordner));
				if (fi != null) {
					String res = fi.getAbsolutePath().substring(SuperMain.ordner.length());

					SuperMain.toLoad.add(res);

				}

			}

		};
	}

	private ActionListener onTexAll() {
		// TODO Auto-generated method stub ?
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int tex = tex_area.getSelectedRow();
				for (quad q : SuperMain.level.getQuads()) {
					q.setTex(tex);
				}
			};

		};
	}

	/**
	 * activate the Textures in the Editor
	 */
	static public void actTex() {
		String[] columnNames = { "Texture" };

		Object[][] data = new Object[SuperMain.level.texS.size()][1];
		for (int i = 0; i < SuperMain.level.texS.size(); i++) {
			data[i][0] = SuperMain.level.texS.get(i);
		}

		tex_area = new JTable(data, columnNames);
		tex_area.addMouseListener(onTexTable());
		scrollingArea.setViewportView(tex_area);
	}

	private void initObjComps() {

		JPanel content = new JPanel();

		content.setLayout(null);
		Hashtable<String, Vector<Class>> vG = new Hashtable<String, Vector<Class>>();
		// Masterklassenladen
		LinkedList<String> masters = new LinkedList<String>();
		try {
			masters = scanformasters();
			// master = new LinkedList<Element>();
			for (int i = 0; i < masters.size(); i++) {
				ClassLoader cl = ClassLoader.getSystemClassLoader();
				String what = masters.get(i);

				Class c3 = cl.loadClass(what + ".master");
				// master.addLast((Element) c3.newInstance());
			}
		} catch (Exception e) {
			SuperMain.out("Fehler beim Masterclassenladen im mOdul EDITOR: " + e);
		}

		// System.out.println(master);
		// Alle unterklassen laden
		LinkedList<String>[] files = new LinkedList[masters.size()];
		try {

			// elementklassen = new Class[masters.size()][];
			// elemente = new Element[masters.size()][];

			for (int j = 0; j < masters.size(); j++) {

				files[j] = getFilesfromDir(masters.get(j), ".class", false);
				// aus files jetzt noch die masterklasse rausschmeien
				int merk = -1;
				for (int xyz = 0; xyz < files[j].size(); xyz++) {
					if (files[j].get(xyz).equals("master"))
						merk = xyz;
				}
				if (merk >= 0)
					files[j].remove(merk);
				// elementklassen[j] = new Class[files[j].size()];
				// elemente[j] = new Element[files[j].size()];

			}

			for (int j = 0; j < masters.size(); j++) {
				Vector<Class> unterKlassen = new Vector<Class>();

				for (int i = 0; i < files[j].size(); i++) {
					ClassLoader cl = ClassLoader.getSystemClassLoader();
					Class c3 = cl.loadClass(masters.get(j) + "." + files[j].get(i));
					// elementklassen[j][i] = c3;
					// elemente[j][i] = (Element) c3.newInstance();
					try {
						if (c3.newInstance() instanceof Objekt)
							unterKlassen.add(c3);
					} catch (InstantiationException e) {

					}
				}
				vG.put(masters.get(j), unterKlassen);
			}
		} catch (Exception e) {
			SuperMain.out("Fehler beim Unterklassenladen im Modul EDITOR: " + e);
		}

		JTree objTree = new JTree(vG);
		objTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		objTree.setBounds(5, 5, 180, 300);
		objTree.addTreeSelectionListener(onTreeClick());

		objectsP = new JPanel();
		objectsP.setLayout(null);
		JScrollPane scrolli = new JScrollPane(objTree);
		scrolli.setBounds(0, 0, 180, 130);
		scrolli.setMaximumSize(new Dimension(180, 180));
		scrolli.setViewportView(objTree);
		objectsP.add(scrolli);

		// Objektbeschreibungen
		objektName = new JLabel("Objekt:");
		objektName.setFont(objektName.getFont().deriveFont(12f));
		objektName.setBounds(0, 130, 190, 20);
		objectsP.add(objektName);

		// description
		objektDesc = new JLabel();
		objektDesc.setFont(objektDesc.getFont().deriveFont(Font.PLAIN));
		objektDesc.setBounds(4, 150, 190, 20);
		objectsP.add(objektDesc);

		// object position
		objektPos = new JLabel();
		objektPos.setFont(objektDesc.getFont());
		objektPos.setBounds(4, 170, 190, 20);
		objectsP.add(objektPos);

		putB = new JButton("put");
		putB.setBounds(5, 300, 100, 20);
		putB.setEnabled(false);
		objectsP.add(putB);
		putB.addActionListener(onPutButton());
	}

	private TreeSelectionListener onTreeClick() {
		// TODO Auto-generated method stub
		return new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent e) {
				if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getPathCount() == 3) {

					DefaultMutableTreeNode o = (DefaultMutableTreeNode) e.getNewLeadSelectionPath()
							.getLastPathComponent();

					postObjektData(o.getUserObject());
					putB.setEnabled(true);
				} else {
					putB.setEnabled(false);
					postObjektData(null);
				}

			}

		};
	}

	/**
	 * Display the Data of an Object
	 * @param objekt Object, which Data is displayed
	 */
	public static void postObjektData(Object objekt) {
		if (optionP != null)
			objectsP.remove(optionP);
		// case #0: Es wird eine Class bergeben-->aus dem linken Tree
		if (objekt instanceof Class) {
			Objekt o = null;
			try {
				o = (Objekt) ((Class) (objekt)).newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();SuperMain.out(e);
				o = null;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();SuperMain.out(e);
				o = null;
			}
			if (o == null)
				return;
			lastShown = (Class) objekt;
			objektName.setText("Objekt: " + o.toString());
			objektDesc.setText("Beschreibung: " + o.getDescription());
			objektName.setToolTipText(objektName.getText());
			objektDesc.setToolTipText(objektDesc.getText());
		} else
		// case #1 null
		if (objekt == null) {
			objektDesc.setText("");
			objektName.setText("");
			objektName.setToolTipText(objektName.getText());
			objektDesc.setToolTipText(objektDesc.getText());
			objektPos.setText("");
		} else // case #2: es wird ein Objekt bergeben-->aus dem Spiel
		if (objekt instanceof Objekt) {
			optionP = new JPanel();
			optionP.setBounds(0, 200, 200, 400);
			objectsP.add(optionP);
			Objekt o = (Objekt) objekt;
			String zusatz = "";
			if (o instanceof StartPkt) {
				zusatz = " # " + ((StartPkt) o).getReihenfolgePlatz();
			}
			objektName.setText("Objekt: " + o.toString() + zusatz);
			objektDesc.setText("Beschreibung: " + o.getDescription());
			objektPos.setText("Position: " + o.getPosition());
			objektName.setToolTipText(objektName.getText());
			objektDesc.setToolTipText(objektDesc.getText());
			int y = 0;
			for (int i = 0; i < o.getOptionCount(); i++) {
				if (o.getOptionType(i) == Objekt.BOOL) {
					JCheckBox l = new JCheckBox(o.getOptionDescription(i));
					l.setSelected((Boolean) o.getOptionValue(i));
					l.setToolTipText(l.getText());
					l.addActionListener(onChangeOption(o, i, l));
					l.addMouseListener(onChangeValueLabel(o, i, l));
					l.setBounds(5, y, 200, 20);
					optionP.add(l);
					y += 20;
				} else {
					JLabel l = new JLabel(o.getOptionDescription(i) + ": " + o.getOptionValue(i));
					l.addMouseListener(onChangeValueLabel(o, i, l));
					l.setBounds(5, y, 200, 20);
					l.setToolTipText(l.getText());
					optionP.add(l);
					y += 20;
				}

			}
		}
		objectsP.repaint();
	}

	private static MouseListener onChangeValueLabel(final Objekt o, final int i, final JComponent l) {
		// TODO Auto-generated method stub
		return new MouseListener() {

			/**
			 * Simple Listener Method
			 */
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			/**
			 * Simple Listener Method
			 */
			public void mouseEntered(MouseEvent arg0) {
				Editor.optionchanging = true;

			}

			/**
			 * Simple Listener Method
			 */
			public void mouseExited(MouseEvent arg0) {
				Editor.optionchanging = false;

			}

			/**
			 * Simple Listener Method
			 */
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			/**
			 * Simple Listener Method
			 */
			public void mouseReleased(MouseEvent arg0) {
				if (l instanceof JLabel) {
					String s = "";
					if (o.getOptionType(i) == Objekt.FILE) {
						final File startvz = new File(SuperMain.ordner + "");
						final JFileChooser f_ = new JFileChooser(startvz) {

							private static final long serialVersionUID = 1L;

							public boolean accept(File f) {
								String s = SuperMain.ordner + "";
								if (!f.getAbsolutePath().toLowerCase().startsWith(s.toLowerCase())) {
									this.setCurrentDirectory(startvz);
								}

								return o.isInWertebereich(i, f);
							}

						};

						File fi = null;
						if (f_.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							fi = f_.getSelectedFile();
						}
						f_.setCurrentDirectory(new File(SuperMain.ordner));
						if (fi != null) {
							s = fi.getAbsolutePath().substring(SuperMain.ordner.length());

						} else {
							// null
							return;
						}
					} else {
						s = javax.swing.JOptionPane.showInputDialog("New Value", o.getOptionValue(i));
					}
					switch (o.getOptionType(i)) {
					case Objekt.FILE:
						o.setOptionValue(i, s);
						Editor.optionchanging = false;
						postObjektData(o);
						break;
					case Objekt.STRING:
						o.setOptionValue(i, s);
						Editor.optionchanging = false;
						postObjektData(o);
						break;
					case Objekt.INT:
						int i_ = 0;
						try {
							i_ = Integer.parseInt(s);
						} catch (Exception e) {
							org.lwjgl.Sys.alert("Cleaners", "Nur Integer Werte zugelassen!");
							Editor.optionchanging = false;
							return;
						}
						if (o.isInWertebereich(i, i_)) {
							o.setOptionValue(i, i_);
							postObjektData(o);
							Editor.optionchanging = false;
						} else {
							org.lwjgl.Sys.alert("Cleaners", "Nicht im zugelassenem Wertebreich!");
						}

						break;
					case Objekt.DOUBLE:
						double d_ = 0;
						try {
							d_ = Double.parseDouble(s);
						} catch (Exception e) {
							org.lwjgl.Sys.alert("Cleaners", "Nur Double Werte zugelassen!");
							Editor.optionchanging = false;
							return;
						}
						if (o.isInWertebereich(i, d_)) {
							o.setOptionValue(i, d_);
							postObjektData(o);
							Editor.optionchanging = false;
						} else {
							org.lwjgl.Sys.alert("Cleaners", "Nicht im zugelassenem Wertebreich!");
						}

						break;

					default:
						break;
					}
				}

			}

		};
	}

	private static ActionListener onChangeOption(final Objekt o, final int i, final JCheckBox l) {

		return new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				o.setOptionValue(i, l.isSelected());
				postObjektData(o);
			}

		};
	}

	private ActionListener onPutButton() {

		return new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					Editor.startPut((Objekt) lastShown.newInstance());
				} catch (Exception e) {
					Editor.putter = null;
					org.lwjgl.Sys.alert("Fehler", "Interner Fehler");
				}

			}

		};
	}

	/**
	 * Hilfsmethode die alle verzeichnise im root nach master.class dateien
	 * durchsucht
	 * 
	 * @return
	 * @throws Exception
	 */
	private LinkedList<String> scanformasters() throws IOException {
		File f = new File(".");
		File[] res = f.listFiles();

		LinkedList<String> sl = new LinkedList<String>();
		for (int i = 0; i < res.length; i++) {
			if (res[i].isDirectory()) {
				if (getFilesfromDir(res[i].getAbsolutePath(), "master.class", false).size() > 0) {
					int last = 0;
					String s1 = res[i].getAbsolutePath();
					for (int j = 0; j < s1.length(); j++)
						if (s1.replace('\\', '/').charAt(j) == '/') {
							last = j;
						}
					String addy = s1.substring(last + 1, s1.length());

					sl.addLast(addy);

				}
			}
		}

		return sl;
	}

	/**
	 * Hilfsmethode, die alle Dateien aus einem Verzeichniss ausliest die auf
	 * .class enden
	 * 
	 * @param dir
	 *            Verzeichnis wo gesucht werden soll
	 * @return einfachverkettete Liste mit den Dateinamen OHNE Erweiterung
	 * @throws Exception
	 *             IOException
	 */
	private LinkedList<String> getFilesfromDir(String dir, String extension, boolean withextension) {
		File f = new File(dir);
		File[] res = f.listFiles();
		LinkedList<String> sl = new LinkedList<String>();
		for (int i = 0; i < res.length; i++) {
			if (!res[i].isDirectory()) {
				try {
					String s = res[i].getCanonicalPath().subSequence(
							(int) res[i].getCanonicalPath().length() - extension.length(),
							(int) res[i].getCanonicalPath().length()).toString();
					if (extension.equalsIgnoreCase(s)) {
						String s1 = res[i].getAbsolutePath();
						int last = 0;
						for (int j = 0; j < s1.length(); j++)
							if (s1.replace('\\', '/').charAt(j) == '/')
								last = j;
						if (!withextension)
							sl.addLast(s1.substring(last + 1, s1.length() - extension.length()));
						else
							sl.addLast(s1.substring(last + 1, s1.length()));
					}
				} catch (Exception e) {
				}
			}
		}

		return sl;
	}

	private void initGelaendeComponents() {
		gelaendeP = new JPanel();
		gelaendeP.setLayout(null);
		initTexComp();
		int y = 0;
		int count = 0;
		int x = 5;
		for (Component c : new Component[] { cb_Begrenzer, cb_andereMitZiehen, cb_Farbe, cb_Gitter, cb_texture,
				cb_light }) {
			c.setBounds(x, y, 100, 20);
			y += 25;
			count++;
			x = (count > 1 ? 105 : 5);
			gelaendeP.add(c);
		}
		cb_Begrenzer.setSelected(true);
		cb_Gitter.setSelected(true);
		cb_Farbe.setSelected(true);
		cb_andereMitZiehen.setSelected(true);

		// Mouseklickauswahl
		bg = new ButtonGroup();
		JLabel l = new JLabel("beim Klick:");
		l.setBounds(5, 65, 100, 20);
		gelaendeP.add(l);
		click_move = new JRadioButton("moven");
		click_move.setBounds(5, 85, 100, 20);
		click_none = new JRadioButton("none");
		click_none.setBounds(5, 105, 100, 20);
		click_tex = new JRadioButton("texen");
		click_tex.setBounds(5, 125, 100, 20);
		bg.add(click_move);

		bg.add(click_none);
		click_none.setSelected(true);
		bg.add(click_tex);
		gelaendeP.add(click_move);
		gelaendeP.add(click_none);
		gelaendeP.add(click_tex);

	}

	/**
	 * Cleares the Texture Area
	 */
	public static void clearTexArea() {
		String[] columnNames = { "Texture" };

		Object[][] data = {};

		tex_area = new JTable(data, columnNames);
	}
}
