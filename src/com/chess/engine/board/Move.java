package com.chess.engine.board;
import com.chess.engine.board.Board.*;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destinationCoordinate;
    public static final Move NULL_MOVE=new NullMove();
    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate){
        this.board=board;
        this.movedPiece=movedPiece;
        this.destinationCoordinate=destinationCoordinate;
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }
    private int getCurrentCoordinate() {
        return this.getMovedPiece().getPiecePosition();
    }

    public boolean isAttack(){
        return false;
    }
    public boolean isCastlingMove(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    @Override
    public int hashCode() {
        int result=1;
        result=31*result+this.destinationCoordinate;
        result=31*result+this.movedPiece.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(!(obj instanceof Move)){
            return false;
        }
        final Move move=(Move)obj;
        return getCurrentCoordinate()==move.getCurrentCoordinate()
                &&getDestinationCoordinate()==move.getDestinationCoordinate()
                && getMovedPiece().equals(move.getMovedPiece());
    }

    public Board execute() {
        final Board.Builder builder= new Board.Builder();
        for(final Piece piece: this.board.getCurrentPlayer().getActivePieces()){
            //To do more
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }
        for(final Piece piece:this.board.getCurrentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
        return builder.build();
    }


    public static final class MajorMove extends Move{
        public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate){
            super(board, movedPiece, destinationCoordinate);
        }

    }
    public static class AttackMove extends Move{
        final Piece attackedPiece;
        public AttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece){
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode()+ super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(this==obj){
                return true;
            }
            if(!(obj instanceof AttackMove)){
                return false;
            }
            final AttackMove otherAttackMove=(AttackMove) obj;
            return super.equals(otherAttackMove)&&getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public Board execute() {
            return null;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }

    public static class PawnMove extends Move{

        public PawnMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }
    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(Board board, Piece movedPiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
    }

    public static class PawnEnPassantAttackMove extends PawnAttackMove{
        public PawnEnPassantAttackMove(Board board, Piece movedPiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
    }
    public static class PawnJump extends Move{

        public PawnJump(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder=new Builder();
            for(final Piece piece:this.board.getCurrentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece:this.board.getCurrentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn=(Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }
    public static class CastleMove extends Move{

        public CastleMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }
    public static class KingSideCastleMove extends CastleMove{

        public KingSideCastleMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }
    public static class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }
    public static class NullMove extends Move{

        private NullMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Can't execute the null move!");
        }
    }
    public static class MoveFactory{
        private MoveFactory(){
            throw new RuntimeException("Not instantiable");
        }
        public static Move createMove(final Board board,final int currentCoordinate,final int destinationCoordinate){
            for(final Move move:board.getAllLegalMoves()){
                if(move.getCurrentCoordinate()==currentCoordinate&&
                        move.getDestinationCoordinate()==destinationCoordinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }


}
