package NeoFxMeteo.vue;

import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class AffichageWeb extends VBox {

    public AffichageWeb(String path) {
        WebView webView = new WebView();
        webView.getEngine().load(path);
        webView.prefHeightProperty().bind(heightProperty());
        getChildren().add(webView);
    }

}
