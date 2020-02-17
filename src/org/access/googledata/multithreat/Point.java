package org.access.googledata.multithreat;

public class Point {
	private final double lat;
	private final double lng;
	
	public Point(double lat,double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLng() {
		return lng;
	}
}
