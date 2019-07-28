package ui;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import gen.ShotGenerator;

public class MainMenu {
	JFrame frame;

	public static MainMenu createMainMenu(UiController controller) {
		MainMenu view = new MainMenu();
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		{
			JPanel practicePanel = new JPanel();
			
			JCheckBox includeWallShots = new JCheckBox("Include wall shots");
			JCheckBox practicePosition = new JCheckBox("Practice position");
			
			JComboBox<ShotGenerator.PracticeType> practiceType = new JComboBox<>(ShotGenerator.PracticeType.values());
			practiceType.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ShotGenerator.PracticeType type = (ShotGenerator.PracticeType) practiceType.getSelectedItem();
					includeWallShots.setEnabled(type.supportsWallToggle());
					practicePosition.setEnabled(type.supportsPosition());
				}
			});
			
			JButton button = new JButton("Practice Shots");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.showPractice(
						(ShotGenerator.PracticeType) practiceType.getSelectedItem(), 
						practicePosition.isSelected() ? 3 : 2,
						includeWallShots.isSelected()
					);
				}
			});
			
			practicePanel.add(practiceType);
			practicePanel.add(includeWallShots);
			practicePanel.add(practicePosition);
			practicePanel.add(button);
			
			panel.add(practicePanel);
		}

		{
			JButton button = new JButton("Feature Selector");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.selectFeatures();
				}
			});
			panel.add(button);
		}

		{
			JButton button = new JButton("Classify");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.classify();
				}
			});
			panel.add(button);
		}
		
		{
			JButton button = new JButton("Show Checkerboard");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.showCheckerBoard();
				}
			});
			panel.add(button);
		}
		
		view.frame = new JFrame();
		view.frame.setBounds(new Rectangle(10, 10, 800, 800));
		view.frame.setTitle("Main Menu");
		view.frame.setContentPane(panel);
		view.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		return view;
	}
}
