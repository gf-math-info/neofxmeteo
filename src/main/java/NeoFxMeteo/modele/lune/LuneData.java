package NeoFxMeteo.modele.lune;

import java.util.GregorianCalendar;

/**
 * Cette classe représente, à un moment précis, le poucentage de la lune, où 1
 * et 0 est une nouvelle lune et 0.5, une pleine lune.
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public class LuneData implements Comparable<LuneData> {

	private double pourcentage = 0;
	private GregorianCalendar calendrier = new GregorianCalendar();
	private String nom = "";

	/**
	 * Construit le moment d'une lune.
	 * @param pourcentage Le pourcentage de la phase de la lune.
	 * @param calendrier  L'instance du calendrier.
	 */
	public LuneData(double pourcentage, GregorianCalendar calendrier) {
		this(pourcentage, calendrier, "");
	}

	/**
	 * Construit le moment d'une lune.
	 * @param pourcentage Le pourcentage de la phase de la lune.
	 * @param calendrier  Le calendrier du moment.
	 * @param nom         Le nom de la lune
	 */
	public LuneData(double pourcentage, GregorianCalendar calendrier, String nom) {
		this.pourcentage = pourcentage;
		this.nom = nom;
		this.calendrier = calendrier;
	}

	/**
	 * Retourne le pourcentage de progression de la lune, où 0 et 1 sont une
	 * nouvelle lune et 0.5 est une pleine lune.
	 * @return Le pourcentage de progression de la lune.
	 */
	public double getPourcentage() {
		return pourcentage;
	}

	/**
	 * Retourne l'instance du calendrier.
	 * @return L'instance du calendrier.
	 */
	public GregorianCalendar getCalendrier() {
		return calendrier;
	}

	/**
	 * Retourne le nom de la phase de la lune.
	 * @return Le nom de la phase de la lune.
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Donne la différence entre deux lunes. Cette méthode permet de trier les
	 * lunes entre elles.
	 */
	@Override
	public int compareTo(LuneData o) {
		return calendrier.compareTo(o.calendrier);
	}
}
