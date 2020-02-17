package org.access.googledata.multithreat;

import java.io.UnsupportedEncodingException;

public class Result {
	private String name = "";
	private String address = "";
	private String latitude;
	private String longitude;
	private String types;
	private String icon;
	private String id;
	private String place_id;
	
	public Result(String name,String address,String latitude,String longitude,String types,String icon,String id ,String place_id){
		try {
			this.name = new String(name.getBytes(),"UTF-8");
			this.address = new String(address.getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		this.latitude = latitude;
		this.longitude = longitude;
		this.types =types;
		this.icon = icon;
		this.id = id;
		this.place_id = place_id;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPlace_id() {
		return place_id;
	}
	public void setPlace_id(String place_id) {
		this.place_id = place_id;
	}
}
