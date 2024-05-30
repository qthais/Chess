package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.*;
import com.chess.engine.board.Move.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {
    private final static int[] CANDIDATE_MOVE_COORDINATE = {8,16,7,9};

    public Pawn(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.PAWN,piecePosition, pieceAlliance,true);
    }
    public Pawn(int piecePosition, Alliance pieceAlliance,final boolean isFirstMove) {
        super(PieceType.PAWN,piecePosition, pieceAlliance,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            int candidateDestinationCoordinate = this.piecePosition + this.getPieceAlliance().getDirection() * currentCandidateOffset;
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                    legalMoves.add(new PawnPromotion( new PawnMove(board,this,candidateDestinationCoordinate)));
                }else{
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }

            }else if(currentCandidateOffset==16&&this.isFirstMove()
                    &&((BoardUtils.SEVENTH_RANK[this.piecePosition]
                    &&this.getPieceAlliance().isBlack())
                    ||(BoardUtils.SECOND_RANK[this.piecePosition]
                    &&this.getPieceAlliance().isWhite()))){
                int bonusPawnMove= this.piecePosition + (this.getPieceAlliance().getDirection() * 8);
                if(!board.getTile(candidateDestinationCoordinate).isTileOccupied()&&!board.getTile(bonusPawnMove).isTileOccupied()){
                    legalMoves.add(new PawnJump(board,this,candidateDestinationCoordinate));
                }
            }
            else if(currentCandidateOffset==7
                        &&!((BoardUtils.EIGHTH_COLUMN[this.piecePosition]&&this.pieceAlliance.isWhite())
                        ||(BoardUtils.FIRST_COLUMN[this.piecePosition]&&this.pieceAlliance.isBlack()))){
                    if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                        final Piece pieceOnCandidate=board.getTile(candidateDestinationCoordinate).getPiece();
                        if(this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                                legalMoves.add(new PawnPromotion( new PawnAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate)));
                            }else{
                                legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate));
                            }
                        }
                    }else if(board.getEnPassantPawn()!=null){
                        if(board.getEnPassantPawn().getPiecePosition()==(this.piecePosition+(this.pieceAlliance.getOppositeDirection()))){
                            final Piece pieceOnCandidate= board.getEnPassantPawn();
                            if(this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()){
                                legalMoves.add(new PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate));
                            }
                        }
                }
            }
            else if(currentCandidateOffset==9
                        &&!((BoardUtils.EIGHTH_COLUMN[this.piecePosition]&&this.pieceAlliance.isBlack())
                        ||(BoardUtils.FIRST_COLUMN[this.piecePosition]&&this.pieceAlliance.isWhite()))){
                    if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                        final Piece pieceOnCandidate=board.getTile(candidateDestinationCoordinate).getPiece();
                        if(this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                                legalMoves.add(new PawnPromotion( new PawnAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate)));
                            }else{
                                legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate));
                            }
                        }
                    }else if(board.getEnPassantPawn()!=null){
                        if(board.getEnPassantPawn().getPiecePosition()==(this.piecePosition-(this.pieceAlliance.getOppositeDirection()))){
                            final Piece pieceOnCandidate= board.getEnPassantPawn();
                            if(this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()){
                                legalMoves.add(new PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate));
                            }
                        }
                    }
                }
            }
        return legalMoves;
        }



    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(),move.getMovedPiece().getPieceAlliance(),false);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
    public Piece getPromotionPiece(){
        return new Queen(this.piecePosition,this.pieceAlliance,false);
    }
}
