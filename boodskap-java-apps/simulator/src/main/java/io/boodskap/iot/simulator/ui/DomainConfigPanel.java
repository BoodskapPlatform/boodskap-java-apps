package io.boodskap.iot.simulator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.StringUtils;

import io.boodskap.iot.simulator.DomainConfig;
import io.boodskap.iot.simulator.DomainConfigs;
import io.boodskap.iot.simulator.SimulatorConfig;
import io.boodskap.iot.simulator.SimulatorConfig.Protocol;

public class DomainConfigPanel extends JPanel implements ChangeListener, ActionListener {

	private static final long serialVersionUID = 4208897879425121976L;
	
	private final DomainConfig cfg;
	
	private JTextField txtLabel, txtDomainKey, txtApiKey, txtApiUrl;
	private JTextField txtUdpHost, txtUdpPort;
	private JTextField txtMqttHost, txtMqttPort, txtMqttCaCertFile, txtMqttClientCertFile, txtMqttClientKeyFile, txtMqttClientKeyPassword;
	private JCheckBox chxMqttSsl;
	private JTextField txtCoapHost, txtCoapPort, txtCoapCaCertFile, txtCoapClientCertFile, txtCoapClientKeyFile, txtCoapClientKeyPassword;
	private JCheckBox chxCoap, chxMqtt, chxUdp, chxCoapSsl;
	private JButton cmdActivate, cmdSave;
	private JPanel center = new JPanel();

	public DomainConfigPanel(final DomainConfig cfg) {
		//super(new FlowLayout(FlowLayout.LEFT));
		//super(new GridLayout(4, 1));
		super(new BorderLayout());
		this.cfg = cfg;
		setup();
	}

	private void setup() {
		add(center, BorderLayout.CENTER);
		//setLayout(new GridLayout(3, 2));
		createDomainPanel();
		createUdpPanel();
		createMqttPanel();
		createCoapPanel();
		createMenuPanel();
		
		stateUpdate(Protocol.COAP, cfg.isCoapEnabled());
		stateUpdate(Protocol.MQTT, cfg.isMqttEnabled());
		stateUpdate(Protocol.UDP, cfg.isUdpEnabled());
	}

	private void createMenuPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		cmdActivate = new JButton("Activate");
		cmdSave = new JButton("Save");
		
		panel.add(cmdActivate);
		panel.add(cmdSave);
		
		try {
			cmdActivate.setEnabled(!cfg.getId().equals(DomainConfigs.get().getActiveConfiguration()));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		cmdActivate.addActionListener(this);
		cmdSave.addActionListener(this);
		
		add(panel, BorderLayout.NORTH);
	}

