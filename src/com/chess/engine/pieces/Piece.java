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
    private final int cachedHashCode;

    private int computeHashCode() {
        int result=pieceType.hashCode();
        result=31+result*pieceAlliance.hashCode();
        result=31+result*piecePosition;
        result=31+result*(isFirstMove?1:0);
        return result;
    }

    Piece(final PieceType pieceType,final int  piecePosition, final Alliance pieceAlliance,boolean isFirstMove){
        this.pieceType=pieceType;
        this.piecePosition=piecePosition;
        this.pieceAlliance=pieceAlliance;
        this.isFirstMove=isFirstMove;
        this.cachedHashCode=computeHashCode();
    }

    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public boolean isFirstMove() {
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

    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }

    public enum PieceType{
        PAWN("P",100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N",300){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B",300){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R",500){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q",500){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K",10000){
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };
        private String pieceName;
        private int pieceValue;
        PieceType(final String pieceName,int pieceValue){

            this.pieceName=pieceName;
            this.pieceValue=pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing();

        public abstract boolean isRook();
        public int getPieceValue(){
            return this.pieceValue;
        }
    }
}
