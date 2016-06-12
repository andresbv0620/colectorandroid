package colector.co.com.collector.persistence;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import colector.co.com.collector.settings.AppSettings;

public class DriverSQL{

	protected SQLiteHelper sqLiteHelper;
	protected Context ctx;
	
	public DriverSQL(Context ctx) {
		this.ctx = ctx;
	}

	public SQLiteDatabase getDBRead() throws SQLException {
		sqLiteHelper = new SQLiteHelper(this.ctx, null);
		return sqLiteHelper.getReadableDatabase(); 
	}

	public SQLiteDatabase getDBWrite() throws SQLException {
		sqLiteHelper = new SQLiteHelper(this.ctx, null);
		return sqLiteHelper.getWritableDatabase(); 
	}
	
	public void close() {
		this.sqLiteHelper.close();
	}

	/**
	 * Delete all entity
	 */
	public void deleteAll(String table) {
		SQLiteDatabase db = getDBWrite();
		db.delete(table, null, null);
		close();
	}
	
	
	public class SQLiteHelper extends SQLiteOpenHelper {
		
		public SQLiteHelper(Context context, CursorFactory factory) {
			super(context, AppSettings.DB_NAME, factory,AppSettings.DB_VERSION);
//			super(context, "/storage/sdcard0/DB_DMS_MOBILE.db", factory, AppSettings.DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("create table if not exists TBL_LOGIN ( ID  INTEGER ,  COLLECTOR_NAME  TEXT,TABLET_ID  TEXT,TOKEN  TEXT,PASSWORD  TEXT,COLLECTOR_ID  INTEGER, PRIMARY KEY (ID, COLLECTOR_ID));");
			db.execSQL("create table if not exists TBL_SURVEY ( ID  INTEGER ,  NAME  TEXT,DESCRIPTION  TEXT,PRECARGADO  TEXT, PRIMARY KEY (ID));");
			db.execSQL("create table if not exists TBL_SECTION ( ID  INTEGER ,  NAME  TEXT,DESCRIPTION  TEXT, SURVEY INTEGER, PRIMARY KEY (ID));");
			db.execSQL("create table if not exists TBL_QUESTION ( ID  INTEGER ,TYPE  INTEGER,  NAME  TEXT,DESCRIPTION  TEXT , DEFECTO  TEXT,MIN  TEXT, MAX  TEXT, REQUIRED  TEXT, VALIDACION TEXT, DEFECTO_PREVIO TEXT, SOLO_LECTURA  TEXT, OCULTO  TEXT, ORDEN  TEXT, SECTION  INTEGER,  PRIMARY KEY (ID));");

			db.execSQL("create table if not exists TBL_RESPONSE ( ID  INTEGER ,  VALUE  TEXT, QUESTION  INTEGER , PRIMARY KEY (ID));");
			db.execSQL("create table if not exists TBL_RESPONSE_COMPLEX ( ID  TEXT , QUESTION  INTEGER , PRIMARY KEY (ID));");
			db.execSQL("create table if not exists TBL_RESPONSE_COMPLEX_OPTION ( ID  INTEGER , LABEL  TEXT, VALUE  TEXT,TYPE  INTEGER, COMPLEX  INTEGER,PRIMARY KEY (ID, COMPLEX) );");
			db.execSQL("create table if not exists TBL_RESPONSE_ATTRIBUTES ( ID  INTEGER , LABEL  TEXT,TYPE  INTEGER, QUESTION  INTEGER, PRIMARY KEY (ID));");
			db.execSQL("CREATE TABLE IF NOT EXISTS TBL_SURVEY_INSTANCE ( ID  INTEGER PRIMARY KEY AUTOINCREMENT, ID_SURVEY  INTEGER, DATE_INSTANCE  TEXT,LATITUDE  TEXT,LONGITUDE  TEXT, HORAINI  TEXT,HORAFIN  TEXT,STATUS TEXT NOT NULL DEFAULT 'FALSE');");
			db.execSQL("CREATE TABLE IF NOT EXISTS TBL_SURVEY_INSTANCE_DETAIL ( ID  INTEGER PRIMARY KEY AUTOINCREMENT, ID_INSTANCE  INTEGER,ID_QUESTION  INTEGER, ANSWER TEXT);");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS TBL_SURVEY");
			db.execSQL("DROP TABLE IF EXISTS TBL_SECTION");
			db.execSQL("DROP TABLE IF EXISTS TBL_QUESTION");
			db.execSQL("DROP TABLE IF EXISTS TBL_RESPONSE");
			db.execSQL("DROP TABLE IF EXISTS TBL_RESPONSE_COMPLEX");
			db.execSQL("DROP TABLE IF EXISTS TBL_RESPONSE_COMPLEX_OPTION");
			db.execSQL("DROP TABLE IF EXISTS TBL_RESPONSE_ATTRIBUTES");
			db.execSQL("DROP TABLE IF EXISTS TBL_SURVEY_INSTANCE");
			db.execSQL("DROP TABLE IF EXISTS TBL_SURVEY_INSTANCE_DETAIL");
	        onCreate(db);
		}
	}

}
