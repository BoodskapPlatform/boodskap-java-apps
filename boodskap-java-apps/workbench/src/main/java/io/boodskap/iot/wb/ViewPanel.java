package io.boodskap.iot.wb;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ViewPanel extends JPanel {

	private static final long serialVersionUID = -1463261249790439854L;
	
	private EditorTabbedPanel tabs;

	public ViewPanel() {
		init();
	}

	private void init() {
		
		setLayout(new BorderLayout());
		
		tabs = new EditorTabbedPanel();
		tabs.setTabPlacement(JTabbedPane.TOP);
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		add(tabs, BorderLayout.CENTER);
		
	}

    public void addTab(String title, Icon icon, Component component, String tip) {
		tabs.addTab(title, icon, component, tip);
	}

	public void addTab(String title, Icon icon, Component component) {
		tabs.addTab(title, icon, component);
	}

	public void addTab(String title, Component component) {
		tabs.addTab(title, component);
	}

	public void addTabNoExit(String title, Icon icon, Component component, String tip) {
		tabs.addTabNoExit(title, icon, component, tip);
	}

	public void addTabNoExit(String title, Icon icon, Component component) {
		tabs.addTabNoExit(title, icon, component);
	}

	public void addTabNoExit(String title, Component component) {
		tabs.addTabNoExit(title, component);
	}

}
