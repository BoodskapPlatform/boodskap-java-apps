package io.boodskap.iot.simulator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;

import io.boodskap.iot.simulator.DomainConfig;
import io.boodskap.iot.simulator.DomainConfigs;
import io.boodskap.iot.simulator.SimulatorConfig;
import io.boodskap.iot.simulator.SimulatorConfigs;

public class MainPanel extends JPanel implements ListSelectionListener {

	private static final long serialVersionUID = 2961273093842202490L;
	
	private final Map<String, DomainConfigPanel> configPanels = new HashMap<>();
	private final Map<String, SimulatorPanel> simulatorPanels = new HashMap<>();
	
	private final JList<DomainConfig> domainConfigs = new JList<>(new DomainConfigListModel());
	private final JList<SimulatorConfig> simulatorConfigs = new JList<>(new SimulatorConfigListModel());
	
	private JPanel current;

	public MainPanel() {
		super(new BorderLayout());
		setup();
	}

	private void setup() {
		
		try {
			
			DomainConfigs.get().getConfigurations().forEach(dc -> {
				((DomainConfigListModel)domainConfigs.getModel()).add(dc);;
				DomainConfigPanel dcp = new DomainConfigPanel(dc);
				configPanels.put(dc.getId(), dcp);
			});
			
			SimulatorConfigs.get().getSimulators().forEach(sc -> {
				((SimulatorConfigListModel)simulatorConfigs.getModel()).add(sc);
				SimulatorPanel sp = new SimulatorPanel(sc);
				simulatorPanels.put(sc.getId(), sp);
			});
			
			String id = DomainConfigs.get().getActiveConfiguration();
			if(StringUtils.isNotBlank(id)) {
				current = configPanels.get(id);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		domainConfigs.setBorder(BorderFactory.createTitledBorder("Domains"));
		simulatorConfigs.setBorder(BorderFactory.createTitledBorder("Simulators"));
		
		//domainConfigs.setMinimumSize(new Dimension(300, 250));
		//simulatorConfigs.setMinimumSize(new Dimension(300, 350));
		
		domainConfigs.addListSelectionListener(this);
		simulatorConfigs.addListSelectionListener(this);
		
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		//panel.setMinimumSize(new Dimension(320, 800));
		//panel.setPreferredSize(panel.getMaximumSize());
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		panel.setMinimumSize(new Dimension(320,  800));
		
		panel.add(new JScrollPane(domainConfigs), BorderLayout.NORTH);
		panel.add(new JScrollPane(simulatorConfigs), BorderLayout.CENTER);
		
		add(panel, BorderLayout.WEST);
		if(null != current) {
			add(current, BorderLayout.CENTER);
		}
		
		setPreferredSize(new Dimension(800, 800));
		
	}
	
	public void addNewDomain() {
		
		String label = JOptionPane.showInputDialog(this, "Label", "New Domain Config", JOptionPane.QUESTION_MESSAGE);
		
		if(StringUtils.isNoneBlank(label)) {
			DomainConfig dc = new DomainConfig();
			dc.setLabel(label);
			((DomainConfigListModel)domainConfigs.getModel()).add(dc);;
			DomainConfigPanel dcp = new DomainConfigPanel(dc);
			configPanels.put(dc.getId(), dcp);
			domainConfigs.setSelectedValue(dc, true);
		}
	}

	public void addNewSimulator() {
		
		String label = JOptionPane.showInputDialog(this, "Label", "New Simulator Config", JOptionPane.QUESTION_MESSAGE);
		
		if(StringUtils.isNoneBlank(label)) {
			SimulatorConfig sc = new SimulatorConfig();
			sc.setLabel(label);
			((SimulatorConfigListModel)simulatorConfigs.getModel()).add(sc);
			SimulatorPanel sp = new SimulatorPanel(sc);
			simulatorPanels.put(sc.getId(), sp);
			simulatorConfigs.setSelectedValue(sc, true);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		System.out.println("Changed");
		
		JPanel panel = null;
		
		if(e.getSource() == domainConfigs) {
			DomainConfig val = domainConfigs.getSelectedValue();
			if(null != val) {
				panel = configPanels.get(val.getId());
			}
		}else if(e.getSource() == simulatorConfigs) {
			SimulatorConfig val = simulatorConfigs.getSelectedValue();
			if(null != val) {
				panel = simulatorPanels.get(val.getId());
			}
		}
		
		if(null != panel && current != panel) {
			
			if(null != current) {
				remove(current);
			}

			current = panel;
			
			add(current, BorderLayout.CENTER);
		}
		
		if(null != current) {
			current.setVisible(false);
			current.setVisible(true);
			
			if(current instanceof DomainConfigPanel) {
				simulatorConfigs.clearSelection();
			}else {
				domainConfigs.clearSelection();
			}
		}
	}
	
}
