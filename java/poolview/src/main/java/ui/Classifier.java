package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class Classifier {
	public JFrame frame;
	private BufferedImage image;
	private int currentIndex = 0;
	private File[] files;
	
	public Classifier(File[] files) {
		this.files = files;
		currentIndex = -1;
	}

	protected void setNextImage() {
		this.currentIndex++;
		if (files == null) {
			return;
		}
		if (currentIndex >= files.length) {
			setImage(null);
			return;
		}
		try {
			setImage(ImageIO.read(files[currentIndex]));
			frame.repaint();
		} catch (IOException e) {
			e.printStackTrace();
			setImage(null);
		}
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	private class ImageViewer extends JPanel {
		@Override
		public void paint(Graphics g) {
			if (image == null) {
				g.setColor(Color.black);
				g.fillRect(0, 0, getWidth(), getHeight());
			} else {
			    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
			}
		}
	}
	
	private class Selection {
		private String destinationDirectory;
		private String label;
		
		public Selection(String destinationDirectory, String label) {
			this.destinationDirectory = destinationDirectory;
			this.label = label;
		}
		
		public void mkdirs() {
			new File(destinationDirectory).mkdirs();
		}
		
		private File getNonExistentFile() {
			int num = 0;
			File ret = null;
			do {
				ret = new File(destinationDirectory + "/" + String.valueOf(num++) + ".png");
			} while (ret.exists());
			return ret;
		}
		
		public void perform() {
			if (image == null) {
				return;
			}
			try (FileOutputStream fileOutputStream = new FileOutputStream(getNonExistentFile());) {
				ImageIO.write(image, "png", fileOutputStream);
				System.out.println("Image written");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Classifier showClassifier(String directory) throws IOException {
		Classifier classifier = new Classifier(new File(directory).listFiles());
		
		ImageViewer imageViewer = classifier.new ImageViewer();
		
		Selection[] selections = new Selection[] {
			classifier.new Selection("./classifier/garbage", "Nothing"),
			classifier.new Selection("./classifier/0",  "Cue Ball"),
			classifier.new Selection("./classifier/1",  "One"),
			classifier.new Selection("./classifier/2",  "Two"),
			classifier.new Selection("./classifier/3",  "Three"),
			classifier.new Selection("./classifier/4",  "Four"),
			classifier.new Selection("./classifier/5",  "Five"),
			classifier.new Selection("./classifier/6",  "Six"),
			classifier.new Selection("./classifier/7",  "Seven"),
			classifier.new Selection("./classifier/8",  "Eight"),
			classifier.new Selection("./classifier/9",  "Nine"),
			classifier.new Selection("./classifier/10", "Ten"),
			classifier.new Selection("./classifier/11", "Eleven"),
			classifier.new Selection("./classifier/12", "Twelve"),
			classifier.new Selection("./classifier/13", "Thirteen"),
			classifier.new Selection("./classifier/14", "Fourteen"),
			classifier.new Selection("./classifier/15", "Fifteen"),
		};
		for (Selection selection : selections) {
			selection.mkdirs();
		}
		
		JPanel selectorPanel = new JPanel();
		selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
		for (Selection selection : selections) {
			JButton button = new JButton(selection.label);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selection.perform();
					classifier.setNextImage();
					imageViewer.repaint();
				}});
			selectorPanel.add(button);
		}
		
		JSplitPane pane = new JSplitPane();
		pane.setDividerLocation(1500);
		pane.setTopComponent(imageViewer);
		pane.setBottomComponent(selectorPanel);
		
		classifier.frame = new JFrame();
		classifier.frame.setBounds(new Rectangle(2000, 10, 1600, 800));
		classifier.frame.setTitle("Classifier");
		classifier.frame.setContentPane(pane);
		classifier.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		classifier.setNextImage();
		
		return classifier;
	}
}
