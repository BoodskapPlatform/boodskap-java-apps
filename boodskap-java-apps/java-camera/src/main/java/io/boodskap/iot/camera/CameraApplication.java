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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.codehaus.jettison.json.JSONObject;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.boodskap.iot.MessageHandler;
import net.coobird.thumbnailator.Thumbnails;

/**
 * Main entry point for the Camera application
 * 
 * @author Jegan Vincent
 *
 */
public class CameraApplication implements WebcamDiscoveryListener, MessageHandler {
	
	private static CameraApplication instance = new CameraApplication();
	
	private static final Map<String, WebcamResolution> RESOLUTIONS = new HashMap<String, WebcamResolution>();
	
	private static final List<String> FORMATS = new ArrayList<String>();
	
	static {
		
		ImageIO.scanForPlugins();
		
		if("arm".equals(System.getProperty("os.arch"))) {
			Webcam.setDriver(new com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver());
		}
		
		for(WebcamResolution res : WebcamResolution.values()) {
			
			int width = (int)res.getSize().getWidth();
			int height = (int)res.getSize().getHeight();
			System.out.format("Resolution: %s[%d x %d]\n", res.name(), width, height);
			
			RESOLUTIONS.put(String.format("%d.%d", width, height), res);
		}
		
		for(String f : ImageIO.getWriterFormatNames()) {
			FORMATS.add(f);
		}
		
		System.out.println(FORMATS);
		
		
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
	
	private CameraApplication() {
	}
	
	private void shutdown() {
		try {
			try{for(CameraStreamer cs : cameras.values()) {cs.shutdown();}}catch(Exception ex) {}
			instance.exec.shutdownNow();
			synchronized(instance) {
				instance.notify();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void init() throws Exception {
		
		File home = new File(String.format("%s%s.camera", System.getProperty("user.home"), File.separator));
		home.mkdirs();
		File file = new File(home, "config.json");
		
		if(!file.exists()) {
			
			CameraConfig nconfig = new CameraConfig(); 
			
			System.out.print("Creating default configuration, please wait...");
			
			nconfig.getAvailableResolutions().addAll(RESOLUTIONS.values());
			nconfig.getAvailableFormats().addAll(FORMATS);
			
			List<Webcam> list = Webcam.getWebcams(3, TimeUnit.SECONDS);
			for(Webcam wc : list) {
				Dimension res = wc.getViewSize();
				int width = (int)res.getSize().getWidth();
				int height = (int)res.getSize().getHeight();
				WebcamResolution wcr = RESOLUTIONS.get(String.format("%d.%d", width, height));
				if(null != wcr) {
					nconfig.getCameraResolutions().put(wc.getName(), wcr);
				}
			}
			
			String json = new GsonBuilder().setPrettyPrinting().create().toJson(nconfig);
			FileWriter fw = new FileWriter(file);
			fw.write(json);
			fw.flush();
			fw.close();
		}
		
		System.out.format("Loading config from %s\n", file.getAbsolutePath());
		
		config = new Gson().fromJson(new FileReader(file), CameraConfig.class);
		config.validate();
		
		Webcam.setAutoOpenMode(true);
		Webcam.addDiscoveryListener(instance);
		
		publisher = new ImageSender(config, this);
		
		try {
			publisher.open();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		List<Webcam> list = Webcam.getWebcams(3, TimeUnit.SECONDS);
		
		if(list.isEmpty()) {
			System.err.println("No cameras detected!");
		}
		
		for(Webcam camera : list) {
			initCamera(camera);
		}

		exec.submit(new Runnable() {
			public void run() {
				try {
					System.out.println("Waiting for camera initializations...");
					while(!Thread.currentThread().isInterrupted()) {
						synchronized(this) {
							wait();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
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
				System.out.format("Webcam:%s supported view-size:[%d X %d] resolution:%s\n", camera.getName(),  w, h, RESOLUTIONS.get(String.format("%d.%d", w, h)));
			}
		}
		
		sizes = camera.getCustomViewSizes();
		if(null != sizes && sizes.length >0) {
			for(Dimension d : sizes) {
				int w = (int)d.getWidth();
				int h = (int)d.getHeight();
				System.out.format("Webcam:%s custom view-size:[%d X %d] resolution:%s\n", camera.getName(), w, h, RESOLUTIONS.get(String.format("%d.%d", w, h)));
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
	
	public void handleMessage(String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, int messageId, JSONObject data) {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		
		try {
			CameraApplication.instance.init();
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
