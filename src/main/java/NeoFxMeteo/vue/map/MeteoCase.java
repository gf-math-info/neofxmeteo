package NeoFxMeteo.vue.map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Cette classe sert à manipuler les variables dans la feuille FXML MeteoCase.
 *
 * @author Turbide, Naomie
 * @author Fortin-Leblanc, Gabriel
 * @author Bourque, Julien
 */
public class MeteoCase {

    @FXML
    private ImageView iconeImageView;
    @FXML
    private Label momentLabel, tempLabel, precLabel, ventLabel;

    /**
     * Change l'image de l'icône par l'image passée en argument.
     *
     * @param image La nouvelle image de l'icône.
     */
    public void setIconeImage(Image image) {
        iconeImageView.setImage(image);
    }

    /**
     * Change le texte de l'étiquette du moment.
     *
     * @param moment    Le nouveau texte de l'étiquette du moment.
     */
    public void setMoment(String moment) {
        momentLabel.setText(moment);
    }

    /**
     * Change le texte de l'étiquette de la température.
     *
     * @param temperature   Le nouveau texte de l'étiquette de la température.
     */
    public void setTemperature(String temperature) {
        tempLabel.setText(temperature);
    }

    /**
     * Change le texte de l'étiquette des précipitations.
     *
     * @param precipication Le nouveau texte de l'étiquette des précipitations.
     */
    public void setPrecipication(String precipication) {
        precLabel.setText(precipication);
    }

    /**
     * Change le texte de l'étiquette de la vitesse du vent.
     *
     * @param vent  Le nouveau texte de l'étiquette de la vitesse de vent.
     */
    public void setVent(String vent) {
        ventLabel.setText(vent);
    }
}
