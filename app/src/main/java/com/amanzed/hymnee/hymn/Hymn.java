package com.amanzed.hymnee.hymn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;


public class Hymn implements Serializable {
	String denom, title, info;
	int id, numb;
	public String getDenom() {
		return denom;
	}
	public void setDenom(String denom) {
		this.denom = denom;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNumb() {
		return numb;
	}
	public void setNumb(int numb) {
		this.numb = numb;
	}

	public static Comparator<Hymn> HymnNumberComparator
			= new Comparator<Hymn>() {

		public int compare(Hymn Hymn1, Hymn Hymn2) {

			int num1 = Hymn1.getNumb();
			int num2 = Hymn2.getNumb();

			//ascending order
			return num1 - num2;
		}

	};

}
