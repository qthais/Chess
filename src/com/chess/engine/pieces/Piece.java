package  com.chess.engine.pieces;
import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public abstract class Piece {
    protected final PieceType pieceType;
    protected final int piecePosition;
    protected  final Alliance pieceAlliance;
    protected boolean isFirstMove;
    private final int cachedHashCode=computeHashCode();

    private int computeHashCode() {
        int result=pieceType.hashCode();
        result=31+result*pieceAlliance.hashCode();
        result=31+result*piecePosition;
        result=31+result*(isFirstMove?1:0);
        return result;
    }

    Piece(final PieceType pieceType,final int  piecePosition, final Alliance pieceAlliance){
        this.pieceType=pieceType;
        this.piecePosition=piecePosition;
        this.pieceAlliance=pieceAlliance;
        this.isFirstMove=false;
    }

    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    protected boolean isFirstMove() {
        return this.isFirstMove;
    }

    public int getPiecePosition() {
        return this.piecePosition;
    }

    public PieceType getPieceType() {
        return pieceType;
    }
    public abstract Piece movePiece(Move move);

    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(!(obj instanceof Piece)){
            return false;
        }
        final Piece otherPiece=(Piece) obj;
        return piecePosition== otherPiece.getPiecePosition()&&pieceType==otherPiece.getPieceType()&&
                pieceAlliance==otherPiece.getPieceAlliance()&&isFirstMove==otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    public enum PieceType{
        PAWN("P") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KNIGHT("N"){
            @Override
            public boolean isKing() {
                return false;
            }
        },
        BISHOP("B"){
            @Override
            public boolean isKing() {
                return false;
            }
        },
        ROOK("R"){
            @Override
            public boolean isKing() {
                return false;
            }
        },
        QUEEN("K"){
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KING("K"){
            @Override
            public boolean isKing() {
                return true;
            }
        };
        private String pieceName;
        PieceType(final String pieceName){
            this.pieceName=pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing() ;
    }
}
