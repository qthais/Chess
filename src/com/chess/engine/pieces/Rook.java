package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.*;
import com.chess.engine.board.Move.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Rook extends Piece{
    private final static int[] CANDIDATE_VECTOR_MOVE_COORDINATES={-8,-1,1,8};
    public Rook(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.ROOK,piecePosition, pieceAlliance,true);
    }
    public Rook(int piecePosition, Alliance pieceAlliance,final boolean isFirstMove) {
        super(PieceType.ROOK,piecePosition, pieceAlliance,isFirstMove);
    }
    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves= new ArrayList<>();
        for(int currentCandidateOffset:CANDIDATE_VECTOR_MOVE_COORDINATES){
            int candidateDestinationCoordinate=this.piecePosition;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                if(isFirstColumnExclusion(candidateDestinationCoordinate,currentCandidateOffset)
                        ||isEighthColumnExclusion(candidateDestinationCoordinate,currentCandidateOffset)){
                    break;
                }
                candidateDestinationCoordinate+=currentCandidateOffset;
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
                        break;
                    }
                }
            }
        }
        return legalMoves;
    }

    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getDestinationCoordinate(),move.getMovedPiece().getPieceAlliance(),false);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition]
                && (candidateOffset == -1 );
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition]
                && (candidateOffset == 1);
    }
    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }
}
