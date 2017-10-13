package io.boodskap.iot.simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import io.boodskap.iot.simulator.ui.MainPanel;

/**
 * Hello world!
 *
 */
public class App {
	
	public static final File CONFIG_PATH;
	
	static {
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					GroovyScheduler.get().stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
		
		File h = new File(System.getProperty("user.home"));
		File b = new File(h, ".boodskap");
		if(!b.exists()) {
			b.mkdirs();
		}
		
		File s = new File(b, "simulator");
		if(!s.exists()) {
			s.mkdirs();
		}
		
		CONFIG_PATH = s;
	}
	
	public static void main(String[] args) throws Exception {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		
		GroovyScheduler.get().start();
		
		JFrame frame = new JFrame("Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final MainPanel main = new MainPanel();
		frame.setContentPane(main);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JMenuBar mbar = new JMenuBar();
		JMenu menu = new JMenu("File");
		final JMenuItem miDomain = new JMenuItem("New Domain"); 
		final JMenuItem miSimulator = new JMenuItem("New Simulator");
		menu.add(miDomain);
		menu.add(miSimulator);
		mbar.add(menu);
		
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == miDomain) {
					main.addNewDomain();
				}else if(e.getSource() == miSimulator) {
					main.addNewSimulator();
				}
			}
		};
		
		miDomain.addActionListener(al);
		miSimulator.addActionListener(al);
		frame.setJMenuBar(mbar);
		
		frame.setVisible(true);
		
		
	}
}
