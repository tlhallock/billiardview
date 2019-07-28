package ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ResetView {
	JFrame frame;
	JPanel panel;
	
	BufferedImage current;
	BufferedImage reset;

	public static ResetView createResetView() {
		ResetView view = new ResetView();

		view.panel = new JPanel() {
			@Override
			public void paint(Graphics graphics) {
				Graphics2D g = (Graphics2D) graphics;
				
				int w = getWidth();
				int h = getHeight();
				
				g.setColor(Color.black);
				g.fillRect(0, 0, w, h);
				

				if (view.current != null) {
					g.drawImage(view.current, 0, 0, w, h, this);
				}
				
				if (view.reset != null) {
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5F));
					g.drawImage(view.reset, 0, 0, w, h, this);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
				}
			}
		};
		

		view.frame = new JFrame();
		view.frame.setBounds(new Rectangle(10, 10, 1500, 800));
		view.frame.setTitle("Pool Reset");
		view.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		view.frame.setContentPane(view.panel);
		
		return view;
	}
}
