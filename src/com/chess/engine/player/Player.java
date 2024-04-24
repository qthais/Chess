package com.chess.engine.player;
import com.chess.engine.board.*;
import com.chess.engine.pieces.*;

import java.util.Collection;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    public Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentLegalMoves) {
        this.board=board;
        this.playerKing=establishKing();
        this.legalMoves=legalMoves;
    }

    private King establishKing() {
        for(Piece piece:getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here!Not a valid board!!");
    }

    public abstract Collection<Piece> getActivePieces();

}
