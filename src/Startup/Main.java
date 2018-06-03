package Startup;

import Settings.Settings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage; // **Declare static Stage**

    private void setPrimaryStage(Stage stage) {
        Main.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return Main.primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Settings.loadSettings();

        Parent root = FXMLLoader.load(getClass().getResource("Database.fxml"));
        primaryStage.setTitle("Series Database");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        setPrimaryStage(primaryStage);

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
