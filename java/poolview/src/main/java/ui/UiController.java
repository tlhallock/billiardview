package ui;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import gen.ShotGenerator;
import model.Pockets;
import model.Pockets.Pocket;
import model.Shot;
import model.Table;

public class UiController {
	Table table;
	
	ShotView shotView;
	PoolView poolView;
	LoggerView practiceView;
	MainMenu mainMenuView;
	ResetView resetView;
	
	private JFrame checkerBoard;
	private FeatureSelector featureSelector;

	
	/////////////////////////////////////////////////////////////////////////////

	public void showMainMenu() {
		resetView.frame.setVisible(false);
		mainMenuView.frame.setVisible(true);
		shotView.frame.setVisible(false);
		poolView.frame.setVisible(false);
		if (practiceView != null)
			practiceView.frame.setVisible(false);
		checkerBoard.setVisible(false);
		featureSelector.frame.setVisible(false);
	}

	///////////////////////////////////////////////////////////////////////////
	
	public void setBaseReset(BufferedImage readImageFromStream) {
		resetView.reset = readImageFromStream;
		resetView.panel.repaint();
	}

	public void setCurrentReset(BufferedImage readImageFromStream) {
		try (OutputStream newOutputStream = Files.newOutputStream(Paths.get("testing" + System.currentTimeMillis() + ".png"));) {
			ImageIO.write(readImageFromStream, "png", newOutputStream);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		resetView.current = readImageFromStream;
		resetView.panel.repaint();
	}
	
	public void showResetView() {
		resetView.frame.setVisible(true);
		shotView.frame.setVisible(false);
		poolView.frame.setVisible(false);
		checkerBoard.setVisible(false);
		featureSelector.frame.setVisible(false);
		if (practiceView != null)
			practiceView.frame.setVisible(false);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	public void showPractice(ShotGenerator.PracticeType type, int i, boolean includeWalls) {
		resetView.frame.setVisible(false);
		shotView.frame.setVisible(true);
		poolView.frame.setVisible(true);
		checkerBoard.setVisible(false);
		featureSelector.frame.setVisible(false);
		if (practiceView != null)
			practiceView.frame.dispose();
		practiceView = LoggerView.createView(this, type, i, includeWalls);
		practiceView.frame.setVisible(true);
		practiceView.showNextPracticeShot();
	}

	public void selectShot(int index, Shot shot) {
		poolView.selectShot(index, shot);
		shotView.setAim(shot.getCutAim());
		if (practiceView != null)
			practiceView.selectShot(shot);
	}
	
	public void showExistingPractice() {
//		resetView.frame.setVisible(false);
		checkerBoard.setVisible(false);
		featureSelector.frame.setVisible(false);
		
		shotView.frame.setVisible(true);
		poolView.frame.setVisible(true);
		if (practiceView != null) {
			practiceView.frame.setVisible(true);
			practiceView.showNextPracticeShot();
		}
	}
	
	public void showTable() {
		poolView.frame.setVisible(true);
	}

	public void showShotView() {
		shotView.frame.setVisible(true);
	}
	
	/////////////////////////////////////////////////////////////////////////////

	public void showCheckerBoard() {
		resetView.frame.setVisible(false);
		shotView.frame.setVisible(false);
		poolView.frame.setVisible(false);
		if (practiceView != null)
			practiceView.frame.setVisible(false);
		checkerBoard.setVisible(true);
		featureSelector.frame.setVisible(false);
	}

	public void classify() {
		resetView.frame.setVisible(false);
		shotView.frame.setVisible(false);
		poolView.frame.setVisible(false);
		if (practiceView != null)
			practiceView.frame.setVisible(false);
		checkerBoard.setVisible(false);
		featureSelector.frame.setVisible(false);
		
		try {
			Classifier classifier = Classifier.showClassifier("/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro/one_undistorted_pic.png");
			classifier.frame.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void selectFeatures() {
		resetView.frame.setVisible(false);
		shotView.frame.setVisible(false);
		poolView.frame.setVisible(false);
		if (practiceView != null)
			practiceView.frame.setVisible(false);
		checkerBoard.setVisible(false);
		featureSelector.frame.setVisible(true);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	public void showShots() {
		int shotCount = 0;
		for (int b = 1; b < table.balls.length; b++) {
			if (table.balls[b].location.getX() < 0 || table.balls[b].location.getY() < 0)
				continue;
			for (Pocket pocket : Pockets.Pocket.values()) {
				Shot shot = Shot.calculateShot(table.balls[0], table.balls[b], pocket);
				if (!shot.isPossible(table)) {
					continue;
				}
				poolView.show(shot);
				shotCount++;
			}
		}
		if (shotCount == 1) {
			selectShot(0, poolView.shots.get(0));
		}
	}

	public void setBallLocations(Point2D[] locations) {
		clear();
		for (int i=0; i<locations.length; i++) {
			table.balls[i].location = locations[i]; 
		}
		showShots();
	}

	public void clear() {
		poolView.clearShots();
		if (practiceView != null)
			practiceView.clear();
		shotView.clear();
	}
	
	public static UiController createController() {
		UiController controller = new UiController();
//		controller.table = Table.createRandomTable();
		controller.table = Table.createEmptyTable();
		controller.shotView = ShotView.createPoolView();
		controller.poolView = PoolView.createPoolView(controller);
		controller.practiceView = null;
		controller.checkerBoard = DisplayCheckerBoard.createCheckerBoard();
		controller.featureSelector = FeatureSelector.createFeatureSelector();
		controller.mainMenuView = MainMenu.createMainMenu(controller);
		controller.resetView = ResetView.createResetView();
		return controller;
	}
}
