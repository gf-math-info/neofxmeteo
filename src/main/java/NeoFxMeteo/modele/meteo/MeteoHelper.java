package NeoFxMeteo.modele.meteo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import NeoFxMeteo.modele.Requetes;
import NeoFxMeteo.modele.Ville;
import NeoFxMeteo.modele.exception.MeteoIntrouvableException;
import NeoFxMeteo.modele.exception.PositionIntrouvableException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Cette classe fournit toutes les données relatives à la météo.
 * 
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public class MeteoHelper {
	
	private static float MS_A_KH = 1 / 3.6f;
	
	private Ville ville;
	private MeteoActuelleData meteoActuelleData;
	private List<MeteoData> meteoJournees, meteoHeures, meteoJours;

	/**
	 * Charge la ville dans laquelle l'utilisateur se trouve est chargée.
	 * 
	 * @throws PositionIntrouvableException	Si la position est introuvable.
	 * @throws MeteoIntrouvableException	Si les données de météo sont
	 * 										introuvables.
	 */
	public void chargerMeteoPositionActuelle()
			throws PositionIntrouvableException, MeteoIntrouvableException {
		try {
			chargerMeteoPosition(Requetes.getVilleActuelle());
		}catch (IOException ioException) {
			throw new PositionIntrouvableException();
		}
	}
	
	/**
	 * Charge les données de météo pour une certaine ville passée en argument.
	 * 
	 * @param ville							La ville pour laquelle les données
	 * 										de météo sont chargées.
	 * @throws MeteoIntrouvableException	Si les données de météo sont
	 * 										introuvables.
	 */
	public void chargerMeteoPosition(Ville ville)
			throws MeteoIntrouvableException {
		this.ville = ville;
		try {
			/*
			 * On télécharge les données de la météo actuelle et la météo par
			 * heure.
			 */
			JSONObject jsonObject = Requetes.getMeteoDataJSON(
					ville.getLatitude(), ville.getLongitude());
			meteoActuelleData = getMeteoActuelle(jsonObject);
			meteoHeures = getMeteoHeures(jsonObject);
			meteoJours = getMeteoJours(jsonObject);
			meteoJournees = new ArrayList<MeteoData>(4);
			
			/*
			 * On calcule des "moyennes" pour la météo des différentes partie de
			 * la journée.
			 */
			int heure = meteoHeures.get(0).getHeure(), indexDebut = 0;

			if (heure <= MeteoData.NUIT_MATIN)
				indexDebut = MeteoData.NUIT_MATIN - heure;

			else if (heure <= MeteoData.MATIN_JOUR)
				indexDebut = MeteoData.MATIN_JOUR - heure;

			else if (heure <= MeteoData.JOUR_SOIR)
				indexDebut = MeteoData.JOUR_SOIR - heure;

			else if (heure <= MeteoData.SOIR_NUIT)
				indexDebut = MeteoData.SOIR_NUIT - heure;

			else
				indexDebut = 24 - heure + MeteoData.NUIT_MATIN;

			long tempsUNIX;
			String iconeID;
			float quantitePluie, temperatureSomme, vitesseVentSomme,
					directionVentSomme;
			MeteoData meteoData;
			for (int i = 0; i < 4; i++) {
				temperatureSomme = quantitePluie = vitesseVentSomme =
						directionVentSomme = tempsUNIX = 0;
				iconeID = "01";

				for (int j = 0; j < 6; j++) {
					meteoData = meteoHeures.get(indexDebut + i * 6 + j);
					if (j == 0)
						tempsUNIX = meteoData.getTempsUNIX();

					temperatureSomme += meteoData.getTemperature();
					quantitePluie += meteoData.getQuantitePluie();
					vitesseVentSomme += meteoData.getVitesseVent();
					if (Integer.parseInt(iconeID) < Integer.parseInt(
							meteoData.getIconeID().substring(0, 2)))
						iconeID = meteoData.getIconeID().substring(0, 2);
				}
				iconeID += (5 <= heure && heure < 23 ? "d" : "n");
				meteoJournees.add(new MeteoData(tempsUNIX, temperatureSomme / 6,
						quantitePluie, vitesseVentSomme / 2,
						(byte) Math.round(directionVentSomme / 6), iconeID));
			}

		} catch (IOException ioException) {
			throw new MeteoIntrouvableException();
		}
	}
	
	/**
	 * Retourne la liste des données de la météo actuelle.
	 * 
	 * @return	La liste des données de la météo actuelle.
	 */
	public MeteoActuelleData getMeteoActuelleData() {
		return meteoActuelleData;
	}
	
	/**
	 * Retourne la liste des données de la météo pour les quatre prochaines
	 * parties de la journée.
	 * 
	 * @return	La liste des données de la météo pour les quatre prochaines
	 * 			parties de la journée.
	 */
	public List<MeteoData> getMeteoJournees() {
		return meteoJournees;
	}
	
	/**
	 * Retourne la liste des données de la météo pour les prochaines heures.
	 * 
	 * @return	La liste des données de la météo pour les prochaines heures.
	 */
	public List<MeteoData> getMeteoHeures() {
		return meteoHeures;
	}
	
	/**
	 * Retourne la liste des données de la météo pour les prochains jours.
	 * 
	 * @return	La liste des données de la météo pour les prochains jours.
	 */
	public List<MeteoData> getMeteoJours() {
		return meteoJours;
	}
	
	/**
	 * Retourne la ville qui est actuellement chargée.
	 * 
	 * @return	La ville qui est actuellement chargée.
	 */
	public Ville getVille() {
		return ville;
	}
	
	/**
	 * Analyse le JSON passé en argument et retourne les données de la météo
	 * actuelle.
	 * @param dataJsonObject	Le JSON contenant les données de la météo.
	 * @return					Une structure de données contenant la météo
	 * 							actuelle.
	 */
	private MeteoActuelleData getMeteoActuelle(JSONObject dataJsonObject) {
		float temperature, temperatureRessentie, vitesseVent;
		byte directionVent;
		String iconeID, description;
		
		JSONObject currentJsonObject = dataJsonObject.getJSONObject("current");
		temperature = currentJsonObject.getFloat("temp");
		temperatureRessentie = currentJsonObject.getFloat("feels_like");
		vitesseVent = currentJsonObject.getFloat("wind_speed");
		directionVent = getDirectionVent(
				currentJsonObject.getDouble("wind_deg"));
		iconeID = currentJsonObject.getJSONArray("weather").getJSONObject(0).
				getString("icon");
		description =  currentJsonObject.getJSONArray("weather").getJSONObject(0).
				getString("description");
		
		return new MeteoActuelleData(temperature, temperatureRessentie,
				vitesseVent, directionVent, iconeID, description);
	}
	
	/**
	 * Analyse le JSON passé en argument et retourne les données de la météo
	 * pour les prochaines heures.
	 * @param dataJsonObject	Le JSON contenant les données de la météo.
	 * @return					Une liste de structure de données contenant la
	 * 							météo des prochaines heures.
	 */
	public List<MeteoData> getMeteoHeures(JSONObject dataJsonObject) {
		JSONArray heuresJsonArray = dataJsonObject.getJSONArray("hourly");
		List<MeteoData> meteoHeures = new ArrayList<MeteoData>(
				heuresJsonArray.length());
		
		JSONObject jsonActuel;
		long tempsUNIX;
		float temperature, quantitePluie, vitesseVent;
		byte directionVent;
		String iconeID;
		for(int i = 0; i < heuresJsonArray.length(); i++) {
			jsonActuel = heuresJsonArray.getJSONObject(i);
			
			tempsUNIX = jsonActuel.getLong("dt");
			temperature = jsonActuel.getFloat("temp");
			if(!jsonActuel.isNull("rain"))
				quantitePluie = jsonActuel.getJSONObject("rain").getFloat("1h");
			else if(!jsonActuel.isNull("snow"))
				quantitePluie = jsonActuel.getJSONObject("snow").getFloat("1h");
			else
				quantitePluie = 0;
			vitesseVent = jsonActuel.getFloat("wind_speed") * MS_A_KH;
			directionVent = getDirectionVent(jsonActuel.getDouble("wind_deg"));
			iconeID = jsonActuel.getJSONArray("weather").getJSONObject(0).
					getString("icon");
			
			meteoHeures.add(new MeteoData(tempsUNIX, temperature, quantitePluie,
					vitesseVent, directionVent, iconeID));
		}
		
		return meteoHeures;
	}
	
	/**
	 * Analyse le JSON passé en argument et retourne les données de la météo
	 * pour les prochains jours.
	 * @param dataJsonObject	Le JSON contenant les données de la météo.
	 * @return					Une liste de structure de données contenant la
	 * 							météo des prochains jours.
	 */
	public List<MeteoData> getMeteoJours(JSONObject dataJsonObject) {
		JSONArray joursJsonArray = dataJsonObject.getJSONArray("daily");
		List<MeteoData> meteoJours = new ArrayList<MeteoData>(
				joursJsonArray.length());
		
		JSONObject jsonActuel;
		long tempsUNIX;
		float temperature, quantitePluie, vitesseVent;
		byte directionVent;
		String iconeID;
		for(int i = 0; i < joursJsonArray.length(); i++) {
			jsonActuel = joursJsonArray.getJSONObject(i);
			
			tempsUNIX = jsonActuel.getLong("dt");
			temperature = jsonActuel.getJSONObject("temp").getFloat("day");
			if(!jsonActuel.isNull("rain"))
				quantitePluie = jsonActuel.getFloat("rain");
			else if(!jsonActuel.isNull("snow"))
				quantitePluie = jsonActuel.getFloat("snow");
			else
				quantitePluie = 0;
			vitesseVent = jsonActuel.getFloat("wind_speed") * MS_A_KH;
			directionVent = getDirectionVent(jsonActuel.getDouble("wind_deg"));
			iconeID = jsonActuel.getJSONArray("weather").getJSONObject(0).
					getString("icon");
			
			meteoJours.add(new MeteoData(tempsUNIX, temperature, quantitePluie,
					vitesseVent, directionVent, iconeID));
		}
		
		return meteoJours;
	}
	
	/**
	 * À partir d'une direction en degrée, retourne la constante de la direction
	 * la plus près. Par exemple 3 degrée est plus près du nord (0 degrée) que
	 * du nord-est (45 degrée).
	 * 
	 * @param directionDegree	La direction entre 0 et 360 degrée
	 * 							inclusivement.
	 * @return					Un entier compris entre 0 et 7 inclusivement.
	 */
	private byte getDirectionVent(double directionDegree) {
		if(0 > directionDegree || directionDegree > 360)
			throw new IllegalArgumentException(
					"Le degrée passé en argument doit être compris entre 0 "
					+ "et 360 degrées inclusivement. Degrée : "
					+ directionDegree);
		
		byte direction = 0;
		final double SAUT = 45;
		double diffMin = Double.MAX_VALUE, diffActulle;
		for(byte i = 0; i < 8; i++) {
			diffActulle = Math.abs(i * SAUT - directionDegree);
			if(diffActulle < diffMin) {
				diffMin = diffActulle;
				direction = i;
			}
		}
		if(Math.abs(360 - directionDegree) < diffMin)
			direction = 0;
		
		return direction;
	}
}
