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

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;

import io.boodskap.iot.camera.CameraConfig.Mode;

/**
 * A streamer instance will be spawned for every cameras found
 * 
 * @author Jegan Vincent
 *
 */
public class CameraStreamer implements Runnable {
	
	public static enum State{
		RUNNING,
		STOPPED,
		PAUSED,
		SHUTDOWN,
		TERMINATED,
	}

	private final CameraApplication app;
	private final Webcam camera;
	private State state = State.RUNNING;
	private CameraConfig config;
	private long pauseTill;
	private long lastAction;
	
	protected CameraStreamer(CameraApplication app, Webcam camera) {
		this.app = app;
		this.camera = camera;
	}
	
	public void stop() {
		state = State.STOPPED;
	}
	
	public void pause(long millis) {
		pauseTill = System.currentTimeMillis() + millis;
		state = State.PAUSED;
	}
	
	public void shutdown() {
		state = State.SHUTDOWN;
	}

	public void run() {
		
		while(state != State.SHUTDOWN && state != State.TERMINATED && !Thread.currentThread().isInterrupted()) {
			
			try {
				
				while(state == State.STOPPED) {
					Thread.sleep(300);
					continue;
				}
				
				while(state == State.PAUSED) {
					if((System.currentTimeMillis() - pauseTill) > 0) {
						state = State.RUNNING;
					}else {
						Thread.sleep(300);
					}
				}
				
				long interval = config.getMode() == Mode.SNAP ? config.getInterval() : (60 / config.getFramePerMinite());
				
				if((interval > 0) && ((System.currentTimeMillis() - lastAction) < interval)){
					Thread.sleep(300);
					continue;
				}
				
				BufferedImage image = camera.getImage();
				
				if(null == image) {
					throw new Exception("Unable to grab image, camera may have been stopped/disconnected");
				}
				
				switch(config.getMode()) {
				case SNAP:
					app.sendSnap(camera, image);
					break;
				case STREAM:
					app.sendStream(camera, image);
					break;
				}
				
				lastAction = System.currentTimeMillis();
				
			} catch (Exception e) {
				
				if(state == State.RUNNING) {
					e.printStackTrace();
				}
				
				state = State.TERMINATED;
			}
		}
		
		System.out.format("Camera %s stopped, state:%s\n", camera.getName(), state);
		
		app.streamingStopped(this, camera);
	}

	public CameraConfig getConfig() {
		return config;
	}

	public void setConfig(CameraConfig config) {
		this.config = config;
	}

}
