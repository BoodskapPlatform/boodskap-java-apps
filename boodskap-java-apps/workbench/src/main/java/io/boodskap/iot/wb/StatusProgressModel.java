package io.boodskap.iot.wb;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class StatusProgressModel extends AbstractTableModel {

	private static final long serialVersionUID = 8677532285932258718L;
	
	private final List<StatusProgress> statuses = new ArrayList<>();
	
	public void add(StatusProgress progress) {
		statuses.add(progress);
		fireTableDataChanged();
	}
	
	public void remove(StatusProgress progress) {
		if(statuses.remove(progress)) {
			fireTableDataChanged();
		}
	}

	@Override
	public int getRowCount() {
		return statuses.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		StatusProgress s = statuses.get(rowIndex);
		
		switch(columnIndex) {
		case 0:
			return s.getName();
		case 1:
			return s.getState();
		case 2:
			return s.getProblem();
		case 3:
			return s.getProgress();
		default:
			return null;
		}
		
	}

}
