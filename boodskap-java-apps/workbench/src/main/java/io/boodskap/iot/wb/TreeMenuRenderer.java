package io.boodskap.iot.wb;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeMenuRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = -8745984751237017215L;
	
	private static final Map<TreeMenuType, Icon> icons = new HashMap<>();
	
	public TreeMenuRenderer() {
	}
	
	public static Icon getIcon(TreeMenuType type) {
		return icons.get(type);
	}

	@Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        
		TreeMenu menu = (TreeMenu) ((DefaultMutableTreeNode)value).getUserObject();
		Icon icon = icons.get(menu.getType());
		
		if(null == icon) {
			icon = ImageHelper.loadImage(String.format("/%s.png", menu.getType().name()));
			if(null != icon) {
				icons.put(menu.getType(), icon);
			}
		}
		
		if(null != icon) {
			setIcon(icon);
		}
		
        return this;
    }
}
