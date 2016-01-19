package org.nasso.engine;

import java.util.Random;

public class Perlin {
	private static float getInterpolatedValue(float[] arr, float i){
		if(i < 0){
			return arr[0];
		}
		
		if(i >= arr.length){
			return arr[arr.length-1];
		}
		
		if(i % 1 == 0){
			return arr[(int) i];
		}
		
		int floor = (int) i;
		
		float prevValue = arr[floor];
		float nextValue = prevValue;
		
		if(floor + 1 < arr.length){
			nextValue = arr[floor+1];
		}
		
		float frac = i - floor;
		
		float cos = (float) ((1 - Math.cos(frac * Math.PI)) * 0.5);
		
		return prevValue * (1 - cos) + nextValue * cos;
	}
	
	public static float[] sinusInterpolate(final float[] arr, int precision){
		if(precision <= 1){
			return arr;
		}
		
		int newLength = arr.length * precision;
		float[] interpolated = new float[newLength];
		
		for(int i = 0; i < newLength; i++){
			interpolated[i] = getInterpolatedValue(arr, ((float) i / (float) newLength) * (float) arr.length);
		}
		
		return interpolated;
	}
	
	public static float[] genPerlin(int length, int precision, long seed){
		// Generate randoms
		Random rand = new Random(seed);
		float[] rawRand = new float[length];
		
		for(int i = 0; i < length; i++){
			rawRand[i] = rand.nextFloat();
		}
		
		// Generate perlin
		float[] perlin = new float[length];
		
		for(int i = 0; i < length; i++){
			float now = rawRand[i];
			float before = now;
			float after = now;

			if(i > 0){
				before = rawRand[i-1];
			}
			
			if(i < rawRand.length-1){
				after = rawRand[i+1];
			}
			
			perlin[i] = (before / 8) + (now / 4) + (after / 8);
		}
		
		// Interpolate for more precision if needed
		return sinusInterpolate(perlin, precision);
	}
}
