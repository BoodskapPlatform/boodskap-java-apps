package io.boodskap.iot.simulator;

public interface TransmitterHandler {

	public static enum Type{
		SERVER,
		CLIENT,
		TRANSMITTER
	};
	
	public void message(Type type, String msg);
}
