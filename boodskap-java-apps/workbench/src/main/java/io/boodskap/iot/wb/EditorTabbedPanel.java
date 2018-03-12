package io.boodskap.iot.wb;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.metal.MetalIconFactory;

public class EditorTabbedPanel extends JTabbedPane {

	private static final long serialVersionUID = 7064360471333850923L;
	
	public EditorTabbedPanel() {
        super();
		setTabPlacement(JTabbedPane.TOP);
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    /* Override Addtab in order to add the close Button everytime */
    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        super.addTab(title, icon, component, tip);
        int count = this.getTabCount() - 1;
        setTabComponentAt(count, new CloseButtonTab(this, component, title, icon));
    }

    @Override
    public void addTab(String title, Icon icon, Component component) {
        addTab(title, icon, component, null);
    }

    @Override
    public void addTab(String title, Component component) {
        addTab(title, null, component);
    }

    /* addTabNoExit */
    public void addTabNoExit(String title, Icon icon, Component component, String tip) {
        super.addTab(title, icon, component, tip);
    }

    public void addTabNoExit(String title, Icon icon, Component component) {
        addTabNoExit(title, icon, component, null);
    }

    public void addTabNoExit(String title, Component component) {
        addTabNoExit(title, null, component);
    }

    public class CloseButtonTab extends JPanel {
    	
		private static final long serialVersionUID = -7285347366307161192L;
		
        public CloseButtonTab(EditorTabbedPanel tabs, final Component tab, String title, Icon icon) {
            setOpaque(false);
            FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 3, 3);
            setLayout(flowLayout);
            JLabel jLabel = new JLabel(title);
            jLabel.setIcon(icon);
            add(jLabel);
            JButton button = new JButton(MetalIconFactory.getInternalFrameCloseIcon(16));
            button.setMargin(new Insets(0, 0, 0, 0));
            button.addMouseListener(new CloseListener(tabs, tab));
            add(button);
        }
    }

    public class CloseListener implements MouseListener {
    	
    	private EditorTabbedPanel tabs;
        private Component tab;

        public CloseListener(EditorTabbedPanel tabs, Component tab){
        	this.tabs = tabs;
            this.tab=tab;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        	
        	if(tab instanceof EditorComponent) {
        		if(!((EditorComponent)tab).close()) {
        			return;
        		}
        	}
        	
            tabs.remove(tab);
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            clickedButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,3));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            clickedButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,3));
        }
    }
}