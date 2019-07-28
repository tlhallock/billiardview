package main;

import java.io.IOException;

import ui.UiController;

public class Driver {
	public static void main(String[] args) throws IOException, InterruptedException {
		UiController controller = UiController.createController();
		
		controller.showMainMenu();
		
//		controller.showResetView();
		
//		controller.show();
//		controller.showNextPracticeShot();
//		controller.showShots();
		
		
		
		// move the collision detection
		// bank shots
		// detect scratch
		// carem
		// model based on speed, spin, follow, 
		
		
//		String pathname = ;
//		File imageFile = ;
//		FeatureSelector selector = FeatureSelector.createFeatureSelector();
//		BufferedImage image = ImageIO.read(imageFile);
//		selector.show(image);
		
//		Classifier.showClassifier("/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro/some_balls").frame.setVisible(true);
		
		
		Network.listenForLocations(controller);
	}
}
