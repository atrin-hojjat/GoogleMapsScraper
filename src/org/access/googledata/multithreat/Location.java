package org.access.googledata.multithreat;

import java.awt.Label;

public class Location {
	private final Point point;
	private final int raduis;
	
	public Location(double lat,double lng,int raduis){
		this.point = new Point(lat, lng);
		this.raduis = raduis;
	}
	
	public Location(Point point,int raduis){
		this.point =point;
		this.raduis = raduis;
	}
	
	public double getLat() {
		return point.getLat();
	}
	
	public double getLng() {
		return point.getLng();
	}

	public int getRaduis() {
		// TODO Auto-generated method stub
		return raduis;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("\nLocation : \n\tLAT : "+point.getLat()+" \n\t LNG :"+point.getLng());
	}
}
