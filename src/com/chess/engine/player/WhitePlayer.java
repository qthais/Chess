package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player {

    public WhitePlayer(Board board, Collection<Move> whiteStandardLegalMoves, Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals) {
        //King Side Castle
        final List<Move> kingCastles= new ArrayList<>();
        if(this.playerKing.isFirstMove()&&!this.isInCheck()){
            if(!this.board.getTile(61).isTileOccupied()&&!this.board.getTile(62).isTileOccupied()){
                final Tile rookTile=this.board.getTile(63);
                if(rookTile.isTileOccupied()&&rookTile.getPiece().isFirstMove()){
                    //to do
                    if(Player.calculateAttacksOnTile(61,opponentLegals).isEmpty()
                            &&Player.calculateAttacksOnTile(62,opponentLegals).isEmpty()
                            &&rookTile.getPiece().getPieceType().isRook()){
                        kingCastles.add(null);
                    }
                }
            }
            if(!this.board.getTile(59).isTileOccupied()&&!this.board.getTile(58).isTileOccupied()
                    &&!this.board.getTile(57).isTileOccupied()){
                final Tile rookTile=this.board.getTile(56);
                if(rookTile.isTileOccupied()&&rookTile.getPiece().isFirstMove()){
                    //to do
                    kingCastles.add(null);
                }
            }
        }
        return null;
    }
}
