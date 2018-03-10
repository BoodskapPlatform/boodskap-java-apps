package io.boodskap.iot.raspberry.pantilt;

import java.io.Serializable;

import io.boodskap.iot.BaseConfig;

/**
 * Pan/Tilt Servo Application Configuration, stored in ${user.home}/.boodskap/PanTiltConfig.json
 * 
 * @author Jegan Vincent
 *
 */
public class PanTiltConfig extends BaseConfig implements Serializable{

	private static final long serialVersionUID = 5256611394455364715L;
	
	private static final String DEVICE_MODEL = "BSKP-PANTLT";
	private static final String FIRMWARE_VERSION = "1.0.0";

	private ServoPin panPin = new ServoPin("P1-11", 50, 250);
	private ServoPin tiltPin = new ServoPin("P1-12", 50, 250);
	
	public class ServoPin{
		
		public String pin;
		public int min;
		public int max;
		
		public ServoPin() {
		}

		public ServoPin(String pin, int min, int max) {
			super();
			this.pin = pin;
			this.min = min;
			this.max = max;
		}

	}
	
	protected PanTiltConfig() {
		
	}

	public void validate() throws Exception {
		super.validate();
	}

	public static void create() throws Exception {
		
		PanTiltConfig nconfig = new PanTiltConfig(); 
		
		System.out.println("Creating default configuration, please wait...");
		
		nconfig.save(PanTiltConfig.class);
		
	}

	public String getDeviceModel() {
		return DEVICE_MODEL;
	}

	public String getFirmwareVersion() {
		return FIRMWARE_VERSION;
	}

	public ServoPin getPanPin() {
		return panPin;
	}

	public void setPanPin(ServoPin panPin) {
		this.panPin = panPin;
	}

	public ServoPin getTiltPin() {
		return tiltPin;
	}

	public void setTiltPin(ServoPin tiltPin) {
		this.tiltPin = tiltPin;
	}

}
