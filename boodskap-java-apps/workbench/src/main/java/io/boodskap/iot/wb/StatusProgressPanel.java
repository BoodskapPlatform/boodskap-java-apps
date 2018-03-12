package io.boodskap.iot.wb;

import javax.swing.JPanel;

public class StatusProgressPanel extends JPanel {
	
	private static final StatusProgressPanel instance = new StatusProgressPanel();

	private StatusProgressPanel() {
	}

	public StatusProgressPanel get() {
		return instance;
	}

}
