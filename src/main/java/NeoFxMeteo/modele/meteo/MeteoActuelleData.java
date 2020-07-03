package NeoFxMeteo.modele.meteo;

/**
 * Cette classe représente une structure de données météo pour le moment
 * actuelle.
 * 
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public class MeteoActuelleData extends MeteoData {
	
	private float temperatureRessentie;
	private String description;
	
	/**
	 * Construit une structure de données contenant la météo du moment actuelle.
	 * 
	 * @param temperature			La température en degrée Celcius.
	 * @param vitesseVent			La vitesse du vent en kilomètre par heure.
	 * @param directionVent			Une constante signifiant la direction du
	 * 								vent.
	 * @param temperatureRessentie	La température ressentie en degrée Celcius.
	 * @param description			La description de la météo.
	 */
	public MeteoActuelleData(float temperature, float temperatureRessentie, 
			float vitesseVent, byte directionVent, String iconeID,
			String description) {
		super(0, temperature, (byte)0, vitesseVent, directionVent,
				iconeID);
		this.temperatureRessentie = temperatureRessentie;
		this.description = description;
	}

	/**
	 * Retourne la température en degrée Celcius.
	 * 
	 * @return	La température en degrée Celcius.
	 */
	public float getTemperatureRessentie() {
		return temperatureRessentie;
	}

	/**
	 * Retourne une description de la météo.
	 * 
	 * @return	Une description de la météo.
	 */
	public String getDescription() {
		return description;
	}

}
