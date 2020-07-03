package NeoFxMeteo.modele.meteo;

import java.sql.Timestamp;

/**
 * Cette classe est une structure de données relative à la météo.
 * 
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public class MeteoData {
	
	public final static byte N = 0, NE = 1, E = 2, SE = 3, S = 4,
			SW = 5, W = 6, NW = 7;
	public final static byte NUIT_MATIN = 5, MATIN_JOUR = 11, JOUR_SOIR = 17,
			SOIR_NUIT = 23;
	
	private long tempsUNIX;
	private byte directionVent;
	private String iconeID;
	private float temperature, quantitePluie, vitesseVent;
	
	/**
	 * Construit une structure de données relative à la météo.
	 * 
	 * @param tempsUNIX		Le temps UNIX.
	 * @param temperature	La température en degrée Celcius.
	 * @param quantitePluie	La quantité de pluie en millimètre.
	 * @param vitesseVent	La vitesse du vent en kilomètre par heure.
	 * @param directionVent	Une constante signifiant la direction du vent.
	 * @param iconeID		L'identifiant de l'icône représentant la météo.
	 */
	public MeteoData(long tempsUNIX, float temperature, float quantitePluie,
			float vitesseVent, byte directionVent, String iconeID) {
		this.tempsUNIX = tempsUNIX * 1000;
		this.temperature = temperature;
		this.quantitePluie = quantitePluie;
		this.vitesseVent = vitesseVent;
		this.directionVent = directionVent;
		this.iconeID = iconeID;
	}
	
	/**
	 * Retourne l'heure des données. L'entier retourné est compris entre 0 et 23
	 * inclusivement. Si les données ne contient pas d'heure, alors -1 est
	 * retourné.
	 * 
	 * @return	L'heure des données.
	 */
	public int getHeure() {
		if(tempsUNIX == 0)
			return -1;
		
		Timestamp timestamp = new Timestamp(tempsUNIX);
		return timestamp.toLocalDateTime().getHour();
	}
	
	/**
	 * Retourne le jour du mois des données. Si les données ne contient pas
	 * d'heure, alors -1 est retourné.
	 * 
	 * @return	Le jour du mois des données.
	 */
	public int getJourDuMois() {
		if(tempsUNIX == 0)
			return -1;
		
		Timestamp timestamp = new Timestamp(tempsUNIX);
		return timestamp.toLocalDateTime().getDayOfMonth();
	}

	/**
	 * Retourne le moment des données en temps UNIX, c'est-à-dire le nombre de millisecondes depuis le 1er janvier 1970.
	 * 
	 * @return	Le temps UNIX.
	 */
	public long getTempsUNIX() {
		return tempsUNIX;
	}

	/**
	 * Retourne la température en degrée Celcius.
	 * 
	 * @return	La température en degrée Celcius.
	 */
	public float getTemperature() {
		return temperature;
	}

	/**
	 * Retourne a quantité de pluie en millimètre.
	 * 
	 * @return	La quantité de pluie en millimètre.
	 */
	public float getQuantitePluie() {
		return quantitePluie;
	}

	/**
	 * Retourne la vitesse du vent en kilomètre par heure.
	 * 
	 * @return	La vitesse du vent en kilomètre par heure.
	 */
	public float getVitesseVent() {
		return vitesseVent;
	}

	/**
	 * Retourne une constante signifiant la direction du vent.
	 * 
	 * @return	Une constante signifiant la direction du vent.
	 */
	public byte getDirectionVent() {
		return directionVent;
	}
	
	/**
	 * Retourne l'identifiant de l'icône.
	 * 
	 * @return	L'identifiant de l'icône.
	 */
	public String getIconeID() {
		return iconeID;
	}
	
}
