package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DisplayCheckerBoard {
	public static JFrame createCheckerBoard() {
		JFrame frame = new JFrame();
		frame.setBounds(new Rectangle(10, 10, 1500, 800));
		frame.setTitle("Image");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setContentPane(new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paint(Graphics g) {
				g.setColor(Color.black);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(Color.white);
				int ROWS = 10;
				int width = getWidth() / ROWS;
				for (int i = 0; i < ROWS; i++) {
					for (int j = 0; j * width < getWidth(); j++) {
						if ((i + j) % 2 == 0)
							continue;
						g.fillRect(i * width, j * width, width, width);
					}
				}
			}
		});
		return frame;
	}
}
