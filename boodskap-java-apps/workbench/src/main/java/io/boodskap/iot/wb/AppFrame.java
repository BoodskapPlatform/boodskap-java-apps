package io.boodskap.iot.wb;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

public class AppFrame extends JFrame{
	
	private static final long serialVersionUID = -6248367063056239294L;
	
	protected EditorTabbedPanel contentPanel = null;
    protected JSplitPane contentSplitPane = null;
    protected JPanel contentLayoutPanel = null;
    
	public AppFrame() {
		super("Boodskap Workbench");
		
        // Setup menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic('N');
        menuItem.setEnabled(false);
        menu.add(menuItem);
        menuItem = new JMenuItem("Open...");
        menuItem.setMnemonic('O');
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(AppFrame.this) == JFileChooser.APPROVE_OPTION) {
                    JOptionPane.showMessageDialog(AppFrame.this, "Your selection: " + fc.getSelectedFile().getName());
                }
            }
        });
        menu.add(menuItem);
        menuItem = new JMenuItem("Save");
        menuItem.setMnemonic('S');
        menuItem.setEnabled(false);
        menu.add(menuItem);
        menuItem = new JMenuItem("Save as");
        menuItem.setMnemonic('a');
        menuItem.setEnabled(false);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic('x');
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        menu.add(menuItem);
        menu.add(menuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        contentPanel = new EditorTabbedPanel();
        
        TreeMenuPanel tree = new TreeMenuPanel(contentPanel, "Platform Menu");
        tree.addMenu(new TreeMenu(TreeMenuType.DOMAIN_RULE, "Domain Rule"));
        DefaultMutableTreeNode mrNode = tree.addMenu(new TreeMenu(TreeMenuType.MESSAGE_RULES, "Message Rules"));
        tree.addMenu(new TreeMenu(TreeMenuType.NAMED_RULES, "Named Rules"));
        tree.addMenu(new TreeMenu(TreeMenuType.SCHEDULED_RULES, "Scheduled Rules"));
        tree.addMenu(new TreeMenu(TreeMenuType.GROOVY_CLASSES, "Groovy Classes"));
        tree.addMenu(new TreeMenu(TreeMenuType.JAR_FILES, "Jar Files"));
        
        tree.addMenu(mrNode, new TreeMenu(TreeMenuType.MESSAGE_RULE, "Motion Sensor Message [1001]"));
        tree.addMenu(mrNode, new TreeMenu(TreeMenuType.MESSAGE_RULE, "Location Message [1002]"));
        tree.addMenu(mrNode, new TreeMenu(TreeMenuType.MESSAGE_RULE, "Alarm Message [1003]"));
        tree.addMenu(mrNode, new TreeMenu(TreeMenuType.MESSAGE_RULE, "Aqua Parameters [2001]"));
        tree.addMenu(mrNode, new TreeMenu(TreeMenuType.MESSAGE_RULE, "Ph Meter [4500]"));
        
        contentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, tree, contentPanel);
        contentSplitPane.setOneTouchExpandable(true);
        
        contentLayoutPanel = new JPanel(new BorderLayout());
        contentLayoutPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        contentLayoutPanel.add(contentSplitPane, BorderLayout.CENTER);
        
        setContentPane(contentLayoutPanel);
        
        // Add listeners
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
    } // end CTor

    public Properties getLAFProps() {
        return new Properties();
    }
    
    public void setLookAndFeel(JList<?> lafList, int selectedLaf) {
        try {
            Properties props = getLAFProps();
            switch (selectedLaf) {
                case Constants.LAF_ACRYL :
                    // First set the theme of the look and feel. This must be done first because there
                    // is some static initializing (color values etc.) when calling setTheme.
                    // Another reason is that the theme variables are shared with all look and feels, so
                    // without calling the setTheme method the look and feel will look ugly (wrong colors).
                    com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme(props);
                    // Now we can set the look and feel
                    UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
                    break;
                case Constants.LAF_AERO :
                    com.jtattoo.plaf.aero.AeroLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
                    break;
                case Constants.LAF_ALUMINIUM :
                    com.jtattoo.plaf.aluminium.AluminiumLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
                    break;
                case Constants.LAF_BERNSTEIN :
                    com.jtattoo.plaf.bernstein.BernsteinLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
                    break;
                case Constants.LAF_FAST :
                    com.jtattoo.plaf.fast.FastLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
                    break;
                case Constants.LAF_GRAPHITE :
                    com.jtattoo.plaf.graphite.GraphiteLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
                    break;
                case Constants.LAF_HIFI :
                    com.jtattoo.plaf.hifi.HiFiLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
                    break;
                case Constants.LAF_LUNA :
                    com.jtattoo.plaf.luna.LunaLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel");
                    break;
                case Constants.LAF_MCWIN :
                    com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
                    break;
                case Constants.LAF_MINT :
                    com.jtattoo.plaf.mint.MintLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
                    break;
                case Constants.LAF_NOIRE :
                    com.jtattoo.plaf.noire.NoireLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
                    break;
                case Constants.LAF_SMART :
                    com.jtattoo.plaf.smart.SmartLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
                    break;
                case Constants.LAF_TEXTURE :
                    com.jtattoo.plaf.texture.TextureLookAndFeel.setTheme(props);
                    UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
                    break;
            }
            // Tell all components that look and feel has changed.
            Window windows[] = Window.getWindows();
            for (Window window : windows) {
                if (window.isDisplayable()) {
                    SwingUtilities.updateComponentTreeUI(window);
                }
            }
            // Maybe selected item is not visible after changing the look and feel so we correct this
            scrollSelectedToVisible(lafList);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    } // end setLookAndFeel

    public void scrollSelectedToVisible(JList<?> list) {
        // Because of the different font size the selected item
        // maybe out of the visible area. So we correct this.
        int idx = list.getLeadSelectionIndex();
        Rectangle rect = list.getCellBounds(idx, idx);
        if (rect != null) {
            list.scrollRectToVisible(rect);
        }
    } // end scrollSelectedToVisible
    

//------------------------------------------------------------------------------    
    public static void main(String[] args) {
//------------------------------------------------------------------------------    
        try {
            // Select the Look and Feel
            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    // Start the application
                    AppFrame app = new AppFrame();
                    app.setSize(800, 600);
                    app.setLocationRelativeTo(null);
                    app.setVisible(true);
                }
            });
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } // end main
}
