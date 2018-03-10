package io.boodskap.iot.camera;

import java.awt.image.BufferedImage;

public abstract class Camera {

	public abstract String getName();

	public abstract BufferedImage getImage() throws Exception;

}
