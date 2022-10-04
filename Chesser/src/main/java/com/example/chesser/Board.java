package com.example.chesser;

import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Board {
    private Piece currentPiece;
    private Tile previousTile;

    Group marks;

    boolean marksAdded = false;

    boolean promotions = false;

    Tile[][] tiles = new Tile[8][8];
    Tile[][] previousTiles = new Tile[8][8];
    int xBegin = 100;
    int yBegin = 100;
    int xEnd = 692;
    int yEnd = 692;

    boolean tileSelection(double x, double y, boolean white) {
        marksAdded = true;
        int [] tileCoords = findTileByCoords(x, y);
        Tile selectedTile = tiles[tileCoords[0]][tileCoords[1]];
        if(selectedTile.status == TileStatus.EMPTY || selectedTile.occupyingPiece.white != white) {
            return false;
        }

        previousTile = selectedTile;
        currentPiece = selectedTile.occupyingPiece;
        turnOptions(selectedTile);

        return true;
    }

    SecondClickResponse secondTileSelection(double x, double y, boolean white) {
        int[] tileCoords = findTileByCoords(x, y);
        Tile selectedTile = tiles[tileCoords[0]][tileCoords[1]];

        if(selectedTile.status == TileStatus.OCCUPIED && white == selectedTile.occupyingPiece.white) {
            marksAdded = false;
            clearMarks();
            return SecondClickResponse.SAMETEAM;
        }

        if(selectedTile.status == TileStatus.EMPTY || selectedTile.status == TileStatus.OCCUPIED) {
            return SecondClickResponse.INCORRECT;
        }

        if(selectedTile.status == TileStatus.READY_FOR_CAPTURE) {
            movePieces(selectedTile, currentPiece, true);
        }

        if(selectedTile.status == TileStatus.READY_FOR_TURN) {
            movePieces(selectedTile, currentPiece, false);
        }

        if(selectedTile.status == TileStatus.READY_FOR_CASTLING) {
            castle(selectedTile);
        }

        marksAdded = false;
        clearMarks();

        updateEnPassantStatuses();

        return SecondClickResponse.CORRECT;
    }

    void clearMarks() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(tiles[i][j].status == TileStatus.READY_FOR_TURN) {
                    tiles[i][j].status = TileStatus.EMPTY;
                }

                if(tiles[i][j].status == TileStatus.READY_FOR_CAPTURE
                        || tiles[i][j].status == TileStatus.READY_FOR_CASTLING) {
                    tiles[i][j].status = TileStatus.OCCUPIED;
                }
            }
        }
    }

    void updateEnPassantStatuses() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(tiles[i][j].enPassant) {
                    tiles[i][j].enPassant = false;
                }

                if(tiles[i][j].preparedForEnPassant) {
                    tiles[i][j].preparedForEnPassant = false;
                    tiles[i][j].enPassant = true;
                }
            }
        }
    }

    boolean isInsideBoard(double x, double y) {
        if(x >= xBegin && y >= yBegin) {
            if(x <= xEnd && y <= yEnd) {
                return true;
            }
        }
        return false;
    }

    public int[] findTileByCoords(double x, double y) {
        int[] answer = new int[2];
        x -= xBegin;
        y -= yBegin;

        int tileLetter = ((int)x / 74);
        if(x % 74 != 0) {
            ++tileLetter;
        }

        y = 592 - y;
        int tileNumber = ((int)y / 74);
        if(y % 74 != 0) {
            ++tileNumber;
        }

        answer[0] = tileNumber - 1;
        answer[1] = tileLetter - 1;

        return answer;

    }

    /*
        Методы, отвечающие за состояние доски.
     */

    Board() throws IOException {
        for(int i=0; i<64; i++) {
            Tile newTile = new Tile();
            newTile.letter = calculateLetter(i);
            newTile.number = calculateNumber(i);
            switch (i) {
                case 0, 7:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(true, PieceType.ROOK,
                            UtilityMethods.uploadImage("white_rook.png", newTile, true));
                    break;
                case 56, 63:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(false, PieceType.ROOK,
                            UtilityMethods.uploadImage("black_rook.png", newTile, true));
                    break;
                case 1, 6:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(true, PieceType.KNIGHT,
                            UtilityMethods.uploadImage("white_knight.png", newTile, true));
                    break;
                case 57, 62:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(false, PieceType.KNIGHT,
                            UtilityMethods.uploadImage("black_knight.png", newTile, true));
                    break;
                case 2, 5:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(true, PieceType.BISHOP,
                            UtilityMethods.uploadImage("white_bishop.png", newTile, true));
                    break;
                case 58, 61:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(false, PieceType.BISHOP,
                            UtilityMethods.uploadImage("black_bishop.png", newTile, true));
                    break;
                case 3:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(true, PieceType.QUEEN,
                            UtilityMethods.uploadImage("white_queen.png", newTile, true));
                    break;
                case 59:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(false, PieceType.QUEEN,
                            UtilityMethods.uploadImage("black_queen.png", newTile, true));
                    break;
                case 4:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(true, PieceType.KING,
                            UtilityMethods.uploadImage("white_king.png", newTile, true));
                    break;
                case 60:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(false, PieceType.KING,
                            UtilityMethods.uploadImage("black_king.png", newTile, true));
                    break;
                case 8, 9, 10, 11, 12, 13, 14, 15:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(true, PieceType.PAWN,
                            UtilityMethods.uploadImage("white_pawn.png", newTile, true));
                    break;
                case 48, 49, 50, 51, 52, 53, 54, 55:
                    newTile.status = TileStatus.OCCUPIED;
                    newTile.occupyingPiece = new Piece(false, PieceType.PAWN,
                            UtilityMethods.uploadImage("black_pawn.png", newTile, true));
                    break;
            }
            tiles[i/8][i%8] = newTile;
            }
        }


    private static int calculateLetter(int i) {
        int answer = (i+1) % 8;
        if (answer == 0) {
            return 8;
        }
        return answer;
    }

    private static int calculateNumber(int i) {
        int answer = (i / 8) + 1;
        return answer;
    }

    /*
        Методы, отвечающие за выделение клеток, доступных для атаки.
     */
    void turnOptions(Tile origin) {
        switch (currentPiece.type) {
            case KING:
                kingMove(origin);
                break;
            case QUEEN:
                queenMove(origin);
                break;
            case ROOK:
                rookMove(origin, false);
                break;
            case BISHOP:
                bishopMove(origin, false);
                break;
            case KNIGHT:
                knightMove(origin);
                break;
            case PAWN:
                pawnMove(origin);
                break;

        }
        return;
    }

    void kingMove(Tile origin) {
        rookMove(origin, true);
        bishopMove(origin, true);

        if(!origin.occupyingPiece.moved) {
            checkCastling(origin.occupyingPiece.white);
        }
    }

    void queenMove(Tile origin) {
        rookMove(origin, false);
        bishopMove(origin, false);

    }

    void rookMove(Tile origin, boolean king) {
        int rayLength = 0;
        while(true) {
            ++rayLength;
            if (origin.number + rayLength > 8) {
                break;
            }
            if(king && isTileAttacked(tiles[origin.number + rayLength - 1][origin.letter - 1], origin.occupyingPiece.white)) {
                break;
            }
            if (tiles[origin.number + rayLength - 1][origin.letter - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number + rayLength - 1][origin.letter - 1].occupyingPiece.white == currentPiece.white) {
                break;
            }
            if (tiles[origin.number + rayLength - 1][origin.letter - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number + rayLength - 1][origin.letter - 1].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number + rayLength - 1][origin.letter - 1].status = TileStatus.READY_FOR_CAPTURE;
                break;
            }
            tiles[origin.number + rayLength - 1][origin.letter - 1].status = TileStatus.READY_FOR_TURN;
            if(king) {
                break;
            }
        }

        rayLength = 0;

        while(true) {
            ++rayLength;
            if (origin.letter - rayLength < 1) {
                break;
            }

            if(king && isTileAttacked(tiles[origin.number - 1][origin.letter - rayLength - 1], origin.occupyingPiece.white)) {
                break;
            }

            if (tiles[origin.number - 1][origin.letter - rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - 1][origin.letter - rayLength - 1].occupyingPiece.white == currentPiece.white) {
                break;
            }
            if (tiles[origin.number - 1][origin.letter - rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - 1][origin.letter - rayLength - 1].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number - 1][origin.letter - rayLength - 1].status = TileStatus.READY_FOR_CAPTURE;
                break;
            }
            tiles[origin.number + rayLength - 1][origin.letter - 1].status = TileStatus.READY_FOR_TURN;
            if(king) {
                break;
            }
        }

        rayLength = 0;

        while(true) {
            ++rayLength;
            if (origin.number - rayLength < 1) {
                break;
            }

            if(king && isTileAttacked(tiles[origin.number - rayLength - 1][origin.letter - 1], origin.occupyingPiece.white)) {
                break;
            }

            if (tiles[origin.number - rayLength - 1][origin.letter - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - rayLength - 1][origin.letter - 1].occupyingPiece.white == currentPiece.white) {
                break;
            }
            if (tiles[origin.number - rayLength - 1][origin.letter - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - rayLength - 1][origin.letter - 1].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number - rayLength - 1][origin.letter - 1].status = TileStatus.READY_FOR_CAPTURE;
                break;
            }
            tiles[origin.number + rayLength - 1][origin.letter - 1].status = TileStatus.READY_FOR_TURN;
            if(king) {
                break;
            }
        }

        rayLength = 0;

        while(true) {
            ++rayLength;
            if (origin.letter + rayLength > 8) {
                break;
            }

            if(king && isTileAttacked(tiles[origin.number - 1][origin.letter + rayLength - 1], origin.occupyingPiece.white)) {
                break;
            }

            if (tiles[origin.number - 1][origin.letter + rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - 1][origin.letter + rayLength - 1].occupyingPiece.white == currentPiece.white) {
                break;
            }
            if (tiles[origin.number - 1][origin.letter + rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - 1][origin.letter + rayLength - 1].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number - 1][origin.letter + rayLength - 1].status = TileStatus.READY_FOR_CAPTURE;
                break;
            }
            tiles[origin.number + rayLength - 1][origin.letter - 1].status = TileStatus.READY_FOR_TURN;

            if(king) {
                break;
            }
        }
    }

    void bishopMove(Tile origin, boolean king) {
        int rayLength = 0;
        while(true) {
            ++rayLength;
            if (origin.number + rayLength > 8 || origin.letter + rayLength > 8) {
                break;
            }
            if(king && isTileAttacked(tiles[origin.number + rayLength - 1][origin.letter + rayLength - 1], origin.occupyingPiece.white)) {
                break;
            }
            if (tiles[origin.number + rayLength - 1][origin.letter + rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number + rayLength - 1][origin.letter + rayLength - 1].occupyingPiece.white == currentPiece.white) {
                break;
            }
            if (tiles[origin.number + rayLength - 1][origin.letter + rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number + rayLength - 1][origin.letter + rayLength - 1].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number + rayLength - 1][origin.letter + rayLength - 1].status = TileStatus.READY_FOR_CAPTURE;
                break;
            }

            tiles[origin.number + rayLength - 1][origin.letter + rayLength - 1].status = TileStatus.READY_FOR_TURN;
            if(king) {
                break;
            }
        }

        rayLength = 0;

        while(true) {
            ++rayLength;
            if (origin.number + rayLength > 8 || origin.letter - rayLength < 1) {
                break;
            }

            if(king && isTileAttacked(tiles[origin.number + rayLength - 1][origin.letter - rayLength - 1], origin.occupyingPiece.white)) {
                break;
            }

            if (tiles[origin.number + rayLength - 1][origin.letter - rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number + rayLength - 1][origin.letter - rayLength - 1].occupyingPiece.white == currentPiece.white) {
                break;
            }
            if (tiles[origin.number + rayLength - 1][origin.letter - rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number + rayLength - 1][origin.letter - rayLength - 1].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number + rayLength - 1][origin.letter - rayLength - 1].status = TileStatus.READY_FOR_CAPTURE;
                break;
            }

            tiles[origin.number + rayLength - 1][origin.letter - rayLength - 1].status = TileStatus.READY_FOR_TURN;

            if(king) {
                break;
            }
        }

        rayLength = 0;

        while(true) {
            ++rayLength;
            if (origin.number - rayLength < 1 || origin.letter - rayLength < 1) {
                break;
            }

            if(king && isTileAttacked(tiles[origin.number - rayLength - 1][origin.letter - rayLength - 1], origin.occupyingPiece.white)) {
                break;
            }

            if (tiles[origin.number - rayLength - 1][origin.letter - rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - rayLength - 1][origin.letter - rayLength - 1].occupyingPiece.white == currentPiece.white) {
                break;
            }
            if (tiles[origin.number - rayLength - 1][origin.letter - rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - rayLength - 1][origin.letter - rayLength - 1].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number - rayLength - 1][origin.letter - rayLength - 1].status = TileStatus.READY_FOR_CAPTURE;
                break;
            }

            tiles[origin.number - rayLength - 1][origin.letter - rayLength - 1].status = TileStatus.READY_FOR_TURN;
            if(king) {
                break;
            }
        }

        rayLength = 0;

        while(true) {
            ++rayLength;
            if (origin.number - rayLength < 1 || origin.letter + rayLength > 8) {
                break;
            }

            if(king && isTileAttacked(tiles[origin.number - rayLength - 1][origin.letter + rayLength - 1], origin.occupyingPiece.white)) {
                break;
            }

            if (tiles[origin.number - rayLength - 1][origin.letter + rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - rayLength - 1][origin.letter + rayLength - 1].occupyingPiece.white == currentPiece.white) {
                break;
            }
            if (tiles[origin.number - rayLength - 1][origin.letter + rayLength - 1].status == TileStatus.OCCUPIED
                    && tiles[origin.number - rayLength - 1][origin.letter + rayLength - 1].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number - rayLength - 1][origin.letter + rayLength - 1].status = TileStatus.READY_FOR_CAPTURE;
                break;
            }

            tiles[origin.number - rayLength - 1][origin.letter + rayLength - 1].status = TileStatus.READY_FOR_TURN;
            if(king) {
                break;
            }
        }
    }

    void knightMove(Tile origin) {
        checkKnightMove(origin, 2, 1 );
        checkKnightMove(origin, 2, -1 );
        checkKnightMove(origin, 1, 2 );
        checkKnightMove(origin, 1, -2 );
        checkKnightMove(origin, -1, 2 );
        checkKnightMove(origin, -1, -2 );
        checkKnightMove(origin, -2, 1 );
        checkKnightMove(origin, -2, -1 );
    }

    void checkKnightMove(Tile origin, int numberAdd, int letterAdd) {
        if (origin.number + numberAdd > 8 || origin.number + numberAdd < 1 ||
            origin.letter + letterAdd > 8 || origin.letter + letterAdd < 1) {
            return;
        }

        switch (tiles[origin.number + numberAdd - 1][origin.letter + letterAdd - 1].status) {
            case OCCUPIED:
                if(currentPiece.white != tiles[origin.number + numberAdd - 1][origin.letter + letterAdd - 1].occupyingPiece.white) {
                    tiles[origin.number + numberAdd - 1][origin.letter + letterAdd - 1].status
                            = TileStatus.READY_FOR_CAPTURE;
                }
                break;
            case EMPTY:
                tiles[origin.number + numberAdd - 1][origin.letter + letterAdd - 1].status = TileStatus.READY_FOR_TURN;
                break;
        }
    }

    void pawnMove(Tile origin) {
        boolean doubleForward = ((origin.number == 2) && (currentPiece.white))
                || ((origin.number == 7) && (!currentPiece.white));
        checkPawnMoves(origin, doubleForward);
        checkPawnCaptures(origin);
    }

    void checkPawnMoves(Tile origin, boolean doubleForward) {
        int directionAdd;

        if(currentPiece.white) {
            directionAdd = 1;
        } else {
            directionAdd = -1;
        }

        while(true) {
            if(tiles[origin.number + directionAdd - 1][origin.letter - 1].status == TileStatus.EMPTY) {
                tiles[origin.number + directionAdd - 1][origin.letter - 1].status = TileStatus.READY_FOR_TURN;
            }

            if(doubleForward) {
                doubleForward = false;
                directionAdd *= 2;
                continue;
            }

            break;
        }
    }

    void checkPawnCaptures(Tile origin) {
        int directionAdd;

        if(currentPiece.white) {
            directionAdd = 1;
        } else {
            directionAdd = -1;
        }

        if(origin.letter != 1 && tiles[origin.number + directionAdd - 1][origin.letter - 2].enPassant) {
            tiles[origin.number + directionAdd - 1][origin.letter - 2].status = TileStatus.READY_FOR_CAPTURE;
        }

        if(origin.letter != 8 && tiles[origin.number + directionAdd - 1][origin.letter].enPassant) {
            tiles[origin.number + directionAdd - 1][origin.letter].status = TileStatus.READY_FOR_CAPTURE;
        }

        if(origin.letter != 1 && tiles[origin.number + directionAdd - 1][origin.letter - 2].status == TileStatus.OCCUPIED) {
            if(tiles[origin.number + directionAdd - 1][origin.letter - 2].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number + directionAdd - 1][origin.letter - 2].status = TileStatus.READY_FOR_CAPTURE;
            }
        }

        if(origin.letter != 8 && tiles[origin.number + directionAdd - 1][origin.letter].status == TileStatus.OCCUPIED) {
            if(tiles[origin.number + directionAdd - 1][origin.letter].occupyingPiece.white != currentPiece.white) {
                tiles[origin.number + directionAdd - 1][origin.letter].status = TileStatus.READY_FOR_CAPTURE;
            }
        }
    }

    void movePieces(Tile destination, Piece movedPiece, boolean attack) {
        if(movedPiece.type == PieceType.PAWN) {
            if(previousTile.number == 2 && destination.number == 4) {
                tiles[destination.number - 2][destination.letter - 1].preparedForEnPassant = true;
            }
            if(previousTile.number == 7 && destination.number == 5) {
                tiles[destination.number][destination.letter - 1].preparedForEnPassant = true;
            }
        }


        previousTile.status = TileStatus.EMPTY;

        if(!attack) {
            destination.status = TileStatus.OCCUPIED;
            destination.occupyingPiece = movedPiece;
        }
        else {
            destination.status = TileStatus.OCCUPIED;

            if(destination.enPassant) {
                destination.occupyingPiece = movedPiece;
                Tile killedPawnTile;
                if(destination.occupyingPiece.white) {
                    killedPawnTile = tiles[destination.number - 2][destination.letter - 1];
                } else {
                    killedPawnTile = tiles[destination.number][destination.letter - 1];
                }
                killedPawnTile.status = TileStatus.EMPTY;
                killedPawnTile.occupyingPiece.image.setVisible(false);
                killedPawnTile.occupyingPiece = null;
            } else {
                destination.occupyingPiece.image.setVisible(false);
                destination.occupyingPiece = movedPiece;
            }
        }

        previousTile.occupyingPiece = null;

        promotions = false;

        double [] coords = UtilityMethods.tileToCoords(destination);
        destination.occupyingPiece.image.setX(coords[0] + 7);
        destination.occupyingPiece.image.setY(coords[1] + 7);
        destination.occupyingPiece.moved = true;

        if(checkPromotion(destination, destination.occupyingPiece.white)) {
            try {
                promote(destination);
                promotions = true;
            }
            catch (IOException error) {
                Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
                ioExceptionAlert.setHeaderText("File System Error");
                ioExceptionAlert.setContentText("There seems to be a problem with the file system. We are sorry.");
                ioExceptionAlert.showAndWait();
            }
        }
    }


    /*
        Методы, отвечающие за рокировку.
     */

    void checkCastling(boolean white) {
        if(white) {
            if(tiles[0][0].status == TileStatus.OCCUPIED && tiles[0][0].occupyingPiece.type == PieceType.ROOK
                && !tiles[0][0].occupyingPiece.moved) {
                if(tiles[0][1].status == TileStatus.EMPTY &&
                        tiles[0][2].status == TileStatus.EMPTY &&
                        tiles[0][3].status == TileStatus.READY_FOR_TURN &&
                        !isTileAttacked(tiles[0][4], true) &&
                        !isTileAttacked(tiles[0][3], true) &&
                        !isTileAttacked(tiles[0][2], true)) {
                    tiles[0][2].status = TileStatus.READY_FOR_CASTLING;
                }
            }
            if(tiles[0][7].status == TileStatus.OCCUPIED && tiles[0][7].occupyingPiece.type == PieceType.ROOK
                    && !tiles[0][7].occupyingPiece.moved) {
                if(tiles[0][6].status == TileStatus.EMPTY &&
                        tiles[0][5].status == TileStatus.READY_FOR_TURN &&
                        !isTileAttacked(tiles[0][4], true) &&
                        !isTileAttacked(tiles[0][5], true) &&
                        !isTileAttacked(tiles[0][6], true)) {
                    tiles[0][6].status = TileStatus.READY_FOR_CASTLING;
                }
            }
        } else {
            if(tiles[7][0].status == TileStatus.OCCUPIED && tiles[7][0].occupyingPiece.type == PieceType.ROOK
                    && !tiles[7][0].occupyingPiece.moved) {
                if(tiles[7][1].status == TileStatus.EMPTY &&
                        tiles[7][2].status == TileStatus.EMPTY &&
                        tiles[7][3].status == TileStatus.READY_FOR_TURN &&
                        !isTileAttacked(tiles[7][4], false) &&
                        !isTileAttacked(tiles[7][3], false) &&
                        !isTileAttacked(tiles[7][2], false)) {
                    tiles[7][2].status = TileStatus.READY_FOR_CASTLING;
                }
            }
            if(tiles[7][7].status == TileStatus.OCCUPIED && tiles[7][7].occupyingPiece.type == PieceType.ROOK
                    && !tiles[7][7].occupyingPiece.moved) {
                if(tiles[7][6].status == TileStatus.EMPTY &&
                        tiles[7][5].status == TileStatus.READY_FOR_TURN &&
                        !isTileAttacked(tiles[7][4], false) &&
                        !isTileAttacked(tiles[7][5], false) &&
                        !isTileAttacked(tiles[7][6], false)) {
                    tiles[7][6].status = TileStatus.READY_FOR_CASTLING;
                }
            }
        }
    }

    void castle(Tile destination) {
        previousTile.status = TileStatus.EMPTY;

        if(currentPiece.white) {
            if(destination.letter == 3) {
                tiles[0][2].status = TileStatus.OCCUPIED;
                tiles[0][2].occupyingPiece = tiles[0][4].occupyingPiece;

                tiles[0][3].status = TileStatus.OCCUPIED;
                tiles[0][3].occupyingPiece = tiles[0][0].occupyingPiece;

                tiles[0][0].status = TileStatus.EMPTY;
                tiles[0][0].occupyingPiece = null;

                tiles[0][4].status = TileStatus.EMPTY;
                tiles[0][4].occupyingPiece = null;

                double[] coords = UtilityMethods.tileToCoords(tiles[0][2]);
                tiles[0][2].occupyingPiece.image.setX(coords[0] + 7);
                tiles[0][2].occupyingPiece.image.setY(coords[1] + 7);

                coords = UtilityMethods.tileToCoords(tiles[0][3]);
                tiles[0][3].occupyingPiece.image.setX(coords[0] + 7);
                tiles[0][3].occupyingPiece.image.setY(coords[1] + 7);
            }

            if(destination.letter == 7) {
                tiles[0][6].status = TileStatus.OCCUPIED;
                tiles[0][6].occupyingPiece = tiles[0][4].occupyingPiece;

                tiles[0][5].status = TileStatus.OCCUPIED;
                tiles[0][5].occupyingPiece = tiles[0][7].occupyingPiece;

                tiles[0][4].status = TileStatus.EMPTY;
                tiles[0][4].occupyingPiece = null;

                tiles[0][7].status = TileStatus.EMPTY;
                tiles[0][7].occupyingPiece = null;

                double[] coords = UtilityMethods.tileToCoords(tiles[0][6]);
                tiles[0][6].occupyingPiece.image.setX(coords[0] + 7);
                tiles[0][6].occupyingPiece.image.setY(coords[1] + 7);

                coords = UtilityMethods.tileToCoords(tiles[0][5]);
                tiles[0][5].occupyingPiece.image.setX(coords[0] + 7);
                tiles[0][5].occupyingPiece.image.setY(coords[1] + 7);
            }
        } else {
            if (destination.letter == 3) {
                tiles[7][2].status = TileStatus.OCCUPIED;
                tiles[7][2].occupyingPiece = tiles[7][4].occupyingPiece;

                tiles[7][3].status = TileStatus.OCCUPIED;
                tiles[7][3].occupyingPiece = tiles[7][0].occupyingPiece;

                tiles[7][0].status = TileStatus.EMPTY;
                tiles[7][0].occupyingPiece = null;

                tiles[7][4].status = TileStatus.EMPTY;
                tiles[7][4].occupyingPiece = null;

                double[] coords = UtilityMethods.tileToCoords(tiles[7][2]);
                tiles[7][2].occupyingPiece.image.setX(coords[0] + 7);
                tiles[7][2].occupyingPiece.image.setY(coords[1] + 7);

                coords = UtilityMethods.tileToCoords(tiles[7][3]);
                tiles[7][3].occupyingPiece.image.setX(coords[0] + 7);
                tiles[7][3].occupyingPiece.image.setY(coords[1] + 7);
            }

            if(destination.letter == 7) {
                tiles[7][6].status = TileStatus.OCCUPIED;
                tiles[7][6].occupyingPiece = tiles[7][4].occupyingPiece;

                tiles[7][5].status = TileStatus.OCCUPIED;
                tiles[7][5].occupyingPiece = tiles[7][7].occupyingPiece;

                tiles[7][4].status = TileStatus.EMPTY;
                tiles[7][4].occupyingPiece = null;

                tiles[7][7].status = TileStatus.EMPTY;
                tiles[7][7].occupyingPiece = null;

                double[] coords = UtilityMethods.tileToCoords(tiles[7][6]);
                tiles[7][6].occupyingPiece.image.setX(coords[0] + 7);
                tiles[7][6].occupyingPiece.image.setY(coords[1] + 7);

                coords = UtilityMethods.tileToCoords(tiles[7][5]);
                tiles[7][5].occupyingPiece.image.setX(coords[0] + 7);
                tiles[7][5].occupyingPiece.image.setY(coords[1] + 7);
            }
        }
    }

    /*
        Методы, отвечающие за повышение пешки.
     */

    void promote(Tile tile) throws IOException {
        PromotionApplication promo = new PromotionApplication();
        promo.start(new Stage());

        tile.occupyingPiece.type = promo.selectedPieceType;

        tile.occupyingPiece.image.setVisible(false);

        boolean white = tile.occupyingPiece.white;

        switch (promo.selectedPieceType) {
            case QUEEN:
                if(tile.occupyingPiece.white) {
                    tile.occupyingPiece.image = UtilityMethods.uploadImage("white_queen.png", tile, true);
                } else {
                    tile.occupyingPiece.image = UtilityMethods.uploadImage("black_queen.png", tile, true);
                }
                break;
            case ROOK:
                if(tile.occupyingPiece.white) {
                    tile.occupyingPiece.image = UtilityMethods.uploadImage("white_rook.png", tile, true);
                } else {
                    tile.occupyingPiece.image = UtilityMethods.uploadImage("black_rook.png", tile, true);
                }
                break;
            case BISHOP:
                if(tile.occupyingPiece.white) {
                    tile.occupyingPiece.image = UtilityMethods.uploadImage("white_bishop.png", tile, true);
                } else {
                    tile.occupyingPiece.image = UtilityMethods.uploadImage("black_bishop.png", tile, true);
                }
                break;
            case KNIGHT:
                if(tile.occupyingPiece.white) {
                    tile.occupyingPiece.image = UtilityMethods.uploadImage("white_knight.png", tile, true);
                } else {
                    tile.occupyingPiece.image = UtilityMethods.uploadImage("black_knight.png", tile, true);
                }
                break;
        }

        tile.occupyingPiece.image.setVisible(true);
    }

    boolean checkPromotion(Tile tile, boolean white) {
        if(white && tile.number == 8 && tile.occupyingPiece.type == PieceType.PAWN) {
            return true;
        }

        if(!white && tile.number == 1 && tile.occupyingPiece.type == PieceType.PAWN) {
            return true;
        }

        return false;
    }


    /*
        Методы, проверяющие атакованность клетки.
     */

    boolean isTileAttacked(Tile attackedTile, boolean white) {
        int let = attackedTile.letter - 1;
        int num = attackedTile.number - 1;

        boolean[] checkResults = makeChecks(let, num, white);

        for(int i=0; i<17; i++) {
            if(checkResults[i]) {
                return true;
            }
        }
        return false;
    }

    boolean[] makeChecks(int let, int num, boolean white) {
        boolean[] checkResults = new boolean[17];

        checkResults[0] = checkForRooks(let, num, white, true, true);
        checkResults[1] = checkForRooks(let, num, white, false, true);
        checkResults[2] = checkForRooks(let, num, white, true, false);
        checkResults[3] = checkForRooks(let, num, white, false, false);

        checkResults[4] = checkForBishops(let, num, white, 1);
        checkResults[5] = checkForBishops(let, num, white, 2);
        checkResults[6] = checkForBishops(let, num, white, 3);
        checkResults[7] = checkForBishops(let, num, white, 4);

        checkResults[8] = checkForKnights(let, num, white, 2, 1);
        checkResults[9] = checkForKnights(let, num, white, 2, -1);
        checkResults[10] = checkForKnights(let, num, white, 1, 2);
        checkResults[11] = checkForKnights(let, num, white, 1, -2);
        checkResults[12] = checkForKnights(let, num, white, -1, -2);
        checkResults[13] = checkForKnights(let, num, white, -1, 2);
        checkResults[14] = checkForKnights(let, num, white, -2, 1);
        checkResults[15] = checkForKnights(let, num, white, -2, -1);

        checkResults[16] = checkForPawns(let, num, white);

        return checkResults;
    }

    boolean checkForRooks(int let, int num, boolean white, boolean horizontal, boolean upOrRight) {
        int rayLength = 0, actualRayLength = 0;

        if(horizontal) {
            while(true) {
                ++rayLength;

                if (!upOrRight) {
                    actualRayLength = -rayLength;
                } else {
                    actualRayLength = rayLength;
                }

                if(let + actualRayLength > 7 || let + actualRayLength < 0) {
                    return false;
                }

                if (tiles[num][let + actualRayLength].status == TileStatus.OCCUPIED) {
                    Piece stranger = tiles[num][let + actualRayLength].occupyingPiece;
                    if(stranger.white == white) {
                        if(stranger.type == PieceType.KING) {
                            continue;
                        }
                        return false;
                    }
                    if(stranger.type == PieceType.KING && rayLength == 1) {
                        return true;
                    }
                    if(stranger.type == PieceType.ROOK || stranger.type == PieceType.QUEEN) {
                        return true;
                    }
                    return false;
                }
            }
        } else {
            while(true) {
                ++rayLength;

                if (!upOrRight) {
                    actualRayLength = -rayLength;
                } else {
                    actualRayLength = rayLength;
                }

                if(num + actualRayLength > 7 || num + actualRayLength < 0) {
                    return false;
                }

                if (tiles[num + actualRayLength][let].status == TileStatus.OCCUPIED) {
                    Piece stranger = tiles[num + actualRayLength][let].occupyingPiece;
                    if(stranger.white == white) {
                        if(stranger.type == PieceType.KING) {
                            continue;
                        }
                        return false;
                    }
                    if(stranger.type == PieceType.KING && rayLength == 1) {
                        return true;
                    }
                    if(stranger.type == PieceType.ROOK || stranger.type == PieceType.QUEEN) {
                        return true;
                    }
                    return false;
                }
            }
        }
    }

    boolean checkForBishops(int let, int num, boolean white, int directionCode) {
        boolean forward = true, upward = true;

        switch (directionCode) {
            case 1:
                break;
            case 2:
                forward = false;
                break;
            case 3:
                upward = false;
                forward = false;
            case 4:
                upward = false;
        }

        int x = 0, y = 0;

        while (true) {
            if(upward) {
                ++y;
            } else {
                --y;
            }

            if(forward) {
                ++x;
            } else {
                --x;
            }

            if(num + y > 7 || num + y < 0 || let + x > 7 || let + x < 0) {
                return false;
            }

            Tile tile = tiles[num + y][let + x];

            if(tile.status == TileStatus.OCCUPIED) {
                if(tile.occupyingPiece.white == white) {
                    if(tile.occupyingPiece.type == PieceType.KING) {
                        continue;
                    }
                    return false;
                }
                if(tile.occupyingPiece.type == PieceType.KING && (x == 1 || x == -1)) {
                    return true;
                }
                if(tile.occupyingPiece.type == PieceType.BISHOP || tile.occupyingPiece.type == PieceType.QUEEN) {
                    return true;
                }
                return false;
            }
        }

    }

    boolean checkForKnights(int let, int num, boolean white, int x, int y) {
        if(let + x > 7 || let + x < 0) {
            return false;
        }
        if(num + y > 7 || num + y < 0) {
            return false;
        }

        Tile checkedTile = tiles[num + y][let + x];

        if(checkedTile.status == TileStatus.OCCUPIED && checkedTile.occupyingPiece.white != white &&
            checkedTile.occupyingPiece.type == PieceType.KNIGHT) {
            return true;
        }

        return false;
    }

    // We check if tiles[num-1][let-1] is not attacked by opposing team pawns.
    boolean checkForPawns(int let, int num, boolean white) {
        if(!white) {
            if(num != 0) {
                if(let !=7) {
                    if (tiles[num-1][let+1].status == TileStatus.OCCUPIED &&
                            tiles[num-1][let+1].occupyingPiece.type == PieceType.PAWN &&
                        tiles[num-1][let+1].occupyingPiece.white) {
                        return true;
                    }
                }

                if(let != 0) {
                    if (tiles[num-1][let-1].status == TileStatus.OCCUPIED &&
                            tiles[num-1][let-1].occupyingPiece.type == PieceType.PAWN &&
                            tiles[num-1][let-1].occupyingPiece.white) {
                        return true;
                    }
                }
            }
        } else {
            if(num != 7) {
                if(let !=7) {
                    if (tiles[num+1][let+1].status == TileStatus.OCCUPIED &&
                            tiles[num+1][let+1].occupyingPiece.type == PieceType.PAWN &&
                            !tiles[num+1][let+1].occupyingPiece.white) {
                        return true;
                    }
                }

                if(let != 0) {
                    if (tiles[num+1][let-1].status == TileStatus.OCCUPIED &&
                            tiles[num+1][let-1].occupyingPiece.type == PieceType.PAWN &&
                            !tiles[num+1][let-1].occupyingPiece.white) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean checkCheck(boolean white) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(tiles[i][j].status == TileStatus.OCCUPIED && tiles[i][j].occupyingPiece.white == white
                        && tiles[i][j].occupyingPiece.type == PieceType.KING) {
                    return isTileAttacked(tiles[i][j], white);
                }
            }
        }
        return false;
    }
}
