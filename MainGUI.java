package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import sample.Generator.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

import static sample.Utils.*;

public class MainGUI {
    public MainGUI(){}

    // Valid input extensions to be dragged on
    private final static List<String> valid_input_extensions = Arrays.asList("png", "jpg");

    @FXML
    AnchorPane background;

    @FXML
    ImageView source_image_view, processed_image_view;

    @FXML
    public void on_drag_entered(){
        // Lighten the background when files are entering
        background.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

    }
    @FXML
    public void on_drag_exited(){
        // Darken the background when files are exiting
        background.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
    }
    @FXML
    public void on_drag_over(DragEvent drag_event){
        // Check if dragged objects are files
        if (drag_event.getDragboard().hasFiles()){
            // Check if the first file is a valid one
            if (!valid_input_extensions.contains(get_extension(drag_event.getDragboard().getFiles().get(0).getName()))) {
                drag_event.consume();
                return;
            }
            drag_event.acceptTransferModes(TransferMode.ANY);
        }
    }
    @FXML
    public void on_drag_dropped(DragEvent drag_event){
        boolean success = false;
        if (drag_event.getDragboard().hasFiles()) {
            try {
                if (core.is_loaded()) core.terminate();
                source_image = new Image(new FileInputStream(drag_event.getDragboard().getFiles().get(0)));
                if (!core.is_loaded()) setup_context_menu();
                setup_image_views();
                core.load_image(source_image);
                core.load_generator(flag);
                start_menu.setText("Start");
                type_menu.setDisable(false);
                setting_box.setDisable(false);
                timeline.stop();
                success = true;
            }
            catch (FileNotFoundException file_not_found_exception){
                success = false;
                source_image = null;
            }
        }
        is_empty |= success;
        drag_event.setDropCompleted(success);
        drag_event.consume();
    }
    @FXML
    public void on_mouse_pressed(MouseEvent mouse_event){
        // Save the pointer's position
        x_offset = stage.getX() - mouse_event.getScreenX();
        y_offset = stage.getY() - mouse_event.getScreenY();
        if (context_menu.isShowing()) context_menu.hide();
    }
    @FXML
    public void on_mouse_dragged(MouseEvent mouse_event){
        // Move the windows as well as the pointer moving
        stage.setX(mouse_event.getScreenX() + x_offset);
        stage.setY(mouse_event.getScreenY() + y_offset);
    }
    @FXML
    public void initialize() throws java.io.IOException{
        is_empty = true;
        source_image = null;
        scale = 1.0;
        attachment = new HashSet<>();
        setup_extension_windows();
        core = new Core();
        initialize_context_menu();
        initialize_timeline();
    }
    private boolean is_empty;               // The existence of image being processed
    private Image source_image;             // The source image
    private BufferedImage display_image;    //
    private WritableImage test;
    private double scale;                   // The ratio of processed image over source image
    private Stage stage, setting_stage;     // The main stage
    private double x_offset, y_offset;
    private ContextMenu context_menu;
    private MenuItem exit_menu, start_menu, stop_menu;
    private CheckMenuItem pure, enhanced, adaptive, setting;
    private Menu type_menu;
    private Core core;
    private HashSet<Stage> attachment;
    private Timeline timeline;
    private int flag = Core.ADAPTIVE_GENETIC_ALGORITHM;
    private VBox setting_box;

