package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        primaryStage.setTitle("Sistema bancario");
        primaryStage.setScene(new Scene(root, 1024, 640));
        primaryStage.show();

        //SystemManager manager = SystemManager.getInstance();

        //manager.novoCliente(10);
        //manager.novoCliente(30);
        //manager.novoCliente(28);


    }


    public static void main(String[] args) {
        launch(args);
    }
}
