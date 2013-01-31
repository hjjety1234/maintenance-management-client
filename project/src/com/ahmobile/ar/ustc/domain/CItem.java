package com.ahmobile.ar.ustc.domain;

public class CItem {
	private String value = "";
	private String text = "";

	public CItem() {
		value = "";
		text = "";
	}

	public CItem(String _value, String _text) {
		value = _value;
		text = _text;
	}

	@Override
	public String toString() {

		return text;
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}
}
