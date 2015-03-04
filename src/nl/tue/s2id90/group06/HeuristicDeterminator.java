package nl.tue.s2id90.group06;

import java.util.Arrays;
import java.util.HashMap;
import nl.tue.s2id90.draughts.DraughtsState;
import static nl.tue.s2id90.draughts.DraughtsState.*;

/**
 *
 * @author Jordie Kolkman & Tijs Maas
 */
public class HeuristicDeterminator {
    // If we have seen a state before in the tree, then we store its heuristic.
    private final HashMap<Integer, Integer> heuristicCache;
    private final boolean isWhite;
    private int cacheErr = 0;
    private int cacheGood = 0;
            
    public HeuristicDeterminator(DraughtsState s) {
        isWhite = s.isWhiteToMove();
        heuristicCache = new HashMap();
    }
    
    // Don't worry, we clear the heuristics cache every turn.
    public void clearCache() {
        heuristicCache.clear();
    }
       
    // Determine the heuristic value for a certain state s
    public int calculateVal(DraughtsState s, int depth) {
        int[] pieces = s.getPieces();
        pieces[0] = depth; // Add depth to the first place of the array: the first place used to be 0 all the time.
        
        // Even though caching these values might give hash collisions, it speeds up the algorithm a lot so it is worth it.
        int hashcode = Arrays.hashCode(pieces);
        Integer cacheResult = heuristicCache.get(hashcode);
        
        int h = 0;
        if(cacheResult == null) {
            // This is where the actual heuristic is calculated.
            for (int i = 1; i < pieces.length; i++) {
                int piece = pieces[i];
                
                h += ((piece == WHITEPIECE) ? 2 : 0) - ((piece == BLACKPIECE) ? 1 : 0) + 
                        ((piece == WHITEKING) ? 3 : 0) - ((piece == BLACKKING) ? 4 : 0);
            }
            // Store this value in our cache so that we do not have to calculate it every time.
            heuristicCache.put(hashcode, h);
        } else {
            h = cacheResult;
        }

        // Turn the heuristic around if we are black
        if (isWhite) {
            return h;
        } else {
            return -h;
        }
    }
}
