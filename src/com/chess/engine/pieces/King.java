package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.*;
import com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece{
    private final static int[] CANDIDATE_MOVE_COORDINATE = {-9,-7,7,9,-8,-1,1,8};
    public King(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.KING,piecePosition, pieceAlliance,true);
    }
    public King(int piecePosition, Alliance pieceAlliance,final boolean isFirstMove) {
        super(PieceType.KING,piecePosition, pieceAlliance,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE){
            final int candidateDestinationCoordinate=this.piecePosition+currentCandidateOffset;
            if(isFirstColumnExclusion(this.piecePosition,currentCandidateOffset)
                    ||isEighthColumnExclusion(this.piecePosition,currentCandidateOffset)){
                continue;
            }
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new MajorMove(board,this,candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                    }
                }
            }
        }
        return legalMoves;
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(),move.getMovedPiece().getPieceAlliance(),false);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition]
                && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition]
                && (candidateOffset == 9 || candidateOffset == 1 || candidateOffset == -7);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }
}
