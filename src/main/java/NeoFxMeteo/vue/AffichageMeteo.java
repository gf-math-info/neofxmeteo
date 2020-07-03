package NeoFxMeteo.vue;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import NeoFxMeteo.controleur.ControleurMeteoLune;
import NeoFxMeteo.modele.Ville;
import NeoFxMeteo.modele.lune.LuneData;
import NeoFxMeteo.modele.meteo.MeteoActuelleData;
import NeoFxMeteo.modele.meteo.MeteoData;

/**
 * Cette interface doit être implémenté par la classe qui s'occupe de l'affiche
 * de la météo et des différentes phases de la lune.
 * 
 * @author Turbide, Naomie
 * @author Fortin-Leblanc, Gabriel
 * @author Bourque, Julien
 */
public interface AffichageMeteo {
	
	/**
	 * Met à jour l'affichage de la météo actuelle.
	 * 
	 * @param meteoActuelleData	Les nouvelles données de la météo actuelle.
	 */
	public void actualiserMeteoActuelle(MeteoActuelleData meteoActuelleData);
	
	/**
	 * Met à jour l'affichage de la météo pour les différentes parties de la
	 * journée.
	 * 
	 * @param meteoJourneeDatas	Les nouvelles données de la météo des
	 * 							différentes parties de la journée.
	 */
	public void actualiserMeteoJournée(List<MeteoData> meteoJourneeDatas);
	
	/**
	 * Met à jour l'affichage de la météo pour les prochaines heures.
	 * 
	 * @param meteoHeuresDatas	Les nouvelles données de la météo pour les
	 * 							prochaines heures.
	 */
	public void actualiserMeteoHeures(List<MeteoData> meteoHeuresDatas);
	
	/**
	 * Met à jour l'affichage de la météo pour les prochains jours.
	 * 
	 * @param meteoJoursDatas	Les nouvelles données de la météo pour les
	 * 							prochains jours.
	 */
	public void actualiserMeteoJours(List<MeteoData> meteoJoursDatas);
	
	/**
	 * Met à jour l'affichage de la phase lunaire actuelle.
	 * 
	 * @param luneData	Les nouvelles données de la phase lunaire actuelle.
	 */
	public void actualiserLuneActuelle(LuneData luneData);
	
	/**
	 * Met à jour l'affichage des prochaines phases lunaires.
	 * 
	 * @param luneDatas	Les nouvelles données des prochaines phases lunaires.
	 */
	public void actualiserProchainesLunes(Set<LuneData> luneDatas);
	
	/**
	 * Signale à l'utilisateur qu'un chargement s'effectue.
	 * 
	 * @param messageChargement	Le message de chargement, ou "null" pour
	 * 							afficher aucun message.
	 */
	public void afficherChargement(String messageChargement);
	
	/**
	 * Signale à l'utilisateur que la position est introuvable.
	 */
	public void afficherPositionIntrouvable();
	
	/**
	 * Signale à l'utilisateur que les données de la météo sont introuvables.
	 */
	public void afficherMeteoIntrouvable();
	
	/**
	 * Affiche à l'utilisateur les données de la météo et les phases lunaires.
	 */
	public void afficherMeteoLune();
	
	/**
	 * Met à jour le message d'alerte météo. Si "null" est passé en argument,
	 * alors aucun message ne sera affiché.
	 * 
	 * @param message	Le message d'alerte à afficher, ou "null" si aucun
	 * 					message ne doit être affiché.
	 */
	public void setMessageAlerte(String message);
	
	/**
	 * Met à jour le message d'information sur les fournisseurs de données. Si
	 * "null" est passé en argument, alors aucun message ne sera affiché.
	 * 
	 * @param message	Le message d'information à afficher, ou "null" si aucun
	 * 					message ne doit être affiché.
	 */
	public void setMessageInfoFournisseur(String message);
	
	/**
	 * Met à jour l'instance de calendrier du dernier moment d'actualisation.
	 * 
	 * @param calendrier	L'instance de calendrier du dernier moment
	 * 						d'actualisation.
	 */
	public void setMomentDerniereActualisation(Calendar calendrier);
	
	/**
	 * Change le nom de la ville par celui passée en argument. Cela permet à
	 * l'utilisateur de connaître de quelle ville provienne les données météos
	 * affichées.
	 * 
	 * @param ville	Le nom de la ville.
	 */
	public void setVille(Ville ville);
	
	/**
	 * Retourne le contrôleur principal.
	 * 
	 * @return	Le contrôleur principal.
	 */
	public ControleurMeteoLune getControleurPrincipal();

}
