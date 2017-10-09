/*******************************************************************************
 * Copyright (C) 2017 Boodskap Inc
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package io.boodskap.iot.camera;

import java.awt.Dimension;
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
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.google.gson.Gson;

import io.boodskap.iot.MessageHandler;
import io.boodskap.iot.camera.CameraConfig.DiscoveryMode;
import net.coobird.thumbnailator.Thumbnails;

/**
 * Main entry point for the Camera application
 * 
 * @author Jegan Vincent
 *
 */
public class CameraApplication implements WebcamDiscoveryListener, MessageHandler {
	
	private static CameraApplication instance = new CameraApplication();
	
	protected static final List<String> FORMATS = new ArrayList<String>();
	
	static {
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				instance.shutdown();
			}
		}));
	}
	
	private final Map<Webcam, CameraStreamer> cameras = new HashMap<Webcam, CameraStreamer>();
	private final ExecutorService exec = Executors.newCachedThreadPool();
	private CameraConfig config;
	private ImageSender publisher;
	
	/**
	 * Commands
	 */
	
	public static final int CMD_START = 1000;
	public static final int CMD_PAUSE = 1001;
	public static final int CMD_STOP = 1002;
	public static final int CMD_CONFIG = 1003;
	
	private CameraApplication() {
	}
	
	private void shutdown() {
		try{for(CameraStreamer cs : cameras.values()) {cs.shutdown();}}catch(Exception ex) {}
		try{instance.exec.shutdownNow();}catch(Exception ex) {}
		try{synchronized(instance) {instance.notify();}}catch(Exception ex) {}
		try{publisher.close();}catch(Exception ex) {}
		try{cameras.clear();}catch(Exception ex) {ex.printStackTrace();}
	}
	
	private void restart() {
		
		try{for(CameraStreamer cs : cameras.values()) {cs.shutdown();}}catch(Exception ex) {ex.printStackTrace();}
		try{publisher.close();}catch(Exception ex) {ex.printStackTrace();}
		try{cameras.clear();}catch(Exception ex) {ex.printStackTrace();}
		
		try{
			init(true);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void init(boolean reinit) throws Exception {
		
		if(!CameraConfig.exists()) {
			CameraConfig.create();
		}
		
		config = CameraConfig.load();
		
		publisher = new ImageSender(config, this);
		
		try {
			publisher.open();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		if(!reinit && config.getDiscoverMode() == DiscoveryMode.AUTO) {
			System.out.println("Running in auto discovery mode");
			Webcam.addDiscoveryListener(instance);
			try {
				Webcam.getWebcams(10, TimeUnit.SECONDS);
			} catch (Exception e) {
			}
		}else {
			
			if(reinit) {
				System.out.println("re-detecting cameras...");
			}else {
				System.out.println("Running in manual discovery mode, detecting cameras...");
			}
			
			List<Webcam> list = new ArrayList<>();
			
			try {
				List<Webcam> tlist = Webcam.getWebcams(config.getInitWait(), TimeUnit.MILLISECONDS);
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
			
			for(Webcam camera : list) {
				initCamera(camera);
			}

		}
		
		if(!reinit) {
			exec.submit(new Runnable() {
				public void run() {
					try {
						
						System.out.println("Camera appliction running...");
						
						while(!Thread.currentThread().isInterrupted()) {
							synchronized(this) {
								wait();
							}
						}
					} catch (Exception e) {
						if(!(e instanceof InterruptedException)) {
							e.printStackTrace();
						}
					}
					System.out.println("Camera appliction stopped.");
				}
			});
		}
		
		
	}
	
	public CameraConfig getConfig() {
		return config;
	}

	public void webcamFound(WebcamDiscoveryEvent event) {
		initCamera(event.getWebcam());
	}
	
	protected void initCamera(Webcam camera) {
		
		System.out.format("Webcam:%s found\n", camera.getName());
		
		try {Thread.sleep(300);}catch(Exception ex) {}
		
		if(cameras.containsKey(camera)) {
			System.err.format("Webcam %s already running...\n", camera.getName());
			return;
		}
		
		Dimension size = camera.getViewSize();
		
		System.out.format("Webcam:%s view-size:[%d X %d]\n", camera.getName(), (int)size.getWidth(), (int)size.getHeight());
		
		Dimension[] sizes = camera.getViewSizes();
		if(null != sizes && sizes.length >0) {
			for(Dimension d : sizes) {
				int w = (int)d.getWidth();
				int h = (int)d.getHeight();
				System.out.format("Webcam:%s supported view-size:[%d X %d] resolution:%s\n", camera.getName(),  w, h, CameraConfig.RESOLUTIONS.get(String.format("%d.%d", w, h)));
			}
		}
		
		sizes = camera.getCustomViewSizes();
		if(null != sizes && sizes.length >0) {
			for(Dimension d : sizes) {
				int w = (int)d.getWidth();
				int h = (int)d.getHeight();
				System.out.format("Webcam:%s custom view-size:[%d X %d] resolution:%s\n", camera.getName(), w, h, CameraConfig.RESOLUTIONS.get(String.format("%d.%d", w, h)));
			}
		}
		
		System.out.format("Starting Webcam:%s ...\n", camera.getName());
		
		CameraStreamer streamer = new CameraStreamer(this, camera);
		streamer.setConfig(config);
		cameras.put(camera, streamer);
		exec.submit(streamer);

	}

	public void webcamGone(WebcamDiscoveryEvent event) {
		System.out.format("Webcam:%s disappeared\n", event.getWebcam().getName());
		CameraStreamer streamer = cameras.remove(event.getWebcam());
		if(null != streamer) {
			streamer.shutdown();
		}
	}
	
	protected void sendStream(Webcam webcam, BufferedImage image) {
		try {
			byte[] data = convertToImage(image);
			publisher.sendVideo(webcam.getName(), data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void sendSnap(Webcam webcam, BufferedImage image) {
		try {
			byte[] data = convertToImage(image);
			publisher.sendPicture(webcam.getName(), data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void streamingStopped(CameraStreamer streamer, Webcam webcam) {
		cameras.remove(webcam);
	}

	protected byte[] convertToImage(BufferedImage oimage) throws IOException {
		
		BufferedImage image = Thumbnails.of(oimage)
		        .size(200, 200)
		        .outputQuality(0.3)
		        .asBufferedImage();
		
		ByteArrayOutputStream compressed = new ByteArrayOutputStream();
		ImageOutputStream outputStream = new MemoryCacheImageOutputStream(compressed);
		
		ImageWriter imageWriter = ImageIO.getImageWritersBySuffix(config.getImageFormat()).next();
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
	
	public boolean handleMessage(String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, int messageId, JSONObject data) {
		
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
		cfg.save();
		restart();
		
		return true;
	}

	private boolean handleStop(JSONObject data) {
		
		Set<CameraStreamer> streamers = new HashSet<>();
		
		for(Map.Entry<Webcam, CameraStreamer> me : cameras.entrySet()) {
			
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
		
		for(Map.Entry<Webcam, CameraStreamer> me : cameras.entrySet()) {
			
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
		
		for(Map.Entry<Webcam, CameraStreamer> me : cameras.entrySet()) {
			
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

	public static void main(String[] args) {
		
		try {
			CameraApplication.instance.init(false);
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
