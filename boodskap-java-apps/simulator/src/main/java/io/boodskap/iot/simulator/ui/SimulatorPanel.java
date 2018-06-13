package io.boodskap.iot.simulator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import io.boodskap.iot.simulator.DomainConfig;
import io.boodskap.iot.simulator.DomainConfigs;
import io.boodskap.iot.simulator.GroovyScheduler;
import io.boodskap.iot.simulator.SimulatorConfig;
import io.boodskap.iot.simulator.SimulatorConfig.Protocol;
import io.boodskap.iot.simulator.SimulatorConfig.State;
import io.boodskap.iot.simulator.SimulatorConfigs;
import io.boodskap.iot.simulator.TransmitterHandler;

public class SimulatorPanel extends JPanel implements TransmitterHandler, ActionListener{

	private static final long serialVersionUID = 7399906305380790283L;

	private final SimulatorConfig cfg;
	private JTextField txtLabel, txtDeviceId, txtModel, txtVersion, txtCron;
	private JComboBox<SimulatorConfig.Protocol> cbxProtocols;
	private JComboBox<DomainConfig> cbxConfig;
	private JTabbedPane tab;
	private JTextPane txtCosole;
	private RSyntaxTextArea txtCode;
	private JButton cmdSave, cmdActivate, cmdDeactivate, cmdClear;
	private StyledDocument doc;
	private SimpleAttributeSet server, client, transmitter;

	public SimulatorPanel(final SimulatorConfig cfg) {
		super(new BorderLayout());
		this.cfg = cfg;
		setup();
	}

	private void setup() {
		createTopPanel();
		createCenterPanel();
		createBottomPanel();
		validateControls();
	}

	private void createBottomPanel() {
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		txtCron = new JTextField(12);
		cmdSave = new JButton("Save");
		cmdActivate = new JButton("Activate");
		cmdDeactivate = new JButton("Deactivate");
		cmdClear = new JButton("Clear Console");
		
		panel.add(new JLabel("CRON"));
		panel.add(txtCron);
		panel.add(cmdSave);
		panel.add(cmdActivate);
		panel.add(cmdDeactivate);
		panel.add(cmdClear);
		
		add(panel, BorderLayout.SOUTH);
		
		cmdSave.addActionListener(this);
		cmdActivate.addActionListener(this);
		cmdDeactivate.addActionListener(this);
		cmdClear.addActionListener(this);
		
		txtCron.setText(cfg.getCronSchedule());
	
	}
	
