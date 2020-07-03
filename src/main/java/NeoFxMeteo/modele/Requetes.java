package NeoFxMeteo.modele;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

/**
 * Cette classe sert à simplifier les requêtes par Internet couramment utilisées
 * dans l'application.
 * 
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public class Requetes {

	private static final String
			CLE_API_METEO = "5f6399d69f191bad7fa005fbbd830451",
			URL_METEO = "https://api.openweathermap.org/data/2.5/onecall",
			URL_IP = "http://checkip.amazonaws.com",
			URL_VILLE_JSON = "http://api.ipstack.com/",
			CLE_API_POSITION = "3bf3aa32490830ba28983cdeaa702f34";
	
	/**
	 * Lance une requête par Internet et retourne un JSON contenant les données
	 * de la météo pour une certaine longitude et latitude.
	 * 
	 * @param latitude		La latitude.
	 * @param longitude		La longitude.
	 * @return				Le JSON contenant les données de la météo.
	 * @throws IOException	Si la requête échoue.
	 */
	public static JSONObject getMeteoDataJSON(double latitude, double longitude)
			throws IOException {
		String jsonObjectString = requete(URL_METEO + "?lat=" +
			latitude + "&lon=" + longitude +
			"&exclude=minutely&units=metric&lang=fr&appid=" + CLE_API_METEO);
		return new JSONObject(jsonObjectString);
	}
	
	/**
	 * Lance une requête par Internet et retourne la ville actuelle de
	 * l'utilisateur.
	 * 
	 * @return				La ville actuelle de l'utilisateur.
	 * @throws IOException	Si la requête échoue.
	 */
	public static Ville getVilleActuelle() throws IOException {
		String ip = getIp();
		String jsonVilleString = requete(URL_VILLE_JSON + ip +
				"?access_key=" + CLE_API_POSITION);
		JSONObject jsonVille = new JSONObject(jsonVilleString);
		
		String nomVille;
		double longitude, latitude;
		
		if(jsonVille.isNull("city"))
			nomVille = jsonVille.getJSONObject("location").getString("capital");
		else
			nomVille = jsonVille.getString("city");

		longitude = jsonVille.getDouble("longitude");
		latitude = jsonVille.getDouble("latitude");
		
		return new Ville(nomVille, longitude, latitude);
	}

	/**
	 * Lance une requête pour récupérer l'adresse IP de l'utilisateur et la
	 * renvoie.
	 * 
	 * @return				L'adresse IP de l'utilisateur.
	 * @throws IOException	Si la requête échoue.
	 */
	private static String getIp() throws IOException {
		return requete(URL_IP);
	}
	
	/**
	 * Lance une requête par Internet selon une adresse URL et une retourne le
	 * résultat.
	 * 
	 * @param urlString 	L'adresse URL de la requête.
	 * @return 				Le résultat de la requête.
	 * @throws IOException 	Si la requête échoue.
	 */
	private static String requete(String urlString) throws IOException {
		URL url = new URL(urlString);
		Scanner in = new Scanner(url.openStream(), "UTF8");
		StringBuilder lines = new StringBuilder();
		while (in.hasNext())
			lines.append(in.nextLine());
		in.close();
		return lines.toString();
	}
}
