package com.lucent.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import acm.gui.TableLayout;

import com.lucent.control.XController;

class Variable {
	public static final String DEFAULT_URL = "http://127.0.0.1:8000/123456/api";
	public static final String DEFAULT_BP_DIR = "\\\"/u/ainet/hongwehl/src/";
	public static final String DEFAULT_MGTS = "cetest";
	private final String name;
	private JTextField inputBox;
	
	public Variable(String name) {
		this.name = name;
		inputBox = new JTextField("", 50);
	}
	
	public Variable(String name, String text) {
		this.name = name;
		inputBox = new JTextField(text, 50);
	}
	
	public String getName() {
		return name;
	}
	
	public String getText() {
		return inputBox.getText();
	}
	
	public void setText(String text) {
		inputBox.setText(text);
	}
	
	public JTextField getInputBox() {
		return inputBox;
	}
	
}
public class ConfigurationGui {
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 800;
	
	private XController controller;
	private JDialog mainWindow;
	private JScrollPane mainPane;
	private JPanel configPanel;
	private JPanel paraPanel;
	private JPanel buttonBar;
	
	private JTextField sunit;
	private JTextField sunitConfig;
	private List<Variable> urlList;
	private List<Variable> bpList;
	private List<Variable> varList;

	private JButton buttonUseSame;
	private JButton buttonSet;
	private JButton buttonApply;
	private JButton buttonCancel;
	
	public ConfigurationGui(XController c) {
		controller = c;
		JFrame parentWindow = null;
		if (controller != null && controller.getGui() != null) {
			parentWindow = controller.getGui().getMainFrame();
		}
		mainWindow = new JDialog(parentWindow, "Configure", true);
		configPanel = new JPanel();
		configPanel.setLayout(new BorderLayout());
		
		paraPanel = new JPanel();
		paraPanel.setLayout(new TableLayout(0, 2));
		
		paraPanel.add(new JLabel("Set SUnit: "));
		paraPanel.add(new JLabel());
		sunit = new JTextField("/root/sunit/lx/SUnit3.x", 50);
		sunitConfig = new JTextField("/root/sunit/lx/config/config.json", 50);
		paraPanel.add(new JLabel("SUnit CLI path: "));
		paraPanel.add(sunit);
		paraPanel.add(new JLabel("SUnit config file: "));
		paraPanel.add(sunitConfig);
		
		paraPanel.add(new JLabel("_________________"));
		paraPanel.add(new JLabel());
		paraPanel.add(new JLabel("Set URLs: "));
		paraPanel.add(new JLabel());
		
		urlList = new ArrayList<Variable>();
		for (String name : XController.URL_LIST) {
			Variable var = new Variable(name, Variable.DEFAULT_URL);
			paraPanel.add(new JLabel(name + ": "));
			paraPanel.add(var.getInputBox());
			urlList.add(var);
		}
		
		buttonUseSame = new JButton("Use Same URL");
		paraPanel.add(new JLabel());
		paraPanel.add(buttonUseSame);
		
		paraPanel.add(new JLabel("_________________"));
		paraPanel.add(new JLabel());
		paraPanel.add(new JLabel("Set BP Location: "));
		paraPanel.add(new JLabel());
		
		bpList = new ArrayList<Variable>();
		for (String name : XController.BP_LIST) {
			int end = name.indexOf("_");
			String spa = name.substring(0, end);
			//for example
			//bp = "\\\"/u/ainet/hongwehl/src/epay.bp\\\"";
			String bp = Variable.DEFAULT_BP_DIR + spa + ".bp\\\"";
			
			Variable var = new Variable(name, bp);
			paraPanel.add(new JLabel(name + ": "));
			paraPanel.add(var.getInputBox());
			bpList.add(var);
		}
		paraPanel.add(new JLabel("_________________"));
		paraPanel.add(new JLabel());
		paraPanel.add(new JLabel("Set MGTS Account: "));
		paraPanel.add(new JLabel());
		
		varList = new ArrayList<Variable>();
		for (String name : XController.VAR_LIST) {
			Variable var = new Variable(name, Variable.DEFAULT_MGTS);
			paraPanel.add(new JLabel(name + ": "));
			paraPanel.add(var.getInputBox());
			varList.add(var);
		}
		configPanel.add(paraPanel, BorderLayout.CENTER);
		
		buttonBar = new JPanel();
		buttonSet = new JButton("Set");
		buttonApply = new JButton("Apply");
		buttonCancel = new JButton("Cancel");
		
		buttonUseSame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String url = urlList.get(0).getText();
				for (Variable var : urlList) {
					var.setText(url);
				}
			}
		});
		buttonSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPara();
				controller.getGui().showConfigInfo();
				mainWindow.dispose();
			}
		});
		buttonApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPara();
				controller.getGui().showConfigInfo();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow.dispose();
			}
		});
		
		buttonBar.add(buttonSet);
		buttonBar.add(buttonApply);
		buttonBar.add(buttonCancel);
		configPanel.add(buttonBar, BorderLayout.SOUTH);
		
		mainPane = new JScrollPane(configPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		mainWindow.setContentPane(mainPane);
		mainWindow.setSize(WIDTH, HEIGHT);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) screensize.getWidth() / 2 - WIDTH / 2;
		int y = (int) screensize.getHeight() / 2 - HEIGHT / 2;
		mainWindow.setLocation(x, y);
		mainWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);
	}
	
	public void setPara() {
		controller.setSunit(sunit.getText());
		controller.setSunitConfig(sunitConfig.getText());
		
		for (Variable var : urlList) {
			controller.setUrl(var.getName(), var.getText());
		}
		for (Variable var : bpList) {
			controller.setBpLocation(var.getName(), var.getText());
		}
		for (Variable var : varList) {
			controller.setVar(var.getName(), var.getText());
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new ConfigurationGui(new XController());
	}

}
