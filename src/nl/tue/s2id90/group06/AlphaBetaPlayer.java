package nl.tue.s2id90.group06;

import java.util.List;
import java.util.Random;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implements Alpha Beta search algorithm to compute the best possible draughts move.
 * @authors Jordie Kolkman & Tijs Maas
 */
public class AlphaBetaPlayer extends DraughtsPlayer {

    private final int INF = Integer.MAX_VALUE;
    private HeuristicDeterminator heuristics;
    private Move bestMove;
    private boolean stopped;
    private double last_alpha;
    
    @Override
    public Move getMove(DraughtsState s) {
        bestMove = null;
        stopped = false;
        // Initialize heuristics calculator if it does not yet exist
        if(heuristics == null) {
            heuristics = new HeuristicDeterminator(s);
        }
        // Store our initial state in case something goes wrong
        DraughtsState initialState = s.clone();
        
        // Iterative deepening, stops immediately when stop() is called
        try {
            for (int maxdepth = 7; maxdepth < 30; maxdepth += 1) {
                // Always start tree with alpha (heuristic adapts to this by being inverted when player is black).
                last_alpha = Alpha(s, -INF, INF, maxdepth, true);
                System.out.println("G06: Depth:" + maxdepth + ", alpha:" + last_alpha);
            }
        } catch(AIStoppedException e) {
            System.out.println("G06: Depth search interrupted");
        } catch(Exception e) {
            // Catch any error, so that we can still continue playing.
            e.printStackTrace();
        }
        
        // We can not return null, so picking a random move would be our best shot in this case.
        if(bestMove == null) {
            Random r = new Random();
            List<Move> moves = initialState.getMoves();
            int i = r.nextInt(moves.size());
            System.out.println("G06: We were not able to calculate our moves, so we moved at random");
            return moves.get(i);
        }
        
        return this.bestMove;
    }

    // Maximizing fuction
    // If initial, then this is the first (best) move to be calculated, this move will then be stored.
    // (Calculating and storing the best move for every GameState as suggested in the assignment is inefficient, 
    //  because you will only actually use the value of the first GameState.)
    public double Alpha(DraughtsState s, double alpha, double beta, int depth, boolean initial) throws AIStoppedException {
        if(stopped) throw new AIStoppedException();
        
        // This node is a leaf
        List<Move> children;
        if(depth <= 0 || (children = s.getMoves()).isEmpty()) {
            return heuristics.calculateVal(s, depth);
        }
        
        // The best move of the future
        Move futureBestMove = null;
        
        double v = -INF;
        for (Move move : children) {
            s.doMove(move);
            double result = Beta(s, v, beta, depth - 1);
            if (result > v) {
                if (initial) {
                    futureBestMove = move;
                }
                v = result;
            }
            s.undoMove(move);

            // If this condition holds then we can do pruning
            if (v >= beta) {
                // break out of the for loop and return v.
                break;
            }
        }

        // Best move can be determined when we have looked at all children (except pruned onces).
        if (initial) {
            this.bestMove = futureBestMove;
        }
        return v;
    }

    // Minimizing function
    public double Beta(DraughtsState s, double alpha, double beta, int depth) throws AIStoppedException {
        // This node is a leaf
        List<Move> children;
        if(depth <= 0 || (children = s.getMoves()).isEmpty()) {
            return heuristics.calculateVal(s, depth);
        }
        
        double v = INF;
        for (Move move : children) {
            s.doMove(move);
            double result = Alpha(s, alpha, v, depth - 1, false);
            if (result < v) {
                v = result;
            }
            s.undoMove(move);

            // If this condition holds then we can do pruning
            if (alpha >= v) {
                // break out of the for loop and return v.
                break;
            }

        }
        
        return v;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

    @Override
    public Integer getValue() {
        return (int) (last_alpha * 10.0);
    }
    
    
    
    private static class AIStoppedException extends Exception {}
}
