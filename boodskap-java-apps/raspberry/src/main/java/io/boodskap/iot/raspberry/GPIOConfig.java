package io.boodskap.iot.raspberry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

import io.boodskap.iot.PublishChannel;

public class GPIOConfig implements Serializable{

	private static final long serialVersionUID = 5256611394455364715L;
	
	static final File home = new File(String.format("%s%s.boodskap%sraspberry", System.getProperty("user.home"), File.separator, File.separator));
	static final File file = new File(home, "config.json");
	
	static {
		try{home.mkdirs();}catch(Exception ex) {}
	}
	
	private String domainKey = "";
	private String apiKey = "";
	private String deviceId = "";
	private String deviceModel = "BSKP-JPI";
	private String firmwareVersion = "1.0.0";
	private PublishChannel publisher = PublishChannel.MQTT;
	private String mqttUrl = "tcp://mqtt.boodskap.io:1883";
	private String udpHost = "udp.boodskap.io";
	private int udpPort = 5555;
	private long heartbeat = 30000;
	private String httpUrl = "http://api.boodskap.io";
	
	private Map<Integer, GPIOMessage> messages = new HashMap<>();
	
	private List<String> availablePins = new ArrayList<>();
	
	protected GPIOConfig() {
		
	}

	public static boolean exists() {
		return file.exists();
	}

	public void validate() throws Exception {
		
		if(null == mqttUrl || mqttUrl.trim().equals("")) throw new IllegalArgumentException("mqttUrl required");
		if(null == udpHost || udpHost.trim().equals("")) throw new IllegalArgumentException("udpHost required");
		if(udpPort <= 0) throw new IllegalArgumentException("udpPort must be > 0");
		if(heartbeat < 1000) throw new IllegalArgumentException("heartbeat must be >= 1000");
		if(null == httpUrl || httpUrl.trim().equals("")) throw new IllegalArgumentException("httpUrl required");
		if(null == domainKey || domainKey.trim().equals("")) throw new IllegalArgumentException("domainKey required");
		if(null == apiKey || apiKey.trim().equals("")) throw new IllegalArgumentException("apiKey required");
		if(null == deviceId || deviceId.trim().equals("")) throw new IllegalArgumentException("deviceId required");
		if(null == deviceModel || deviceModel.trim().equals("deviceModel")) throw new IllegalArgumentException("deviceModel required");
		if(null == firmwareVersion || firmwareVersion.trim().equals("")) throw new IllegalArgumentException("firmwareVersion required");
		
		if(messages.isEmpty()) throw new IllegalArgumentException("Expects atleast one message definition");
		
		for(GPIOMessage msg : messages.values()) {
			msg.validate();
		}
	}

	public static void create() throws Exception {
		
		GPIOConfig nconfig = new GPIOConfig(); 
		
		System.out.println("Creating default configuration, please wait...");
		
		
		GPIOPin pin = new GPIOPin();
		pin.setName("field_name");
		pin.setPin(RaspiPin.GPIO_02.getName());
		pin.setPull(PinPullResistance.PULL_DOWN);
		pin.setShutState(false);
		
		GPIOMessage msg = new GPIOMessage();
		msg.setHeartbeat(30000);
		msg.setMessageId(10000);
		msg.getPins().add(pin);
		
		nconfig.getMessages().put(msg.getMessageId(), msg);
		
		for(Pin p : RaspiPin.allPins()) {
			nconfig.getAvailablePins().add(p.getName());
		}
		
		String json = new GsonBuilder().setPrettyPrinting().create().toJson(nconfig);
		FileWriter fw = new FileWriter(file);
		fw.write(json);
		fw.flush();
		fw.close();
	}

	public static GPIOConfig load() throws Exception {
		System.out.format("Loading config from %s\n", file.getAbsolutePath());
		GPIOConfig config = new Gson().fromJson(new FileReader(file), GPIOConfig.class);
		config.validate();
		return config;
	}
	
	public void save() throws IOException {
		String json = new GsonBuilder().setPrettyPrinting().create().toJson(this);
		FileWriter writer = new FileWriter(file);
		writer.write(json);
		writer.flush();
		writer.close();
	}

	public String getDomainKey() {
		return domainKey;
	}

	public void setDomainKey(String domainKey) {
		this.domainKey = domainKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getMqttUrl() {
		return mqttUrl;
	}

	public void setMqttUrl(String mqttUrl) {
		this.mqttUrl = mqttUrl;
	}

	public String getUdpHost() {
		return udpHost;
	}

	public void setUdpHost(String udpHost) {
		this.udpHost = udpHost;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public long getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(long heartbeat) {
		this.heartbeat = heartbeat;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	public Map<Integer, GPIOMessage> getMessages() {
		return messages;
	}

	public void setMessages(Map<Integer, GPIOMessage> messages) {
		this.messages = messages;
	}

	public PublishChannel getPublisher() {
		return publisher;
	}

	public void setPublisher(PublishChannel publisher) {
		this.publisher = publisher;
	}

	public List<String> getAvailablePins() {
		return availablePins;
	}

	public void setAvailablePins(List<String> availablePins) {
		this.availablePins = availablePins;
	}

}
