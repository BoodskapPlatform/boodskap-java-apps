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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDriver;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.boodskap.iot.PublishChannel;

/**
 * Camera Application Configuration, stored in ${user.home}/.camera/config.json
 * 
 * @author Jegan Vincent
 *
 */
public class CameraConfig implements Serializable{
	
	private static final long serialVersionUID = 2838776972406711700L;

	static final File home = new File(String.format("%s%s.boodskap%scamera", System.getProperty("user.home"), File.separator, File.separator));
	static final File file = new File(home, "config.json");
	
	protected static final Map<String, WebcamResolution> RESOLUTIONS = new HashMap<String, WebcamResolution>();
	
	protected static final List<String> FORMATS = new ArrayList<String>();
	
	static {
		
		try{home.mkdirs();}catch(Exception ex) {}

		ImageIO.scanForPlugins();
		
		boolean driverLoaded = false;
		
		String driver = System.getProperty("camdriver"); //Example: java -Dcamdriver=com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver
		
		if(null != driver && !"".equals(driver.trim())) {
			System.out.format("Loading supplied driver: %s\n", driver);
			try {
				WebcamDriver wdriver = (WebcamDriver) Class.forName(driver).newInstance();
				Webcam.setDriver(wdriver);
				driverLoaded = true;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.format("Loading driver: %s failed\n", driver);
				//System.exit(-1);
			}
		}
		
		if(!driverLoaded && "arm".equals(System.getProperty("os.arch"))) {
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
		
		Webcam.setAutoOpenMode(true);
				
	}
	
	public static enum Mode{
		STREAM,
		SNAP
	}
	
	public static enum DiscoveryMode{
		AUTO,
		INIT
	}
	
	public static enum State{
		RUN,
		WAIT
	}
	
	private String domainKey="";
	private String apiKey="";
	private String deviceId="";
	private String deviceModel="BSKP-JCAM";
	private String firmwareVersion="1.0.0";
	private DiscoveryMode discoverMode = DiscoveryMode.AUTO;
	private long initWait = 5000;
	private Mode mode = Mode.SNAP;
	private State defaultState = State.RUN;
	private PublishChannel publisher = PublishChannel.MQTT;
	private long interval = 750;
	private int framePerMinite = 60;
	private String imageFormat = "jpeg";
	private String streamFormat = "mp4";
	private int imageWidth = 200;
	private int imageHeight = 200;
	private float imageQuality = 0.5F;
	private String mqttUrl = "tcp://mqtt.boodskap.io:1883";
	private String udpHost = "udp.boodskap.io";
	private int udpPort = 5555;
	private long heartbeat = 30000;
	private String httpUrl = "http://api.boodskap.io";
	private Map<String, WebcamResolution> cameraResolutions = new HashMap<String, WebcamResolution>();
	private List<WebcamResolution> availableResolutions = new ArrayList<WebcamResolution>();
	private List<String> availableFormats = new ArrayList<String>();

	protected CameraConfig() {
	}

	public void validate() throws Exception {
		
		if(null == mqttUrl || mqttUrl.trim().equals("")) throw new IllegalArgumentException("mqttUrl required");
		if(null == udpHost || udpHost.trim().equals("")) throw new IllegalArgumentException("udpHost required");
		if(udpPort <= 0) throw new IllegalArgumentException("udpPort must be > 0");
		if(heartbeat < 1000) throw new IllegalArgumentException("heartbeat must be >= 1000");
		if(null == httpUrl || httpUrl.trim().equals("")) throw new IllegalArgumentException("httpUrl required");
		if(null == domainKey || domainKey.trim().equals("")) throw new IllegalArgumentException("domainKey required");
		if(null == apiKey || apiKey.trim().equals("")) throw new IllegalArgumentException("apiKey required");
		if(null == deviceId || deviceId.trim().equals("")) throw new IllegalArgumentException("deviceId required");
		if(null == deviceModel || deviceModel.trim().equals("deviceModel")) throw new IllegalArgumentException("deviceModel required");
		if(null == firmwareVersion || firmwareVersion.trim().equals("")) throw new IllegalArgumentException("firmwareVersion required");
		if(null == mode) throw new IllegalArgumentException("mode required");
		if(null == publisher) throw new IllegalArgumentException("publisher required");
		if(interval < 10) throw new IllegalArgumentException("interval must be >= 10");
		if(framePerMinite > 2500) throw new IllegalArgumentException("framePerMinite must be <= 2500");
		if(null == imageFormat || imageFormat.trim().equals("")) throw new IllegalArgumentException("imageFormat required");
		if(null == streamFormat || streamFormat.trim().equals("")) throw new IllegalArgumentException("streamFormat required");
		
		Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix(imageFormat);
		if(!iter.hasNext()) throw new IllegalArgumentException(String.format("No image writer found for image format: %s", imageFormat));
		while(iter.hasNext()) {
			System.out.format("Writer for Image Format %s: %s\n", imageFormat, iter.next().getClass());
		}
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public int getFramePerMinite() {
		return framePerMinite;
	}

	public void setFramePerMinite(int framePerMinite) {
		this.framePerMinite = framePerMinite;
	}

	public String getImageFormat() {
		return imageFormat;
	}

	public void setImageFormat(String imageFormat) {
		this.imageFormat = imageFormat;
	}

	public String getStreamFormat() {
		return streamFormat;
	}

	public void setStreamFormat(String streamFormat) {
		this.streamFormat = streamFormat;
	}

	public String getMqttUrl() {
		return mqttUrl;
	}

	public void setMqttUrl(String mqttUrl) {
		this.mqttUrl = mqttUrl;
	}

	public String getDomainKey() {
		return domainKey;
	}

	public void setDomainKey(String domainKey) {
		this.domainKey = domainKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public Map<String, WebcamResolution> getCameraResolutions() {
		return cameraResolutions;
	}

	public void setCameraResolutions(Map<String, WebcamResolution> cameraResolutions) {
		this.cameraResolutions = cameraResolutions;
	}

	public List<WebcamResolution> getAvailableResolutions() {
		return availableResolutions;
	}

	public void setAvailableResolutions(List<WebcamResolution> availableResolutions) {
		this.availableResolutions = availableResolutions;
	}

	public List<String> getAvailableFormats() {
		return availableFormats;
	}

	public void setAvailableFormats(List<String> availableFormats) {
		this.availableFormats = availableFormats;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public float getImageQuality() {
		return imageQuality;
	}

	public void setImageQuality(float imageQuality) {
		this.imageQuality = imageQuality;
	}

	public PublishChannel getPublisher() {
		return publisher;
	}

	public void setPublisher(PublishChannel publisher) {
		this.publisher = publisher;
	}

	public String getUdpHost() {
		return udpHost;
	}

	public void setUdpHost(String udpHost) {
		this.udpHost = udpHost;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	public static boolean exists() {
		return file.exists();
	}


	public State getDefaultState() {
		return defaultState;
	}

	public void setDefaultState(State defaultState) {
		this.defaultState = defaultState;
	}

	public DiscoveryMode getDiscoverMode() {
		return discoverMode;
	}

	public void setDiscoverMode(DiscoveryMode discoverMode) {
		this.discoverMode = discoverMode;
	}

	public long getInitWait() {
		return initWait;
	}

	public void setInitWait(long initWait) {
		this.initWait = initWait;
	}

	public long getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(long heartbeat) {
		this.heartbeat = heartbeat;
	}

	public static void create() throws Exception {
		CameraConfig nconfig = new CameraConfig(); 
		
		System.out.println("Creating default configuration, please wait...");
		
		nconfig.getAvailableResolutions().addAll(RESOLUTIONS.values());
		nconfig.getAvailableFormats().addAll(FORMATS);
		
		List<Webcam> list = new ArrayList<>();
		
		try {
			List<Webcam> tlist = Webcam.getWebcams(10, TimeUnit.SECONDS);
			list.addAll(tlist);
		} catch (Exception e) {
			if(e instanceof TimeoutException) {
				System.err.println(e.getMessage());
			}else {
				e.printStackTrace();
			}
		}
		
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

	public static CameraConfig load() throws Exception {
		System.out.format("Loading config from %s\n", file.getAbsolutePath());
		CameraConfig config = new Gson().fromJson(new FileReader(file), CameraConfig.class);
		config.validate();
		return config;
	}
	
	public void save() throws IOException {
		String json = new GsonBuilder().setPrettyPrinting().create().toJson(this);
		FileWriter writer = new FileWriter(file);
		writer.write(json);
		writer.flush();
		writer.close();
	}

}
