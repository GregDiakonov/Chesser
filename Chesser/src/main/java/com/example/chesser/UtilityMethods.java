package com.example.chesser;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.IOException;

public class UtilityMethods {
    final static private String textureFilePath = ".\\src\\main\\java\\com\\example\\chesser\\textures\\";

    static public ImageView uploadImage(String fileName, int xPosition, int yPosition)
            throws IOException {
        Image imageBuffer = new Image(new FileInputStream(textureFilePath + fileName));
        ImageView image = new ImageView(imageBuffer);

        image.setX(xPosition);
        image.setY(yPosition);
        image.setPreserveRatio(true);

        return image;
    }

    static public ImageView uploadImage(String fileName, Tile tile, boolean piece)
            throws IOException {
        double[] tileCoords = tileToCoords(tile);
        Image imageBuffer = new Image(new FileInputStream(textureFilePath + fileName));
        ImageView image = new ImageView(imageBuffer);

        if(piece) {
            image.setX(tileCoords[0]+7);
            image.setY(tileCoords[1]+7);
            image.setPreserveRatio(true);

            return image;
        }
        image.setX(tileCoords[0]);
        image.setY(tileCoords[1]);
        image.setPreserveRatio(true);

        return image;
    }

    static public Group uploadMarks(double x, double y, Board board) throws IOException {
        Group marks = new Group();

        int[] tileCoords = board.findTileByCoords(x, y);
        Tile selected = board.tiles[tileCoords[0]][tileCoords[1]];
        marks.getChildren().add(uploadImage("highlight.png",
                board.tiles[tileCoords[0]][ tileCoords[1]], false));

        double[] selectedTileCoords = tileToCoords(selected);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.tiles[i][j].status == TileStatus.READY_FOR_TURN ||
                        board.tiles[i][j].status == TileStatus.READY_FOR_CASTLING) {
                    marks.getChildren().add(uploadImage("turn_possible.png", board.tiles[i][j], false));
                }
                if (board.tiles[i][j].status == TileStatus.READY_FOR_CAPTURE) {
                    marks.getChildren().add(uploadImage("capture_possible.png", board.tiles[i][j], false));
                }
            }
        }

        return marks;
    }

    static public double[] tileToCoords(Tile tile) {
        double[] coords = new double[2];
        coords[0] = 100 + (tile.letter - 1) * 74;
        coords[1] = 692 - (tile.number) * 74;
        return coords;
    }
}
