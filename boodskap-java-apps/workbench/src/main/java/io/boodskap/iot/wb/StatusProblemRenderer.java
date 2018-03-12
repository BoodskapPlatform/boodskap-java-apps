package io.boodskap.iot.wb;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class StatusProblemRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = -5896175100689015791L;
	
	public StatusProblemRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		Integer severity = (Integer)value;
		
		switch(severity) {
		case 0:
			setBackground(Color.WHITE);
			break;
		case 1:
			setBackground(Color.YELLOW);
			break;
		case 2:
			setBackground(Color.RED);
			break;
		}
		
		return this;
	}

}
