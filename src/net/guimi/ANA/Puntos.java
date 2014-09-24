package net.guimi.ANA;

public class Puntos {
	public String version;
	public String android_id;
	public String nivel;
	public String puntos;
	public String piloto;
	
	public Puntos (String miVersion, String miId, String miNivel, String misPuntos, String miNombre) {
		version = miVersion;
		android_id = miId;
		nivel = miNivel;
		puntos = misPuntos;
		piloto = miNombre;
	}

	public String toString() {
		return version+";"+android_id+";"+nivel+";"+puntos+";"+piloto+"\n";
	}
}