package org.nasso.engine;

import javafx.scene.input.MouseButton;

public class MouseInfo {
	private float x = 0;
	private float y = 0;
	
	private MouseButton btn;
	
	public MouseInfo(float x, float y, MouseButton btn) {
		this.x = x;
		this.y = y;
		this.btn = btn;
	}

	public MouseButton getBtn() {
		return btn;
	}

	public float getY() {
		return y;
	}

	public float getX() {
		return x;
	}
}
