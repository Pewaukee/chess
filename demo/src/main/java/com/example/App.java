package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Group gridGroup;
    private static Group pieceGroup;
    private static Pane pane;
    
    // for adding the pieces to the screen
    private static InputStream stream;
    private static Image image;
    private static ImageView imageView;
    private static int[] coordinates;
    private static ArrayList<Piece> pieces = new ArrayList<Piece>();
    private static Piece whiteKing;
    private static Piece blackKing;
    private static Piece king;
    private static String turn;
    private static boolean toMove;
    private static Piece curPieceSelected;
    private static Piece p = new Piece(-1, -1, "n/a", "placement", "filename");
    private static int enPassant = -1;
    
    private static String whiteRookFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\whiteRook.png";
    private static String blackRookFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\blackRook.png";
    private static String whiteKnightFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\whiteKnight.png";
    private static String blackKnightFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\blackKnight.png";
    private static String whiteBishopFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\whiteBishop.png";
    private static String blackBishopFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\blackBishop.png";
    private static String whiteQueenFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\whiteQueen.png";
    private static String blackQueenFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\blackQueen.png";
    private static String whiteKingFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\whiteKing.png";
    private static String blackKingFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\blackKing.png";
    private static String whitePawnFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\whitePawn.png";
    private static String blackPawnFileStr = "demo\\src\\main\\resources\\com\\example\\PiecePics\\blackPawn.png";

    @Override
    public void start(Stage stage) throws IOException {
        gridGroup = new Group();
        pieceGroup = new Group();
        pane = new Pane(gridGroup, pieceGroup);

        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // make the grid with alternating colors white and grey
                Rectangle r = new Rectangle();
                
                coordinates = coordinateFormula(i, j);
                
                r.setX(coordinates[0]);
                r.setY(coordinates[1]);
                r.setWidth(100);
                r.setHeight(100);
                
                if (count % 2 == 0) r.setFill(Color.WHITE);
                else r.setFill(Color.rgb(51, 153, 175)); // 8 bit numbers for a light blue
                gridGroup.getChildren().add(r);
                count++;
            }
            count++;
        }
        
        setUpPieces();
        drawBoard();
        scene = new Scene(pane, 850, 850);
        stage.setScene(scene);
        stage.setTitle("Chess");
        stage.setResizable(false);
        stage.show();
        
        startGame();
    }

    private void setUpPieces() {
        /* purpose
         * set up all the pieces with coordinates, 
         * color, and set the available moves
         */
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = null;
                if (j == 0) { // 8th rank
                    if (i == 0 || i == 7) {piece = new Piece(i, j, "black", "rook", blackRookFileStr);} // add black rook
                    if (i == 1 || i == 6) {piece = new Piece(i, j, "black", "knight", blackKnightFileStr);} // add black knight
                    if (i == 2 || i == 5) {piece = new Piece(i, j, "black", "bishop", blackBishopFileStr);} // add black bishop
                    if (i == 3) {piece = new Piece(i, j, "black", "queen", blackQueenFileStr);} // add black queen
                    if (i == 4) { // add black king
                        piece = new Piece(i, j, "black", "king", blackKingFileStr); 
                        blackKing = piece;
                    } 
                }
                if (j == 1) {piece = new Piece(i, j, "black", "pawn", blackPawnFileStr);} // add black pawns
                if (j == 6) {piece = new Piece(i, j, "white", "pawn", whitePawnFileStr);} // add white pawns
                if (j == 7) { // 1st rank
                    if (i == 0 || i == 7) {piece = new Piece(i, j, "white", "rook", whiteRookFileStr);} // add white rook
                    if (i == 1 || i == 6) {piece = new Piece(i, j, "white", "knight", whiteKnightFileStr);} // add white knight
                    if (i == 2 || i == 5) {piece = new Piece(i, j, "white", "bishop", whiteBishopFileStr);} // add white bishop
                    if (i == 3) {piece = new Piece(i, j, "white", "queen", whiteQueenFileStr);} // add white queen
                    if (i == 4) { // add white king
                        piece = new Piece(i, j, "white", "king", whiteKingFileStr);
                        whiteKing = piece;
                    } 
                }
                if (piece != null) {pieces.add(piece);}
            }   
        }        
    }

    private void startGame() {
        turn = "white";

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // getting x and y coordinates
                int x = (int)event.getX();
                int y = (int)event.getY();
                int[] square = findSquare(x, y);
                x = square[0];
                y = square[1];
                king = turn.equals("white") ? whiteKing: blackKing;
                if (!toMove) {
                    toMove = setPiece(x, y, toMove);
                }
                else {
                    toMove = false;
                    System.out.println(curPieceSelected.getMoves().size());
                    for (Location location: curPieceSelected.getMoves()) {
                        System.out.println(location.x + " " + location.y);
                        if (location.getX() == x && location.getY() == y) {
                            
                            Piece occupiedPiece = p.isOccupied(pieces, new Location(x, y));
                            // if a piece is took
                            if (occupiedPiece != null) {pieces.remove(occupiedPiece);}

                            String type = curPieceSelected.type;
                            if (type.equals("rook") || type.equals("king")) {curPieceSelected.hasMoved = true;}
                            
                            // if the king castled, move the rook as accordingly
                            if (curPieceSelected.type.equals("king")) {
                                Piece rookToTransfer;
                                if (curPieceSelected.x - x == -2) { // right side castle
                                    rookToTransfer = curPieceSelected.isOccupied(pieces, new Location(7, 7));
                                    rookToTransfer.x = x - 1;
                                }
                                if (curPieceSelected.x - x == 2) { // left side castle
                                    rookToTransfer = curPieceSelected.isOccupied(pieces, new Location(0, 7));
                                    rookToTransfer.x = x + 1;
                                }
                            }
                            // TODO en passant, can take but the wrong piece disappears

                            // en passant check
                            if (enPassant == x) {
                                Location location1 = new Location(x, y);
                                Location location2 = new Location(curPieceSelected.x, curPieceSelected.y);
                                double distance = location1.distance(location1, location2);
                                System.out.println(distance);
                                if (distance > 1.0 && distance < 2.0) { // checks for a diagonal move
                                    Piece enPassantPiece = p.isOccupied(pieces, new Location(enPassant, 3));
                                    if (enPassantPiece.type.equals("pawn"))  {
                                        pieces.remove(enPassantPiece);
                                    }
                                }
                                enPassant = -1;
                            }
                            
                            if (curPieceSelected.type.equals("pawn")) {
                                if (curPieceSelected.y - y == 2) {
                                    enPassant = curPieceSelected.x;
                                    // must flip the en passant column value for the next turn only
                                    int xDifference = Math.abs(3 - enPassant);
                                    if (enPassant <= 3) {enPassant = 4 + xDifference;}
                                    else {enPassant = 4 - xDifference;}
                                }
                            } else {enPassant = -1;}

                            // move the piece to its new location
                            curPieceSelected.setX(x);
                            curPieceSelected.setY(y);

                            if (curPieceSelected.type.equals("pawn") && curPieceSelected.y == 0) {
                                curPieceSelected.type = "queen"; 
                                if (curPieceSelected.color.equals("white")) {curPieceSelected.setFileString(whiteQueenFileStr);}
                                else {curPieceSelected.setFileString(blackQueenFileStr);}
                            }
                            try {
                                pieceGroup.getChildren().clear();
                                
                                // if turn is white, then turn is black and vice versa
                                turn = turn.equals("white") ? "black": "white";
                                king = turn.equals("white") ? whiteKing: blackKing;

                                king.check = king.inCheck(pieces, king); // determine if the king is in check
                                if (king.checkMate(pieces, turn)) {
                                    String otherColor = turn.equals("white") ? "black": "white";
                                    System.out.println("checkmate for " + otherColor);
                                }
                                if (king.staleMate(pieces, turn)) {
                                    System.out.println("stalemate");
                                }
                                flipBoard();
                                drawBoard();
                            } catch (FileNotFoundException e) {e.printStackTrace(); System.out.println("paths are likely wrong");}
                        }
                        toMove = setPiece(x, y, toMove); // see if user selected another piece of same color to move
                    }
                }
            }
        });
    }

    private boolean setPiece(int x, int y, boolean toMove) {
        try {
            Piece piece = p.isOccupied(pieces, new Location(x, y));
            if (piece.color.equals(turn)) {
                curPieceSelected = piece;
                curPieceSelected.setMoves(pieces, curPieceSelected, king, true);
                if (curPieceSelected.type.equals("pawn") && enPassant != -1 && curPieceSelected.y == 3) {
                    curPieceSelected.addMove(pieces, king, new Location(enPassant, 2));
                }
                return true;
            }
        } catch (NullPointerException e) {}
        return false;
    }

    private void flipBoard() {
        for (Piece piece: pieces) {
            int xDifference = Math.abs(3 - piece.x);
            if (piece.x <= 3) {piece.x = 4 + xDifference;}
            else {piece.x = 4 - xDifference;}
            int yDifference = Math.abs(3 - piece.y);
            if (piece.y <= 3) {piece.y = 4 + yDifference;}
            else {piece.y = 4 - yDifference;}
        }
    }

    private int[] findSquare(int x, int y) { // returns the square coordinates given the clicked mouse coords
        int[] res = new int[2];
        res[0] = (x-25)/100;
        res[1] = (y-25)/100;
        return res;
    }

    private void drawBoard() throws FileNotFoundException {
        for (Piece piece: pieces) {
            setImage(piece.getFileString(), piece.getX(), piece.getY());
        }
        /*for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j == 0) { // 8th rank
                    if (i == 0 || i == 7) {setImage(blackRookStr, i, j);} // add black rook
                    if (i == 1 || i == 6) {setImage(blackKnightStr, i, j);} // add black knight
                    if (i == 2 || i == 5) {setImage(blackBishopStr, i, j);} // add black bishop
                    if (i == 3) {setImage(blackQueenStr, i, j);} // add black queen
                    if (i == 4) {setImage(blackKingStr, i, j);} // add black king
                }
                if (j == 1) {setImage(blackPawnStr, i, j);} // add black pawns
                if (j == 6) {setImage(whitePawnStr, i, j);} // add white pawns
                if (j == 7) { // 1st rank
                    if (i == 0 || i == 7) {setImage(whiteRookStr, i, j);} // add black rook
                    if (i == 1 || i == 6) {setImage(whiteKnightStr, i, j);} // add black knight
                    if (i == 2 || i == 5) {setImage(whiteBishopStr, i, j);} // add black bishop
                    if (i == 3) {setImage(whiteQueenStr, i, j);} // add black queen
                    if (i == 4) {setImage(whiteKingStr, i, j);} // add black king
                }
            }   
        }*/
    }

    private void setImage(String pathname, int i, int j) throws FileNotFoundException {
        try {
            stream = new FileInputStream(pathname);
            image = new Image(stream);
            imageView = new ImageView();
            imageView.setImage(image);
            coordinates = coordinateFormula(i, j);
            imageView.setX(coordinates[0]);
            imageView.setY(coordinates[1]); 
            pieceGroup.getChildren().add(imageView);
        } catch (FileNotFoundException e) {System.out.println("cannot find file"); e.printStackTrace();}
    }

    private int[] coordinateFormula(int i, int j) {
        int[] res = new int[2];
        res[0] = 25+100*i;
        res[1] = 25+100*j;
        return res;
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}


