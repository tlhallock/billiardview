package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ShotView {
	JFrame frame;
	BallView panel;
	double minCut;
	double maxCut;
	
	private static final int LINE_WIDTH = 1;

	private class BallView extends JPanel {
		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			int w = getWidth();
			int ah = getHeight();
			int r = w / 4;
			int h = 2 * r;
			
			g.setColor(Color.black);
			g.fillRect(0, 0, w, ah);
			g.setColor(Color.white);
			g.fillOval(r, 0, 2 * r, 2 * r);
			g.setColor(Color.red);
			
			if (minCut >= -2 && maxCut >= -2) {
				g.fillRect((int)(2 * r + minCut * r), 0, 1, h);
				g.fillRect((int)(2 * r + maxCut * r) - 1, 0, 1, h);
			}
		}
	}

	public void clear() {
		minCut = -5.0;
		maxCut = -5.0;
		panel.repaint();
	}
	
	public void setAim(double[] cutAim) {
		minCut = Math.min(cutAim[0], cutAim[1]);
		maxCut = Math.max(cutAim[0], cutAim[1]);
		panel.repaint();
	}
		
	public static ShotView createPoolView() {
		ShotView view = new ShotView();
		
		view.panel = view.new BallView();
		
		view.frame = new JFrame();
		view.frame.setBounds(new Rectangle(10, 10, 1600, 800));
		view.frame.setTitle("Shot View");
		view.frame.setContentPane(view.panel);
		view.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		view.clear();
		
		return view;
	}
}
