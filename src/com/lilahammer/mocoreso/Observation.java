package com.lilahammer.mocoreso;

public class Observation {
	private String name;
	private double latitude;
	private double longitude;
	private String path;
	private String date;

	public Observation(String name, double latitude, double longitude,
			String path,String date) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.path = path;
		this.date =date;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getPath() {
		return path;
	}
	public String getDate(){
		return date;
	}
}
