package nl.tue.s2id90.group06;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.BetterMove;
import org10x10.dam.game.BoardState;
import org10x10.dam.game.Move;

/**
 * A simple draughts player that plays the first moves that comes to mind and values all moves with value 0.
 *
 * @author huub
 */
public class OptimisticPlayer extends DraughtsPlayer {

    boolean isWhite = true;

    public OptimisticPlayer() {
        super(UninformedPlayer.class.getResource("resources/optimist.png"));
        this.moves = new HashMap<>();
    }

    OptimisticPlayer(MyDraughtsPlugin aThis) {
        this.moves = new HashMap<>();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static final int EMPTY = 0;
    public static final int WHITEPIECE = 1;
    public static final int BLACKPIECE = 2;
    public static final int WHITEKING = 3;
    public static final int BLACKKING = 4;
    public static final int WHITEFIELD = 5;
    public static final int COLS = 10;
    int[] board;
    /**
     * NOTE: Explanations are given as if we always start on top. (black I guess)
     *
     */

    // This knowledge reduces a lot of checks, hence faster tree branching for us :)
    // rows 0-9
    int minrow = 4;
    int maxrow = 5;
    boolean king = false;
    int ourdraught = BLACKPIECE;
    int ourking = BLACKKING;
    int enemydraught = WHITEPIECE;
    int enemyking = WHITEKING;
    HashMap<Integer, List<Integer>> moves;

    /**
     * Some macro defintions so that we can search neighbouring squares/cells easily.
     * @param c
     * @return 
     */
    public int SQ_LEFT_TOP(int c) {
        //If already on the left or top, we can not select a left top, hence return 0;
        // The second statement checks whether the row is odd/even (this matters, because the 'distance' between squares depends on this).
        return (c % 10 == 6 || c <= 5)
                ? 0 : ((c % 10 >= 1) && (c % 10 <= 5))
                        ? c - 5 : c - 6; // ODD ROW : EVEN ROW
    }

    public int SQ_LEFT_BOTTOM(int c) {
        return (c % 10 == 6 || c > 45)
                ? 0 : ((c % 10 >= 1) && (c % 10 <= 5))
                        ? c - 5 : c - 4; // ODD ROW : EVEN ROW
    }

    public int SQ_RIGHT_TOP(int c) {
        return (c % 10 == 5 || c <= 5)
                ? 0 : ((c % 10 >= 1) && (c % 10 <= 5))
                        ? c - 6 : c - 5; // ODD ROW : EVEN ROW
    }

    public int SQ_RIGHT_BOTTOM(int c) {
        return (c % 10 == 5 || c > 45)
                ? 0 : ((c % 10 >= 1) && (c % 10 <= 5))
                        ? c + 6 : c + 5; // ODD ROW : EVEN ROW
    }
    
    public boolean ENEMY(int c) {
        return c == enemydraught || c == enemyking;
    }

    /**
     * Check if this square has to hit an enemy draught. Put the moves in the global hashmap.
     *
     * @param c
     * @return
     */
    public List<TMove> SQ_HIT(TMove base) {
        List<TMove> possible_moves = new ArrayList();
        possible_moves.add(base);
        
        int c = base.getPos();
        // Draught can hit if an enemy is next to it, and after that should be an empty place.
        if (board[c] == ourdraught) {
            // Can we search 2 left/right and under/above? Or is the edge in the way?
            if (c % 5 == 0) {
                // We can only search left squares
                int newpos = SQ_LEFT_BOTTOM(SQ_LEFT_BOTTOM(c));
                if (c <= 40 && board[SQ_LEFT_BOTTOM(c)] == enemydraught && board[newpos] == EMPTY) {
                    // Search recursively if we can jump over more enemies
                    TMove nextbase = new TMove(newpos);
                    base.next(nextbase);
                    List<TMove> next_moves = SQ_HIT(nextbase);
                    // WATCH THIS AGAIN, I FELL ASLEEP WHILE PROGRAMMING THIS
                    possible_moves.addAll(next_moves);
                }
                if (c > 10 && board[SQ_LEFT_TOP(c)] == enemydraught && board[SQ_LEFT_TOP(SQ_LEFT_TOP(c))] == EMPTY) {
                    moves.put(c, new ArrayList(SQ_LEFT_TOP(SQ_LEFT_TOP(c))));
                }
            } else if (c - 1 % 5 == 0) {
                // We can only search right squares
                if (c <= 40 && board[SQ_RIGHT_BOTTOM(c)] == enemydraught && board[SQ_RIGHT_BOTTOM(SQ_RIGHT_BOTTOM(c))] == EMPTY) {
                    //moves.put(c, new ArrayList(SQ_RIGHT_BOTTOM(SQ_RIGHT_BOTTOM(c))));
                    int newpos = SQ_RIGHT_BOTTOM(SQ_RIGHT_BOTTOM(c));
                    TMove nextbase = new TMove(newpos);
                    base.next(nextbase);
                    List<TMove> next_moves = SQ_HIT(nextbase);
                    // WATCH THIS AGAIN, I FELL ASLEEP WHILE PROGRAMMING THIS
                    possible_moves.addAll(next_moves);
                }
                if (c > 10 && board[SQ_RIGHT_TOP(c)] == enemydraught && board[SQ_RIGHT_TOP(SQ_RIGHT_TOP(c))] == EMPTY) {
                    moves.put(c, new ArrayList(SQ_RIGHT_TOP(SQ_RIGHT_TOP(c))));
                }
            } else {
                // We can search all 4 squares around it
                if (c <= 40 && board[SQ_RIGHT_BOTTOM(c)] == enemydraught && board[SQ_RIGHT_BOTTOM(SQ_RIGHT_BOTTOM(c))] == EMPTY) {
                    //moves.put(c, new ArrayList(SQ_RIGHT_BOTTOM(SQ_RIGHT_BOTTOM(c))));
                    int newpos = SQ_RIGHT_BOTTOM(SQ_RIGHT_BOTTOM(c));
                    TMove nextbase = new TMove(newpos);
                    base.next(nextbase);
                    List<TMove> next_moves = SQ_HIT(nextbase);
                    // WATCH THIS AGAIN, I FELL ASLEEP WHILE PROGRAMMING THIS
                    possible_moves.addAll(next_moves);
                }
                if (c > 10 && board[SQ_RIGHT_TOP(c)] == enemydraught && board[SQ_RIGHT_TOP(SQ_RIGHT_TOP(c))] == EMPTY) {
                    moves.put(c, new ArrayList(SQ_RIGHT_TOP(SQ_RIGHT_TOP(c))));
                }
                if (c <= 40 && board[SQ_LEFT_BOTTOM(c)] == enemydraught && board[SQ_LEFT_BOTTOM(SQ_LEFT_BOTTOM(c))] == EMPTY) {
                    //moves.put(c, new ArrayList(SQ_LEFT_BOTTOM(SQ_LEFT_BOTTOM(c))));
                    int newpos = SQ_RIGHT_BOTTOM(SQ_RIGHT_BOTTOM(c));
                    TMove nextbase = new TMove(newpos);
                    base.next(nextbase);
                    List<TMove> next_moves = SQ_HIT(nextbase);
                    // WATCH THIS AGAIN, I FELL ASLEEP WHILE PROGRAMMING THIS
                    possible_moves.addAll(next_moves);
                }
                if (c > 10 && board[SQ_LEFT_TOP(c)] == enemydraught && board[SQ_LEFT_TOP(SQ_LEFT_TOP(c))] == EMPTY) {
                    moves.put(c, new ArrayList(SQ_LEFT_TOP(SQ_LEFT_TOP(c))));
                }
            }
        // King can hit if an enemy is in the same line, and after that should be an empty place. For every empty place a possible move will be generated.
        } // TODO KING
        
        return possible_moves;
    }

    @Override
    /**
     * @return a random move *
     */
    public Move getMove(DraughtsState s) {
        long startTime = System.nanoTime();
        isWhite = s.isWhiteToMove();
        board = s.getPieces();
        
        // If your amount of checkers has decreased, check minrow/maxrow boundaries

        /**
         * Scan from the bottom up if we must hit the opponent. - every row before minrow is safe. (Only our draughts) -
         * every row after maxrow does not contain our draughts (so no danger). - every other row contains potential hit
         * danger and must be scanned.
         */
        List<TMove> hitmoves = new ArrayList();
        for (int c = maxrow * COLS/2; c >= minrow * COLS/2; c--) {
            TMove base = new TMove(c);
            hitmoves.addAll(SQ_HIT(base));
        }
        long endTime = System.nanoTime();
        System.out.println("2ID90-GROUP-06: Checking for enemy collision took "+(endTime-startTime)+"ns");
        
        if (!hitmoves.isEmpty()) {
            // We have to hit some enemy, lets choose the best one ;)
            
            TMove chosen = hitmoves.get(0);
            System.out.println("2ID90-GROUP-06: HITMOVE:"+chosen.toString());
            
            // Convert our TMoves to builtin (slower) moves.
            startTime = System.nanoTime();
            List<Move> generatedmoves = s.getMoves();
            endTime = System.nanoTime();
            System.out.println("2ID90-GROUP-06: Back-conversion of moves took "+(endTime-startTime)+"ns");
            for(Move m : generatedmoves) {
                if(m.getBeginField() == chosen.getRoot().getPos() && m.getEndField() == chosen.getPos()) {
                    
                    return m;
                }
            }
            endTime = System.nanoTime();
            System.out.println("2ID90-GROUP-06: Back-conversion of moves took "+(endTime-startTime)+"ns");
            System.out.println("2ID90-GROUP-06: Error occured, calculated jump path not available:"+chosen.toString());
            
        } else {
            // Generate optimal moves
            return s.getMoves().get(0);
        }

        // Assign points for every possible move, and recursively call min/max with our new chessboard
        
    
        
        Move finalmove = new BetterMove();
        return finalmove;// moves.get(0);
    }

    @Override
    public void stop() {
        super.stop(); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    public Integer getValue() {
        return 0;
    }
}
