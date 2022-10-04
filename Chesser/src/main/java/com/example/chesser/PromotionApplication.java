package com.example.chesser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class PromotionApplication extends Application {
    PieceType selectedPieceType = PieceType.QUEEN;

    @Override
    public void start(Stage stage) {
        stage.setWidth(200);
        stage.setHeight(200);
        stage.setTitle("Choose promotion!");

        Button queenButton = configureQueenButton();
        Button rookButton = configureRookButton();
        Button bishopButton = configureBishopButton();
        Button knightButton = configureKnightButton();

        Group buttonGroup = new Group();
        buttonGroup.getChildren().add(queenButton);
        buttonGroup.getChildren().add(rookButton);
        buttonGroup.getChildren().add(bishopButton);
        buttonGroup.getChildren().add(knightButton);

        Scene scene = new Scene(buttonGroup);
        scene.setFill(Color.BEIGE);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);

        stage.showAndWait();
    }

    Button configureQueenButton() {
        Button promotionButton = new Button();

        promotionButton.setLayoutX(0);
        promotionButton.setLayoutY(0);
        promotionButton.setText("Queen");

        promotionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Stage)((promotionButton.getScene()).getWindow())).close();
            }
        });

        return promotionButton;
    }

    Button configureRookButton() {
        Button promotionButton = new Button();

        promotionButton.setLayoutX(75);
        promotionButton.setLayoutY(0);
        promotionButton.setText("Rook");

        promotionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedPieceType = PieceType.ROOK;

                ((Stage)((promotionButton.getScene()).getWindow())).close();
            }
        });

        return promotionButton;
    }
    Button configureBishopButton() {
        Button promotionButton = new Button();

        promotionButton.setLayoutX(0);
        promotionButton.setLayoutY(50);
        promotionButton.setText("Bishop");

        promotionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedPieceType = PieceType.BISHOP;

                ((Stage)((promotionButton.getScene()).getWindow())).close();
            }
        });

        return promotionButton;
    }
    Button configureKnightButton() {
        Button promotionButton = new Button();

        promotionButton.setLayoutX(75);
        promotionButton.setLayoutY(50);
        promotionButton.setText("Knight");

        promotionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedPieceType = PieceType.KNIGHT;

                ((Stage)((promotionButton.getScene()).getWindow())).close();
            }
        });

        return promotionButton;
    }
}
