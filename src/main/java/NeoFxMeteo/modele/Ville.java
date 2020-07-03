package NeoFxMeteo.modele;

import java.io.Serializable;

/**
 * Cette classe est utilisé comme une structure de données afin de simplifier
 * la localisation des villes.
 * 
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public class Ville implements Comparable<Ville>, Serializable {
	
	private String nom;
	private double longitude, latitude;
	
	/**
	 * Construit une ville son nom et sa localisation.
	 * 
	 * @param nom		Le nom de la ville.¸
	 * @param longitude	La longitude de la ville.
	 * @param latitude	La latitude de la ville.
	 */
	public Ville(String nom, double longitude, double latitude) {
		super();
		this.nom = nom;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	/**
	 * Retourne le nom de la ville.
	 * 
	 * @return	Le nom de la ville.
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Retourne la longitude de la ville.
	 * 
	 * @return	La longitude de la ville.
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Retourne la latitude de la ville.
	 * 
	 * @return	La latitude de la ville.
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Retourne la decription de la ville en chaîne de caractère. Sa
	 * représentation est le nom de la ville.
	 */
	@Override
	public String toString() {
		return nom;
	}

	/**
	 * Compare la ville avec une autre ville passée en argument selon l'ordre
	 * alphabétique du nom des villes.
	 */
	@Override
	public int compareTo(Ville ville) {
		return nom.compareTo(ville.nom);
	}
}
