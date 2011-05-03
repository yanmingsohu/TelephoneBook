package jym.wit;
// CatfoOD 2008.2.27

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import jym.img.logo_jpg;
import jym.lan.Lang;
import jym.tel.VersionCortrol;


public class AboutDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -6337746095748402016L;
	private JButton close;
	private Image img = 
		Toolkit.getDefaultToolkit().createImage(logo_jpg.getImage());
	
	
	public AboutDialog(Frame f) {
		super(f, Lang.get("about.about"));
		boolean modal = false;
		if (f==null) {
			modal = false;
		} else {
			modal = true;
		}
		this.setModal(modal);
		setResizable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 550;
		int height= 400;
		int x = (int)( (dim.width-width)/2 );
		int y = (int)( (dim.height-height)/2);
		setBounds(x, y, width, height);
		
		MediaTracker md = new MediaTracker(this);
		img = img.getScaledInstance(width, -1, 0);
		md.addImage(img, 0);
		try {
			md.waitForAll();
		} catch (InterruptedException e) {}
		
		Panel pan = new Panel(new FlowLayout(FlowLayout.CENTER,8,10));
		if (modal==true) {
			close = new JButton(Lang.get("all.close"));
			close.addActionListener(this);
			pan.add(close);
		}
		
		setLayout(new BorderLayout());
		add(new JLabel( new ImageIcon(img)), BorderLayout.NORTH);
		add(pan,	BorderLayout.SOUTH);

		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		String s1 = VersionCortrol.programname + Lang.get("about.mess1");
		String s2 = Lang.get("about.mess2");
		FontMetrics mm = g.getFontMetrics();
		int l1 = mm.charsWidth(s1.toCharArray(), 0, s1.toCharArray().length);
		int l2 = mm.charsWidth(s2.toCharArray(), 0, s2.toCharArray().length);

		g.drawString(s1, 500-l1, 205);
		g.drawString(s2, 500-l2, 225);
		g.drawString("Copyright CatfoOD 2008-2011 " + VersionCortrol.net, 25, 290);
		g.drawString("Telephone book. "+VersionCortrol.version, 25,310);
		g.drawString("", 25, 330);
		g.drawLine(0, 340, 550, 340);
		g.drawLine(20, 180, 20, 380);
	}
}
