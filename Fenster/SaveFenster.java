package Fenster;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import main.SuperMain;
import Classes.Level;

/**
 * Load and save methods for the Editor
 * 
 * @author laDanz
 * 
 */
public class SaveFenster extends EditorFenster {
	JButton bu_save = new JButton("speichern");
	JButton bu_load = new JButton("laden");
	JButton bu_neu = new JButton("neu");
	JButton bu_test = new JButton("testen");
	JButton bu_smothen = new JButton("smothen");
	static JLabel file_name_l = new JLabel("");

	/**
	 * Constructor with position and size of the window 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public SaveFenster(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.setTitle("MainMenu");
		initComp();
		repaint();
	}

	/**
	 * Simple Setter for File Name
	 * @param s
	 */
	public static void setFileName(String s) {
		s = s.substring(Math.max(0, s.length() - 20));
		file_name_l.setText(s);
	}

	private void initComp() {
		bu_save.setBounds(20, 20, 100, 20);
		bu_save.addMouseListener(onSave());
		add(bu_save);
		bu_load.setBounds(20, 50, 100, 20);
		bu_load.addMouseListener(onLoad());
		add(bu_load);
		bu_neu.setBounds(20, 0, 100, 20);
		bu_neu.addMouseListener(onNeu());
		add(bu_neu);
		file_name_l.setBounds(10, 140, 200, 20);
		add(file_name_l);
		bu_test.setBounds(20, 80, 100, 20);
		add(bu_test);
		bu_test.addMouseListener(onTest());
		
		bu_smothen.setBounds(20, 110, 100, 20);
		add(bu_smothen);
		bu_smothen.addMouseListener(onSmoothen());
	}

	private MouseListener onSmoothen() {
		return new MouseListener(
			
		){
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			public void mouseReleased(MouseEvent e) {
				SuperMain.toRun.add(new Runnable(){
				public void run() {
					SuperMain.level.smoothen();
						
					}	
				});
				
				
			}
			
		};
	

	}

	private MouseListener onTest() {
		// TODO Auto-generated method stub
		return new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent arg0) {

				SuperMain.level
						.save("level" + SuperMain.seperator + "temp.lvl");
				main.Editor.state = "LevelPlay,level" + SuperMain.seperator
						+ "temp.lvl,Editor,level" + SuperMain.seperator
						+ "temp.lvl";

			}

		};
	}

	private MouseListener onNeu() {
		// TODO Auto-generated method stub
		return new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent arg0) {
				new NeuDialog();

			}
		};
	}

	private MouseListener onLoad() {
		// TODO Auto-generated method stub
		return new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent arg0) {
				final File startvz = new File(SuperMain.ordner + "level");
				final JFileChooser f_ = new JFileChooser(startvz) {

					public boolean accept(File f) {
						String s = SuperMain.ordner + "level";
						if (!f.getAbsolutePath().toLowerCase().startsWith(
								s.toLowerCase())) {
							this.setCurrentDirectory(startvz);
						}

						return (f.getAbsolutePath().toLowerCase()
								.endsWith("xml"));
					}

				};

				if (f_.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					SuperMain.level = new Level();
					SuperMain.level
							.load(f_.getSelectedFile().getAbsolutePath());
					String file = f_.getSelectedFile().getAbsolutePath();
					setFileName(file);
					GelaendeFenster.clearTexArea();
				}

			}

		};
	}

	private MouseListener onSave() {
		// TODO Auto-generated method stub
		return new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent arg0) {
				final File startvz = new File(SuperMain.ordner + "level");
				final JFileChooser f_ = new JFileChooser(startvz) {

					public boolean accept(File f) {
						String s = SuperMain.ordner + "level";
						if (!f.getAbsolutePath().toLowerCase().startsWith(
								s.toLowerCase())) {
							this.setCurrentDirectory(startvz);
						}

						return (f.getAbsolutePath().toLowerCase()
								.endsWith("xml"));
					}

				};

				if (f_.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					String file = f_.getSelectedFile().getAbsolutePath();
					if (!file.toLowerCase().endsWith(".xml")) {
						file += ".xml";
					}
					SuperMain.level.save(file);
					setFileName(file);
				}

			}

		};
	}

}
