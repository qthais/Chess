package com.chess.engine.board;
import java.util.*;
import com.chess.engine.pieces.Piece;

import static com.chess.engine.board.BoardUtils.NUM_TILES;

public abstract class Tile {
    protected final int tileCoordinate;
    private  static final Map<Integer,EmptyTile> EMPTY_TILES_CACHE= createAllPossibleEmptyTiles();

    private static Map<Integer,EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Integer,EmptyTile> emptyTileMap=new HashMap<>();
        for(int i=0;i<NUM_TILES;i++){
            emptyTileMap.put(i,new EmptyTile(i));
        }
        return  Collections.unmodifiableMap(emptyTileMap);
    }

    private Tile(final int tileCoordinate){
        this.tileCoordinate=tileCoordinate;
    }
    public static Tile createTile(final int tileCoordinate,final Piece piece){
        return piece!=null ? new OccupiedTile(tileCoordinate,piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }
    public abstract boolean isTileOccupied();
    public abstract Piece getPiece();

    public int getTileCoordinate() {
        return tileCoordinate;
    }

    public static final class EmptyTile extends Tile{
        private EmptyTile(final int coordinate){
            super(coordinate);
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }
    public static final class OccupiedTile extends Tile{
        private final Piece pieceOnTile;
        private OccupiedTile(final int tileCoordinate,final Piece pieceOnTile){
            super(tileCoordinate);
            this.pieceOnTile=pieceOnTile;
        }

        @Override
        public String toString() {
            return getPiece().getPieceAlliance().isBlack()?getPiece().toString().toLowerCase():
                    getPiece().toString();
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }

    }
}