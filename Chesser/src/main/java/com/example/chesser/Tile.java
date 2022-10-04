package com.example.chesser;

public class Tile {
    int letter;
    int number;

    boolean preparedForEnPassant = false;
    boolean enPassant = false;
    TileStatus status = TileStatus.EMPTY;
    Piece occupyingPiece;
}
