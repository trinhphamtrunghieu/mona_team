package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static String get_extension(String file_name){
        int last_index = file_name.lastIndexOf(".");
        if (last_index > 0 && last_index < file_name.length() - 1) return file_name.substring(last_index + 1).toLowerCase();
        return "";
    }
    public static double absolute(double value){
        return value > 0 ? value : - value;
    }
    public static double square(double value){
        return value * value;
    }
    public static double square_root(double value) {
        return Math.sqrt(value);
    }
    public static int inclusive_random_int(int low, int high){
        return exclusive_random_int(low, high + 1);
    }
    public static int exclusive_random_int(int low, int high){
        return ThreadLocalRandom.current().nextInt(low, high);
    }
    public static double exclusive_random_double(double low, double high){
        return ThreadLocalRandom.current().nextDouble(low, high);
    }
    public static int constrain(int value, int low, int high){
        return value < low ? low : value > high ? high : value;
    }
    public static boolean attempt(double rate){
        return ThreadLocalRandom.current().nextDouble() < rate;
    }
    public static boolean attempt(int chance){
        return exclusive_random_int(0, chance) == 1;
    }
    public static int max(int a, int b){
        return a > b ? a : b;
    }
    public static int min(int a, int b){
        return a > b ? b : a;
    }
    public static Slider[] create_dual_sliders(IntegerProperty min_property, IntegerProperty max_property,int min, int max){
        Slider min_slider, max_slider;
        min_slider = new Slider();
        min_slider.setMin(min);
        min_slider.setMax(max);
        min_slider.setValue(min_property.get());
        max_slider = new Slider();
        max_slider.setMin(min);
        max_slider.setMax(max);
        max_slider.setValue(max_property.get());
        min_property.bindBidirectional(min_slider.valueProperty());
        max_property.bindBidirectional(max_slider.valueProperty());
        min_slider.valueProperty().addListener((observable, old_value, new_value) -> {
            int min_value = new_value.intValue();
            int max_value = (int) max_slider.getValue();
            if (min_value > max_value) {
                max_slider.setValue(min_value);
            }
            min_slider.setValue(min_value);
        });
        max_slider.valueProperty().addListener((observable, old_value, new_value) -> {
            int min_value = (int) min_slider.getValue();
            int max_value = new_value.intValue();
            if (min_value > max_value) {
                min_slider.setValue(max_value);
            }
            max_slider.setValue(max_value);
        });
        return new Slider[]{min_slider, max_slider};
    }
    public static Slider create_slider(IntegerProperty property, int min, int max){
        Slider slider = new Slider();
        slider.setValue(property.get());
        slider.setMin(min);
        slider.setMax(max);
        property.bindBidirectional(slider.valueProperty());
        return slider;
    }
    public static Slider create_slider(DoubleProperty property, double min, double max){
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(property.get());
        property.bindBidirectional(slider.valueProperty());
        return slider;
    }
    public static HBox create_hbox(DoubleProperty property, String text){
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        Label label = new Label(text);
        Label value = new Label();
        value.textProperty().bind(property.asString("%.4f"));
        hbox.getChildren().addAll(label, value);
        return hbox;
    }
    public static HBox create_hbox(IntegerProperty property, String text){
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        Label label = new Label(text);
        Label value = new Label();
        value.textProperty().bind(property.asString("%d"));
        hbox.getChildren().addAll(label, value);
        return hbox;
    }
    public static void append(VBox vbox, DoubleProperty property, String text, double min, double max){
        Slider slider = create_slider(property, min, max);
        HBox hbox = create_hbox(property, text);
        vbox.getChildren().addAll(hbox, slider);
    }
    public static void append(VBox vbox, IntegerProperty property, String text, int min, int max){
        Slider slider = create_slider(property, min, max);
        HBox hbox = create_hbox(property, text);
        vbox.getChildren().addAll(hbox, slider);
    }
    public static void append(VBox vbox, IntegerProperty min_property, IntegerProperty max_property, String min_text, String max_text, int min, int max){
        Slider[] sliders = create_dual_sliders(min_property, max_property, min, max);
        HBox min_hbox = create_hbox(min_property, min_text);
        HBox max_hbox = create_hbox(max_property, max_text);
        vbox.getChildren().addAll(min_hbox, sliders[0]);
        vbox.getChildren().addAll(max_hbox, sliders[1]);
    }
}
