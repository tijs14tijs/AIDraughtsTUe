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
    // Our standard search depth (for now)
    private int MAXDEPTH = 6;
    private Move bestMove;

    @Override
    public Move getMove(DraughtsState s) {
        boolean isWhite = s.isWhiteToMove();
        if(isWhite) {
            Alpha(s, -INF, INF, 0);
        }else{
            Beta(s, -INF, INF, 0);
        }
        System.out.println("a:"+Alpha(s, -INF, INF, 0)+", b:"+Beta(s, -INF, INF, 0));
        return bestMove;
    }
    
    public int Alpha(DraughtsState s, int alpha, int beta, int depth) {
        List<Move> children = s.getMoves();
        int v = -INF;
        for(Move move : children) {
            // At maxdepth give the heuristic values
            if (depth >= MAXDEPTH) {
                int result = heuristicVal(move, depth);
                v = Math.max(result, v); 
            } else {
                s.doMove(move);
                int result = Beta(s, v, beta, depth + 1);
                if(result > v) {
                    if(depth == 0) bestMove = move;
                    v = result;
                }
                s.undoMove(move);
                
                // If this condition holds then we can do pruning
                if(v > beta) {
                    // break out of the for loop and return v.
                    return v;
                }
            }
        }
        
        if(v == -INF) {
            MAXDEPTH--;
            return Alpha(s, alpha, beta, depth);
        }
        
        return v;
    }
    
    public int Beta(DraughtsState s, int alpha, int beta, int depth) {
        List<Move> children = s.getMoves();
        int v = INF;
        for(Move move : children) {
            if(depth >= MAXDEPTH) {
                int result = heuristicVal(move, depth);
                v = Math.min(result, v); 
            } else {
                s.doMove(move);
                int result = Alpha(s, alpha, v, depth + 1);
                if(result < v) {
                    if(depth == 0) bestMove = move;
                    v = result;
                }
                s.undoMove(move);
                
                // If this condition holds then we can do pruning
                if(alpha > v) {
                    // break out of the for loop and return v.
                    return v;
                }
            }
        }
        
        if(v == INF) {
            MAXDEPTH--;
            return Beta(s, alpha, beta, depth);
        }
        return v;
    }
    
    public int heuristicVal(Move move, int depth) {
        return -depth + move.getCaptureCount() * 5 + (move.isPromotion() ? 30 : 0);
    }

    @Override
    public void stop() {
        
    }
    
    
    
    
}
