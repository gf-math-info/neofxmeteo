package NeoFxMeteo.vue.map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Cette classe sert à manipuler les variables de la feuille FXML AttentePanel.
 *
 * @author Turbide, Naomie
 * @author Fortin-Leblanc, Gabriel
 * @author Bourque, Julien
 */
public class AttentePanel {

    @FXML
    private Label messageAttente;

    /**
     * Change le texte de l'étiquette du message d'attente.
     *
     * @param message   Le texte de l'étiquette du message d'attente.
     */
    public void setMessage(String message) {
        messageAttente.setText(message);
    }
}
