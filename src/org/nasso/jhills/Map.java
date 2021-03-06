package org.nasso.jhills;

import org.nasso.engine.Perlin;

public class Map {
	private long seed = 0;
	
	private float minHeight = 0.50f;
	private float maxHeight = 10.80f;
	
	private float xSpace = 2.56f;
	private float preciseXSpace = 2.56f;
	
	private float[] heights;
	private float[] preciseHeights;
	
	public Map(int length, int precision, float xSpace, long seed){
		this.seed = seed;
		
		this.heights = Perlin.genPerlin(length, 1, seed);
		this.preciseHeights = Perlin.sinusInterpolate(this.heights, precision);
		
		this.xSpace = xSpace;
		this.preciseXSpace = xSpace / precision;
	}

	public float[] getHeights() {
		return heights;
	}

	public float[] getPreciseHeights() {
		return preciseHeights;
	}

	public float getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(float minHeight) {
		this.minHeight = minHeight;
	}

	public float getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(float maxHeight) {
		this.maxHeight = maxHeight;
	}

	public float getXSpace() {
		return xSpace;
	}

	public void setxSpace(float xSpace) {
		this.xSpace = xSpace;
	}

	public float getPreciseXSpace() {
		return preciseXSpace;
	}

	public void setPreciseXSpace(float preciseXSpace) {
		this.preciseXSpace = preciseXSpace;
	}

	public float getMeterWidth() {
		return (this.heights.length-1) * this.xSpace;
	}
	
	public float getPreciseHeightAt(int where){
		if(where < 0){
			where = 0;
		}
		if(where >= preciseHeights.length){
			where = preciseHeights.length-1;
		}
		
		return this.getMinHeight() + (preciseHeights[where] * (this.getMaxHeight() - this.getMinHeight()));
	}

	public long getSeed() {
		return seed;
	}

	public float getHeightAt(int where) {
		return this.getMinHeight() + (heights[where] * (this.getMaxHeight() - this.getMinHeight()));
	}
}
