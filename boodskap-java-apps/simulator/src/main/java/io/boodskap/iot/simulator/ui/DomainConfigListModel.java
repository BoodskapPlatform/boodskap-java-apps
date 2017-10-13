package io.boodskap.iot.simulator.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import io.boodskap.iot.simulator.DomainConfig;

public class DomainConfigListModel extends AbstractListModel<DomainConfig> {

	private static final long serialVersionUID = 5982277190954942233L;
	
	private final List<DomainConfig> list = new ArrayList<>();

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public DomainConfig getElementAt(int index) {
		return list.get(index);
	}
	
	public void add(DomainConfig dc) {
		list.add(dc);
		super.fireContentsChanged(this, 0, list.size());
	}

}
