package main;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import gen.ShotGenerator.PracticeType;
import model.Constants;
import ui.UiController;

public class Network {
	public static final int END_STREAM = 0;
	
	public static final int UPDATE_TABLE = 1;

	public static final int SHOW_RESET_VIEW = 2;
	public static final int UPDATE_RESET_BASE = 3;
	public static final int UPDATE_RESET_CURRENT = 4;
	public static final int HIDE_RESET = 5;
	
	public static final int BEGIN_PRACTICE =  6;
	public static final int SHOW_TABLE = 7;
	public static final int SHOW_SHOT_VIEW = 8;
	public static final int END_PRACTICE =  9;
	
	public static final int SELECT_SHOT = 10;
	public static final int SELECT_SHOT_RESULT = 11;
	public static final int TURN_ON_TRY_AGAIN = 12;
	public static final int TURN_OFF_TRY_AGAIN = 13;
	
	
	private static final int MESSAGE_LENGTH = Double.BYTES * Constants.NUMBER_OF_BALLS * 2;
	private static final int PORT_NUMBER = 8096;
	
	private static boolean readFullBuffer(byte[] buffer, BufferedInputStream input) throws IOException {
		int read = 0;
		while (read != buffer.length) {
			int r = input.read(buffer, read, buffer.length - read);
			if (r < 0) {
				return false;
			}
			read += r;
		}
		return true;
	}
	private static void readPoolTable(ByteBuffer buffer, UiController controller) {
		LinkedList<Point2D> points = new LinkedList<>();
		for (int i=0; i<Constants.NUMBER_OF_BALLS; i++) {
			points.add(new Point2D.Double(
				buffer.getDouble(Double.BYTES * 2 * i),
				buffer.getDouble(Double.BYTES * (2 * i + 1))
			));
		}
		controller.setBallLocations(points.toArray(Constants.DUMMY));
	}
	
	private static BufferedImage readImageFromStream(BufferedInputStream inputStream) throws IOException {
		return ImageIO.read(inputStream);
	}
	
	public static void listenForLocations(UiController controller) throws IOException {
		byte[] poolTableBuffer = new byte[MESSAGE_LENGTH];
		try (ServerSocket socket = new ServerSocket(PORT_NUMBER);) {
			while (true) {
				try (
					Socket accept = socket.accept();
					BufferedInputStream bufferedInputStream = new BufferedInputStream(accept.getInputStream());
				) {
					int b = bufferedInputStream.read();
					if (b <= 0)
						continue;

					switch (b) {
					case UPDATE_TABLE:
						readFullBuffer(poolTableBuffer, bufferedInputStream);
						readPoolTable(ByteBuffer.wrap(poolTableBuffer), controller);
						break;
					case UPDATE_RESET_BASE:
						controller.setBaseReset(readImageFromStream(bufferedInputStream));
						break;
					case UPDATE_RESET_CURRENT:
						controller.setCurrentReset(readImageFromStream(bufferedInputStream));
						break;
					case SHOW_TABLE:
						controller.showTable();
						break;
					case SHOW_SHOT_VIEW:
						controller.showShotView();
						break;
					case SHOW_RESET_VIEW:
						controller.showResetView();
						break;
					case BEGIN_PRACTICE:
						int typeNum = 0;
						int numBalls = 0;
						boolean includeWalls = false;

						PracticeType type = null;
						if (typeNum == 0)
							type = PracticeType.Uniform; 
						else if (typeNum == 1)
							type = PracticeType.RandomSpot;
						else if (typeNum == 2)
							type = PracticeType.Learned;
						else
							throw new RuntimeException("Unknown practice type: " + typeNum);
						controller.showPractice(type, numBalls, includeWalls);
						break;
					default:
						System.out.println("Unknown: " + b);
					}
				}
			}
		}
	}
}
