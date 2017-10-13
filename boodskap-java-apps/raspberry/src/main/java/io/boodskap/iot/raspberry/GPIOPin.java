package io.boodskap.iot.raspberry;

import java.io.Serializable;

import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;

public class GPIOPin implements Serializable {

	private static final long serialVersionUID = 3645337970294028216L;

	private String name;
	private String pin;
	private PinPullResistance pull = PinPullResistance.PULL_DOWN;
	private boolean shutState = true;

	private PinState state;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public PinPullResistance getPull() {
		return pull;
	}

	public void setPull(PinPullResistance pull) {
		this.pull = pull;
	}

	public boolean isShutState() {
		return shutState;
	}

	public void setShutState(boolean shutState) {
		this.shutState = shutState;
	}

	public PinState getState() {
		return state;
	}

	public void setState(PinState state) {
		this.state = state;
	}

}
