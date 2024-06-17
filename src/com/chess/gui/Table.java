package com.chess.gui;

import com.chess.engine.board.*;
import com.chess.engine.pieces.*;
import com.chess.engine.player.AI.MiniMax;
import com.chess.engine.player.AI.MoveStrategy;
import com.chess.engine.player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(1000,1000);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(704,704);
    private static final Dimension TILE_PANEL_DIMENSION=new Dimension(10,10);
    private final JFrame gameFrame;
    private MoveLog moveLog;
    private GameHistoryPanel gameHistoryPanel;
    private TakenPiecesPanel takenPiecesPanel;
    private BoardPanel boardPanel;
    private Board chessBoard;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private Move computerMove;
    private boolean highlightLegalMoves;
    private static String defaultPieceImagePath="art/pieces/plain/";

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");
    private static Table INSTANCE=new Table();
    private final GameSetup gameSetup;
    private Table(){
        this.gameFrame=new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setResizable(false);
        final JMenuBar tableMenuBar=createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard=Board.createStandardBoard();
        this.gameHistoryPanel=new GameHistoryPanel();
        this.takenPiecesPanel=new TakenPiecesPanel();
        this.boardPanel=new BoardPanel();
        this.moveLog=new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup=new GameSetup(this.gameFrame,true);
        this.boardDirection=BoardDirection.NORMAL;
        highlightLegalMoves=false;
        this.gameFrame.add(this.boardPanel,BorderLayout.CENTER);
        this.gameFrame.add(this.takenPiecesPanel,BorderLayout.WEST);
        this.gameFrame.add(this.gameHistoryPanel,BorderLayout.EAST);
        this.gameFrame.setVisible(true);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setLocationRelativeTo(null);
        this.gameFrame.pack();
    }
    public static Table get(){
        return INSTANCE;
    }

    public static void setINSTANCE(Table INSTANCE) {
        Table.INSTANCE = INSTANCE;
    }
    public void restartGame(){
        this.gameFrame.dispose();
        setINSTANCE(new Table());
        show();
    }

    public void show(){
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }
    private Board getGameBoard(){
        return this.chessBoard;
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar=new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu=new JMenu("File");
        final JMenuItem openPGN=new JMenuItem("New Game");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
                gameFrame.repaint();
            }
        });
        fileMenu.add(openPGN);
        final JMenuItem exitMenuItem=new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }
    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu=new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem=new JMenuItem("FlipBoard");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection=boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMoveHighLighterCheckBox= new JCheckBoxMenuItem("Highlight Legal Moves",false);
        legalMoveHighLighterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves=legalMoveHighLighterCheckBox.isSelected();
            }
        });
        preferencesMenu.add(legalMoveHighLighterCheckBox);
        return preferencesMenu;
    }
    public static class MoveLog{
        private List<Move> moves;
        MoveLog(){
            this.moves=new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(Move move){
            this.moves.add(move);
        }
        public int size(){
            return this.moves.size();
        }
        public void clear(){
            this.moves.clear();
        }
        public Move removeMove(int index){
            return this.moves.remove(index);
        }
        public boolean removeMove(Move move){
            return this.moves.remove(move);
        }
    }
    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles=new ArrayList<>();
            for(int i=0;i< BoardUtils.NUM_TILES;i++){
                final TilePanel tilePanel=new TilePanel(this,i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);

        }

        public void drawBoard(Board board) {
            removeAll();
            for(final TilePanel tilePanel:boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }
    enum PlayerType{
        HUMAN,
        COMPUTER
    }
    private class TilePanel extends JPanel{
        public BufferedImage blackBackground;
        public BufferedImage whiteBackground;
        private final int tileId;
        TilePanel(final BoardPanel boardPanel,final int tileId){
            try {
                blackBackground =ImageIO.read(new File("art/holywarriors/Black.JPG"));
                whiteBackground =ImageIO.read(new File("art/holywarriors/White.JPG"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            setLayout(new GridBagLayout());
            this.tileId=tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(isRightMouseButton(e)) {
                        sourceTile = null;
                        humanMovedPiece = null;
                    }else if(isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        }else{
                            destinationTile=chessBoard.getTile((tileId));
                            final Move move=Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());

                            final MoveTransition transition=chessBoard.getCurrentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()){
                                chessBoard=transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard,moveLog);
                                takenPiecesPanel.redo(moveLog);
                                if(gameSetup.isAIPlayer(chessBoard.getCurrentPlayer())){
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);

                            }
                        });

                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            validate();
        }
        private void assignTilePieceIcon(final Board board){
            this.removeAll();//can be removed
            if(board.getTile(this.tileId).isTileOccupied()){
                try {
                    final BufferedImage image= ImageIO.read(
                            new File(defaultPieceImagePath+
                                    board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1)+
                                    board.getTile(this.tileId).getPiece().toString()+".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            assignTileColor(g);
        }

        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK[this.tileId]
                    ||BoardUtils.SIXTH_RANK[this.tileId]
                    ||BoardUtils.FOURTH_RANK[this.tileId]
                    ||BoardUtils.SECOND_RANK[this.tileId]){
                setBackground(this.tileId%2==0?lightTileColor:darkTileColor);
            }else if(BoardUtils.SEVENTH_RANK[this.tileId]
                    ||BoardUtils.FIFTH_RANK[this.tileId]
                    ||BoardUtils.THIRD_RANK[this.tileId]
                    ||BoardUtils.FIRST_RANK[this.tileId]){
                setBackground(this.tileId%2!=0?lightTileColor:darkTileColor);
            }
        }
        private  void assignTileColor(Graphics g){
            if(BoardUtils.EIGHTH_RANK[this.tileId]
                    ||BoardUtils.SIXTH_RANK[this.tileId]
                    ||BoardUtils.FOURTH_RANK[this.tileId]
                    ||BoardUtils.SECOND_RANK[this.tileId]){
                g.drawImage(this.tileId%2==0?whiteBackground:blackBackground,0,0,this.getWidth(),this.getHeight(),this);
            }else if(BoardUtils.SEVENTH_RANK[this.tileId]
                    ||BoardUtils.FIFTH_RANK[this.tileId]
                    ||BoardUtils.THIRD_RANK[this.tileId]
                    ||BoardUtils.FIRST_RANK[this.tileId]){
                g.drawImage(this.tileId%2!=0?whiteBackground:blackBackground,0,0,this.getWidth(),this.getHeight(),this);
            }
        }

        public void drawTile(Board board) {
            assignTilePieceIcon(board);
            highlightLegals(board);
//            validate();
            repaint();
        }
        private void highlightLegals(final Board board){
            if(highlightLegalMoves){
                for(final Move move:pieceLegalMoves(board)){
                    if(move.getDestinationCoordinate()==this.tileId){
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("./art/misc/green_dot.png")))));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        private Collection<Move> pieceLegalMoves(final Board board){
            if(humanMovedPiece!=null&& humanMovedPiece.getPieceAlliance()==board.getCurrentPlayer().getAlliance()){
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }
    }
    private JMenu createOptionMenu(){
        final JMenu optionMenu=new JMenu("Options");
        final JMenuItem setupGameMenuItem= new JMenuItem("Set up Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionMenu.add(setupGameMenuItem);
        return  optionMenu;
    }

    private void setupUpdate(GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }
    private static class TableGameAIWatcher implements Observer{
        @Override
        public void update(Observable o, Object arg) {
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().getCurrentPlayer())
                    &&!Table.get().getGameBoard().getCurrentPlayer().isInCheckMate()
                    &&!Table.get().getGameBoard().getCurrentPlayer().isInStaleMate()){
                //create an AI thread
                final AIThinkTank thinkTank= new AIThinkTank();
                thinkTank.execute();
            }
            if(Table.get().getGameBoard().getCurrentPlayer().isInCheckMate()){
                JOptionPane.showMessageDialog(null,"game over, "+Table.get().getGameBoard().getCurrentPlayer()+" is in checkmate!","Game Result",JOptionPane.INFORMATION_MESSAGE);
            }
            if(Table.get().getGameBoard().getCurrentPlayer().isInStaleMate()){
                JOptionPane.showMessageDialog(null,"game over, "+Table.get().getGameBoard().getCurrentPlayer()+" is in stalemate!","Game Result",JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }
    public void updateGameBoard(Board board){
        this.chessBoard=board;
    }
    public void updateComputerMove(Move move){
        this.computerMove=move;
    }

    private MoveLog getMoveLog() {
        return moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return takenPiecesPanel;
    }

    private BoardPanel getBoardPanel() {
        return boardPanel;
    }
    private void moveMadeUpdate(PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }

    private static class AIThinkTank extends SwingWorker<Move,String>{
        private AIThinkTank(){

        }
        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy miniMax=new MiniMax(Table.get().gameSetup.getSearchDepth());
            final Move bestMove=miniMax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        protected void done() {
            try {
                final Move bestMove=get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().getCurrentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(),Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public enum BoardDirection{
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                Collections.reverse(boardTiles);
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }
}
