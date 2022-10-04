package com.example.chesser;

import javafx.scene.image.ImageView;

public class Piece {
    PieceType type;
    boolean white;

    ImageView image;

    boolean moved = false;

    Piece(boolean isWhite, PieceType typeConfig, ImageView imageConfig) {
        white = isWhite;
        type = typeConfig;
        image = imageConfig;
    }
}
