package com.chess.engine.player;
import com.chess.engine.Alliance;
import com.chess.engine.board.*;
import com.chess.engine.pieces.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;
    public Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentMoves) {
        this.board=board;
        this.playerKing=establishKing();
        this.legalMoves=legalMoves;
        this.legalMoves.addAll(calculateKingCastles(legalMoves,opponentMoves));
        this.isInCheck=!calculateAttacksOnTile(this.playerKing.getPiecePosition(),opponentMoves).isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves=new ArrayList<>();
        for(final Move move:moves){
            if(piecePosition==move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return attackMoves;
    }

    private King establishKing() {
        for(Piece piece:getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here!Not a valid board!!");
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }
    
    public  boolean isInCheckMate(){
        return this.isInCheck&&!hasEscapeMoves();
    }

    private boolean hasEscapeMoves() {
        for(final Move move:this.legalMoves){
            final MoveTransition transition=makeMove(move);
            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public  boolean isInStaleMate(){
        return !this.isInCheck&&!hasEscapeMoves();
    }
    public boolean isCastled(){
        return false;
    }
    public MoveTransition makeMove(final Move move){
        if(!isMoveLegal(move)){
            return new MoveTransition(this.board,move,MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard=move.execute();
        final Collection<Move> kingAttacks=Player.calculateAttacksOnTile(transitionBoard.getCurrentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.getCurrentPlayer().getLegalMoves());
        if(!kingAttacks.isEmpty()){
            return new MoveTransition(this.board,move,MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard,move,MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,Collection<Move> opponentLegals);
}
