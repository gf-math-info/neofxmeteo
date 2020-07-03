package NeoFxMeteo.vue.map;

import NeoFxMeteo.NeoFxMeteo;
import NeoFxMeteo.controleur.ControleurMeteoLune;
import NeoFxMeteo.vue.AffichageWeb;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import NeoFxMeteo.modele.Ville;
import NeoFxMeteo.modele.lune.LuneData;
import NeoFxMeteo.modele.meteo.MeteoActuelleData;
import NeoFxMeteo.modele.meteo.MeteoData;
import NeoFxMeteo.vue.AffichageMeteo;
import NeoFxMeteo.vue.RechercheTextField;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class MeteoVue implements AffichageMeteo {

    @FXML
    private BorderPane borderPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button favoriButton, rechercheButton, positionButton, actualiserButton, aProposButton, aideButton;
    @FXML
    private RechercheTextField rechercheTextField;
    @FXML
    private HBox favoriHBox, meteoJourneeHBox, meteoHeuresHBox, meteoJoursHBox, grandesPhasesHBox;
    @FXML
    private VBox petitesPhasesVBox;
    @FXML
    private Canvas alerteCanvas;
    @FXML
    private Label villeLabel, tempLabel, tempResLabel, ventLabel, desMeteoLabel, pourcentageLabel, desLuneLabel,
            infoFournisseurLabel;
    @FXML
    private ImageView meteoImageView, luneImageView;

    private Stage stage;
    private ControleurMeteoLune controleur;
    private AtomicBoolean alerteEnCours;
    private HashMap<String, Image> iconeImages;
    private Ville ville;
    private List<Ville> villesFavorites;

    private VBox attenteVBox;
    private AttentePanel attenteMap;

    @FXML
    private void initialize() {
        alerteEnCours = new AtomicBoolean();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AttentePanel.fxml"));
            attenteVBox = (VBox) loader.load();
            attenteMap = loader.getController();
        } catch(IOException e) {
            e.printStackTrace();
        }

        favoriButton.setOnAction(actionEvent -> {
            if(favoriHBox.getChildren().size() >= 3)
                return;
            if(villesFavorites.contains(ville))
                return;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VilleFavoriteCase.fxml"));
                favoriHBox.getChildren().add((HBox) loader.load());
                VilleFavoriteCase villeFavoriteCase = loader.getController();

                villeFavoriteCase.setControleur(controleur);
                villeFavoriteCase.setMeteoVue(this);
                villeFavoriteCase.setVille(ville);

                villesFavorites.add(ville);
            } catch(IOException e) {
                e.printStackTrace();
            }
        });

        rechercheButton.setOnAction(actionEvent -> {
            Ville ville = rechercheTextField.getVilleEntree();
            if(ville == null)
                return;

            new Thread(() -> controleur.demanderMeteoVille(ville)).start();
        });

        positionButton.setOnAction(actionEvent -> {
            new Thread(() -> controleur.demanderMeteoVilleActuelle()).start();
        });

        actualiserButton.setOnAction(actionEvent -> {
            new Thread(() -> controleur.actualisation()).start();
        });

        aProposButton.setOnAction(actionEvent -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new AffichageWeb(MeteoVue.this.getClass().getResource("/APropos.html").toExternalForm())));
            stage.setMaximized(true);
            stage.show();
        });

        aideButton.setOnAction(actionEvent -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new AffichageWeb(MeteoVue.this.getClass().getResource("/Aide.html").toExternalForm())));
            stage.setMaximized(true);
            stage.show();
        });

        rechercheTextField.setOnAction(actionEvent -> rechercheButton.fire());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setControleur(ControleurMeteoLune controleur) {
        this.controleur = controleur;
    }

    public void setIconeImages(HashMap<String, Image> iconeImages) {
        this.iconeImages = iconeImages;
    }

    public void setPropositionsVilles(List<Ville> propositions) {
        rechercheTextField.setPropositions(propositions);
    }

    public void setVillesFavorites(List<Ville> villesFavorites) {
        this.villesFavorites = villesFavorites;
        villesFavorites.forEach(villeFavorite -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VilleFavoriteCase.fxml"));
                favoriHBox.getChildren().add((HBox) loader.load());
                VilleFavoriteCase villeFavoriteCase = loader.getController();
                villeFavoriteCase.setVille(villeFavorite);
                villeFavoriteCase.setMeteoVue(MeteoVue.this);
                villeFavoriteCase.setControleur(controleur);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public List<Ville> getVillesFavorites() {
        return villesFavorites;
    }

    /**
     * Met à jour l'affichage de la météo actuelle.
     *
     * @param meteoActuelleData	Les nouvelles données de la météo actuelle.
     */
    @Override
    public void actualiserMeteoActuelle(MeteoActuelleData meteoActuelleData) {
        Platform.runLater(() -> {
            tempLabel.setText("Temp. : " + arrondirDixieme(meteoActuelleData.getTemperature()) + " \u00B0C");
            tempResLabel.setText("Temp. res. : " + arrondirDixieme(meteoActuelleData.getTemperatureRessentie()) +
                    " \u00B0C");
            ventLabel.setText("Vent : " + getDirectionRepresentation(meteoActuelleData.getDirectionVent()) + " " +
                    arrondirDixieme(meteoActuelleData.getVitesseVent()) + " Km/h");
            meteoImageView.setImage(iconeImages.get(meteoActuelleData.getIconeID()));
            desMeteoLabel.setText(meteoActuelleData.getDescription().substring(0, 1).toUpperCase() +
                    meteoActuelleData.getDescription().substring(1));
        });
    }

    /**
     * Met à jour l'affichage de la météo pour les différentes parties de la
     * journée.
     *
     * @param meteoJourneeDatas	Les nouvelles données de la météo des
     * 							différentes parties de la journée.
     */
    @Override
    public void actualiserMeteoJournée(List<MeteoData> meteoJourneeDatas) {
        Platform.runLater(() -> {
            meteoJourneeHBox.getChildren().clear();
            meteoJourneeDatas.forEach(meteoData -> {
                MeteoCase map = null;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MeteoCase.fxml"));
                    meteoJourneeHBox.getChildren().add((VBox) loader.load());
                    map = (MeteoCase) loader.getController();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                switch (meteoData.getHeure()) {
                    case MeteoData.NUIT_MATIN:
                        map.setMoment("Matin");
                        break;

                    case MeteoData.MATIN_JOUR:
                        map.setMoment("Jour");
                        break;

                    case MeteoData.JOUR_SOIR:
                        map.setMoment("Soir");
                        break;

                    case MeteoData.SOIR_NUIT:
                        map.setMoment("Nuit");
                        break;
                }
                map.setIconeImage(iconeImages.get(meteoData.getIconeID()));
                map.setTemperature(arrondirDixieme(meteoData.getTemperature()) + " \u00B0C");
                map.setPrecipication(arrondirDixieme(meteoData.getQuantitePluie()) + " mm");
                map.setVent(getDirectionRepresentation(meteoData.getDirectionVent()) + " " +
                        arrondirDixieme(meteoData.getVitesseVent()) + " Km/h");
            });
        });
    }

    /**
     * Met à jour l'affichage de la météo pour les prochaines heures.
     *
     * @param meteoHeuresDatas	Les nouvelles données de la météo pour les
     * 							prochaines heures.
     */
    @Override
    public void actualiserMeteoHeures(List<MeteoData> meteoHeuresDatas) {
        Platform.runLater(() -> {
            meteoHeuresHBox.getChildren().clear();
            meteoHeuresDatas.forEach(meteoData -> {
                MeteoCase map = null;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MeteoCase.fxml"));
                    meteoHeuresHBox.getChildren().add((VBox) loader.load());
                    map = (MeteoCase) loader.getController();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                map.setMoment(meteoData.getHeure() + ":00");
                map.setIconeImage(iconeImages.get(meteoData.getIconeID()));
                map.setTemperature(arrondirDixieme(meteoData.getTemperature()) + " \u00B0C");
                map.setPrecipication(arrondirDixieme(meteoData.getQuantitePluie()) + " mm");
                map.setVent(getDirectionRepresentation(meteoData.getDirectionVent()) + " " +
                        arrondirDixieme(meteoData.getVitesseVent()) + " Km/h");
            });
        });
    }

    /**
     * Met à jour l'affichage de la météo pour les prochains jours.
     *
     * @param meteoJoursDatas	Les nouvelles données de la météo pour les
     * 							prochains jours.
     */
    @Override
    public void actualiserMeteoJours(List<MeteoData> meteoJoursDatas) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMM", Locale.CANADA_FRENCH);
        Platform.runLater(() -> {
            meteoJoursHBox.getChildren().clear();
            meteoJoursDatas.forEach(meteoData -> {
                MeteoCase map = null;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MeteoCase.fxml"));
                    meteoJoursHBox.getChildren().add((VBox) loader.load());
                    map = (MeteoCase) loader.getController();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                map.setMoment(simpleDateFormat.format(new Date(1000 * meteoData.getTempsUNIX())));
                map.setIconeImage(iconeImages.get(meteoData.getIconeID()));
                map.setTemperature(arrondirDixieme(meteoData.getTemperature()) + " \u00B0C");
                map.setPrecipication(arrondirDixieme(meteoData.getQuantitePluie()) + " mm");
                map.setVent(getDirectionRepresentation(meteoData.getDirectionVent()) + " " +
                        arrondirDixieme(meteoData.getVitesseVent()) + " Km/h");
            });
        });
    }

    /**
     * Met à jour l'affichage de la phase lunaire actuelle.
     *
     * @param luneData	Les nouvelles données de la phase lunaire actuelle.
     */
    @Override
    public void actualiserLuneActuelle(LuneData luneData) {
        Platform.runLater(() -> {
            luneImageView.setImage(getLuneImageCouverte(luneData.getPourcentage()));
            pourcentageLabel.setText(String.valueOf(arrondirDixieme((float)luneData.getPourcentage()) + "%"));
            desLuneLabel.setText(luneData.getNom());
        });
    }

    /**
     * Met à jour l'affichage des prochaines phases lunaires.
     *
     * @param luneDatas	Les nouvelles données des prochaines phases lunaires.
     */
    @Override
    public void actualiserProchainesLunes(Set<LuneData> luneDatas) {
        Platform.runLater(() -> {
            grandesPhasesHBox.getChildren().clear();
            petitesPhasesVBox.getChildren().clear();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd MMM", Locale.CANADA_FRENCH);
            int i = 0;
            LuneData luneData;
            Iterator<LuneData> it = luneDatas.iterator();

            LuneGrandeCase luneGrandeCase = null;
            while(it.hasNext() && i < 5) {
                luneData = it.next();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LuneGrandeCase.fxml"));
                    grandesPhasesHBox.getChildren().add((VBox) loader.load());
                    luneGrandeCase = (LuneGrandeCase) loader.getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                luneGrandeCase.setMoment(simpleDateFormat.format(luneData.getCalendrier().getTime()));
                luneGrandeCase.setIconeImage(getLuneImageCouverte(luneData.getPourcentage()));
                luneGrandeCase.setDescription(luneData.getNom());
                i++;
            }

            LunePetiteCase lunePetiteCase = null;
            while(it.hasNext()) {
                luneData = it.next();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LunePetiteCase.fxml"));
                    petitesPhasesVBox.getChildren().add((HBox) loader.load());
                    lunePetiteCase = (LunePetiteCase) loader.getController();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                lunePetiteCase.setMoment(simpleDateFormat.format(luneData.getCalendrier().getTime()));
                lunePetiteCase.setIconeImage(getLuneImageCouverte(luneData.getPourcentage()));
            }
        });
    }

    @Override
    public void afficherChargement(String messageChargement) {
        Platform.runLater(() -> {
            String msg = messageChargement == null ? "" : messageChargement;
            borderPane.setCenter(attenteVBox);
            attenteMap.setMessage(msg);
        });
    }

    /**
     * Signale à l'utilisateur que la position est introuvable.
     */
    @Override
    public void afficherPositionIntrouvable() {
        Platform.runLater(() -> {
            attenteMap.setMessage("La position est introuvable." + System.lineSeparator() +
                    "Veuillez vérifier votre connexion Internet ou " + System.lineSeparator() +
                    "entrez votre ville dans la barre de recherche.");
            borderPane.setCenter(attenteVBox);
        });
    }

    /**
     * Signale à l'utilisateur que les données de la météo sont introuvables.
     */
    @Override
    public void afficherMeteoIntrouvable() {
        Platform.runLater(() -> {
            attenteMap.setMessage("Les données de la météo sont introuvables." + System.lineSeparator() +
                    "Veuillez vérifier votre connexion Internet.");
            borderPane.setCenter(attenteVBox);
        });
    }

    /**
     * Affiche à l'utilisateur les données de la météo et les phases lunaires.
     */
    @Override
    public void afficherMeteoLune() {
        Platform.runLater(() -> borderPane.setCenter(scrollPane));
    }

    /**
     * Met à jour le message d'alerte météo. Si "null" est passé en argument,
     * alors aucun message ne sera affiché.
     *
     * @param message	Le message d'alerte à afficher, ou "null" si aucun message ne doit être affiché.
     */
    @Override
    public void setMessageAlerte(String message) {
        Platform.runLater(() -> {
            if(message == null) {

                alerteEnCours.set(false);
                alerteCanvas.setHeight(0);

            } else {

                alerteEnCours.set(true);
                alerteCanvas.setHeight(50);
                new Thread(() -> {

                    Text messageText = new Text(message);
                    final double LARGEUR_TEXTE = messageText.getLayoutBounds().getWidth();
                    final double VITESSE = 20, NANOSEC_A_SEC = 1e-9;


                    GraphicsContext graphicsContext = alerteCanvas.getGraphicsContext2D();
                    AtomicLong dernierMoment = new AtomicLong(System.nanoTime()), maintenant = new AtomicLong();
                    AtomicReference<Double> x = new AtomicReference<>(alerteCanvas.getWidth());
                    final double y = 5;

                    while(alerteEnCours.get()) {

                        maintenant.set(System.nanoTime());
                        x.set(x.get() - VITESSE * (maintenant.get() - dernierMoment.get()) * NANOSEC_A_SEC);
                        dernierMoment = maintenant;

                        if (x.get() < -LARGEUR_TEXTE)
                            x.set(alerteCanvas.getWidth());

                        Platform.runLater(() -> {

                            graphicsContext.setFill(Color.RED);
                            graphicsContext.fillRect(0, 0, alerteCanvas.getWidth(), alerteCanvas.getHeight());
                            graphicsContext.fillText(message, x.get(), y);

                        });

                    }

                }).start();

            }
        });
    }

    /**
     * Met à jour le message d'information sur les fournisseurs de données. Si
     * "null" est passé en argument, alors aucun message ne sera affiché.
     *
     * @param message	Le message d'information à afficher, ou "null" si aucun
     * 					message ne doit être affiché.
     */
    @Override
    public void setMessageInfoFournisseur(String message) {
        Platform.runLater(() -> infoFournisseurLabel.setText(message));
    }

    /**
     * Met à jour l'instance de calendrier du dernier moment d'actualisation.
     *
     * @param calendrier	L'instance de calendrier du dernier moment
     * 						d'actualisation.
     */
    @Override
    public void setMomentDerniereActualisation(Calendar calendrier) {
        Platform.runLater(() ->
                stage.setTitle("NeoFxMétéo - " + new SimpleDateFormat("HH:mm").format(calendrier.getTime())));
    }

    /**
     * Change le nom de la ville par celui passée en argument. Cela permet à
     * l'utilisateur de connaître de quelle ville provienne les données météos
     * affichées.
     *
     * @param ville	La ville.
     */
    @Override
    public void setVille(Ville ville) {
        this.ville = ville;
        Platform.runLater(() -> villeLabel.setText(ville.getNom()));
    }

    /**
     * Retourne le contrôleur principal.
     *
     * @return	Le contrôleur principal.
     */
    @Override
    public ControleurMeteoLune getControleurPrincipal() {
        return null;
    }

    /**
     * Retire la case d'une ville favorite passée en argument.
     * @param caseFavorite  La case favorite retirée.
     */
    public void retirerCaseFavorite(HBox caseFavorite) {
        Platform.runLater(() -> favoriHBox.getChildren().remove(caseFavorite));
    }

    /**
     * Retourne la représentation en chaîne de caractère selon une direction de
     * vent passée en argument.
     *
     * @param direction	Une constante représentant la direction du vent.
     * @return			Une chaîne de caractère représentant la direction du
     * 					vent.
     */
    private String getDirectionRepresentation(byte direction) {
        String directionString;
        switch(direction) {
            case MeteoData.N:
                directionString = "N";
                break;

            case MeteoData.NE:
                directionString = "NE";
                break;

            case MeteoData.E:
                directionString = "E";
                break;

            case MeteoData.SE:
                directionString = "SE";
                break;

            case MeteoData.S:
                directionString = "S";
                break;

            case MeteoData.SW:
                directionString = "SO";
                break;

            case MeteoData.W:
                directionString = "O";
                break;

            default:
                directionString = "NO";

        }
        return directionString;
    }

    /**
     * Arrondit le nombre passé en argument au dixième et retourne le résultat.
     *
     * @param nombre    Le nombre à arrondir.
     * @return          Le nombre arrondi.
     */
    private float arrondirDixieme(float nombre) {
        return ((int)(nombre * 10 + 0.5)) / 10f;
    }

    /**
     * Retourne une image modifiée de la lune afin d'afficher la phase réelle en
     * fonction du pourcentage passé en argument.
     *
     * @param pourcentage	Le pourcentage d'avancement de la phase lunaire.
     * @return 				L'image modifiée avec la phase.
     */
    private Image getLuneImageCouverte(double pourcentage) {
        if (pourcentage == 1) {
            pourcentage = 0;
        }
        double constanteProg;
        boolean nouvLune;
        if (pourcentage <= 0.25) {
            constanteProg = 1 - pourcentage * 4;
            nouvLune = true;
        } else if (pourcentage <= 0.50) {
            constanteProg = -(pourcentage - 0.25) * 4;
            nouvLune = true;

        } else if (pourcentage <= 0.75) {
            constanteProg = 1 - (pourcentage - 0.5) * 4;
            nouvLune = false;
        } else {
            constanteProg = -(pourcentage - 0.75) * 4;
            nouvLune = false;
        }

        ImageView imageV = new ImageView(iconeImages.get("lune"));
        int largeur = (int) imageV.getImage().getWidth(),
                hauteur = (int) imageV.getImage().getHeight();


        WritableImage imageEcrite = new WritableImage(largeur, hauteur);
        PixelReader reader = imageV.getImage().getPixelReader();
        PixelWriter writer = imageEcrite.getPixelWriter();
        Function<Double, Double> fonction = y -> {
            double val = (Math.sqrt((-(y * y) + 1) * Math.abs(constanteProg)));
            if (constanteProg < 0)
                val *= -1;
            return val;
        };

        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                double yRel = ((hauteur / 2d) - y) / (hauteur / 2d),
                        xRel = ((-largeur / 2d) + x) / (largeur / 2d);
                double xRelMax = fonction.apply(yRel);
                Color couleur = reader.getColor(x, y);

                if (couleur.getOpacity() > 0) {
                    if (nouvLune) {
                        if (xRel < xRelMax) {
                            writer.setColor(x, y,
                                    couleur.darker().darker().darker());
                        } else {
                            writer.setColor(x, y, couleur);
                        }
                    } else if (xRel < xRelMax) {
                        writer.setColor(x, y, couleur);
                    } else {
                        writer.setColor(x, y,
                                couleur.darker().darker().darker());
                    }
                }
            }
        }
        return new ImageView(imageEcrite).getImage();
    }
}
