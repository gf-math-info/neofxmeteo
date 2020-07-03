package NeoFxMeteo.controleur;

import java.util.Calendar;

import NeoFxMeteo.modele.Ville;
import NeoFxMeteo.modele.exception.MeteoIntrouvableException;
import NeoFxMeteo.modele.exception.PositionIntrouvableException;
import NeoFxMeteo.modele.lune.LuneHelper;
import NeoFxMeteo.modele.meteo.MeteoHelper;
import NeoFxMeteo.vue.AffichageMeteo;

/**
 * Cette classe est la couche de liaison entre la couche métier et la couche
 * vue. Les classes s'occupant de la vue demande d'exécuter certaines actions à
 * la présente classe qui, suite à l'exécution de ces actions demande à la vue
 * de se mettre à jour.
 *
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public class ControleurMeteoLune {
	
	private static String FOURNISSEUR_METEO = "OpenWeatherMap",
			FOURNISSEUR_POSITION = "ipstack";
	
	private AffichageMeteo affichageMeteo;
	private MeteoHelper meteoHelper;
	
	/**
	 * Construit une contrôleur afin de lier la couche vue à la couche métier.
	 * Des requêtes par Internet sont réalisées pour charger les données de la
	 * météo. L'affichage est ensuite mis à jour.
	 *
	 * @param affichageMeteo	L'instance s'occupant d'afficher les données.
	 */
	public ControleurMeteoLune(AffichageMeteo affichageMeteo) {
		this.affichageMeteo = affichageMeteo;
		meteoHelper = new MeteoHelper();

		affichageMeteo.afficherChargement("En attente des données...");
		affichageMeteo.actualiserLuneActuelle(LuneHelper.getLuneActuelle());
		affichageMeteo.actualiserProchainesLunes(LuneHelper.getProchainesLunes());
		
		affichageMeteo.afficherMeteoLune();
	}

	/**
	 * Met à jour les données de la météo pour la vile actuelle. Des requêtes par Internet sont réalisées pour charger
	 * les données de la météo. L'affichage est ensuite mise à jour.
	 */
	public void demanderMeteoVilleActuelle() {
		affichageMeteo.afficherChargement("Chargement des données...");
		try {

			meteoHelper.chargerMeteoPositionActuelle();
			actualisationMeteoAffichage();
			affichageMeteo.afficherMeteoLune();

		} catch (PositionIntrouvableException positionIntrouvableException) {
			affichageMeteo.afficherPositionIntrouvable();
		} catch (MeteoIntrouvableException meteoIntrouvableException) {
			affichageMeteo.afficherMeteoIntrouvable();
		}
	}
	
	/**
	 * Met à jour les données de la météo pour une certaine ville passée en
	 * argument. Des requêtes par Internet sont réalisées pour charger les
	 * données de la météo pour la ville. L'affichage est ensuite mis à jour.
	 *
	 * @param ville	La ville pour laquelle les données doivent être chargées.
	 */
	public void demanderMeteoVille(Ville ville) {
		affichageMeteo.afficherChargement("Chargement des données...");
		try {
			
			meteoHelper.chargerMeteoPosition(ville);
			actualisationMeteoAffichage();
			affichageMeteo.afficherMeteoLune();
			
		} catch(MeteoIntrouvableException meteoException) {
			affichageMeteo.afficherMeteoIntrouvable();
		}
	}

	/**
	 * Affiche les données des phases lunaires les plus récentes.
	 */
	public void demanderPhasesLunaires() {
		affichageMeteo.actualiserLuneActuelle(LuneHelper.getLuneActuelle());
		affichageMeteo.actualiserProchainesLunes(LuneHelper.getProchainesLunes());
	}
	
	/**
	 * Actualise les données de la météo. Des requêtes par Internet sont
	 * réalisées pour charger les données de la météo pour la ville. L'affichage
	 * est ensuite mis à jour.
	 */
	public void actualisation() {
		affichageMeteo.afficherChargement("Chargement des données...");
		affichageMeteo.actualiserLuneActuelle(LuneHelper.getLuneActuelle());
		affichageMeteo.actualiserProchainesLunes(
				LuneHelper.getProchainesLunes());
		
		try {
			
			meteoHelper.chargerMeteoPosition(meteoHelper.getVille());
			actualisationMeteoAffichage();
			
		} catch(MeteoIntrouvableException meteoException) {
			
			affichageMeteo.afficherMeteoIntrouvable();
			
		}
		affichageMeteo.afficherMeteoLune();
	}
	
	private void actualisationMeteoAffichage() {
		affichageMeteo.actualiserMeteoActuelle(
				meteoHelper.getMeteoActuelleData());
		affichageMeteo.actualiserMeteoJournée(
				meteoHelper.getMeteoJournees());
		affichageMeteo.actualiserMeteoHeures(
				meteoHelper.getMeteoHeures());
		affichageMeteo.actualiserMeteoJours(
				meteoHelper.getMeteoJours());
		affichageMeteo.setMomentDerniereActualisation(Calendar.getInstance());
		affichageMeteo.setVille(meteoHelper.getVille());
		affichageMeteo.setMessageInfoFournisseur(
				"Les données de la météo sont fournies par " +
						FOURNISSEUR_METEO + "\tLa position est fournies par " +
						FOURNISSEUR_POSITION);
	}
}
