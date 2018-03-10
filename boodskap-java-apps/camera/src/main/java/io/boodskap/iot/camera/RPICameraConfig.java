package io.boodskap.iot.camera;

import io.boodskap.iot.camera.rpi.AWB;
import io.boodskap.iot.camera.rpi.DRC;
import io.boodskap.iot.camera.rpi.Exposure;
import io.boodskap.iot.camera.rpi.ImageEffect;
import io.boodskap.iot.camera.rpi.MeteringMode;
import io.boodskap.iot.camera.rpi.Rotation;

public class RPICameraConfig {

	private AWB awb = AWB.NONE;
	private DRC drc = DRC.NONE;
	private Exposure exposure = Exposure.NONE;
	private ImageEffect imageEffect = ImageEffect.NONE;
	private MeteringMode meteringMode = MeteringMode.NONE;
	private Rotation rotation = Rotation.NONE;
	private int cameraId = 0;
	private int iso = -1;
	private int saturation = -1;
	private int brightness = 50;
	private int contrast = 0;
	private int sharpness = 0;
	private int shutterSpeed = -1;
	private boolean verticalFlip = false;
	private boolean horizontalFlip = false;
	private int[] colorEffect = new int[] {-1, -1};

	public AWB getAwb() {
		return awb;
	}

	public void setAwb(AWB awb) {
		this.awb = awb;
	}

	public DRC getDrc() {
		return drc;
	}

	public void setDrc(DRC drc) {
		this.drc = drc;
	}

	public Exposure getExposure() {
		return exposure;
	}

	public void setExposure(Exposure exposure) {
		this.exposure = exposure;
	}

	public ImageEffect getImageEffect() {
		return imageEffect;
	}

	public void setImageEffect(ImageEffect imageEffect) {
		this.imageEffect = imageEffect;
	}

	public MeteringMode getMeteringMode() {
		return meteringMode;
	}

	public void setMeteringMode(MeteringMode meteringMode) {
		this.meteringMode = meteringMode;
	}

	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

	public int getCameraId() {
		return cameraId;
	}

	public void setCameraId(int cameraId) {
		this.cameraId = cameraId;
	}

	public int getIso() {
		return iso;
	}

	public void setIso(int iso) {
		this.iso = iso;
	}

	public int getSaturation() {
		return saturation;
	}

	public void setSaturation(int saturation) {
		this.saturation = saturation;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getContrast() {
		return contrast;
	}

	public void setContrast(int contrast) {
		this.contrast = contrast;
	}

	public int getSharpness() {
		return sharpness;
	}

	public void setSharpness(int sharpness) {
		this.sharpness = sharpness;
	}

	public int getShutterSpeed() {
		return shutterSpeed;
	}

	public void setShutterSpeed(int shutterSpeed) {
		this.shutterSpeed = shutterSpeed;
	}

	public boolean isVerticalFlip() {
		return verticalFlip;
	}

	public void setVerticalFlip(boolean verticalFlip) {
		this.verticalFlip = verticalFlip;
	}

	public boolean isHorizontalFlip() {
		return horizontalFlip;
	}

	public void setHorizontalFlip(boolean horizontalFlip) {
		this.horizontalFlip = horizontalFlip;
	}

	public int[] getColorEffect() {
		return colorEffect;
	}

	public void setColorEffect(int[] colorEffect) {
		this.colorEffect = colorEffect;
	}

}
