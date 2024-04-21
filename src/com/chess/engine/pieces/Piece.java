package  com.chess.engine.pieces;
import java.util.List;
import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public abstract class Piece {
    protected final int piecePosition;
    protected  final Alliance pieceAlliance;
    Piece(final int  piecePosition, final Alliance pieceAliiance){
        this.piecePosition=piecePosition;
        this.pieceAlliance=pieceAliiance;
    }

    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public abstract List<Move> calculateLegalMoves(final Board board);

}
