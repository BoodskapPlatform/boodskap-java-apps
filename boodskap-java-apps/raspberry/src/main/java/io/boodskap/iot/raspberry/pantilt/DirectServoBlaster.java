package io.boodskap.iot.raspberry.pantilt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DirectServoBlaster {
	
	public static final File DEV_FILE = new File("/dev/servoblaster");
	
	private final String pin;
	private final int min;
	private final int max;
	private final boolean invert;
	private int position;

	public DirectServoBlaster(String pin, int min, int max) {
		this(pin, min, max, false);
	}
	
	public DirectServoBlaster(String pin, int min, int max, boolean invert) {
		
		this.pin = pin;
		this.min = min;
		this.max = max;
		this.invert = invert;
		
		System.out.format("PIN: %s, min: %d, max: %d, inverted: %s\n", pin, invert ? max:min, invert? min:max, invert);
		
		if(!DEV_FILE.exists()) {
			throw new IllegalArgumentException("ServoBlaster not found, https://github.com/richardghirst/PiBits/tree/master/ServoBlaster");
		}
	}
	
	protected void write(int value) throws IOException {
		
		position = value;
		
		String command = String.format("%s=%d\n", pin, value);
		FileWriter fout = new FileWriter(DEV_FILE);
		try {
			fout.write(command);
			fout.flush();
		}finally {
			fout.close();
		}
	}

	public void moveToBegin() throws IOException {
		System.out.println("move to begin");
		write(invert ? max : min);
	}
	
	public void moveToEnd() throws IOException {
		System.out.println("move to end");
		write(invert ? min : max);
	}
	
	public void moveToCenter() throws IOException {
		System.out.println("move to center");
		write(max/2);
	}
	
	public void moveTo(int value) throws IOException {
		System.out.format("move to %d\n", value);
		write(value);
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public static void main(String[] args) throws Exception {
		String pin = args[0];
		int min = Integer.valueOf(args[1]);
		int max = Integer.valueOf(args[2]);
		boolean invert = false;
		if(args.length >= 4) {
			invert = Boolean.valueOf(args[3]);
		}
		DirectServoBlaster servo = new DirectServoBlaster(pin, min, max, invert);
		servo.moveToBegin();
		Thread.sleep(1500);
		servo.moveToCenter();
		Thread.sleep(1500);
		servo.moveToEnd();
		Thread.sleep(1500);
		Random r = new Random();
		int value = min + r.nextInt((max-min) + 1);
		servo.moveTo(value);
		System.exit(0);
	}
}
