package io.boodskap.iot.raspberry.pantilt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jettison.json.JSONObject;

import io.boodskap.iot.MessageHandler;
import io.boodskap.iot.MessagePublisher;

public class PanTiltApplication implements MessageHandler {
	
	public static final int MSG_STATUS = 500000500;
	
	public static final int CMD_PAN = 100;
	public static final int CMD_TILT = 101;
	public static final int CMD_RESET = 102;
	public static final int CMD_PAN_BEGIN = 200;
	public static final int CMD_PAN_CENTER = 201;
	public static final int CMD_PAN_END = 202;
	public static final int CMD_TILT_BEGIN = 300;
	public static final int CMD_TILT_CENTER = 301;
	public static final int CMD_TILT_END = 302;

	private PanTiltConfig config;
	private DirectServoBlaster panServo;
	private DirectServoBlaster tiltServo;
	private MessagePublisher<PanTiltConfig> publisher;
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	
	private PanTiltApplication() {
	}
	
	private void init() throws Exception {
		
		if(!PanTiltConfig.exists(PanTiltConfig.class)) {
			PanTiltConfig.create();
		}
		
		config = PanTiltConfig.load(PanTiltConfig.class);
		panServo = new DirectServoBlaster(config.getPanPin().pin, config.getPanPin().min, config.getPanPin().max);
		tiltServo = new DirectServoBlaster(config.getTiltPin().pin, config.getTiltPin().min, config.getTiltPin().max);
		publisher = new MessagePublisher<PanTiltConfig>(config, config.getDeviceModel(), config.getFirmwareVersion(), this);
		
		try{publisher.open();}catch(Exception ex) {ex.printStackTrace();}
		
		panServo.moveToCenter();
		tiltServo.moveToCenter();
		
		service.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, Object> json = new HashMap<>();
					json.put("ppin", config.getPanPin().pin);
					json.put("tpin", config.getTiltPin().pin);
					json.put("ppos", panServo.getPosition());
					json.put("tpos", tiltServo.getPosition());
					publisher.publish(MSG_STATUS, json );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1, 1, TimeUnit.MINUTES);
		
	}
	
	@Override
	public boolean handleMessage(String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, long corrId, int messageId, JSONObject data) {
		
		boolean acked = false;
		
		try {
			switch(messageId) {
			case CMD_PAN:
				acked = true;
				int pos = data.getInt("pos");
				panServo.moveTo(pos);
				break;
			case CMD_TILT:
				acked = true;
				pos = data.getInt("pos");
				tiltServo.moveTo(pos);
				break;
			case CMD_RESET:
				acked = true;
				
				if(data.has("ppos")) {
					pos = data.getInt("ppos");
					panServo.moveTo(pos);
				}else {
					panServo.moveToCenter();
				}
				
				if(data.has("tpos")) {
					pos = data.getInt("tpos");
					tiltServo.moveTo(pos);
				}else {
					tiltServo.moveToCenter();
				}
				
				break;
			case CMD_PAN_BEGIN:
				acked = true;
				panServo.moveToBegin();
				break;
			case CMD_PAN_CENTER:
				acked = true;
				panServo.moveToCenter();
				break;
			case CMD_PAN_END:
				panServo.moveToEnd();
				acked = true;
				break;
			case CMD_TILT_BEGIN:
				acked = true;
				tiltServo.moveToBegin();
				break;
			case CMD_TILT_CENTER:
				acked = true;
				tiltServo.moveToCenter();
				break;
			case CMD_TILT_END:
				acked = true;
				tiltServo.moveToEnd();
				break;
			default:
				System.err.format("Unknown command: %d, ignored.\n", messageId);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return acked;
	}

	public static void main(String[] args) throws InterruptedException {

		try {
			
			PanTiltApplication app = new PanTiltApplication();
			app.init();
			
		} catch (Exception e) {
			if(e instanceof IllegalArgumentException) {
				System.err.println(e.getMessage());
			}else {
				e.printStackTrace();
			}
			System.exit(-1);
		}

	}

}
