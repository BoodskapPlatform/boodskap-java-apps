package io.boodskap.iot.camera.rpi;

public enum Rotation {
	NONE(-1),
	R0(0),
	R90(90),
	R180(180),
	R270(270)
	;
	
	private Rotation(int value) {
		this.value = value;
	}
	
	private final int value;
	
	public final int value() {
		return value;
	}
}
