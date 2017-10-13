package io.boodskap.iot.simulator.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import io.boodskap.iot.simulator.SimulatorConfig;

public class SimulatorConfigListModel extends AbstractListModel<SimulatorConfig> {

	private static final long serialVersionUID = 5982277190954942233L;
	
	private final List<SimulatorConfig> list = new ArrayList<>();

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public SimulatorConfig getElementAt(int index) {
		return list.get(index);
	}
	
	public void add(SimulatorConfig dc) {
		list.add(dc);
		super.fireContentsChanged(this, 0, list.size());
	}

}
