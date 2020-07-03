package NeoFxMeteo.modele.lune;

/**
 * Cette énumération contient les différentes phases de la lune.
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public enum PhaseLunaire {
	
	NOUVELLE_LUNE("Nouvelle lune", 0f),
	PREMIER_CROISSANT("Premier croissant", 0.125f),
	PREMIER_QUARTIER("Premier quartier", 0.25f),
	GIBBEUSE_CROISSANTE("Gibbeuse croissante", 0.375f),
	PLEINE_LUNE("Pleine lune", 0.5f),
	GIBBEUSE_DECROISSANTE("Gibbeuse décroissante", 0.625f),
	DERNIER_QUARTIER("Dernier quart", 0.75f),
	DERNIER_CROISSANT("Dernier croissant", 0.875f);
	
	private String nom;
	private float pourcentageProgression;
	
	/**
	 * Construit une phase lunaire avec un nom représentant la phases lunaire et
	 * le pourcentage de progression. La nouvelle lune est fixée à 0%.
	 * @param nom						Le nom représentant la phase lunaire.
	 * @param pourcentageProgression	Le pourcentage de progression de la lune
	 * 									à cet état.
	 */
	PhaseLunaire(String nom, float pourcentageProgression) {
		this.nom = nom;
		this.pourcentageProgression = pourcentageProgression;
	}
	
	/**
	 * Renvoie le nom représentant la phase lunaire.
	 * @return	Le nom représentant la phase lunaire.
	 */
	public String getNom() {
		return nom;
	}
	
	/**
	 * Renvoie le pourcentage de progression de la lune à cet état. La nouvelle
	 * lune est fixée à 0%
	 * @return	Le pourcentage de progression de la lune.
	 */
	public float getPourcentageProgression() {
		return pourcentageProgression;
	}
}
