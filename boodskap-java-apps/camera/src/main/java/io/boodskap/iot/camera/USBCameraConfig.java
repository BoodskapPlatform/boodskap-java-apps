package io.boodskap.iot.camera;

import io.boodskap.iot.camera.CameraConfig.DiscoveryMode;

public class USBCameraConfig {

	private DiscoveryMode discoverMode = DiscoveryMode.AUTO;
	private long initWait = 5000;
	
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

}
