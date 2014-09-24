package net.guimi.ANA;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteGestor {
	private Context miContexto;
	// Este es un valor propio, por si modificando el juego modificamos la BDD
    private static final int VERSION_DB = 1;
	private static final String BASE_DE_DATOS = "guimiANA.db";
    private static final String TABLA = "puntuaciones";
   	private SQLiteDatabase miDB;
	private SQLiteStatement miSentencia;
	PuntuacionesOH miPuntuacionesOH = null;

	// Definimos consultas estáticas para la creación de la tabla y la inserción de datos
	private static final String CREA_TABLA =
			"CREATE TABLE " + TABLA + "(_id INTEGER PRIMARY KEY autoincrement, version TEXT, android_id TEXT, nivel INTEGER, puntos INTEGER, piloto TEXT)";
	private static final String INSERTA_REGISTRO =
		"INSERT INTO " + TABLA + "(_id, version, android_id, nivel, puntos, piloto) values (?, ?, ?, ?, ?, ?)";
	
	public SQLiteGestor (Context contexto) {
		this.miContexto = contexto;
		miPuntuacionesOH = new PuntuacionesOH (this.miContexto);
		this.miDB = miPuntuacionesOH.getWritableDatabase();
		this.miSentencia = this.miDB.compileStatement(INSERTA_REGISTRO);
	}

	public void cierraOH() {
		miPuntuacionesOH.close();
	}
	
	public long insertaPuntuacion(String miVersion, String miId, String miNivel, String misPuntos, String miNombre) {
		this.miSentencia.bindNull(1);
		this.miSentencia.bindString(2, miVersion);
		this.miSentencia.bindString(3, miId);
		this.miSentencia.bindLong(4, Integer.valueOf(miNivel));
		this.miSentencia.bindLong(5, Integer.valueOf(misPuntos));
		this.miSentencia.bindString(6, miNombre);
		return this.miSentencia.executeInsert();
	}

	public void borraPuntuaciones() {
		this.miDB.delete(TABLA, null, null);
	}

	public ArrayList<Puntos> obtenPuntuaciones() {
		ArrayList<Puntos> listaPuntos = new ArrayList<Puntos>();
		Cursor cursor = this.miDB.query(TABLA, new String[] { "_id", "version", "android_id", "nivel", "puntos", "piloto" },
					null, null, null, null, "puntos desc");
		if (cursor.moveToFirst()) {
			do {
				//int miId = cursor.getInt(0);
				String miVersion = cursor.getString(1);
				String miAndroidId = cursor.getString(2);
				int miNivel = cursor.getInt(3);
				int misPuntos = cursor.getInt(4);
				String miPiloto = cursor.getString(5);
				Puntos puntuacionActual = new Puntos(miVersion, miAndroidId, String.valueOf(miNivel), String.valueOf(misPuntos), miPiloto);
				listaPuntos.add(puntuacionActual);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return listaPuntos;
	}

	public class PuntuacionesOH extends SQLiteOpenHelper {
	    PuntuacionesOH (Context context) {
	        super(context, BASE_DE_DATOS, null, VERSION_DB);
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        db.execSQL(CREA_TABLA);
	    }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLA);
			onCreate(db);			
		}
	}
	
}
