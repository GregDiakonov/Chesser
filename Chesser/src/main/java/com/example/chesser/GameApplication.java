package com.example.chesser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class GameApplication extends Application {
    @Override
    public void start(Stage gameStage) throws IOException {
        gameStage.setTitle("Play!");
        gameStage.setWidth(850);
        gameStage.setHeight(850);

        Board board = new Board();

        Group root = new Group(UtilityMethods.uploadImage("board.png", 100, 100));
        Group pieces = uploadPieces(board);
        root.getChildren().add(pieces);

        ImageView blackTurnIndicator = UtilityMethods.uploadImage("black_turn_indicator.png", 729, 100);
        ImageView whiteTurnIndicator = UtilityMethods.uploadImage("white_turn_indicator.png", 729, 404);
        ImageView checkPlaque = UtilityMethods.uploadImage("check_plaque.png", 492, 0);
        root.getChildren().add(blackTurnIndicator);
        root.getChildren().add(whiteTurnIndicator);
        root.getChildren().add(checkPlaque);

        checkPlaque.setVisible(false);
        blackTurnIndicator.setVisible(false);

        Button quitButton = configureQuitButton();
        root.getChildren().add(quitButton);

        Scene gameScene = new Scene(root);
        gameScene.setFill(Color.LIGHTGREEN);
        gameStage.setScene(gameScene);
        gameStage.show();

        final boolean[] whiteTurn = {true};

        gameStage.getScene().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(board.isInsideBoard(mouseEvent.getX(), mouseEvent.getY())) {
                    if(!board.marksAdded) {
                        board.marksAdded = true;

                        boolean anyChanges = board.tileSelection(mouseEvent.getX(), mouseEvent.getY(), whiteTurn[0]);
                        if(!anyChanges) {
                            return;
                        }
                        try {
                            board.marks = UtilityMethods.uploadMarks(mouseEvent.getX(), mouseEvent.getY(), board);
                        }
                        catch (IOException e) {
                            Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
                            ioExceptionAlert.setHeaderText("File System Error");
                            ioExceptionAlert.setContentText("There seems to be a problem with the file system. We are sorry.");
                            ioExceptionAlert.showAndWait();
                            return;
                        }
                        root.getChildren().add(board.marks);

                        return;
                    }

                    if(board.marksAdded) {
                        SecondClickResponse response = board.secondTileSelection(mouseEvent.getX(),
                                mouseEvent.getY(), whiteTurn[0]);
                        if(response == SecondClickResponse.INCORRECT) {
                            return;
                        }
                        if(response == SecondClickResponse.SAMETEAM) {
                            board.marksAdded = false;
                            root.getChildren().remove(board.marks);
                            handle(mouseEvent);
                            return;
                        }

                        root.getChildren().remove(board.marks);

                        if(board.promotions) {
                            root.getChildren().remove(pieces);
                            try {
                                root.getChildren().add(uploadPieces(board));
                            }
                            catch (IOException error) {
                                Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
                                ioExceptionAlert.setHeaderText("File System Error");
                                ioExceptionAlert.setContentText("There seems to be a problem with the file system. We are sorry.");
                                ioExceptionAlert.showAndWait();
                            }
                        }

                        whiteTurn[0] = !whiteTurn[0];

                        if(!whiteTurn[0]) {
                            blackTurnIndicator.setVisible(true);
                            whiteTurnIndicator.setVisible(false);
                        } else {
                            blackTurnIndicator.setVisible(false);
                            whiteTurnIndicator.setVisible(true);
                        }

                        if(board.checkCheck(whiteTurn[0])) {
                            if(whiteTurn[0]) {
                                checkPlaque.setVisible(true);
                                checkPlaque.setX(492);
                                checkPlaque.setY(692);
                            } else {
                                checkPlaque.setVisible(true);
                                checkPlaque.setX(492);
                                checkPlaque.setY(0);
                            }
                        } else {
                            checkPlaque.setVisible(false);
                        }
                    }


                }
            }
        });
    }

    private static Group uploadPieces(Board board) throws IOException {
        Group pieces = new Group();

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board.tiles[i][j].status == TileStatus.OCCUPIED) {
                    pieces.getChildren().add(board.tiles[i][j].occupyingPiece.image);
                }
            }
        }

        return pieces;
    }

    private static Button configureQuitButton() {
        Button quitButton = new Button();

        quitButton.setText("Abandon this game");
        quitButton.setLayoutX(100);
        quitButton.setLayoutY(752);

        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MenuApplication menu = new MenuApplication();

                ((Stage)((quitButton.getScene()).getWindow())).close();
                try {
                    menu.start(new Stage());
                }
                catch (IOException e) {
                    Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
                    ioExceptionAlert.setHeaderText("File System Error");
                    ioExceptionAlert.setContentText("There seems to be a problem with the file system. We are sorry.");
                    ioExceptionAlert.showAndWait();
                }
            }
        });

        return quitButton;
    }
}
