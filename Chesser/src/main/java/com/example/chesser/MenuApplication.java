package com.example.chesser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuApplication extends Application {
    static boolean returnedFromGame = false;

    /**
     * Метод запуска главного меню.
     * @param primaryStage
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Chesser");
        primaryStage.setWidth(1040);
        primaryStage.setHeight(804);

        ImageView wallpaper = UtilityMethods.uploadImage("layout.png", 0, 0);

        Button startButton = configureStartButton();
        Button quitButton = configureQuitButton();

        Group root = new Group(wallpaper, startButton, quitButton);
        Scene entryScene = new Scene(root);
        primaryStage.setScene(entryScene);
        primaryStage.show();
    }

    private static Button configureStartButton() {
        Button startButton = new Button();
        startButton.setText("Start the game");
        startButton.setLayoutX(90);
        startButton.setLayoutY(472);

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)  {
                GameApplication game = new GameApplication();
                try {
                    ((Stage)((startButton.getScene()).getWindow())).close();
                    game.start(new Stage());
                }
                catch (IOException error) {
                    Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
                    ioExceptionAlert.setHeaderText("File System Error");
                    ioExceptionAlert.setContentText("There seems to be a problem with the file system. We are sorry.");
                    ioExceptionAlert.showAndWait();
                }
            }
        });

        return startButton;
    }

    private static Button configureQuitButton() {
        Button quitButton = new Button();
        quitButton.setText("Quit the game");
        quitButton.setLayoutX(90);
        quitButton.setLayoutY(572);

        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                ((Stage)((quitButton.getScene()).getWindow())).close();
            }
        });

        return quitButton;
    }

    public static void main(String[] args) {
        launch();
    }
}