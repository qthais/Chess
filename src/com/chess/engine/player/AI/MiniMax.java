package com.chess.engine.player.AI;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    public MiniMax(final int searchDepth){
        this.boardEvaluator=new StandardBoardEvaluator();
        this.searchDepth=searchDepth;
    }


    @Override
    public Move execute(Board board, int depth) {
        final long startTime=System.currentTimeMillis();
        Move bestMove=null;
        int highestSeenValue=Integer.MIN_VALUE;
        int lowestSeenValue=Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.getCurrentPlayer()+" THINKING with depth = "+depth);
        for(final Move move:board.getCurrentPlayer().getLegalMoves()){
            final MoveTransition moveTransition=board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                currentValue=board.getCurrentPlayer().getAlliance().isWhite()
                        ?min(moveTransition.getTransitionBoard(),depth-1):
                        max(moveTransition.getTransitionBoard(),depth-1);
                if(board.getCurrentPlayer().getAlliance().isWhite()&& currentValue>=highestSeenValue){
                    highestSeenValue=currentValue;
                    bestMove=move;
                } else if (board.getCurrentPlayer().getAlliance().isBlack()&& currentValue<=lowestSeenValue) {
                    lowestSeenValue=currentValue;
                    bestMove=move;
                }
            }
        }
        final long executionTime=System.currentTimeMillis()-startTime;
        return bestMove;
    }

    @Override
    public Move execute(Board board) {
        Move bestMove=null;
        int highestSeenValue=Integer.MIN_VALUE;
        int lowestSeenValue=Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.getCurrentPlayer()+" THINKING with depth = "+searchDepth);
        for(final Move move:board.getCurrentPlayer().getLegalMoves()){
            final MoveTransition moveTransition=board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                currentValue=board.getCurrentPlayer().getAlliance().isWhite()
                        ?min(moveTransition.getTransitionBoard(),searchDepth-1):
                        max(moveTransition.getTransitionBoard(),searchDepth-1);
                if(board.getCurrentPlayer().getAlliance().isWhite()&& currentValue>=highestSeenValue){
                    highestSeenValue=currentValue;
                    bestMove=move;
                } else if (board.getCurrentPlayer().getAlliance().isBlack()&& currentValue<=lowestSeenValue) {
                    lowestSeenValue=currentValue;
                    bestMove=move;
                }
            }
        }
        return bestMove;
    }
    public int min(Board board, int depth){
        if(depth==0||isEndGame(board)){
            return this.boardEvaluator.evaluate(board,depth);
        }
        int lowestSeenValue=Integer.MAX_VALUE;
        for(final Move move:board.getCurrentPlayer().getLegalMoves()){
            final MoveTransition moveTransition=board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue=max(moveTransition.getTransitionBoard(),depth-1);
                if(currentValue<=lowestSeenValue){
                    lowestSeenValue=currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    private static boolean isEndGame(Board board) {
        return board.getCurrentPlayer().isInCheckMate()||board.getCurrentPlayer().isInStaleMate();
    }

    public int max(Board board, int depth){
        if(depth==0){
            return this.boardEvaluator.evaluate(board,depth);
        }
        int highestSeenValue=Integer.MIN_VALUE;
        for(final Move move:board.getCurrentPlayer().getLegalMoves()){
            final MoveTransition moveTransition=board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue=min(moveTransition.getTransitionBoard(),depth-1);
                if(currentValue>=highestSeenValue){
                    highestSeenValue=currentValue;
                }
            }
        }
        return highestSeenValue;
    }

    @Override
    public String toString() {
        return "Minimax";
    }
}
