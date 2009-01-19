package Fenster;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import main.SuperMain;
import Classes.Level;
import Classes.quad;
import Classes.v3;

/**
 * Dialog for new field
 * 
 * @author laDanz
 * 
 */
public class NeuDialog extends JDialog {
	JButton ok;
	JButton cancel;
	JTextField x;
	JTextField y;
	JTextField resolution;

	/**
	 * Constructs a new Dialog
	 */
	public NeuDialog() {
		this.setTitle("Felddaten");
		this.setSize(300, 200);
		this.setLayout(null);
		//this.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
		//FIXME für linux raus 
		int x = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width - 300) / 2;
		int y = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - 200) / 2;
		setBounds(x, y, 300, 200);
		initComps();
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		repaint();
	}

	/**
	 * Init the GUI of the Dialog
	 */
	private void initComps() {
		ok = new JButton("OK");
		ok.setBounds(30, 130, 100, 20);
		ok.addActionListener(onOK());
		add(ok);
		cancel = new JButton("Abbrechen");
		cancel.setBounds(170, 130, 100, 20);
		cancel.addActionListener(onCancel());
		add(cancel);
		JLabel l3 = new JLabel("Lnge eines Feldes");
		l3.setBounds(5, 45, 120, 20);
		add(l3);
		JLabel l2 = new JLabel("Anzahl Felder Y");
		l2.setBounds(5, 25, 100, 20);
		add(l2);
		JLabel l1 = new JLabel("Anzahl Felder X");
		l1.setBounds(5, 5, 100, 20);
		add(l1);
		x = new JTextField();
		x.setBounds(150, 5, 100, 20);
		add(x);
		y = new JTextField();
		y.setBounds(150, 25, 100, 20);
		add(y);
		resolution = new JTextField();
		resolution.setBounds(170, 45, 80, 20);
		add(resolution);
	}

	private ActionListener onCancel() {

		return new ActionListener() {

			/**
			 * Simple action
			 */
			public void actionPerformed(ActionEvent e) {
				dispose();

			}

		};
	}

	private ActionListener onOK() {

		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int x_ = 0;
				try {
					x_ = Integer.parseInt(x.getText());
					if (x_ < 1)
						throw new RuntimeException("kleiner 1!");
				} catch (Exception ex) {
					org.lwjgl.Sys.alert("Fehler!",
							"X muss eine natrliche Zahl sein!");
					return;
				}
				int y_ = 0;
				try {
					y_ = Integer.parseInt(y.getText());
					if (y_ < 1)
						throw new RuntimeException("kleiner 1!");
				} catch (Exception ex) {
					org.lwjgl.Sys.alert("Fehler!",
							"Y muss eine natrliche Zahl sein!");
					return;
				}
				Double z_ = null;
				try {
					z_ = Double.parseDouble(resolution.getText());
					if (z_ < 0)
						throw new RuntimeException("kleiner 0!");
				} catch (Exception ex) {
					org.lwjgl.Sys.alert("Fehler!", "Z muss eine Zahl>0 sein!");
					return;
				}
				SaveFenster.setFileName("");
				Level l = new Level();
				LinkedList<quad> quads = new LinkedList<quad>();
				for (int x = 0; x < x_; x++) {
					for (int y = 0; y < y_; y++) {
						quad q = new quad(new v3(x * z_, 0, -y * z_), new v3(
								z_, 0, -z_));
						quads.add(q);
					}
				}
				l.addQuads(quads);
				l.width = x_ * z_;
				l.depth = y_ * z_;
				SuperMain.level = l;
				GelaendeFenster.clearTexArea();
				dispose();
			}

		};
	}

	/**
	 * Creates the Dialog
	 * @param args
	 */
	public static void main(String[] args) {
		new NeuDialog();
	}

}
