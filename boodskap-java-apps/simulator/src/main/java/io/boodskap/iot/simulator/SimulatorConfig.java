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
	private int qos = 2;
	
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

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	@Override
	public String toString() {
		return label;
	}

}
