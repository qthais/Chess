package  com.chess.engine.pieces;
import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public abstract class Piece {
    protected final int piecePosition;
    protected  final Alliance pieceAlliance;
    protected boolean isFirstMove;
    Piece(final int  piecePosition, final Alliance pieceAlliance){
        this.piecePosition=piecePosition;
        this.pieceAlliance=pieceAlliance;
        this.isFirstMove=false;
    }

    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    protected boolean isFirstMove() {
        return this.isFirstMove();
    }
}