    void connect(Stage primary_stage){
        stage = primary_stage;
        stage.setResizable(false);
    }
    private void setup_extension_windows() throws java.io.IOException{
        FXMLLoader fxml_loader = new FXMLLoader();
        Parent root = fxml_loader.load(getClass().getResource("Setting.fxml").openStream());
        setting_stage = new Stage();
        setting_stage.setScene(new Scene(root));
        setting_stage.initStyle(StageStyle.UNDECORATED);
        Setting setting = fxml_loader.getController();
        setting.connect(stage, setting_stage);
        setting_box = setting.setting_box;
        setting_box.setSpacing(10);
    }
    private void setup_image_views() {
        int width = (int) source_image.getWidth();
        int height = (int) source_image.getHeight();
        stage.setWidth(width * scale);
        stage.setHeight(height * scale);
        source_image_view.setFitWidth(width* scale / 5);
        source_image_view.setFitHeight(height * scale / 5);
        source_image_view.setOpacity(0.1);
        source_image_view.setImage(source_image);
        processed_image_view.setFitWidth(width * scale);
        processed_image_view.setFitHeight(height * scale);
        test = new WritableImage(width, height);
        processed_image_view.setImage(test);
    }
    private void initialize_context_menu(){
        exit_menu = new MenuItem("Exit");
        exit_menu.setOnAction(action_event -> {
            if (core != null){
                core.terminate();
            }
            setting_stage.close();
            stage.close();
        });
        start_menu = new MenuItem("Start");
        start_menu.setOnAction(action_event -> {
            switch(start_menu.getText()){
                case "Start":{

                    if (timeline != null) {
                        type_menu.setDisable(true);
                        setting_box.setDisable(true);
                        core.load_generator(flag);
                        core.activate();
                        timeline.playFromStart();
                        start_menu.setText("Pause");
                    }
                    break;
                }
                case "Pause":{
                    if (timeline != null) {
                        core.pause();
                        timeline.stop();
                        start_menu.setText("Continue");
                    }
                    break;
                }
                case "Continue":{
                    if (timeline != null) {
                        core.resume();
                        timeline.play();
                        start_menu.setText("Pause");
                    }
                    break;
                }
            }
        });
        stop_menu = new MenuItem("Stop");
        stop_menu.setOnAction(action_event -> {
            if (timeline != null){
                type_menu.setDisable(false);
                setting_box.setDisable(false);
                start_menu.setText("Start");
                timeline.stop();
                if (core != null) core.terminate();
            }
        });
        enhanced = new CheckMenuItem("Enhanced algorithm");
        pure = new CheckMenuItem("Pure hill-climbing algorithm");
        adaptive = new CheckMenuItem("Default genetic algorithm");
        enhanced.setOnAction(action_event -> {
            enhanced.setSelected(true);
            pure.setSelected(false);
            adaptive.setSelected(false);
            flag = Core.ENHANCED_ALGORITHM;
            core.load_generator(flag);
            core.load_setting(setting_box);
        });
        pure.setOnAction(action_event -> {
            enhanced.setSelected(false);
            pure.setSelected(true);
            adaptive.setSelected(false);
            flag = Core.PURE_HILL_CLIMBING_ALGORITHM;
            core.load_generator(flag);
            core.load_setting(setting_box);
        });
        adaptive.setOnAction(action_event -> {
            enhanced.setSelected(false);
            pure.setSelected(false);
            adaptive.setSelected(true);
            flag = Core.ADAPTIVE_GENETIC_ALGORITHM;
            core.load_generator(flag);
            core.load_setting(setting_box);
        });
        type_menu = new Menu("Type");
        type_menu.getItems().addAll(pure, enhanced, adaptive);
        context_menu = new ContextMenu();
        context_menu.getItems().addAll(exit_menu);
        setting = new CheckMenuItem("Setting");
        setting.setSelected(false);
        setting.setOnAction(action_event -> {
            if (setting.isSelected()) {
                setting_stage.show();
                setting.setSelected(true);
            }
            else {
                setting_stage.hide();
                setting.setSelected(false);
            }
        });
        background.setOnContextMenuRequested(context_menu_event -> {
            context_menu.show(background, context_menu_event.getScreenX(), context_menu_event.getScreenY());
        });
    }
    private void initialize_timeline(){
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame keyframe = new KeyFrame(Duration.millis(200), action_event -> {
            if (core != null){
                core.get_image(test);
            }
        });
        timeline.getKeyFrames().add(keyframe);
    }
    private void setup_context_menu(){
        context_menu.getItems().clear();
        context_menu.getItems().addAll(start_menu, stop_menu, type_menu, setting, exit_menu);
    }
    private void attach(Stage stage){
        attachment.add(stage);
    }
    private void detach(Stage stage){
        attachment.remove(stage);
    }
}
