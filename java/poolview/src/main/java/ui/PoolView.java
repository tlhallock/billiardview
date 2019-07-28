package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Ball;
import model.Constants;
import model.Pockets;
import model.Pockets.Pocket;
import model.Shot;
import model.Shot.Line;

public class PoolView {
	JFrame frame;
	TableView panel;
	final LinkedList<Shot> shots = new LinkedList<>();
	boolean rotated = true;
	UiController controller;
	
	public void show(Shot shot) {
		synchronized (shots) {
			shots.add(shot);
		}
	}
	
	public void setRotated(boolean rotated) {
		this.rotated = rotated;
	}

	public void selectShot(int index, Shot shot) {
		panel.setSelected(index);
	}
	
	public void clearShots() {
		synchronized (shots) {
			shots.clear();
		}
		panel.setSelected(-1);
		panel.repaint();
	}
		
	class TableView extends JPanel {
		private int mouseX = -1;
		private int mouseY = -1;
		
		private int selectedIdx = -1;

		public void setMousePosition(int x, int y) {
			mouseX = x;
			mouseY = y;
			repaint();
		}

		public void setSelected(int indexOfClosestShot) {
			selectedIdx = indexOfClosestShot;
		}

		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			int actualW = getWidth();
			int actualH = getHeight();
			g.setColor(Color.black);
			g.fillRect(0, 0, actualW, actualH);
			
			Point2D bd = getBoardDimensions(actualH);
			double w = bd.getX();
			double h = bd.getY();

			Rectangle tableLocation = mapRectangle(new Rectangle2D.Double(0, 0, Constants.TABLE_WIDTH, Constants.TABLE_HEIGHT), w, h);
			g.setColor(Color.green);
			g.fillRect(tableLocation.x, tableLocation.y, tableLocation.width, tableLocation.height);
			
			g.setColor(Color.WHITE);
			for (int x = 1; x <= Constants.NUM_HORIZONTAL_SPOTS; x++) {
				for (int y = 1; y <= Constants.NUM_VERTICAL_SPOTS; y++) {
					Point sp = mapPoint(Constants.getSpotLocation(x, y), w, h);
					g.fillOval(
						sp.x - Constants.SPOT_RADIUS,
						sp.y - Constants.SPOT_RADIUS,
						2 * Constants.SPOT_RADIUS,
						2 * Constants.SPOT_RADIUS
					);
				}
			}
			
			g.setColor(Color.blue);
			Stroke stroke = g.getStroke();
			g.setStroke(new BasicStroke(6));
			for (Pocket pocket : Pockets.Pocket.values()) {
				Line l = pocket.getLocation();
				Point p1 = mapPoint(l.start, w, h);
				Point p2 = mapPoint(l.stop, w, h);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
			}
			g.setStroke(stroke);
			
			for(Ball ball : controller.table.balls) {
				Rectangle l = mapRectangle(ball.getLocation(), w, h);
				if (ball.solid) {
					g.setColor(ball.getColor());
					g.fillOval(l.x, l.y, l.width, l.height);
				} else {
					g.setColor(Color.WHITE);
					g.fillOval(l.x, l.y, l.width, l.height);
					g.setColor(ball.getColor());
					double alpha = 0.4;
					g.fillRect(l.x, (int)(l.y + (1 - alpha) / 2 * l.height), l.width, (int)(alpha * l.height));
				}
				g.setColor(Color.BLACK);
				g.drawString(
					String.valueOf(ball.number),
					l.x,
					l.y
				);
			}
			
			int highlightedIdx = getIndexOfClosestShot(mouseX, mouseY, w, h);
			List<Shot> cshots = null;
			synchronized (shots) {
				cshots = (List<Shot>) shots.clone();
			}
			for (int i = 0; i < cshots.size(); i++) {
				draw(g, cshots.get(i), w, h, getShotColor(selectedIdx == i, highlightedIdx == i), selectedIdx == i || highlightedIdx == i);
			}
		}
		
		private Color getShotColor(boolean selected, boolean highlighted) {
			if (selected) {
				if (highlighted) {
					return Color.white;
				} else {
					return Color.white;
				}
			} else {
				if (highlighted) {
					return Color.blue;
				} else {
					return Color.black;
				}
			}
		}
		
