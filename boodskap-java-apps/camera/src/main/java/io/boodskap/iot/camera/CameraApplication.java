package io.boodskap.iot.camera;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.github.sarxos.webcam.Webcam;
import com.google.gson.Gson;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

import io.boodskap.iot.MessageHandler;
import io.boodskap.iot.camera.rpi.RPiCamera;

public class CameraApplication implements MessageHandler {
	
	private static final CameraApplication instance = new CameraApplication();

	static {
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				instance.shutdown();
			}
		}));
	}
	
	/**
	 * Commands
	 */
	
	public static final int CMD_START = 1000;
	public static final int CMD_PAUSE = 1001;
	public static final int CMD_STOP = 1002;
	public static final int CMD_CONFIG = 1003;
	
	private final Map<Camera, CameraStreamer> cameras = new HashMap<Camera, CameraStreamer>();
	private final ExecutorService exec = Executors.newCachedThreadPool();
	
	private CameraConfig config;
	private ImageSender publisher;

	private CameraApplication() {
	}
	
	private void start(CameraConfig config) throws FailedToRunRaspistillException {

		this.config = config;
		publisher = new ImageSender(config, this);
		
		try {
			publisher.open();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		switch (config.getCameraType()) {
		case RPI: {
			RPiCamera camera = new RPiCamera(config);
			CameraStreamer streamer = new CameraStreamer(this, camera);
			exec.submit(streamer);
		}
			break;
		case USB: {

			List<Webcam> list = new ArrayList<>();
			
			try {
				List<Webcam> tlist = Webcam.getWebcams(config.getUsb().getInitWait(), TimeUnit.MILLISECONDS);
				list.addAll(tlist);
			} catch (Exception e) {
				if(e instanceof TimeoutException) {
					System.err.format("%s\n", e.getMessage());
				}else {
					e.printStackTrace();
				}
			}
			
			if(list.isEmpty()) {
				System.err.println("No cameras detected!");
			}
			
			for(Webcam cam : list) {
				USBCamera camera = new USBCamera(config, cam);
				CameraStreamer streamer = new CameraStreamer(this, camera);
				exec.submit(streamer);
			}
			
		}
			break;
		}
	}
	
	protected void sendStream(String cameraId, BufferedImage image) {
		try {
			byte[] data = convertToImage(image);
			publisher.sendVideo(cameraId, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void sendSnap(String cameraId, BufferedImage image) {
		try {
			byte[] data = convertToImage(image);
			publisher.sendPicture(cameraId, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected byte[] convertToImage(BufferedImage image) throws IOException {
		
		ByteArrayOutputStream compressed = new ByteArrayOutputStream();
		ImageOutputStream outputStream = new MemoryCacheImageOutputStream(compressed);
		
		ImageWriter imageWriter = ImageIO.getImageWritersBySuffix(config.getImageFormat().name().toLowerCase()).next();
		ImageWriteParam jpgWriteParam = imageWriter.getDefaultWriteParam();
		
		try {
			jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		}catch(Exception ex) {}
		try {
			jpgWriteParam.setCompressionQuality(0.7f);
		}catch(Exception ex) {}
		
		imageWriter.setOutput(outputStream);
		imageWriter.write(null, new IIOImage(image, null, null), jpgWriteParam);
		imageWriter.dispose();
		byte[] imageData = compressed.toByteArray();
		return imageData;
	}
	
	public boolean handleMessage(String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, long corrId, int messageId, JSONObject data) {
		
		boolean acked = false;
		
		try {
			
			switch(messageId) {
			case CMD_START:
				acked = handleStart(data);
				break;
			case CMD_PAUSE:
				acked = handlePause(data);
				break;
			case CMD_STOP:
				acked = handleStop(data);
				break;
			case CMD_CONFIG:
				acked = handleConfig(data);
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return acked;
	}

	private boolean handleConfig(JSONObject data) throws Exception {
		
		System.out.println("Saving new configuration...");
		
		Gson gson = new Gson();
		CameraConfig cfg = gson.fromJson(data.toString(), CameraConfig.class);
		cfg.validate();
		cfg.save(CameraConfig.class);
		
		restart(cfg);
		
		return true;
	}

	private void shutdown() {
		try{for(CameraStreamer cs : cameras.values()) {cs.shutdown();}}catch(Exception ex) {}
		try{exec.shutdownNow();}catch(Exception ex) {}
		try{publisher.close();}catch(Exception ex) {}
		try{cameras.clear();}catch(Exception ex) {ex.printStackTrace();}
	}
	
	private void restart(CameraConfig config) {
		
		try{for(CameraStreamer cs : cameras.values()) {cs.shutdown();}}catch(Exception ex) {ex.printStackTrace();}
		try{publisher.close();}catch(Exception ex) {ex.printStackTrace();}
		try{cameras.clear();}catch(Exception ex) {ex.printStackTrace();}
		
		try{
			start(config);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean handleStop(JSONObject data) {
		
		Set<CameraStreamer> streamers = new HashSet<>();
		
		for(Map.Entry<Camera, CameraStreamer> me : cameras.entrySet()) {
			
			if(!data.isNull(me.getKey().getName())) {
				streamers.add(me.getValue());
			}
		}
		
		if(streamers.isEmpty()) {
			streamers.addAll(cameras.values());
		}
		
		streamers.forEach(s -> {
			s.stop();
		});
		
		return !streamers.isEmpty();
	}

	private boolean handlePause(JSONObject data) throws JSONException {
		
		Set<CameraStreamer> streamers = new HashSet<>();
		
		long pauseFor = data.getLong("pause");
		
		for(Map.Entry<Camera, CameraStreamer> me : cameras.entrySet()) {
			
			if(!data.isNull(me.getKey().getName())) {
				streamers.add(me.getValue());
			}
		}
		
		if(streamers.isEmpty()) {
			streamers.addAll(cameras.values());
		}
		
		streamers.forEach(s -> {
			s.pause(pauseFor);
		});
		
		return !streamers.isEmpty();
	}

	private boolean handleStart(JSONObject data) {
		
		Set<CameraStreamer> streamers = new HashSet<>();
		
		for(Map.Entry<Camera, CameraStreamer> me : cameras.entrySet()) {
			
			if(!data.isNull(me.getKey().getName())) {
				streamers.add(me.getValue());
			}
		}
		
		if(streamers.isEmpty()) {
			streamers.addAll(cameras.values());
		}
		
		streamers.forEach(s -> {
			s.start();
		});
		
		return !streamers.isEmpty();
	}

	public CameraConfig getConfig() {
		return config;
	}

	public static void main(String[] args) {
		
		try {

			if(!CameraConfig.exists(CameraConfig.class)) {
				CameraConfig.create();
			}
			
			CameraConfig config = CameraConfig.load(CameraConfig.class);
			
			CameraApplication.instance.start(config);
			
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
