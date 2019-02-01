package sample;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Setting {
    @FXML
    public void on_mouse_pressed(MouseEvent mouse_event){
        // Save the pointer's position
        x_offset = stage.getX() - mouse_event.getScreenX();
        y_offset = stage.getY() - mouse_event.getScreenY();
    }
    @FXML
    public void on_mouse_dragged(MouseEvent mouse_event){
        // Move the windows as well as the pointer moving
        stage.setX(mouse_event.getScreenX() + x_offset);
        stage.setY(mouse_event.getScreenY() + y_offset);
    }
    private double x_offset, y_offset;
    private Stage main_stage, stage;

    public void connect(Stage main_stage, Stage stage){
        this.main_stage = main_stage;
        this.stage = stage;
    }

    @FXML
    public VBox setting_box;
}
