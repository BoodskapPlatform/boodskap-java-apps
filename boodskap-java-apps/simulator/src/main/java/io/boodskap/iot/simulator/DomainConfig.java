package io.boodskap.iot.simulator;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

public class DomainConfig implements Serializable{

	private static final long serialVersionUID = 4655617513881082641L;
	
	static final String BASE_PATH = String.format("%s%s.boodskap%scerts", System.getProperty("user.home"), File.separator, File.separator);
	
	private String id = UUID.randomUUID().toString();
	private String label = id;
	private String domainKey;
	private String apiKey;
	private String apiUrl = "http://api.boodskap.io";
	private boolean udpEnabled = true;
	private boolean mqttEnabled = true;
	private boolean coapEnabled = true;
	private String udpHost = "udp.boodskap.io";
	private int udpPort = 5555;
	private String mqttHost = "mqtt.boodskap.io";
	private int mqttPort = 1883;
	private String coapHost = "coap.boodskap.io";
	private int coapPort = 5683;
	private String pushUrl;
	private String userId;
	private boolean mqttSSL;
	private boolean coapSSL;
	private String mqttCAFile = String.format("%s%sca-cert.pem", BASE_PATH, File.separator);
	private String mqttCertFile = String.format("%s%scert.pem", BASE_PATH, File.separator);
	private String mqttKeyFile = String.format("%s%skey.pem", BASE_PATH, File.separator);
	private String mqttKeyPassword = "";
	private String coapCAFile = String.format("%s%sca-cert.pem", BASE_PATH, File.separator);
	private String coapCertFile = String.format("%s%scert.pem", BASE_PATH, File.separator);
	private String coapKeyFile = String.format("%s%skey.pem", BASE_PATH, File.separator);
	private String coapKeyPassword = "";
	
	public DomainConfig() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
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

	public String getMqttHost() {
		return mqttHost;
	}

	public void setMqttHost(String mqttHost) {
		this.mqttHost = mqttHost;
	}

	public int getMqttPort() {
		return mqttPort;
	}

	public void setMqttPort(int mqttPort) {
		this.mqttPort = mqttPort;
	}

	public String getCoapHost() {
		return coapHost;
	}

	public void setCoapHost(String coapHost) {
		this.coapHost = coapHost;
	}

	public int getCoapPort() {
		return coapPort;
	}

	public void setCoapPort(int coapPort) {
		this.coapPort = coapPort;
	}

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isMqttSSL() {
		return mqttSSL;
	}

	public void setMqttSSL(boolean mqttSSL) {
		this.mqttSSL = mqttSSL;
	}

	public boolean isCoapSSL() {
		return coapSSL;
	}

	public void setCoapSSL(boolean coapSSL) {
		this.coapSSL = coapSSL;
	}

	public String getMqttCAFile() {
		return mqttCAFile;
	}

	public void setMqttCAFile(String mqttCAFile) {
		this.mqttCAFile = mqttCAFile;
	}

	public String getMqttCertFile() {
		return mqttCertFile;
	}

	public void setMqttCertFile(String mqttCertFile) {
		this.mqttCertFile = mqttCertFile;
	}

	public String getMqttKeyFile() {
		return mqttKeyFile;
	}

	public void setMqttKeyFile(String mqttKeyFile) {
		this.mqttKeyFile = mqttKeyFile;
	}

	public String getMqttKeyPassword() {
		return mqttKeyPassword;
	}

	public void setMqttKeyPassword(String mqttKeyPassword) {
		this.mqttKeyPassword = mqttKeyPassword;
	}

	public String getCoapCAFile() {
		return coapCAFile;
	}

	public void setCoapCAFile(String coapCAFile) {
		this.coapCAFile = coapCAFile;
	}

	public String getCoapCertFile() {
		return coapCertFile;
	}

	public void setCoapCertFile(String coapCertFile) {
		this.coapCertFile = coapCertFile;
	}

	public String getCoapKeyFile() {
		return coapKeyFile;
	}

	public void setCoapKeyFile(String coapKeyFile) {
		this.coapKeyFile = coapKeyFile;
	}

	public String getCoapKeyPassword() {
		return coapKeyPassword;
	}

	public void setCoapKeyPassword(String coapKeyPassword) {
		this.coapKeyPassword = coapKeyPassword;
	}

	public boolean isUdpEnabled() {
		return udpEnabled;
	}

	public void setUdpEnabled(boolean udpEnabled) {
		this.udpEnabled = udpEnabled;
	}

	public boolean isMqttEnabled() {
		return mqttEnabled;
	}

	public void setMqttEnabled(boolean mqttEnabled) {
		this.mqttEnabled = mqttEnabled;
	}

	public boolean isCoapEnabled() {
		return coapEnabled;
	}

