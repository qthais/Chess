package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.*;
import com.chess.engine.board.Move.*;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BlackPlayer extends Player {

    public BlackPlayer(Board board, Collection<Move> whiteStandardLegalMoves, Collection<Move> blackStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals) {
        //blackKing Side Castle
        final List<Move> kingCastles= new ArrayList<>();
        if(this.playerKing.isFirstMove()&&!this.isInCheck()){
            if(!this.board.getTile(5).isTileOccupied()&&!this.board.getTile(6).isTileOccupied()){
                final Tile rookTile=this.board.getTile(7);
                if(rookTile.isTileOccupied()&&rookTile.getPiece().isFirstMove()){
                    //to do
                    if(Player.calculateAttacksOnTile(5,opponentLegals).isEmpty()
                            &&Player.calculateAttacksOnTile(6,opponentLegals).isEmpty()
                            &&rookTile.getPiece().getPieceType().isRook()){
                        kingCastles.add(new KingSideCastleMove(this.board,this.playerKing,
                                6,(Rook) rookTile.getPiece(),
                                rookTile.getTileCoordinate(), 5));
                    }
                }
            }
            if(!this.board.getTile(1).isTileOccupied()
                    &&!this.board.getTile(2).isTileOccupied()
                    &&!this.board.getTile(3).isTileOccupied()){
                final Tile rookTile=this.board.getTile(0);
                if(rookTile.isTileOccupied()&&rookTile.getPiece().isFirstMove()){
                    //to do
                    kingCastles.add(new KingSideCastleMove(this.board,this.playerKing,
                            2,(Rook) rookTile.getPiece(),
                            rookTile.getTileCoordinate(), 3));
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}