	void validateControls() {
		try {
			cmdDeactivate.setEnabled(SimulatorConfigs.get().exists(cfg.getId()) && GroovyScheduler.get().isActive(cfg.getId()));
			cmdActivate.setEnabled(SimulatorConfigs.get().exists(cfg.getId()) && !GroovyScheduler.get().isActive(cfg.getId()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public void showConsole() {
		tab.setSelectedIndex(1);
	}

	private void createCenterPanel() {
		
		tab = new JTabbedPane();
		
		txtCode = new RSyntaxTextArea();
		txtCode.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		txtCode.setCodeFoldingEnabled(true);
		txtCode.setText(cfg.getCode());
		
		txtCosole = new JTextPane();
		txtCosole.setEditable(false);
		txtCosole.setBackground(Color.BLACK);
		doc = txtCosole.getStyledDocument();

		server = new SimpleAttributeSet();
		StyleConstants.setAlignment(server, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(server, Color.RED);

		client = new SimpleAttributeSet();
		StyleConstants.setAlignment(client, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(client, Color.BLUE);

		transmitter = new SimpleAttributeSet();
		StyleConstants.setAlignment(transmitter, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(transmitter, Color.WHITE);

		tab.addTab("Script", new RTextScrollPane(txtCode));
		tab.addTab("Console", new JScrollPane(txtCosole));
		
		add(tab, BorderLayout.CENTER);
	}

	private void createTopPanel() {
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		txtLabel = new JTextField(15);
		txtDeviceId = new JTextField(12);
		txtModel = new JTextField(10);
		txtVersion = new JTextField(6);
		cbxProtocols = new JComboBox<>(Protocol.values());
		cbxConfig = new JComboBox<>();
		
		try {
			DomainConfigs.get().getConfigurations().forEach(dc -> {cbxConfig.addItem(dc);});
		} catch (FileNotFoundException e) {
		}
		
		txtLabel.setText(cfg.getLabel());
		txtDeviceId.setText(cfg.getDeviceId());
		txtModel.setText(cfg.getDeviceModel());
		txtVersion.setText(cfg.getFirmwareVersion());
		cbxProtocols.setSelectedItem(cfg.getProtocol());
		try{cbxConfig.setSelectedItem(DomainConfigs.get().getConfiguration(cfg.getDomainConfig()));}catch(Exception ex) {}
		
		panel.add(new JLabel("Label"));
		panel.add(txtLabel);

		panel.add(new JLabel("Device"));
		panel.add(txtDeviceId);

		panel.add(new JLabel("Model"));
		panel.add(txtModel);

		panel.add(new JLabel("Version"));
		panel.add(txtVersion);
		
		panel.add(new JLabel("Protocol"));
		panel.add(cbxProtocols);
		
		panel.add(new JLabel("Domain Config"));
		panel.add(cbxConfig);
		
		panel.setPreferredSize(new Dimension(1600, 55));
		
		add(new JScrollPane(panel), BorderLayout.NORTH);

	}

	@Override
	public void message(Type type, String msg) {
		
		try {
			
			SimpleAttributeSet style;
			
			switch(type) {
			case CLIENT:
				style = client;
				break;
			case SERVER:
				style = server;
				break;
			case TRANSMITTER:
			default:
				style = transmitter;
				break;
			
			}
			
			String text = String.format("\n%s\n\t%s\n", new Date(), msg);
			
			doc.insertString(doc.getLength(), text, style);
			doc.setParagraphAttributes(doc.getLength(), 1, style, false);
			txtCosole.setCaretPosition(doc.getLength());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected void save() {
		
		try {
			
			cfg.setCode(txtCode.getText());
			cfg.setCronSchedule(txtCron.getText());
			cfg.setDeviceId(txtDeviceId.getText());
			cfg.setDeviceModel(txtModel.getText());
			cfg.setFirmwareVersion(txtVersion.getText());
			cfg.setLabel(txtLabel.getText());
			cfg.setProtocol((Protocol) cbxProtocols.getSelectedItem());
			cfg.setDomainConfig(null);
			
			DomainConfig dCfg = (DomainConfig) cbxConfig.getSelectedItem();
			if(null != dCfg) {
				cfg.setDomainConfig(dCfg.getId());
			}
			
			validateConfig();
			
			SimulatorConfigs.get().save(cfg);
			
			switch(cfg.getState()) {
			case DISABLED:
				GroovyScheduler.get().unschedule(cfg.getId());
				break;
			case ENABLED:
				GroovyScheduler.get().schedule(dCfg, cfg, this);
				tab.setSelectedIndex(1);
				break;
			}
			
		} catch (Exception ex) {
			
			if(!(ex instanceof IllegalArgumentException)) {
				ex.printStackTrace();
			}
			
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}finally {
			validateControls();
		}
	}

	private void validateConfig() {
		
		if(StringUtils.isBlank(cfg.getCode())) {
			txtCode.requestFocus();
			throw new IllegalArgumentException("Script required");
		}
		
		if(StringUtils.isBlank(cfg.getCronSchedule())) {
			txtCron.requestFocus();
			throw new IllegalArgumentException("Cron expression required");
		}
		
		if(StringUtils.isBlank(cfg.getDeviceId())) {
			txtDeviceId.requestFocus();
			throw new IllegalArgumentException("Device ID required");
		}
		
		if(StringUtils.isBlank(cfg.getDeviceModel())) {
			txtModel.requestFocus();
			throw new IllegalArgumentException("Device Model required");
		}
		
		if(StringUtils.isBlank(cfg.getDomainConfig())) {
			cbxConfig.requestFocus();
			throw new IllegalArgumentException("Domain Configuration required");
		}
		
		if(StringUtils.isBlank(cfg.getFirmwareVersion())) {
			txtVersion.requestFocus();
			throw new IllegalArgumentException("Firmware Version required");
		}
		
		if(StringUtils.isBlank(cfg.getLabel())) {
			txtLabel.requestFocus();
			throw new IllegalArgumentException("Label required");
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == cmdActivate) {
			cfg.setState(State.ENABLED);
		}else if(e.getSource() == cmdDeactivate) {
			cfg.setState(State.DISABLED);
		}else if(e.getSource() == cmdClear) {
			txtCosole.setText("");
			return;
		}
		
		save();
	}

	@Override
	public String toString() {
		return cfg.getLabel();
	}

}
