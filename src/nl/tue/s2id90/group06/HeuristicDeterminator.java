package nl.tue.s2id90.group06;

import nl.tue.s2id90.draughts.DraughtsState;
import static nl.tue.s2id90.draughts.DraughtsState.*;

/**
 *
 * @author Jordie Kolkman & Tijs Maas
 */
public class HeuristicDeterminator {
    private final boolean isWhite;
            
    public HeuristicDeterminator(DraughtsState s) {
        isWhite = s.isWhiteToMove();
    }
       
    public double SQ_LEFT(int c) {
        //checks if a piece is on the left side of the board.
        return (c % 10 == 6)
                ? 1.5 : 1;
                        
    }
    
    public double SQ_RIGHT(int c) {
        //checks if a piece is on the left side of the board.
        return (c % 10 == 5)
                ? 1.5 : 1;
                        
    }
    
    // Determine the heuristic value for a certain state s
    public double calculateVal(DraughtsState s, int depth) {
        int[] pieces = s.getPieces();
        pieces[0] = depth; // Add depth to the first place of the array: the first place used to be 0 all the time.
        
        // This is where the actual heuristic is calculated.
        double h = 0;
        for (int i = 1; i < pieces.length; i++) {
            int piece = pieces[i];
            
            
            
            
            h += SQ_LEFT(i) * SQ_RIGHT(i) * ((piece == WHITEPIECE) ? 1 : 0) - SQ_LEFT(i) * SQ_RIGHT(i) * ((piece == BLACKPIECE) ? 1 : 0) + 
                    ((piece == WHITEKING) ? 5 : 0) - ((piece == BLACKKING) ? 5 : 0);
        }

        // Turn the heuristic around if we are black
        if (isWhite) {
            return h;
        } else {
            return -h;
        }
    }
}
