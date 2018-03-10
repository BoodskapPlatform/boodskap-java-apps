package io.boodskap.iot.raspberry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

import io.boodskap.iot.BaseConfig;

/**
 * Camera Application Configuration, stored in ${user.home}/.boodskap/GPIOConfig.json
 * 
 * @author Jegan Vincent
 *
 */
public class GPIOConfig extends BaseConfig implements Serializable{

	private static final long serialVersionUID = 5256611394455364715L;
	
	private static final String DEVICE_MODEL = "BSKP-JPI";
	private static final String FIRMWARE_VERSION = "1.0.0";

	private Map<Integer, GPIOMessage> messages = new HashMap<>();
	
	private List<String> availablePins = new ArrayList<>();
	
	protected GPIOConfig() {
		
	}

	public void validate() throws Exception {
		
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
		
		nconfig.save(GPIOConfig.class);
		
	}

	public String getDeviceModel() {
		return DEVICE_MODEL;
	}

	public String getFirmwareVersion() {
		return FIRMWARE_VERSION;
	}

	public Map<Integer, GPIOMessage> getMessages() {
		return messages;
	}

	public void setMessages(Map<Integer, GPIOMessage> messages) {
		this.messages = messages;
	}

	public List<String> getAvailablePins() {
		return availablePins;
	}

	public void setAvailablePins(List<String> availablePins) {
		this.availablePins = availablePins;
	}

}