/* additional notes
 *
 * https://www.geeksforgeeks.org/class-type-casting-in-java/#:~:text=Typecasting%20is%20the%20assessment%20of,direction%20to%20the%20inheritance%20tree.
 * shows how to Override functions in parent classes to properly call functions of subclasses
 * just use the same name and arguments with @Override
 * 
 * java platform se binary was telling me that my program was in an infinite loop
 * 
 * extra code to identify checks
 * ArrayList<Location> toRemove = new ArrayList<Location>();
            for (Location loc: res) {
                this.x = loc.x;
                this.y = loc.y;
                if (king.inCheck(pieces)) {
                    toRemove.add(loc);
                }
            }
            for (Location loc: toRemove) {res.remove(loc);}
            this.x = x;
            this.y = y;
 * 
 * extra code for pawn.setMoves()
 * case "black":
                // up 1 and not at end of the board
                Location downOne = new Location(x, y+1);
                if (y < 7 && isOccupied(pieces, downOne) == null) {
                    res.add(downOne);
                    // up 2 and at starting position
                    Location downTwo = new Location(x, y+2); // can only move down two if can move down one
                    if (y == 1 && isOccupied(pieces, downTwo) == null) {res.add(downTwo);}
                }

                //check diagonals
                Location downRight = new Location(x+1, y+1);
                if (isOccupied(pieces, downRight) != null) {helper(pieces, downRight, res, otherColor);}
                Location downLeft = new Location(x-1, y+1);
                if (isOccupied(pieces, downLeft) != null) {helper(pieces, downLeft, res, otherColor);}
                break;
 * 
 * extra code for castling without flipBoard()
 * 
 * for castling with white always at the bottom of the screen
 * try {
                if (isOccupied(pieces, new Location(7, 7)).hasMoved) {kingSideCastle = false;}
            } catch (NullPointerException e) {}
            try {
                if (isOccupied(pieces, new Location(0, 7)).hasMoved && isOccupied(pieces, new Location(1, 7)) == null) {
                    queenSideCastle = false;
                }
            } catch (NullPointerException e) {}
 * 
 * 
 * extra code for moving a rook when castling
 * // move the rook if a castle move is played
                            if (curPieceSelected.type.equals("king") && curPieceSelected.x - x == -2) { // king side castle
                                if (curPieceSelected.color.equals("white")) {
                                    Piece rook = curPieceSelected.isOccupied(pieces, new Location(7, 7));
                                    rook.setX(rook.getX()-2);
                                }
                                else { // color = "black"
                                    Piece rook = curPieceSelected.isOccupied(pieces, new Location(7, 0));
                                    rook.setX(rook.getX()-2);
                                }
                            }
                            if (curPieceSelected.type.equals("king") && curPieceSelected.x - x == 2) { // queen side castle
                                if (curPieceSelected.color.equals("white")) {
                                    Piece rook = curPieceSelected.isOccupied(pieces, new Location(0, 7));
                                    rook.setX(rook.getX()+3);
                                }
                                else { // color = "black"
                                    Piece rook = curPieceSelected.isOccupied(pieces, new Location(0, 0));
                                    rook.setX(rook.getX()+3);
                                }
                            }
 */


 