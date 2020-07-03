package NeoFxMeteo.vue.map;

import NeoFxMeteo.controleur.ControleurMeteoLune;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import NeoFxMeteo.modele.Ville;

/**
 * Cette classe sert à manipuler les variables de la feuille FXML VilleFavoriteCase.
 *
 * @author Turbide, Naomie
 * @author Fortin-Leblanc, Gabriel
 * @author Bourque, Julien
 */
public class VilleFavoriteCase {

    @FXML
    private Label nomVilleLabel;
    @FXML
    private Button retirerFavoriButton;
    @FXML
    private HBox hBox;

    private ControleurMeteoLune controleur;
    private MeteoVue meteoVue;
    private Ville ville;

    @FXML
    private void initialize() {
        nomVilleLabel.setOnMouseClicked((mouseEvent -> controleur.demanderMeteoVille(ville)));
        retirerFavoriButton.setText("\uD83D\uDDD9");
        retirerFavoriButton.setOnAction(actionEvent -> {
            meteoVue.retirerCaseFavorite(hBox);
            meteoVue.getVillesFavorites().remove(ville);
        });
    }

    /**
     * Change la ville que la case rerésente. Le nom de ville sera afficher sur l'étiquette prévue à cet effet.
     *
     * @param ville La ville que représente cette case.
     */
    public void setVille(Ville ville) {
        this.ville = ville;
        nomVilleLabel.setText(ville.getNom());
    }

    /**
     * Change l'instance de MeteoVue.
     *
     * @param meteoVue  L'instance de MeteoVue.
     */
    public void setMeteoVue(MeteoVue meteoVue) {
        this.meteoVue = meteoVue;
    }

    /**
     * Change le contrôleur.
     *
     * @param controleur    Le contrôleur.
     */
    public void setControleur(ControleurMeteoLune controleur) {
        this.controleur = controleur;
    }
}
