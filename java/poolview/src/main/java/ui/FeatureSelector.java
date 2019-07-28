package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


public class FeatureSelector {
	JFrame frame;
	FeatureView view;
		
	BufferedImage image;
	Rectangle currentSelection;
	double width = 1.0;
	double locationX;
	double locationY;
	
	int translateStartX;
	int translateStartY;
	int translateStartLocationX;
	int translateStartLocationY;
	
	int selectStartX;
	int selectStartY;
	private JButton saveButton;
	
	private File parentDirectory;
	private JTextField pathLocation;
	
	protected void saveSelection(String text) {
		File file = new File(text);
		File parent = file.getParentFile();
		if (!parent.exists()) {
			if (!parent.mkdirs()) {
				System.out.println("Unable to create parent directory");
				return;
			}
		}
		if (file.exists()) {
			System.out.println("File already exists");
			return;
		}
		int idx = text.lastIndexOf('.');
		if (idx < 0 || idx + 1 == text.length()) {
			System.out.println("No extension");
			return;
		}
		try {
			if (!ImageIO.write(
					image.getSubimage(currentSelection.x, currentSelection.y, currentSelection.width, currentSelection.height),
					text.substring(idx + 1, text.length()),
					file
				)
			) {
				System.out.println("Unable to save image.");
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File saved");
		int number_index = idx - 1;
		LinkedList<Integer> indexes = new LinkedList<>();
		while ("0123456789".indexOf(text.charAt(number_index)) >= 0) {
			indexes.add(number_index);
			number_index -= 1;
		}
		if (!indexes.isEmpty()) {
			Collections.reverse(indexes);
			StringBuilder num = new StringBuilder();
			for (Integer i : indexes) {
				num.append(text.charAt(i));
			}
			String nextPath = text.substring(0, number_index + 1) + (Integer.valueOf(num.toString()) + 1) + text.substring(idx);
			pathLocation.setText(nextPath);
		}
	}

	protected void openNewImage() {        
		JFileChooser chooser = new JFileChooser();
		if (parentDirectory == null) {
			chooser.setCurrentDirectory(new File("."));
		} else {
			chooser.setCurrentDirectory(parentDirectory);
		}
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "gif", "png"));
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		try {
			File file = chooser.getSelectedFile();
			parentDirectory = file.getParentFile();
			show(ImageIO.read(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void startDrag(int x, int y, int modifiers) {
		if(modifiers == 8) {
			translateStartX = x;
			translateStartY = y;
			translateStartLocationX = (int) locationX;
			translateStartLocationY = (int) locationY;
		} else if (modifiers == 16) {
			selectStartX = x;
			selectStartY = y;
		}
	}

	protected void dragged(int x, int y, int modifiers) {
		if (modifiers == 8 && translateStartX >= 0 && translateStartY >= 0) {
			locationX = translateStartLocationX + (translateStartX - x) * (width / (double) view.getWidth());
			locationY = translateStartLocationY + (translateStartY - y) * (width / (double) view.getWidth());
		} else if (modifiers == 16 && selectStartX >= 0 && selectStartY >= 0) { 
			currentSelection = mapToImg(getRectangle(selectStartX, selectStartY, x, y));
			saveButton.setEnabled(true);
			System.out.println(currentSelection);
		}
		view.repaint();
	}

	protected void resetDrag() {
		selectStartX = -1;
		selectStartY = -1;
		translateStartX = -1;
		translateStartY = -1;
		translateStartLocationX = -1;
		translateStartLocationY = -1;
		view.repaint();
	}

	protected void finalizeDrag() {
		resetDrag();
	}

	public void show(BufferedImage image) {
		this.image = image;
		currentSelection = null;
		this.width = image.getWidth();
		this.locationX = 0;
		this.locationY = 0;
		view.repaint();
	}

	public void zoom(int wheelRotation, int mx, int my) {
		double ww = view.getWidth();
		double wh = view.getHeight();
		double centerX = locationX + width * (mx / ww);
		double centerY = locationY + (wh / ww) * width * (my / wh);
		for (int i = 0; i < Math.abs(wheelRotation); i++) {
			if (wheelRotation < 0) {
				width *= 0.9;
			} else {
				width *= 1.2;
			}
		}
		locationX = centerX - width * (mx / ww);
		locationY = centerY - (wh / ww) * width * (my / wh);
		view.repaint();
	}
	
	private Rectangle mapToImg(Rectangle window) {
		return new Rectangle(
			(int)(window.x * width / view.getWidth() + locationX),
			(int)(window.y * width / view.getWidth() + locationY),
			(int)(window.width * width / view.getWidth()),
			(int)(window.height * width / view.getWidth())
		);
	}
	
	private Rectangle mapToWindow(Rectangle img) {
		if (img == null) {
			return null;
		}
		return new Rectangle(
			(int)((img.x - locationX) * view.getWidth() / width),
			(int)((img.y - locationY) * view.getWidth() / width),
			(int)(img.width * view.getWidth() / width),
			(int)(img.height * view.getWidth() / width)
		);
	}
	
	private Rectangle getRectangle(int x1, int y1, int x2, int y2) {
		return new Rectangle(
			Math.min(x1, x2),
			Math.min(y1, y2),
			Math.max(x1, x2) - Math.min(x1, x2),
			Math.max(y1, y2) - Math.min(y1, y2)
		);
	}
	
	private Rectangle intersect(
		double r1x,
		double r1y,
		double r1w,
		double r1h,
		double r2x,
		double r2y,
		double r2w,
		double r2h
	) {
		return new Rectangle(
			(int)(Math.max(r1x, r2x)),
			(int)(Math.max(r1y, r2y)),
			(int)(Math.min(r1x + r1w, r2x + r2w) - Math.max(r1x, r2x)),
			(int)(Math.min(r1y + r1h, r2y + r2h) - Math.max(r1y, r2y))
		);	
	}

	class FeatureView extends JPanel {
		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			int w = getWidth();
			int h = getHeight();
			g.setColor(Color.black);
			g.fillRect(0, 0, w, h);
			if (image == null) {
				return;
			}
			int iw = image.getWidth();
			int ih = image.getHeight();
			Rectangle windowDest = intersect(
				-locationX * (w / width),
				-locationY * (w / width),
				iw * w / width,
				ih * w / width,
				0, 0, w, h
			);
			Rectangle imageSrc = intersect(
				locationX,
				locationY,
				width,
				(h / (double) w) * width,
				0, 0, iw, ih
			);
			
			g.drawImage(
				image,
				windowDest.x,
				windowDest.y,
				windowDest.x + windowDest.width,
				windowDest.y + windowDest.height,
				imageSrc.x,
				imageSrc.y,
				imageSrc.x + imageSrc.width,
				imageSrc.y + imageSrc.height,
				Color.black,
				this
			);
			Rectangle selection = mapToWindow(currentSelection);
			if (selection != null) {
				g.setColor(Color.red);
				g.drawRect(selection.x, selection.y, selection.width, selection.height);
			}
		}
	}
	
	public static FeatureSelector createFeatureSelector() {
		FeatureSelector selector = new FeatureSelector();
		selector.view = selector.new FeatureView();
		selector.parentDirectory = new File("/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro");
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				selector.zoom(e.getWheelRotation(), e.getX(), e.getY());
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				selector.saveButton.setEnabled(false);
				selector.currentSelection = null;
				selector.resetDrag();

				if (selector.image == null) {
					return;
				}
				int imgX = (int) (e.getX() * selector.width / selector.view.getWidth() + selector.locationX);
				int imgY = (int) (e.getY() * selector.width / selector.view.getWidth() + selector.locationY);
				System.out.println(imgX + ", " + imgY + ": " + new Color(selector.image.getRGB(imgX, imgY)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				selector.finalizeDrag();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				selector.startDrag(e.getX(), e.getY(), e.getModifiers());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				selector.resetDrag();
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				selector.dragged(e.getX(), e.getY(), e.getModifiers());
			}
		};
		selector.view.addMouseMotionListener(mouseAdapter);
		selector.view.addMouseListener(mouseAdapter);
		selector.view.addMouseWheelListener(mouseAdapter);
		
		selector.pathLocation = new JTextField("/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro/balls/6/stickers/0.png");
		selector.saveButton = new JButton("Save");
		selector.saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selector.saveSelection(selector.pathLocation.getText());
			}
		});
		selector.saveButton.setEnabled(false);
		JButton openButton = new JButton("Open");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selector.openNewImage();
			}
		});
		
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, 0));
		controlPanel.add(selector.pathLocation);
		controlPanel.add(selector.saveButton);
		controlPanel.add(openButton);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(selector.view);
		splitPane.setBottomComponent(controlPanel);
		splitPane.setDividerLocation(600);
		
		selector.frame = new JFrame();
		selector.frame.setBounds(new Rectangle(10, 10, 1500, 800));
		selector.frame.setTitle("Image");
		selector.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		selector.frame.setContentPane(splitPane);
		
		return selector;
	}
}