		private void draw(Graphics2D g, Shot shot, double w, double h, Color color, boolean info) {
			g.setColor(color);

			if (info) {
				Point p1 = mapPoint(shot.cueBallMotion[1].start, w, h);
				Point p2 = mapPoint(shot.cueBallMotion[0].stop, w, h);
				Point p3 = mapPoint(shot.cueBallMotion[2].stop, w, h);
				g.fillPolygon(
					new int[] {p1.x, p2.x, p3.x},
					new int[] {p1.y, p2.y, p3.y},
					3
				);
				
				p1 = mapPoint(shot.objectBallMotion[1].start, w, h);
				p2 = mapPoint(shot.objectBallMotion[0].stop, w, h);
				p3 = mapPoint(shot.objectBallMotion[2].stop, w, h);
				g.fillPolygon(
					new int[] {p1.x, p2.x, p3.x},
					new int[] {p1.y, p2.y, p3.y},
					3
				);
			} else {
				Point p1 = mapPoint(shot.cueBallMotion[1].start, w, h);
				Point p2 = mapPoint(shot.cueBallMotion[1].stop, w, h);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
				
				p1 = mapPoint(shot.objectBallMotion[1].start, w, h);
				p2 = mapPoint(shot.objectBallMotion[1].stop, w, h);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
			}
						
			Rectangle ghostBall = mapRectangle(shot.getGhostBallLocation(), w, h);
			g.drawOval(ghostBall.x, ghostBall.y, ghostBall.width, ghostBall.height);

			if (info) {
				g.setColor(Color.white);
				for (int i=0; i<shot.secondary.length - 1; i++) {
					Point p1 = mapPoint(shot.secondary[i], w, h);
					Point p2 = mapPoint(shot.secondary[i+1], w, h);
					g.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}
		}
		
		private Rectangle mapRectangle(Rectangle2D boardSpace, double w, double h) {
			double percentage = 1.0;
			double buffer = (1 - percentage) * h / 2;
			if (rotated) {
				return new Rectangle(
					(int)(buffer + percentage * boardSpace.getY() / Constants.TABLE_WIDTH * w),
					(int)(buffer + percentage * boardSpace.getX() / Constants.TABLE_HEIGHT * h),
					(int)(percentage * boardSpace.getHeight() / Constants.TABLE_WIDTH * w),
					(int)(percentage * boardSpace.getWidth() / Constants.TABLE_HEIGHT * h)
				);
			} else {
				return new Rectangle(
					(int)(buffer + percentage * boardSpace.getX() / Constants.TABLE_WIDTH * w),
					(int)(buffer + percentage * boardSpace.getY() / Constants.TABLE_HEIGHT * h),
					(int)(percentage * boardSpace.getWidth() / Constants.TABLE_WIDTH * w),
					(int)(percentage * boardSpace.getHeight() / Constants.TABLE_HEIGHT * h)
				);
			}
		}
		
		private Point2D unMapPoint(int x, int y, double w, double h) {
			double percentage = 1.0;
			double buffer = (1 - percentage) * h / 2;
			if (rotated) {
				return new Point2D.Double(
					(y - buffer) * Constants.TABLE_WIDTH / (percentage * w),
					(x - buffer) * Constants.TABLE_HEIGHT / (percentage * h)
				);
			} else {
				return new Point2D.Double(
					(x - buffer) * Constants.TABLE_WIDTH / (percentage * w),
					(y - buffer) * Constants.TABLE_HEIGHT / (percentage * h)
				);
			}
		}
		
		private Point mapPoint(Point2D point, double w, double h) {
			Rectangle r = mapRectangle(new Rectangle2D.Double(point.getX(), point.getY(), 0.0, 0.0), w, h);
			return new Point(r.x, r.y);
		}
		
		private Point2D getBoardDimensions(int actualH) {
			double w, h;
			if (rotated) {
				w = actualH;
				h = w * Constants.TABLE_HEIGHT / Constants.TABLE_WIDTH;
			} else {
				h = actualH;
				w = h * Constants.TABLE_WIDTH / Constants.TABLE_HEIGHT;
			}
			return new Point2D.Double(w, h);
		}
		
		private int getIndexOfClosestShot(int x, int y, double w, double h) {
			if (x < 0 || y < 0) {
				return -1;
			}
			Point2D unmapped = unMapPoint(x, y, w, h);
			int minIndex = -1;
			double minDistance = Double.MAX_VALUE;
			for (int i = 0; i < shots.size(); i++) {
				double otherDistance = shots.get(i).distanceTo(unmapped);
				if (otherDistance > minDistance)
					continue;
				minDistance = otherDistance;
				minIndex = i;
			}
			return minIndex;
		}

		private int getIndexOfClosestShot(int x, int y) {
			Point2D bd = getBoardDimensions(getHeight());
			double w = bd.getX();
			double h = bd.getY();
			return getIndexOfClosestShot(x, y, w, h);
		}
	}
	
	
	public static PoolView createPoolView(UiController controller) {
		PoolView view = new PoolView();
		
		view.controller = controller;
		view.panel = view.new TableView();
		
		view.frame = new JFrame();
		view.frame.setBounds(new Rectangle(10, 10, 1500, 800));
		view.frame.setTitle("Pool Table");
		view.frame.setContentPane(view.panel);
		view.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		view.panel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				view.panel.setMousePosition(e.getX(), e.getY());
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {}
		});
		view.panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = view.panel.getIndexOfClosestShot(e.getX(), e.getY());
				if (index < 0)
					return;
				controller.selectShot(index, view.shots.get(index));
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {
				view.panel.setMousePosition(-1, -1);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
		
		return view;
	}
}
