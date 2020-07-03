package NeoFxMeteo.vue.map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Cette classe sert à manipuler les variables de la feuille FXML LuneGrandeCase.
 *
 * @author Turbide, Naomie
 * @author Fortin-Leblanc, Gabriel
 * @author Bourque, Julien
 */
public class LuneGrandeCase {

    @FXML
    private ImageView iconeImageView;
    @FXML
    private Label momentLabel, desLuneLabel;

    /**
     * Change l'image de l'icône par l'image passée en argument.
     *
     * @param iconeImage    La nouvelle image de l'icône.
     */
    public void setIconeImage(Image iconeImage) {
        iconeImageView.setImage(iconeImage);
    }

    /**
     * Change le texte de l'étiquette du moment.
     *
     * @param moment    Le texte de l'étiquette du moment.
     */
    public void setMoment(String moment) {
        momentLabel.setText(moment);
    }

    /**
     * Change le texte de l'étiquette de la description.
     *
     * @param description   Le texte de l'étiquette de la description.
     */
    public void setDescription(String description) {
        desLuneLabel.setText(description);
    }
}
