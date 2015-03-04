package nl.tue.s2id90.group06;

import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 *
 * @authors Jordie Kolkman & Tijs Maas
 */
public class AlphaBetaPlayer extends DraughtsPlayer {

    private int INF = Integer.MAX_VALUE;
    // Warning: bestMove may be used with concurrency, use synchonized methods below to access this variable.
    private Move bestMove;
    private boolean stopped;
    private HeuristicDeterminator heuristics;
    
    @Override
    public Move getMove(DraughtsState s) {
        stopped = false;
        // Initialize heuristics calculator or just clear its cache if it already exists.
        if(heuristics == null) {
            heuristics = new HeuristicDeterminator(s);
        }
        
        // Iterative deepening, stops immediately when stop() is called
        try {
            for (int maxdepth = 4; maxdepth < 30; maxdepth += 1) {
                // Always start tree with alpha (heuristic adapts to this by being inverted when player is black).
                double a = Alpha(s, -INF, INF, maxdepth, true);
                System.out.println("depth:" + maxdepth + ", a:" + a);
            }
        // Just in case that we get an exception, we want to keep playing.
        } catch(AIStoppedException e) {
            System.out.println("depth search interrupted");
        } catch(Exception e) {
            e.printStackTrace();
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
        List<Move> children = s.getMoves();
        if(depth <= 0 || children.isEmpty()) {
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
            if (v > beta) {
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
        List<Move> children = s.getMoves();
        if(depth <= 0 || children.isEmpty()) {
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
//            if (alpha > v) {
//                // break out of the for loop and return v.
//                break;
//            }

        }
        
        return v;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }
    
    private static class AIStoppedException extends Exception {}
}
