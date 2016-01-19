package org.nasso.engine;

import javafx.scene.input.KeyCode;

public class KeyInfo {
	private KeyCode keyCode;
	private String keyText;
	private String keyChar;
	
	public KeyInfo(KeyCode keyCode, String keyText, String keyChar) {
		this.keyCode = keyCode;
		this.keyText = keyText;
		this.keyChar = keyChar;
	}

	public KeyCode getKeyCode() {
		return keyCode;
	}
	
	public String getKeyText() {
		return keyText;
	}

	public String getKeyChar() {
		return keyChar;
	}
}
