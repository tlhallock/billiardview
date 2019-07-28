package ui;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import gen.ShotGenerator;
import gen.ShotResult;
import model.Shot;


public class LoggerView {
	UiController controller;
	JFrame frame;
	
	private ShotGenerator logger;
	private LinkedList<JComponent> buttons = new LinkedList<>();
	
	public void clear() {
		for (JComponent button : buttons) {
			button.setEnabled(false);
		}
		logger.clear();
	}
	
	public void selectShot(Shot shot) {
		for (JComponent button : buttons) {
			button.setEnabled(true);
		}
		logger.shotSelected(shot);
	}
	
	public void showNextPracticeShot() {
		controller.setBallLocations(logger.getNextBallLocations());
	}
	
	public static LoggerView createView(UiController controller, ShotGenerator.PracticeType type, int numBalls, boolean includesWalls) {
		LoggerView view = new LoggerView();
		view.logger = ShotGenerator.createShotGenerator(type, numBalls, includesWalls);
		view.controller = controller;
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		
		JButton skip = new JButton("Skip");
		panel.add(skip);
		skip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.showNextPracticeShot();
			}
		});
		
		JCheckBox tryAgain = new JCheckBox("Try Again");
		panel.add(tryAgain);
		view.buttons.add(tryAgain);
		
		for (ShotResult result : ShotResult.values()) {
			if (result.requiredBalls() != numBalls) {
				continue;
			}
			JButton button = new JButton(result.text());
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean tryingAgain = tryAgain.isSelected();
					view.logger.attemptedShot(result, tryingAgain);
					if (!tryingAgain) {
						view.showNextPracticeShot();
					}

					File outputFile = new File("attempts.csv");
					try {
						view.logger.save(outputFile);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});
			button.setEnabled(false);
			panel.add(button);
			view.buttons.add(button);
		}
		
		view.frame = new JFrame();
		view.frame.setBounds(new Rectangle(10, 10, 800, 800));
		view.frame.setTitle("Logger Prompt");
		view.frame.setContentPane(panel);
		view.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		view.clear();
		
		return view;
	}
}
