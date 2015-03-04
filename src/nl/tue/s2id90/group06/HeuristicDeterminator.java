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
       
    // Determine the heuristic value for a certain state s
    public int calculateVal(DraughtsState s, int depth) {
        int[] pieces = s.getPieces();
        pieces[0] = depth; // Add depth to the first place of the array: the first place used to be 0 all the time.
        
        // This is where the actual heuristic is calculated.
        int h = 0;
        for (int i = 1; i < pieces.length; i++) {
            int piece = pieces[i];

            h += ((piece == WHITEPIECE) ? 2 : 0) - ((piece == BLACKPIECE) ? 1 : 0) + 
                    ((piece == WHITEKING) ? 3 : 0) - ((piece == BLACKKING) ? 4 : 0);
        }

        // Turn the heuristic around if we are black
        if (isWhite) {
            return h;
        } else {
            return -h;
        }
    }
}
