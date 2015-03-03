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
    private boolean isWhite;

    @Override
    public Move getMove(DraughtsState s) {
        isWhite = s.isWhiteToMove();
        // Iterative deepening
        for (MAXDEPTH = 5; MAXDEPTH < 7; MAXDEPTH += 1) {
            int a = Alpha(s, -INF, INF, 0);
            System.out.println("depth:" + MAXDEPTH + ", a:" + a);
        }
        return bestMove;
    }

    public int Alpha(DraughtsState s, int alpha, int beta, int depth) {
        List<Move> children = s.getMoves();
        // This node is a leaf
        if(children.isEmpty() || depth == MAXDEPTH) {
            return heuristicVal(s, depth);
        }
        
        int v = -INF;
        for (Move move : children) {
            s.doMove(move);
            int result = Beta(s, v, beta, depth + 1);
            if (result > v) {
                if (depth == 0) {
                    bestMove = move;
                }
                v = result;
            }
            s.undoMove(move);

            // If this condition holds then we can do pruning
            if (v >= beta) {
                // break out of the for loop and return v.
                return v;
            }
        }

        return v;
    }

    public int Beta(DraughtsState s, int alpha, int beta, int depth) {
        List<Move> children = s.getMoves();
        // This node is a leaf
        if(children.isEmpty() || depth == MAXDEPTH) {
            return heuristicVal(s, depth);
        }
        
        int v = INF;
        for (Move move : children) {
            s.doMove(move);
            int result = Alpha(s, alpha, v, depth + 1);
            if (result < v) {
                if (depth == 0) {
                    bestMove = move;
                }
                v = result;
            }
            s.undoMove(move);

            // If this condition holds then we can do pruning
            if (alpha >= v) {
                // break out of the for loop and return v.
                return v;
            }

        }
        
        return v;
    }

    public static final int WHITEPIECE = 1;
    public static final int BLACKPIECE = 2;
    public static final int WHITEKING = 3;
    public static final int BLACKKING = 4;
    
    public int heuristicVal(DraughtsState s, int depth) {
        // TODO count all pieces/types, opposite value if iswhite
        int[] pieces = s.getPieces();

        int h = 0;
        for (int piece : pieces) {
            h += ((piece == WHITEPIECE) ? 3 : 0) - ((piece == BLACKPIECE) ? 2 : 0) + 
                    ((piece == WHITEKING) ? 8 : 0) - ((piece == BLACKKING) ? 5 : 0);
        }

        if (isWhite) {
            return h;
        } else {
            return -h;
        }
    }

    @Override
    public void stop() {

    }
}
