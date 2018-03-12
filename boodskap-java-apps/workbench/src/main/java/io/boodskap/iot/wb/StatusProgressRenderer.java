package io.boodskap.iot.wb;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class StatusProgressRenderer extends JProgressBar implements TableCellRenderer {

	private static final long serialVersionUID = -5896175100689015791L;
	
	public StatusProgressRenderer() {
		setOpaque(true);
		setMinimum(0);
		setMaximum(100);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		Integer progress = (Integer)value;
		
		if(progress == -1) {
			setIndeterminate(progress == -1);
		}else {
			setValue(progress);
		}
		
		
		return this;
	}

}
