package io.boodskap.iot.camera;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.github.sarxos.webcam.Webcam;

import net.coobird.thumbnailator.Thumbnails;

public class USBCamera extends Camera {
	
	private CameraConfig config;
	private Webcam camera;
	
	public USBCamera(CameraConfig config, Webcam camera) {
		this.config = config;
		this.camera = camera;
	}

	@Override
	public String getName() {
		return camera.getName();
	}

	@Override
	public BufferedImage getImage() throws IOException {
		
		BufferedImage oimage =  camera.getImage();
		
		return Thumbnails.of(oimage)
		        .size(config.getImageWidth(), config.getImageHeight())
		        .asBufferedImage();
		
	}

}
