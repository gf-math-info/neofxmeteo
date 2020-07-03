package NeoFxMeteo;

import NeoFxMeteo.controleur.ControleurMeteoLune;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import NeoFxMeteo.modele.Ville;
import NeoFxMeteo.vue.map.MeteoVue;

import java.io.*;
import java.util.*;

/**
 * Cette classe est la classe d'entrée du programme. Elle charge les ressources sur une pile différente que la pile
 * principale.
 *
 * @author Turbide, Naomie
 * @author Fortin, Gabriel
 * @author Bourque, Julien
 */
public class NeoFxMeteo extends Application {

	private static final String VILLES_FAVORITES_FICHIER = "/villesFavorites.dat";
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stagePrincipal) throws Exception {
		Locale.setDefault(Locale.CANADA_FRENCH);

		HashMap<String, Image> icones = new HashMap<>();
		List<Ville> villes = new ArrayList<>();
		List<Ville> villesFavorites = new ArrayList<>();
		Thread ressourcesThread = new Thread(() -> {

			final double GRANDEUR = 200;
			icones.put("01d", new Image("/imgs/temperature/09d.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("01n", new Image("/imgs/temperature/09n.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("02d", new Image("/imgs/temperature/09d.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("02n", new Image("/imgs/temperature/09n.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("03d", new Image("/imgs/temperature/09d.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("03n", new Image("/imgs/temperature/09n.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("04d", new Image("/imgs/temperature/09d.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("04n", new Image("/imgs/temperature/09n.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("09d", new Image("/imgs/temperature/09d.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("09n", new Image("/imgs/temperature/09n.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("10d", new Image("/imgs/temperature/10d.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("10n", new Image("/imgs/temperature/10n.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("11d", new Image("/imgs/temperature/11d.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("11n", new Image("/imgs/temperature/11n.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("13d", new Image("/imgs/temperature/13d.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("13n", new Image("/imgs/temperature/13n.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("50d", new Image("/imgs/temperature/50d.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("50n", new Image("/imgs/temperature/50n.png", GRANDEUR, GRANDEUR, true, true));
			icones.put("lune", new Image("/imgs/temperature/lune.png", GRANDEUR, GRANDEUR, true, true));

			try {
				BufferedReader input = new BufferedReader(new FileReader(
						NeoFxMeteo.this.getClass().getResource("/ville.csv").getFile()));
				input.lines().forEach(ligne -> {
					String[] tabs = ligne.split("\t");
					villes.add(new Ville(tabs[0], Double.parseDouble(tabs[2]), Double.parseDouble(tabs[1])));
				});
				input.close();
			} catch (IOException e) {}

			try {
				ObjectInputStream input = new ObjectInputStream(new FileInputStream(
						NeoFxMeteo.this.getClass().getResource("").getFile() + VILLES_FAVORITES_FICHIER));
				List<Ville> villesTabs = (ArrayList<Ville>) input.readObject();
				input.close();
				villesFavorites.addAll(villesTabs);
			} catch (IOException | ClassNotFoundException | NullPointerException e) {}

			stagePrincipal.getIcons().add(new Image("/imgs/logoXSoleil.png"));
		});
		ressourcesThread.start();

		stagePrincipal.setMaximized(true);
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MeteoVue.fxml"));
			BorderPane borderPane = (BorderPane)loader.load();
			borderPane.getStylesheets().add("/style/MeteoVue.css");
			stagePrincipal.setScene(new Scene(borderPane));
			MeteoVue meteoVue = loader.getController();
			ControleurMeteoLune controleur = new ControleurMeteoLune(meteoVue);

			meteoVue.setControleur(controleur);
			meteoVue.setStage(stagePrincipal);
			meteoVue.setPropositionsVilles(villes);
			meteoVue.setVillesFavorites(villesFavorites);

			stagePrincipal.setOnCloseRequest((windowEvent) -> {
				List<Ville> villeTabs = meteoVue.getVillesFavorites();
				try {
					ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(
							NeoFxMeteo.this.getClass().getResource("").getFile() + VILLES_FAVORITES_FICHIER));
					output.writeObject(villeTabs);
					output.close();
				} catch (IOException e) {e.printStackTrace();}

				Platform.exit();
				System.exit(0);
			});

			try {
				ressourcesThread.join();
			} catch (InterruptedException interruptedException) {
				throw new IOException();
			}

			meteoVue.setIconeImages(icones);

			controleur.demanderMeteoVilleActuelle();
			controleur.demanderPhasesLunaires();

			stagePrincipal.show();

		} catch(IOException e) {
			e.printStackTrace();
			Alert alerte = new Alert(Alert.AlertType.ERROR,
					"Un erreur s'est produit lors du chargement des ressources.");
			alerte.setTitle("Erreur de chargement.");
			alerte.setResizable(true);
			alerte.show();
		}
	}
}
