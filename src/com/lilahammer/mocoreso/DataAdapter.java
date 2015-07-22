package com.lilahammer.mocoreso;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataAdapter {

	// Variables pour accéder à la base de données
	private BaseDonnees datamo; // définie dans une classe interne
	private SQLiteDatabase sqliteDatabase;
	private Observation observation;
	private ArrayList<Observation> MyObservations;
	

	// Constructeur
	public DataAdapter(Context context) {
		datamo = new BaseDonnees(context);
	}

	// insertion de données
	public void insertObservation(String name, double lat, double lon,
			String currentDateandTime, String path) {

		sqliteDatabase = datamo.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(BaseDonnees.NAME, name);
		contentValues.put(BaseDonnees.LAT, lat);
		contentValues.put(BaseDonnees.LONG, lon);
		contentValues.put(BaseDonnees.DATE, currentDateandTime);
		contentValues.put(BaseDonnees.PATH_PHOTO, path);

		sqliteDatabase.insert(BaseDonnees.TABLE_NAME,
				BaseDonnees.LAT, contentValues);
		sqliteDatabase.close();
	}

	// supprimer les observations par name
	public int deleteData(String oldname) {
		sqliteDatabase = datamo.getWritableDatabase();
		String[] whereArgs = { oldname };
		int count = sqliteDatabase.delete(BaseDonnees.TABLE_NAME,
				BaseDonnees.NAME + " =? ", whereArgs);
		return count;
	}

	// supprimer les dates
	public int deleteDate(String date) {
		sqliteDatabase = datamo.getWritableDatabase();
		String[] whereArgs = { date };
		int count = sqliteDatabase.delete(BaseDonnees.TABLE_DATE,
				BaseDonnees.DATE_UNIQUE + " =? ", whereArgs);
		return count;
	}

	// mettre à jour les observations par name
	public int updatedata(String oldname, String name, double lat, double lon,
			String currentDateandTime, String path) {
		sqliteDatabase = datamo.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(BaseDonnees.NAME, name);
		contentValues.put(BaseDonnees.LAT, lat);
		contentValues.put(BaseDonnees.LONG, lon);
		contentValues.put(BaseDonnees.DATE, currentDateandTime);
		contentValues.put(BaseDonnees.PATH_PHOTO, path);
		String[] whereArgs = { oldname };
		int count = sqliteDatabase.update(BaseDonnees.TABLE_NAME,
				contentValues, BaseDonnees.NAME + " =? ", whereArgs);
		return count;
	}

	// récupérer les observations par name
	public Observation getObservationsByName(String oldname) {

		sqliteDatabase = datamo.getWritableDatabase();
		String[] columns = { BaseDonnees.NAME, BaseDonnees.DATE,
				BaseDonnees.LAT, BaseDonnees.LONG, BaseDonnees.PATH_PHOTO };
		Cursor cursor = sqliteDatabase.query(BaseDonnees.TABLE_NAME, columns,
				BaseDonnees.NAME + " = '" + oldname + "'", null, null, null,
				null);
		Observation observation = null;
		while (cursor.moveToNext()) {
			int index1 = cursor.getColumnIndex(BaseDonnees.NAME);
			String name = cursor.getString(index1);
			int index2 = cursor.getColumnIndex(BaseDonnees.LAT);
			double latitude = cursor.getDouble(index2);
			int index3 = cursor.getColumnIndex(BaseDonnees.LONG);
			double longitude = cursor.getDouble(index3);
			int index4 = cursor.getColumnIndex(BaseDonnees.PATH_PHOTO);
			String path = cursor.getString(index4);
			int index5 = cursor.getColumnIndex(BaseDonnees.DATE);
			String date = cursor.getString(index5);
			observation = new Observation(name, latitude, longitude, path, date);

		}
		cursor.close();
		sqliteDatabase.close();
		return observation;
	}

	// récupérer toutes les observations
	public ArrayList<Observation> getObservations() {

		sqliteDatabase = datamo.getWritableDatabase();
		String[] columns = { BaseDonnees.NAME, BaseDonnees.DATE,
				BaseDonnees.LAT, BaseDonnees.LONG, BaseDonnees.PATH_PHOTO };
		Cursor cursor = sqliteDatabase.query(BaseDonnees.TABLE_NAME, columns,
				null, null, null, null, null);
		MyObservations = new ArrayList<Observation>();
		while (cursor.moveToNext()) {
			int index1 = cursor.getColumnIndex(BaseDonnees.NAME);
			String name = cursor.getString(index1);
			int index2 = cursor.getColumnIndex(BaseDonnees.LAT);
			double latitude = cursor.getDouble(index2);
			int index3 = cursor.getColumnIndex(BaseDonnees.LONG);
			double longitude = cursor.getDouble(index3);
			int index4 = cursor.getColumnIndex(BaseDonnees.PATH_PHOTO);
			String path = cursor.getString(index4);
			int index5 = cursor.getColumnIndex(BaseDonnees.DATE);
			String date = cursor.getString(index5);
			observation = new Observation(name, latitude, longitude, path, date);
			MyObservations.add(observation);
		}
		cursor.close();
		sqliteDatabase.close();
		return MyObservations;
	}

	// récupérer les observations par date
	public ArrayList<String> getObservationsByDate(String currentDateandTime) {
		sqliteDatabase = datamo.getWritableDatabase();
		String[] columns = { BaseDonnees.NAME, BaseDonnees.DATE };
		Cursor cursor = sqliteDatabase.query(BaseDonnees.TABLE_NAME, columns,
				BaseDonnees.DATE + " = '" + currentDateandTime + "'", null,
				null, null, null);
		ArrayList<String> buffer = new ArrayList<String>();
		while (cursor.moveToNext()) {
			int index1 = cursor.getColumnIndex(BaseDonnees.NAME);
			String name = cursor.getString(index1);
			buffer.add(name);
		}
		cursor.close();
		sqliteDatabase.close();
		return buffer;
	}

	// récupère les dates d'observations
	public ArrayList<String> getAllDate() {
		sqliteDatabase = datamo.getWritableDatabase();
		Cursor cursor = sqliteDatabase.query(BaseDonnees.TABLE_DATE, null,
				null, null, null, null, null);
		ArrayList<String> buffer = new ArrayList<String>();
		while (cursor.moveToNext()) {
			int index1 = cursor.getColumnIndex(BaseDonnees.DATE_UNIQUE);
			String date = cursor.getString(index1);
			String annees = date.substring(0, 4);
			String mois = date.substring(4, 6);
			String jour = date.substring(6, 8);
			date = jour + "-" + mois + "-" + annees;
			buffer.add(date);
		}
		cursor.close();
		sqliteDatabase.close();
		return buffer;
	}

	// récupère la date correspondant à un index
	public String getDate(int index) {

		sqliteDatabase = datamo.getWritableDatabase();
		Log.d("d", "index" + index);
		String[] columns = { BaseDonnees.UID_2, BaseDonnees.DATE_UNIQUE };
		Cursor cursor = sqliteDatabase.query(BaseDonnees.TABLE_DATE, columns,
				BaseDonnees.UID_2 + " = '" + index + "'", null, null, null,
				null);
		String buffer = new String();
		while (cursor.moveToNext()) {
			int index1 = cursor.getColumnIndex(BaseDonnees.DATE_UNIQUE);
			String name = cursor.getString(index1);
			buffer = name;
		}
		cursor.close();
		sqliteDatabase.close();
		return buffer;
	}

	// récupere toutes les localisations
	public ArrayList<Observation> getAllLocalisation(String date) {
		sqliteDatabase = datamo.getWritableDatabase();
		ArrayList<Observation> myObservations = new ArrayList<Observation>();
		String[] columns = { BaseDonnees.DATE, BaseDonnees.NAME,
				BaseDonnees.LAT, BaseDonnees.LONG, BaseDonnees.PATH_PHOTO };
		Cursor cursor = sqliteDatabase.query(BaseDonnees.TABLE_NAME, columns,
				BaseDonnees.DATE + " = '" + date + "'", null, null, null, null);
		while (cursor.moveToNext()) {

			int index1 = cursor.getColumnIndex(BaseDonnees.NAME);
			String name = cursor.getString(index1);
			int index2 = cursor.getColumnIndex(BaseDonnees.LAT);
			double latitude = cursor.getDouble(index2);
			int index3 = cursor.getColumnIndex(BaseDonnees.LONG);
			double longitude = cursor.getDouble(index3);
			int index4 = cursor.getColumnIndex(BaseDonnees.PATH_PHOTO);
			String path = cursor.getString(index4);
			int index5 = cursor.getColumnIndex(BaseDonnees.DATE);
			String date2 = cursor.getString(index5);
			Observation observation = new Observation(name, latitude,
					longitude, path, date2);
			myObservations.add(observation);
		}
		cursor.close();
		sqliteDatabase.close();
		return myObservations;
	}

	// Définition de la base de données

	static class BaseDonnees extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "database_moco";
		private static final String TABLE_NAME = "observations";
		private static final String TABLE_DATE = "dates";
		private static final String UID = "_id";
		private static final String UID_2 = "_id2";
		private static final String NAME = "name";
		private static final String LAT = "latitude";
		private static final String LONG = "longitude";
		private static final String DATE = "date";
		private static final String DATE_UNIQUE = "date_unique";
		private static final String PATH_PHOTO = "path";
		private static final int DATABASE_VERSION = 1;
		private static final String CREATE_TABLE1 = "CREATE TABLE "
				+ TABLE_NAME + " (" + UID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
				+ " VARCHAR(255), " + DATE + " VARCHAR(255), " + PATH_PHOTO
				+ " VARCHAR(255), " + LAT + " DOUBLE," + LONG + " DOUBLE);";

		private static final String DROP_TABLE = "DROP TABLE IF EXISTS OBSERVATIONS";

		public BaseDonnees(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			try {
				db.execSQL(CREATE_TABLE1);
			} catch (SQLException e) {
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL(DROP_TABLE);
			onCreate(db);
		}
	}
}
