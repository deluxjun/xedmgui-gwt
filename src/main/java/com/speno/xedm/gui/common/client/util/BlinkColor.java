package com.speno.xedm.gui.common.client.util;

import com.google.gwt.animation.client.Animation;

public abstract class BlinkColor<T> extends Animation {

	protected T element;
	private int[] increment = new int[3];
	private int[] sourceRGB;
	private int[] targetRGB;

	public BlinkColor(T element) {
		this.element = element;
	}
	
	@Override
	protected void onUpdate(double progress) {
		String color = "#"; 
		for (int j = 0; j < increment.length; j++) {
			String hex = Integer.toHexString((int)(sourceRGB[j] + progress * increment[j]));
			if (hex.length() > 2){
				hex = hex.substring(hex.length()-2, hex.length());
			} else if (hex.length() < 2){
				hex = "0" + hex;
			}
			color += hex;
		}
		job(color);
	}

	@Override
	protected void onComplete() {
		super.onComplete();

		String color = "#";
		for (int i = 0; i < targetRGB.length; i++) {
			String hex = Integer.toHexString((int)(targetRGB[i]));
			if (hex.length() > 2){
				hex = hex.substring(hex.length()-2, hex.length());
			}
			color += hex;
		}
		lastJob(color);
	}

	public void animate(int duration, String source, String target) {
		this.cancel();
		
		int[] sourceRGB = new int[3];
		int[] targetRGB = new int[3];
		sourceRGB[0] = Integer.parseInt(source.substring(0, 2), 16);
		sourceRGB[1] = Integer.parseInt(source.substring(2, 4), 16);
		sourceRGB[2] = Integer.parseInt(source.substring(4, 6), 16);
		targetRGB[0] = Integer.parseInt(target.substring(0, 2), 16);
		targetRGB[1] = Integer.parseInt(target.substring(2, 4), 16);
		targetRGB[2] = Integer.parseInt(target.substring(4, 6), 16);
		this.sourceRGB = sourceRGB;
		this.targetRGB = targetRGB;
		try {
			for (int i = 0; i < increment.length; i++) {
				increment[i] = targetRGB[i] - sourceRGB[i];
			}
				
			run(duration);
		} catch (Exception e) {
			// set opacity directly
			onComplete();
		}
	}

	protected abstract void job(String value);
	protected abstract void lastJob(String value);
}
