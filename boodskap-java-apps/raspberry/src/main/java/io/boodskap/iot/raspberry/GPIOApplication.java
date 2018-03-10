package io.boodskap.iot.raspberry;

import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.RaspiPin;

import io.boodskap.iot.MessageHandler;

public class GPIOApplication implements MessageHandler {
	
	static final GpioController gpio = GpioFactory.getInstance();
	
	static {
		try {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						gpio.shutdown();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private GPIOConfig config;
	private GPIOMessagePublisher sender;
	
	private GPIOApplication() {
		
	}
	
	private void init() {
		
		try {
			
			if(!GPIOConfig.exists(GPIOConfig.class)) {
				GPIOConfig.create();
			}
			
			config = GPIOConfig.load(GPIOConfig.class);
			
			sender = new GPIOMessagePublisher(config, config.getDeviceModel(), config.getFirmwareVersion(), this);
			
			try {
				sender.open();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			for(Map.Entry<Integer, GPIOMessage> me : config.getMessages().entrySet()) {
				
				for(GPIOPin pin : me.getValue().getPins()) {
					
					System.out.format("Configuring Message:%d, Pin:%s, Pullup:%s\n", me.getKey(), pin.getPin(), pin.getPull());
					
					GpioPinDigitalInput button = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(pin.getPin()), pin.getPull());
					button.setShutdownOptions(pin.isShutState());
					button.addListener(new GPIOPinListener(this, me.getValue(), pin));
				}
				
			}
			
		} catch (Exception e) {
			if(e instanceof IllegalArgumentException) {
				System.err.println(e.getMessage());
			}else {
				e.printStackTrace();
			}
		}
	}

	public GPIOMessagePublisher getSender() {
		return sender;
	}

	public static void main(String[] args) {

		System.out.println("<--Pi4J--> GPIO Listen Example ... started.");

		GPIOApplication app = new GPIOApplication();
		app.init();
		
		System.out.println(" ... complete the GPIO #02 circuit and see the listener feedback here in the console.");
		
		while(true) {
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public boolean handleMessage(String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, long corrId, int messageId, JSONObject data) {
		return false;
	}
}