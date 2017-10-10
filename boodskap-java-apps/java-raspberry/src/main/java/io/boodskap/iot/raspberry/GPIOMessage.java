package io.boodskap.iot.raspberry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.RaspiPin;

public class GPIOMessage implements Serializable {

	private static final long serialVersionUID = -3042273977304781066L;

	private int messageId;
	private List<GPIOPin> pins = new ArrayList<>();
	private long heartbeat = 60000;

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public List<GPIOPin> getPins() {
		return pins;
	}

	public void setPins(List<GPIOPin> pins) {
		this.pins = pins;
	}

	public boolean hasPin(RaspiPin pin) {
		
		for(GPIOPin gp : pins) {
			if(gp.getPin().equals(pin)) return true;
		};
		
		return false;
	}
	
	public void validate() throws IllegalArgumentException {
		if(messageId < 100) throw new IllegalArgumentException("messageId should be >= 100");
		if(heartbeat < 1000) throw new IllegalArgumentException("heartbeat should be >= 1000");
	}

	public long getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(long heartbeat) {
		this.heartbeat = heartbeat;
	}

}
