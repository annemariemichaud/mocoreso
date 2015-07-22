package com.lilahammer.mocoreso;

public class Observation {
	private String name;
	private double latitude;
	private double longitude;
	private String path;
	private String date;

	public Observation(String name, double latitude, double longitude,
			String path, String date) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.path = path;
		this.date = date;
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

	public String getDate() {
		return date;
	}

	public int getSaisons() {
		int saison = 0;
		int mois = Integer.parseInt(date.substring(4, 6));
		int jour = Integer.parseInt(date.substring(6));
		if (mois >= 3 && mois <= 5 && jour >= 1 && jour <= 31)
			saison = 0;
		if (mois >= 6 && mois <= 9 && jour >= 1 && jour <= 31)
			saison = 1;
		if (mois >= 9 && mois <= 11 && jour >= 1 && jour <= 31)
			saison = 2;
		if (mois >= 12 && mois <= 2 && jour >= 1 && jour <= 31)
			saison = 3;
		return saison;
	}
}
