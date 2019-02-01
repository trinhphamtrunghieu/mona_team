package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primary_stage) throws Exception{
        FXMLLoader fxml_loader = new FXMLLoader();
        Parent root = fxml_loader.load(getClass().getResource("MainGUI.fxml").openStream());
        ((MainGUI) fxml_loader.getController()).connect(primary_stage);
        primary_stage.setTitle("ArtOfShape");
        primary_stage.setScene(new Scene(root, 500, 500));
        primary_stage.initStyle(StageStyle.UNDECORATED);
        primary_stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
