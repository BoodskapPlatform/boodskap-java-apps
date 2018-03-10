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

import io.boodskap.iot.BaseConfig;

/**
 * Camera Application Configuration, stored in ${user.home}/.boodskap/CameraConfig.json
 * 
 * @author Jegan Vincent
 *
 */
public class CameraConfig extends BaseConfig implements Serializable{
	
	private static final long serialVersionUID = 2838776972406711700L;

	protected static final Map<String, WebcamResolution> RESOLUTIONS = new HashMap<String, WebcamResolution>();
	protected static final List<String> FORMATS = new ArrayList<String>();
	
	private static final String DEVICE_MODEL = "BSKP-JCAM";
	private static final String FIRMWARE_VERSION = "1.0.0";
	
	private Mode mode = Mode.SNAP;
	private State defaultState = State.RUN;
	private long interval = 750;
	private int framePerMinite = 60;
	private ImageEncoding imageFormat = ImageEncoding.JPG;
	private String streamFormat = "mp4";
	private int imageWidth = 200;
	private int imageHeight = 200;
	private int imageQuality = 75;
	private CameraType cameraType = CameraType.USB;
	private USBCameraConfig usb = new USBCameraConfig();
	private RPICameraConfig rpi = new RPICameraConfig();
	private Map<String, WebcamResolution> cameraResolutions = new HashMap<String, WebcamResolution>();
	private List<WebcamResolution> availableResolutions = new ArrayList<WebcamResolution>();
	private List<String> availableFormats = new ArrayList<String>();

	static {
		
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
	
	public static enum CameraType{
		USB,
		RPI
	}
	
	protected CameraConfig() {
	}

	public void validate() throws Exception {
		
		super.validate();
		
		if(null == mode) throw new IllegalArgumentException("mode required");
		if(interval < 10) throw new IllegalArgumentException("interval must be >= 10");
		if(framePerMinite > 2500) throw new IllegalArgumentException("framePerMinite must be <= 2500");
		if(null == imageFormat) throw new IllegalArgumentException("imageFormat required");
		if(null == streamFormat || streamFormat.trim().equals("")) throw new IllegalArgumentException("streamFormat required");
		
		Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix(imageFormat.name().toLowerCase());
		if(!iter.hasNext()) throw new IllegalArgumentException(String.format("No image writer found for image format: %s", imageFormat));
		while(iter.hasNext()) {
			System.out.format("Writer for Image Format %s: %s\n", imageFormat, iter.next().getClass());
		}
	}

	public CameraType getCameraType() {
		return cameraType;
	}

	public void setCameraType(CameraType cameraType) {
		this.cameraType = cameraType;
	}

	public USBCameraConfig getUsb() {
		return usb;
	}

	public void setUsb(USBCameraConfig usb) {
		this.usb = usb;
	}

	public RPICameraConfig getRpi() {
		return rpi;
	}

	public void setRpi(RPICameraConfig rpi) {
		this.rpi = rpi;
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

	public ImageEncoding getImageFormat() {
		return imageFormat;
	}

	public void setImageFormat(ImageEncoding imageFormat) {
		this.imageFormat = imageFormat;
	}

	public String getStreamFormat() {
		return streamFormat;
	}

	public void setStreamFormat(String streamFormat) {
		this.streamFormat = streamFormat;
	}

	public String getDeviceModel() {
		return DEVICE_MODEL;
	}

	public String getFirmwareVersion() {
		return FIRMWARE_VERSION;
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

	public int getImageQuality() {
		return imageQuality;
	}

	public void setImageQuality(int imageQuality) {
		this.imageQuality = imageQuality;
	}

	public State getDefaultState() {
		return defaultState;
	}

	public void setDefaultState(State defaultState) {
		this.defaultState = defaultState;
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
		
		nconfig.save(CameraConfig.class);
		
	}

}
