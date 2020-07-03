package NeoFxMeteo.vue;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import NeoFxMeteo.modele.Ville;

/**
 * Cette classe représente le champ de texte accompagné d'un autocomplétion pour les différentes villes. Une instance
 * doit avoir avoir une liste de ville sinon un NullPointerException sera lancé à son utilisation.
 *
 * @author Turbide, Naomie
 * @author Fortin-Leblanc, Gabriel
 * @author Bourque, Julien
 */
public class RechercheTextField extends TextField {
	
	private final short MAX_PROPOSITION = 5, LONGUEUR_MAX = 4;
	
	private ContextMenu menu;
	private List<Ville> propositions;
	private AtomicBoolean enRecherche, texteChange;

	/**
	 * Construit un champ de texte avec un autocomplétion.
	 */
	public RechercheTextField() {
		super();
		enRecherche = new AtomicBoolean();
		texteChange = new AtomicBoolean();
		menu = new ContextMenu();

		menu.getItems().addListener(new ListChangeListener<MenuItem>() {
			@Override
			public void onChanged(Change<? extends MenuItem> change) {
				if(menu.getItems().size() == 0)
					Platform.runLater(() -> menu.hide());
				else
					Platform.runLater(() -> menu.show(RechercheTextField.this, Side.BOTTOM, 0, 0));
			}
		});

		addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				switch (keyEvent.getCode()) {
					case LEFT:
					case RIGHT:
					case UP:
					case DOWN:
						return;

					case ENTER:
						RechercheTextField.this.fireEvent(new ActionEvent());
						break;

					default:
						texteChange.set(true);
						if(getText().length() > 0 && !enRecherche.get()) {

							enRecherche.set(true);
							demarrerThreadRecherchePropositions();

						} else if(getText().length() == 0)

							enRecherche.set(false);
				}
			}
		});
	}

	/**
	 * Change la liste de proposition.
	 * @param propositions	Les propositions à offrir à l'utilisateur.
	 */
	public void setPropositions(List<Ville> propositions) {
		this.propositions = propositions;
	}

	public Ville getVilleEntree() {
		return propositions.stream().filter(ville -> getText().equalsIgnoreCase(ville.getNom())).findAny().orElse(null);
	}

	/**
	 * Démarre une pile de tâche afin d'évaluer les entrées de l'utilisateur et de lui proposer des résultats.
	 */
	private void demarrerThreadRecherchePropositions() {
		new Thread(() -> {
			
			while(enRecherche.get()) {
				/*
				 * Le thread est en cours tant qu'il y a des lettres dans le TextField.
				 * 
				 * On retire les propositions qui ne ressembles pas à ce qui
				 * est écrit dans le champ de texte.
				 * Ensuite, on ajoute de nouvelles propositions.
				 */
				while(!texteChange.get() && enRecherche.get()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
				}
				
				final String TEXTE = getText();
				texteChange.set(false);
				
				Platform.runLater(() -> {
					menu.getItems().clear();
					propositions.stream().filter(ville ->
							distanceDamerauLevenshtein(TEXTE, ville.getNom()) <= LONGUEUR_MAX).limit(MAX_PROPOSITION)
							.forEach(ville -> {
						MenuItem menuItem = new MenuItem(ville.getNom());
						menuItem.setOnAction((event) -> {
							Platform.runLater(() -> {
								setText(menuItem.getText());
								enRecherche.set(false);
							});
						});
						menu.getItems().add(menuItem);
					});
				});
			}

			Platform.runLater(() -> menu.getItems().clear());
		}).start();
	}

	/**
	 * Retourne la distance entre les deux chaînes passées en argument selon la distance de Damerau-Levenshtein.
	 * @param chaine1	La première chaîne.
	 * @param chaine2	La deuxième chaîne.
	 * @return			La distance entre les deux chaînes.
	 */
	private int distanceDamerauLevenshtein(String chaine1, String chaine2) {
		
		if(chaine1.length() == 0)
			return chaine2.length();
		if(chaine2.length() == 0)
			return chaine1.length();
		
		int cout;
		int[] lignePrecedente2 = null,
				lignePrecedente = new int[chaine1.length() + 1],
				ligneActuelle = null;
		for(int i = 0; i < lignePrecedente.length; i++)
			lignePrecedente[i] = i;
		
		for(int j = 1; j < chaine2.length() + 1; j++) {
			
			ligneActuelle = new int[chaine1.length() + 1];
			ligneActuelle[0] = j;
			for(int i = 1; i < lignePrecedente.length; i++) {
				
				cout = (chaine1.charAt(i - 1) != chaine2.charAt(j - 1) ? 1 : 0);
				ligneActuelle[i] = Math.min(
						Math.min(ligneActuelle[i - 1] + 1,
								lignePrecedente[i] + 1),
						lignePrecedente[i - 1] + cout);
				
				if(i > 1 && j > 1 &&
						chaine1.length() > i &&
						chaine2.length() > j &&
						chaine1.charAt(i) == chaine2.charAt(j - 1) &&
						chaine1.charAt(i - 1) == chaine2.charAt(j))
					ligneActuelle[i] = (byte)Math.min(
							ligneActuelle[i],
							lignePrecedente2[i - 2] + cout);
				
			}
			lignePrecedente2 = lignePrecedente;
			lignePrecedente = ligneActuelle;
			
		}
				
		return ligneActuelle[chaine1.length()];
	}
}
