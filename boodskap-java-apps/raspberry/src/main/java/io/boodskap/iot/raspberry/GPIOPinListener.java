package io.boodskap.iot.raspberry;

import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class GPIOPinListener implements GpioPinListenerDigital {
	
	private static final Map<String, Long> LAST_CHANGE = new HashMap<>();
	
	private final GPIOApplication app;
	private final GPIOMessage message;
	private final GPIOPin pin;
	
	public GPIOPinListener(GPIOApplication app, GPIOMessage message, GPIOPin pin) {
		this.app = app;
		this.message = message;
		this.pin = pin;
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		
		Long last = LAST_CHANGE.get(pin.getPin());
		
		if(null == last || (System.currentTimeMillis() - last) > 750) {
			try {
				System.out.format("Message: %d, Pin: %s, State: %s\n", message.getMessageId(), pin.getPin(), event.getState());
				LAST_CHANGE.put(pin.getPin(), System.currentTimeMillis());
				pin.setState(event.getState());
				app.getSender().send(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
