package io.boodskap.iot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BaseConfig implements Serializable {

	private static final long serialVersionUID = 334420988357625717L;

	static final File home = new File(String.format("%s%s.boodskap", System.getProperty("user.home"), File.separator));
	
	private String domainKey = "";
	private String apiKey = "";
	private String deviceId="";
	private PublishChannel publisher = PublishChannel.MQTT;
	private String mqttUrl = "tcp://mqtt.boodskap.io:1883";
	private int mqttQos = 2;
	private String udpHost = "udp.boodskap.io";
	private int udpPort = 5555;
	private long heartbeat = 30000;
	private String httpUrl = "https://api.boodskap.io";
	
	static {
		try{home.mkdirs();}catch(Exception ex) {}
	}
	
	
	public BaseConfig() {
	}
	
	public static boolean exists(final Class<? extends BaseConfig> dataClass) {
		File file = new File(home, String.format("%s.json", dataClass.getSimpleName()));
		return file.exists();
	}

	public static <T extends BaseConfig> T load(final Class<T> dataClass) throws Exception {

		File file = new File(home, String.format("%s.json", dataClass.getSimpleName()));
		System.out.format("Loading config from %s\n", file.getAbsolutePath());
		
		T config = new Gson().fromJson(new FileReader(file),  BaseConfig.getType(dataClass, BaseConfig.class));
		config.validate();
		return config;
	}
	
	public void save(final Class<? extends BaseConfig> dataClass) throws IOException {

		File file = new File(home, String.format("%s.json", dataClass.getSimpleName()));
		System.out.format("Saving config to %s\n", file.getAbsolutePath());
		
		String json = new GsonBuilder().setPrettyPrinting().create().toJson(this);
		FileWriter writer = new FileWriter(file);
		writer.write(json);
		writer.flush();
		writer.close();
	}
	
	public void validate() throws Exception{
		
		if(null == domainKey || domainKey.trim().equals("")) throw new IllegalArgumentException("domainKey required");
		if(null == apiKey || apiKey.trim().equals("")) throw new IllegalArgumentException("apiKey required");
		if(null == deviceId || deviceId.trim().equals("")) throw new IllegalArgumentException("deviceId required");
		if(null == mqttUrl || mqttUrl.trim().equals("")) throw new IllegalArgumentException("mqttUrl required");
		if(null == udpHost || udpHost.trim().equals("")) throw new IllegalArgumentException("udpHost required");
		if(mqttQos < 0 || mqttQos > 2) throw new IllegalArgumentException("mqttQos must be [0, 1, 2]");
		if(udpPort <= 0) throw new IllegalArgumentException("udpPort must be > 0");
		if(heartbeat < 1000) throw new IllegalArgumentException("heartbeat must be >= 1000");
		if(null == httpUrl || httpUrl.trim().equals("")) throw new IllegalArgumentException("httpUrl required");
		if(null == publisher) throw new IllegalArgumentException("publisher required");
		
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

	public PublishChannel getPublisher() {
		return publisher;
	}

	public void setPublisher(PublishChannel publisher) {
		this.publisher = publisher;
	}

	public String getMqttUrl() {
		return mqttUrl;
	}

	public void setMqttUrl(String mqttUrl) {
		this.mqttUrl = mqttUrl;
	}

	public int getMqttQos() {
		return mqttQos;
	}

	public void setMqttQos(int mqttQos) {
		this.mqttQos = mqttQos;
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

	private static Type getType(final Class<?> rawClass, final Class<?> parameterClass) {
		return new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[]{parameterClass};
			}

			@Override
			public Type getRawType() {
				return rawClass;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}

		};
	}

}