	public void setCoapEnabled(boolean coapEnabled) {
		this.coapEnabled = coapEnabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apiKey == null) ? 0 : apiKey.hashCode());
		result = prime * result + ((apiUrl == null) ? 0 : apiUrl.hashCode());
		result = prime * result + ((coapCAFile == null) ? 0 : coapCAFile.hashCode());
		result = prime * result + ((coapCertFile == null) ? 0 : coapCertFile.hashCode());
		result = prime * result + (coapEnabled ? 1231 : 1237);
		result = prime * result + ((coapHost == null) ? 0 : coapHost.hashCode());
		result = prime * result + ((coapKeyFile == null) ? 0 : coapKeyFile.hashCode());
		result = prime * result + ((coapKeyPassword == null) ? 0 : coapKeyPassword.hashCode());
		result = prime * result + coapPort;
		result = prime * result + (coapSSL ? 1231 : 1237);
		result = prime * result + ((domainKey == null) ? 0 : domainKey.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((mqttCAFile == null) ? 0 : mqttCAFile.hashCode());
		result = prime * result + ((mqttCertFile == null) ? 0 : mqttCertFile.hashCode());
		result = prime * result + (mqttEnabled ? 1231 : 1237);
		result = prime * result + ((mqttHost == null) ? 0 : mqttHost.hashCode());
		result = prime * result + ((mqttKeyFile == null) ? 0 : mqttKeyFile.hashCode());
		result = prime * result + ((mqttKeyPassword == null) ? 0 : mqttKeyPassword.hashCode());
		result = prime * result + mqttPort;
		result = prime * result + (mqttSSL ? 1231 : 1237);
		result = prime * result + ((pushUrl == null) ? 0 : pushUrl.hashCode());
		result = prime * result + (udpEnabled ? 1231 : 1237);
		result = prime * result + ((udpHost == null) ? 0 : udpHost.hashCode());
		result = prime * result + udpPort;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainConfig other = (DomainConfig) obj;
		if (apiKey == null) {
			if (other.apiKey != null)
				return false;
		} else if (!apiKey.equals(other.apiKey))
			return false;
		if (apiUrl == null) {
			if (other.apiUrl != null)
				return false;
		} else if (!apiUrl.equals(other.apiUrl))
			return false;
		if (coapCAFile == null) {
			if (other.coapCAFile != null)
				return false;
		} else if (!coapCAFile.equals(other.coapCAFile))
			return false;
		if (coapCertFile == null) {
			if (other.coapCertFile != null)
				return false;
		} else if (!coapCertFile.equals(other.coapCertFile))
			return false;
		if (coapEnabled != other.coapEnabled)
			return false;
		if (coapHost == null) {
			if (other.coapHost != null)
				return false;
		} else if (!coapHost.equals(other.coapHost))
			return false;
		if (coapKeyFile == null) {
			if (other.coapKeyFile != null)
				return false;
		} else if (!coapKeyFile.equals(other.coapKeyFile))
			return false;
		if (coapKeyPassword == null) {
			if (other.coapKeyPassword != null)
				return false;
		} else if (!coapKeyPassword.equals(other.coapKeyPassword))
			return false;
		if (coapPort != other.coapPort)
			return false;
		if (coapSSL != other.coapSSL)
			return false;
		if (domainKey == null) {
			if (other.domainKey != null)
				return false;
		} else if (!domainKey.equals(other.domainKey))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (mqttCAFile == null) {
			if (other.mqttCAFile != null)
				return false;
		} else if (!mqttCAFile.equals(other.mqttCAFile))
			return false;
		if (mqttCertFile == null) {
			if (other.mqttCertFile != null)
				return false;
		} else if (!mqttCertFile.equals(other.mqttCertFile))
			return false;
		if (mqttEnabled != other.mqttEnabled)
			return false;
		if (mqttHost == null) {
			if (other.mqttHost != null)
				return false;
		} else if (!mqttHost.equals(other.mqttHost))
			return false;
		if (mqttKeyFile == null) {
			if (other.mqttKeyFile != null)
				return false;
		} else if (!mqttKeyFile.equals(other.mqttKeyFile))
			return false;
		if (mqttKeyPassword == null) {
			if (other.mqttKeyPassword != null)
				return false;
		} else if (!mqttKeyPassword.equals(other.mqttKeyPassword))
			return false;
		if (mqttPort != other.mqttPort)
			return false;
		if (mqttSSL != other.mqttSSL)
			return false;
		if (pushUrl == null) {
			if (other.pushUrl != null)
				return false;
		} else if (!pushUrl.equals(other.pushUrl))
			return false;
		if (udpEnabled != other.udpEnabled)
			return false;
		if (udpHost == null) {
			if (other.udpHost != null)
				return false;
		} else if (!udpHost.equals(other.udpHost))
			return false;
		if (udpPort != other.udpPort)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return label;
	}

}
