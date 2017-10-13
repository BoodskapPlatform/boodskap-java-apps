package io.boodskap.iot.simulator;

import java.io.Serializable;
import java.util.UUID;

public class SimulatorConfig implements Serializable{

	private static final long serialVersionUID = 3465951628686748832L;
	
	private String id = UUID.randomUUID().toString();
	private String label = id;
	private String deviceId;
	private String deviceModel;
	private String firmwareVersion;
	private String cronSchedule = "0 * * ? * *";
	private String code;
	private State state = State.ENABLED;
	private Protocol protocol = Protocol.MQTT;
	private String domainConfig;
	
	public static enum State{
		DISABLED,
		ENABLED,
	};
	
	public static enum Protocol{
		UDP,
		MQTT,
		COAP,
		HTTP,
	}

	public SimulatorConfig() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDomainConfig() {
		return domainConfig;
	}

	public void setDomainConfig(String domainConfig) {
		this.domainConfig = domainConfig;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((cronSchedule == null) ? 0 : cronSchedule.hashCode());
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((deviceModel == null) ? 0 : deviceModel.hashCode());
		result = prime * result + ((domainConfig == null) ? 0 : domainConfig.hashCode());
		result = prime * result + ((firmwareVersion == null) ? 0 : firmwareVersion.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		SimulatorConfig other = (SimulatorConfig) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (cronSchedule == null) {
			if (other.cronSchedule != null)
				return false;
		} else if (!cronSchedule.equals(other.cronSchedule))
			return false;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (deviceModel == null) {
			if (other.deviceModel != null)
				return false;
		} else if (!deviceModel.equals(other.deviceModel))
			return false;
		if (domainConfig == null) {
			if (other.domainConfig != null)
				return false;
		} else if (!domainConfig.equals(other.domainConfig))
			return false;
		if (firmwareVersion == null) {
			if (other.firmwareVersion != null)
				return false;
		} else if (!firmwareVersion.equals(other.firmwareVersion))
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
		if (protocol != other.protocol)
			return false;
		if (state != other.state)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return label;
	}

}
