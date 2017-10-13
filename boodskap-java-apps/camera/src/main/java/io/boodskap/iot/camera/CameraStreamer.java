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
	private State state;
	private CameraConfig config;
	private long pauseTill;
	private long lastAction;
	
	protected CameraStreamer(CameraApplication app, Webcam camera) {
		
		this.app = app;
		this.camera = camera;
		
		switch(app.getConfig().getDefaultState()) {
		case WAIT:
			state = State.STOPPED;
			break;
		case RUN:
		default:
			state = State.RUNNING;
			break;
		}
	}
	
	public void stop() {
		state = State.STOPPED;
		synchronized(this) {
			notify();
		}
	}
	
	public void start() {
		state = State.RUNNING;
		synchronized(this) {
			notify();
		}
	}
	
	public void pause(long millis) {
		pauseTill = System.currentTimeMillis() + millis;
		state = State.PAUSED;
		synchronized(this) {
			notify();
		}
	}
	
	public void shutdown() {
		state = State.SHUTDOWN;
		synchronized(this) {
			notify();
		}
	}

	public void run() {
		
		while(state != State.SHUTDOWN && state != State.TERMINATED && !Thread.currentThread().isInterrupted()) {
			
			try {
				
				while(state == State.STOPPED) {
					synchronized(this) {
						System.out.format("Camera %s stopped, waiting...\n", camera.getName());
						wait();
						System.out.format("Camera %s started, running...\n", camera.getName());
					}
				}
				
				while(state == State.PAUSED) {
					long remaining = (pauseTill - System.currentTimeMillis()); 
					if(remaining <= 0) {
						state = State.RUNNING;
					}else {
						System.out.format("Camera %s paused, sleeping...\n", camera.getName());
						Thread.sleep(remaining);
						System.out.format("Camera %s started, running...\n", camera.getName());
					}
				}
				
				long interval = config.getMode() == Mode.SNAP ? config.getInterval() : (60000 / config.getFramePerMinite());
				long remaining = (System.currentTimeMillis() - lastAction);
				long wait = interval-remaining;
				
				if(lastAction > 0 && wait > 0){
					Thread.sleep(wait);
				}
				
				synchronized(this) {
					
					BufferedImage image = camera.getImage();
					
					if(null == image) {
						System.err.format("Unable to grab image, camera %S may have been closed/disconnected", camera.getName());
						lastAction = System.currentTimeMillis();
						pause(3000);
						continue;
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
					
				}
				
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
