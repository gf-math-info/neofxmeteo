package NeoFxMeteo.modele.lune;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Cette classe fournit toutes les données relatives aux phases lunaires.
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public class LuneHelper {
	
	private static final long PERIODICITE_TEMPS = 2569443000L,
			NOUVELLE_LUNE_TEMPS = 1554475942000L, MILLISECONDES_JOUR = 86400000;

	/**
	 * Retourne une collection triée des prochaines phases de la lune à venir.
	 * @return La collection des prochaines phases de la lune.
	 */
	public static Set<LuneData> getProchainesLunes() {
		Set<LuneData> lunes = new TreeSet<>();
		final double pourcentagePresent = getPourcentageActuelleLune();
		final Calendar calendrierPresent = Calendar.getInstance();
		calendrierPresent.setTimeZone(TimeZone.getTimeZone("UTC-0"));
		Calendar calendrier;
		int pourHuit = 0;
		long reste = 0;
		int jour = 0;
		boolean flag = false;
		PhaseLunaire phaseLunaire;
		
		for(int i = 0; i < 8; i++) {
			pourHuit = (int) Math.ceil(pourcentagePresent * 8 + i);
			flag = pourHuit > 8;
			pourHuit %= 8;
			switch(pourHuit) {
				case 1:
					phaseLunaire = PhaseLunaire.PREMIER_CROISSANT;
					break;
				case 2:
					phaseLunaire = PhaseLunaire.PREMIER_QUARTIER;
					break;
				case 3:
					phaseLunaire = PhaseLunaire.GIBBEUSE_CROISSANTE;
					break;
				case 4:
					phaseLunaire = PhaseLunaire.PLEINE_LUNE;
					break;
				case 5:
					phaseLunaire = PhaseLunaire.GIBBEUSE_DECROISSANTE;
					break;
				case 6:
					phaseLunaire = PhaseLunaire.DERNIER_QUARTIER;
					break;
				case 7:
					phaseLunaire = PhaseLunaire.DERNIER_CROISSANT;
					break;
				default:
					phaseLunaire = PhaseLunaire.NOUVELLE_LUNE;
					break;
			}
			reste = (long) ((phaseLunaire.getPourcentageProgression() -
					pourcentagePresent) * PERIODICITE_TEMPS);
			if (flag)
				reste += PERIODICITE_TEMPS;
			jour = (int) (reste / MILLISECONDES_JOUR);
			calendrier = (Calendar) calendrierPresent.clone();
			calendrier.add(Calendar.DAY_OF_MONTH, jour);
			calendrier.add(Calendar.MILLISECOND,
					(int) (reste - jour * MILLISECONDES_JOUR));
			lunes.add(new LuneData(
					phaseLunaire.getPourcentageProgression(),
					new GregorianCalendar(
							calendrier.get(Calendar.YEAR),
							calendrier.get(Calendar.MONTH),
							calendrier.get(Calendar.DAY_OF_MONTH)),
					phaseLunaire.getNom()));
		}
		return lunes;
	}

	/**
	 * Retourne la lune actuelle.
	 * @return La lune actuelle.
	 */
	public static LuneData getLuneActuelle() {
		double pourcentage = getPourcentageActuelleLune();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC-0"));
		AtomicReference<PhaseLunaire> type = new AtomicReference<>();
		double diff = 0.0625d;

		if (pourcentage <= diff || pourcentage >= 1 - diff) {
			type.set(PhaseLunaire.NOUVELLE_LUNE);
		} else {
			Arrays.stream(PhaseLunaire.values()).forEach(e -> {
				if (pourcentage <= (e.getPourcentageProgression() + diff) &&
						pourcentage >= (e.getPourcentageProgression() - diff)) {
					type.set(e);
				}
			});
		}
		return new LuneData(
				pourcentage,
				new GregorianCalendar(
						cal.get(Calendar.YEAR),
						cal.get(Calendar.MONTH),
						cal.get(Calendar.DAY_OF_MONTH)),
				type.get().getNom());
	}


	/**
	 * Retourne le pourcentage de la lune actuelle.
	 * @return Le pourcentage de la phase de la lune.
	 */
	private static float getPourcentageActuelleLune() {
		double n = (System.currentTimeMillis() - NOUVELLE_LUNE_TEMPS) %
				PERIODICITE_TEMPS;
		return (float) (n / PERIODICITE_TEMPS);
	}

}