	private void createCoapPanel() {
		
		final String[] LABELS = {"Enable", "Host/IP: ", "Port: ", "SSL", "CA File", "Client Cert File", "Client Key File", "Client Key Password"};
		final int NUM_PAIRS = LABELS.length;
		
		JPanel panel = new JPanel(new SpringLayout());
		panel.setPreferredSize(new Dimension(750, 240));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "CoAP Settings"));
		
		panel.add(new JLabel(LABELS[0], JLabel.TRAILING));
		chxCoap = new JCheckBox("");
		chxCoap.setSelected(cfg.isCoapEnabled());
		panel.add(chxCoap);
		
		panel.add(new JLabel(LABELS[1], JLabel.TRAILING));
		txtCoapHost = new JTextField(cfg.getCoapHost(), 30);
		panel.add(txtCoapHost);
		
		panel.add(new JLabel(LABELS[2], JLabel.TRAILING));
		txtCoapPort = new JTextField(String.valueOf(cfg.getCoapPort()), 10);
		panel.add(txtCoapPort);
		
		panel.add(new JLabel(LABELS[3], JLabel.TRAILING));
		chxCoapSsl = new JCheckBox("", cfg.isCoapSSL());
		panel.add(chxCoapSsl);
		
		panel.add(new JLabel(LABELS[4], JLabel.TRAILING));
		txtCoapCaCertFile = new JTextField(String.valueOf(cfg.getCoapCAFile()), 30);
		panel.add(txtCoapCaCertFile);
		
		panel.add(new JLabel(LABELS[5], JLabel.TRAILING));
		txtCoapClientCertFile = new JTextField(String.valueOf(cfg.getCoapCertFile()), 30);
		panel.add(txtCoapClientCertFile);
		
		panel.add(new JLabel(LABELS[6], JLabel.TRAILING));
		txtCoapClientKeyFile = new JTextField(String.valueOf(cfg.getCoapKeyFile()), 30);
		panel.add(txtCoapClientKeyFile);

		panel.add(new JLabel(LABELS[7], JLabel.TRAILING));
		txtCoapClientKeyPassword = new JTextField(String.valueOf(cfg.getCoapKeyPassword()), 10);
		panel.add(txtCoapClientKeyPassword);

		chxCoap.addChangeListener(this);
		chxCoapSsl.addChangeListener(this);
		
		txtCoapCaCertFile.setEnabled(cfg.isCoapSSL());
		txtCoapClientCertFile.setEnabled(cfg.isCoapSSL());
		txtCoapClientKeyFile.setEnabled(cfg.isCoapSSL());
		txtCoapClientKeyPassword.setEnabled(cfg.isCoapSSL());
		
		SpringUtilities.makeCompactGrid(panel, NUM_PAIRS, 2, 6, 6, 6, 6);
		
		center.add(panel);
	}

	private void createMqttPanel() {
		
		final String[] LABELS = {"Enable", "Host/IP: ", "Port: ", "SSL", "CA File", "Client Cert File", "Client Key File", "Client Key Password"};
		final int NUM_PAIRS = LABELS.length;
		
		JPanel panel = new JPanel(new SpringLayout());
		panel.setPreferredSize(new Dimension(750, 240));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "MQTT Settings"));
		
		panel.add(new JLabel(LABELS[0], JLabel.TRAILING));
		chxMqtt = new JCheckBox("");
		chxMqtt.setSelected(cfg.isMqttEnabled());
		panel.add(chxMqtt);
		
		panel.add(new JLabel(LABELS[1], JLabel.TRAILING));
		txtMqttHost = new JTextField(cfg.getMqttHost(), 30);
		panel.add(txtMqttHost);
		
		panel.add(new JLabel(LABELS[2], JLabel.TRAILING));
		txtMqttPort = new JTextField(String.valueOf(cfg.getMqttPort()), 10);
		panel.add(txtMqttPort);
		
		panel.add(new JLabel(LABELS[3], JLabel.TRAILING));
		chxMqttSsl = new JCheckBox("", cfg.isMqttSSL());
		panel.add(chxMqttSsl);
		
		panel.add(new JLabel(LABELS[4], JLabel.TRAILING));
		txtMqttCaCertFile = new JTextField(String.valueOf(cfg.getMqttCAFile()), 30);
		panel.add(txtMqttCaCertFile);
		
		panel.add(new JLabel(LABELS[5], JLabel.TRAILING));
		txtMqttClientCertFile = new JTextField(String.valueOf(cfg.getMqttCertFile()), 30);
		panel.add(txtMqttClientCertFile);
		
		panel.add(new JLabel(LABELS[6], JLabel.TRAILING));
		txtMqttClientKeyFile = new JTextField(String.valueOf(cfg.getMqttKeyFile()), 30);
		panel.add(txtMqttClientKeyFile);

		panel.add(new JLabel(LABELS[7], JLabel.TRAILING));
		txtMqttClientKeyPassword = new JTextField(String.valueOf(cfg.getMqttKeyPassword()), 10);
		panel.add(txtMqttClientKeyPassword);
		
		chxMqtt.addChangeListener(this);
		chxMqttSsl.addChangeListener(this);
		
		txtMqttCaCertFile.setEnabled(cfg.isMqttSSL());
		txtMqttClientCertFile.setEnabled(cfg.isMqttSSL());
		txtMqttClientKeyFile.setEnabled(cfg.isMqttSSL());
		txtMqttClientKeyPassword.setEnabled(cfg.isMqttSSL());
		
		SpringUtilities.makeCompactGrid(panel, NUM_PAIRS, 2, 6, 6, 6, 6);
		
		center.add(panel);
	}

	private void createUdpPanel() {
		
		final String[] LABELS = {"Enable", "Host/IP: ", "Port: "};
		final int NUM_PAIRS = LABELS.length;
		
		JPanel panel = new JPanel(new SpringLayout());
		panel.setPreferredSize(new Dimension(750, 110));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "UDP Settings"));
		
		panel.add(new JLabel(LABELS[0], JLabel.TRAILING));
		chxUdp = new JCheckBox("");
		chxUdp.setSelected(cfg.isUdpEnabled());
		panel.add(chxUdp);
		
		panel.add(new JLabel(LABELS[1], JLabel.TRAILING));
		txtUdpHost = new JTextField(cfg.getUdpHost(), 30);
		panel.add(txtUdpHost);
		
		panel.add(new JLabel(LABELS[2], JLabel.TRAILING));
		txtUdpPort = new JTextField(String.valueOf(cfg.getUdpPort()), 10);
		panel.add(txtUdpPort);
		
		chxUdp.addChangeListener(this);
		
		SpringUtilities.makeCompactGrid(panel, NUM_PAIRS, 2, 6, 6, 6, 6);
		
		center.add(panel);
	}

	private void createDomainPanel() {
		
		final String[] LABELS = {"Label", "Domain Key: ", "API Key: ", "API URL: "};
		final int NUM_PAIRS = LABELS.length;
		
		JPanel panel = new JPanel(new SpringLayout());
		panel.setPreferredSize(new Dimension(750, 130));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Domain Settings"));
		
		panel.add(new JLabel(LABELS[0], JLabel.TRAILING));
		txtLabel = new JTextField(cfg.getLabel(), 20);
		panel.add(txtLabel);
		
		panel.add(new JLabel(LABELS[1], JLabel.TRAILING));
		txtDomainKey = new JTextField(cfg.getDomainKey(), 20);
		panel.add(txtDomainKey);
		
		panel.add(new JLabel(LABELS[2], JLabel.TRAILING));
		txtApiKey = new JTextField(cfg.getApiKey(), 30);
		panel.add(txtApiKey);
		
		panel.add(new JLabel(LABELS[3], JLabel.TRAILING));
		txtApiUrl = new JTextField(cfg.getApiUrl(), 40);
		panel.add(txtApiUrl);
		
		SpringUtilities.makeCompactGrid(panel, NUM_PAIRS, 2, 6, 6, 6, 6);
		
		center.add(panel);
	}
	
	private void stateUpdate(SimulatorConfig.Protocol p, boolean enabled) {
		
		switch(p) {
		case COAP:
			txtCoapCaCertFile.setEnabled(chxCoapSsl.isSelected() && enabled);
			txtCoapClientCertFile.setEnabled(chxCoapSsl.isSelected() && enabled);
			txtCoapClientKeyFile.setEnabled(chxCoapSsl.isSelected() && enabled);
			txtCoapClientKeyPassword.setEnabled(chxCoapSsl.isSelected() && enabled);
			txtCoapHost.setEnabled(enabled);
			txtCoapPort.setEnabled(enabled);
			break;
		case MQTT:
			txtMqttCaCertFile.setEnabled(chxMqttSsl.isSelected() && enabled);
			txtMqttClientCertFile.setEnabled(chxMqttSsl.isSelected() && enabled);
			txtMqttClientKeyFile.setEnabled(chxMqttSsl.isSelected() && enabled);
			txtMqttClientKeyPassword.setEnabled(chxMqttSsl.isSelected() && enabled);
			txtMqttHost.setEnabled(enabled);
			txtMqttPort.setEnabled(enabled);
			break;
		case UDP:
			txtUdpHost.setEnabled(enabled);
			txtUdpPort.setEnabled(enabled);
			break;
		default:
			break;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		
		if(e.getSource() == chxMqttSsl) {
			txtMqttCaCertFile.setEnabled(chxMqttSsl.isSelected());
			txtMqttClientCertFile.setEnabled(chxMqttSsl.isSelected());
			txtMqttClientKeyFile.setEnabled(chxMqttSsl.isSelected());
			txtMqttClientKeyPassword.setEnabled(chxMqttSsl.isSelected());
		}else if(e.getSource() == chxCoapSsl) {
			txtCoapCaCertFile.setEnabled(chxCoapSsl.isSelected());
			txtCoapClientCertFile.setEnabled(chxCoapSsl.isSelected());
			txtCoapClientKeyFile.setEnabled(chxCoapSsl.isSelected());
			txtCoapClientKeyPassword.setEnabled(chxCoapSsl.isSelected());
		}else if(e.getSource() == chxCoap) {
			stateUpdate(Protocol.COAP, chxCoap.isSelected());
		}else if(e.getSource() == chxMqtt) {
			stateUpdate(Protocol.MQTT, chxMqtt.isSelected());
		}else if(e.getSource() == chxUdp) {
			stateUpdate(Protocol.UDP, chxUdp.isSelected());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		try {
			
			cfg.setLabel(txtLabel.getText());
			cfg.setDomainKey(txtDomainKey.getText());
			cfg.setApiKey(txtApiKey.getText());
			cfg.setApiUrl(txtApiUrl.getText());
			
			cfg.setCoapCAFile(txtCoapCaCertFile.getText());
			cfg.setCoapCertFile(txtCoapClientCertFile.getText());
			cfg.setCoapEnabled(chxCoap.isSelected());
			cfg.setCoapHost(txtCoapHost.getText());
			cfg.setCoapKeyFile(txtCoapClientKeyFile.getText());
			cfg.setCoapKeyPassword(txtCoapClientKeyPassword.getText());
			cfg.setCoapSSL(chxCoapSsl.isSelected());

			cfg.setMqttCAFile(txtMqttCaCertFile.getText());
			cfg.setMqttCertFile(txtMqttClientCertFile.getText());
			cfg.setMqttEnabled(chxMqtt.isSelected());
			cfg.setMqttHost(txtMqttHost.getText());
			cfg.setMqttKeyFile(txtMqttClientKeyFile.getText());
			cfg.setMqttKeyPassword(txtMqttClientKeyPassword.getText());
			cfg.setMqttSSL(chxMqttSsl.isSelected());
			
			cfg.setUdpEnabled(chxUdp.isSelected());
			cfg.setUdpHost(txtUdpHost.getText());
			
			try{cfg.setUdpPort(Integer.valueOf(txtUdpPort.getText()));}catch(Exception ex) {txtUdpPort.requestFocus(); throw new IllegalArgumentException("Invalid Udp Port");}
			try{cfg.setCoapPort(Integer.valueOf(txtCoapPort.getText()));}catch(Exception ex) {txtCoapPort.requestFocus(); throw new IllegalArgumentException("Invalid CoAP Port");}
			try{cfg.setMqttPort(Integer.valueOf(txtMqttPort.getText()));}catch(Exception ex) {txtMqttPort.requestFocus(); throw new IllegalArgumentException("Invalid Mqtt Port");}
			
			validateConfig();
			
			if(e.getSource() == cmdActivate) {
				DomainConfigs.get().setActiveConfiguration(cfg.getId());
			}else if(e.getSource() == cmdSave) {
				DomainConfigs.get().save(cfg);
			}
			
		} catch (Exception ex) {
			
			if(!(ex instanceof IllegalArgumentException)) {
				ex.printStackTrace();
			}
			
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	private void validateConfig() {
		
		if(StringUtils.isBlank(cfg.getLabel())) {
			txtLabel.requestFocus();
			throw new IllegalArgumentException("Label Required");
		}
		
		if(StringUtils.isBlank(cfg.getDomainKey())) {
			txtDomainKey.requestFocus();
			throw new IllegalArgumentException("Domain Key Required");
		}
		
		if(StringUtils.isBlank(cfg.getApiKey())) {
			txtApiKey.requestFocus();
			throw new IllegalArgumentException("API Key Required");
		}
		
		if(StringUtils.isBlank(cfg.getApiUrl())) {
			txtApiUrl.requestFocus();
			throw new IllegalArgumentException("API URL Required");
		}
		
		if(cfg.isUdpEnabled()) {
			
			if(StringUtils.isBlank(cfg.getUdpHost())) {
				txtUdpHost.requestFocus();
				throw new IllegalArgumentException("UDP Host Required");
			}
		}
		
		if(cfg.isCoapEnabled()) {
			
			if(StringUtils.isBlank(cfg.getCoapHost())) {
				txtCoapHost.requestFocus();
				throw new IllegalArgumentException("CoAP Host Required");
			}
			
			if(cfg.isCoapSSL()) {
				
				if(StringUtils.isBlank(cfg.getCoapCAFile())) {
					txtCoapCaCertFile.requestFocus();
					throw new IllegalArgumentException("CoAP CA File Required");
				}
				
				if(StringUtils.isBlank(cfg.getCoapCertFile())) {
					txtCoapClientCertFile.requestFocus();
					throw new IllegalArgumentException("CoAP Client Cert File Required");
				}
				
				if(StringUtils.isBlank(cfg.getCoapKeyFile())) {
					txtCoapClientKeyFile.requestFocus();
					throw new IllegalArgumentException("CoAP Client Key File Required");
				}
				
				if(StringUtils.isBlank(cfg.getCoapKeyPassword())) {
					txtCoapClientKeyPassword.requestFocus();
					throw new IllegalArgumentException("CoAP Client Key Password Required");
				}
				
				validateFileExists(cfg.getCoapCAFile(), txtCoapCaCertFile, "CoAP CA File %s not found");
				validateFileExists(cfg.getCoapCertFile(), txtCoapClientCertFile, "CoAP Client Cert File %s not found");
				validateFileExists(cfg.getCoapKeyFile(), txtCoapClientKeyFile, "CoAP Client Key File %s not found");
				
			}
		}

		if(cfg.isMqttEnabled()) {
			
			if(StringUtils.isBlank(cfg.getMqttHost())) {
				txtMqttHost.requestFocus();
				throw new IllegalArgumentException("Mqtt Host Required");
			}
			
			if(cfg.isMqttSSL()) {
				
				if(StringUtils.isBlank(cfg.getMqttCAFile())) {
					txtMqttCaCertFile.requestFocus();
					throw new IllegalArgumentException("Mqtt CA File Required");
				}
				
				if(StringUtils.isBlank(cfg.getMqttCertFile())) {
					txtMqttClientCertFile.requestFocus();
					throw new IllegalArgumentException("Mqtt Client Cert File Required");
				}
				
				if(StringUtils.isBlank(cfg.getMqttKeyFile())) {
					txtMqttClientKeyFile.requestFocus();
					throw new IllegalArgumentException("Mqtt Client Key File Required");
				}
				
				if(StringUtils.isBlank(cfg.getMqttKeyPassword())) {
					txtMqttClientKeyPassword.requestFocus();
					throw new IllegalArgumentException("Mqtt Client Key Password Required");
				}
				
				validateFileExists(cfg.getMqttCAFile(), txtMqttCaCertFile, "Mqtt CA File %s not found");
				validateFileExists(cfg.getMqttCertFile(), txtMqttClientCertFile, "Mqtt Client Cert File %s not found");
				validateFileExists(cfg.getMqttKeyFile(), txtMqttClientKeyFile, "Mqtt Client Key File %s not found");
				
			}
		}
	}
	
	private void validateFileExists(String path, JComponent c, String msg) {
		File f = new File(path);
		if(!f.exists()) {
			c.requestFocus();
			throw new IllegalArgumentException(String.format(msg, path));
		}
	}

	@Override
	public String toString() {
		return cfg.getLabel();
	}

	public static void main(String[] args) throws FileNotFoundException {
		JFrame frame = new JFrame("Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//frame.setUndecorated(true);
		//frame.setSize(800, 800);
		String cfg = DomainConfigs.get().getActiveConfiguration();
		if(StringUtils.isNotBlank(cfg)) {
			frame.setContentPane(new DomainConfigPanel(DomainConfigs.get().getConfiguration(cfg)));
		}else {
			frame.setContentPane(new DomainConfigPanel(new DomainConfig()));
		}
		
		frame.setVisible(true);
	}

}
