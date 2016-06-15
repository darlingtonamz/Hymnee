package com.amanzed.hymnee.hymn;

public class Verse {
	String  body;
	int  id, hymnId, position;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHymnId() {
		return hymnId;
	}

	public void setHymnId(int hymnId) {
		this.hymnId = hymnId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
}
