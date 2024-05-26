package com.chess.engine.board;
import com.chess.engine.board.Board.*;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;
    public static final Move NULL_MOVE=new NullMove();
    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate){
        this.board=board;
        this.movedPiece=movedPiece;
        this.destinationCoordinate=destinationCoordinate;
        this.isFirstMove=movedPiece.isFirstMove();
    }
    private Move(final Board board, final int destinationCoordinate){
        this.board=board;
        this.movedPiece=null;
        this.destinationCoordinate=destinationCoordinate;
        this.isFirstMove=false;
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Board getBoard() {
        return board;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }
    public int getCurrentCoordinate() {
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
        result=31*result+this.movedPiece.getPiecePosition();
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
        final Builder builder= new Board.Builder();
        for(final Piece piece: this.board.getCurrentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }
        for(final Piece piece:this.board.getCurrentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
        return builder.build();//return new board(builder)
    }

    public static final class MajorAttackMove extends AttackMove{

        public MajorAttackMove(Board board, Piece movedPiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj||obj instanceof AttackMove&& super.equals(obj);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType()+BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static final class MajorMove extends Move{
        public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate){
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj||obj instanceof MajorMove&&super.equals(obj);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString()+BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
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
            if(!(obj instanceof AttackMove otherAttackMove)){
                return false;
            }
            return super.equals(otherAttackMove)&&getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
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

        @Override
        public boolean equals(Object obj) {
            return this==obj|| obj instanceof PawnMove&& super.equals(obj);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(Board board, Piece movedPiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj||obj instanceof PawnAttackMove&&super.equals(obj);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0,1)+"x"+
                    BoardUtils.getPositionAtCoordinate((this.destinationCoordinate));
        }
    }

    public static class PawnEnPassantAttackMove extends PawnAttackMove{
        public PawnEnPassantAttackMove(Board board, Piece movedPiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj||obj instanceof PawnAttackMove && super.equals(obj);
        }

        @Override
        public Board execute() {
            final Builder builder= new Builder();
            for(final Piece piece:this.board.getCurrentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece:this.board.getCurrentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackedPiece())){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }
    public static class PawnPromotion extends Move{
        final Move decoratedMove;
        final Pawn promotedPawn;
        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove=decoratedMove;
            this.promotedPawn=(Pawn)decoratedMove.getMovedPiece();
        }

        @Override
        public int hashCode() {
            return decoratedMove.hashCode()+31*promotedPawn.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj || obj instanceof PawnPromotion&&(super.equals(obj));
        }

        @Override
        public Board execute() {
            final Board pawnMovedBoard=this.decoratedMove.execute();
            final Builder builder=new Builder();
            for(final Piece piece: pawnMovedBoard.getCurrentPlayer().getActivePieces()){
                if(!this.promotedPawn.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece:pawnMovedBoard.getCurrentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.getCurrentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            return "";
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

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static abstract class CastleMove extends Move{
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;
        public CastleMove(Board board,
                          Piece movedPiece,
                          int destinationCoordinate,
                          Rook castleRook,
                          int castleRookStart,
                          int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook=castleRook;
            this.castleRookStart=castleRookStart;
            this.castleRookDestination=castleRookDestination;
        }

        public Rook getCastleRook() {
            return castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder= new Builder();
            for(final Piece piece:this.board.getCurrentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)&&!this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece:this.board.getCurrentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination,this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode() {
            int result=31*super.hashCode()+this.castleRook.hashCode();
            result=31*result+this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
           if(this==obj){
               return true;
           }
           if(!(obj instanceof CastleMove otherCastleMove)){
               return false;
           }
            return super.equals(otherCastleMove)&&this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }
    public static class KingSideCastleMove extends CastleMove{

        public KingSideCastleMove(Board board,
                                  Piece movedPiece,
                                  int destinationCoordinate,
                                  Rook castleRook,
                                  int castleRookStart,
                                  int castleRookDestination) {
            super(board,movedPiece,destinationCoordinate,castleRook,castleRookStart,castleRookDestination);

        }

        @Override
        public boolean equals(Object obj) {
            return this==obj|| obj instanceof KingSideCastleMove &&super.equals(obj);
        }

        @Override
        public String toString() {
            return "O-O";
        }
    }
    public static class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(Board board,
                                   Piece movedPiece,
                                   int destinationCoordinate,
                                   Rook castleRook,
                                   int castleRookStart,
                                   int castleRookDestination) {
            super(board,movedPiece,destinationCoordinate,castleRook,castleRookStart,castleRookDestination);
        }
        @Override
        public boolean equals(Object obj) {
            return this==obj|| obj instanceof QueenSideCastleMove &&super.equals(obj);
        }

        @Override
        public String toString() {
            return "O-O-O";
        }
    }
    public static class NullMove extends Move{

        private NullMove() {
            super(null, 65);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Can't execute the null move!");
        }

        @Override
        public int getCurrentCoordinate() {
            return -1;
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
