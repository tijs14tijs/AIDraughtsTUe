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
        //checks if a piece is on the left side of the board and multiplies the weight if this is the case.
        return (c % 10 == 6)
                ? 1.5 : 1;
                        
    }
    
    public double SQ_RIGHT(int c) {
        //checks if a piece is on the right side of the board and multiplies the weight if this is the case.
        return (c % 10 == 5)
                ? 1.5 : 1;
                        
    }
    
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
    
   // With c, find rightbelow d, then leftbelow e, then leftabove f. 
   //  If (c, f, d, e) are our color, then we have a square. And then we return 1, otherwise 0. (maybe sum this in our loop?)
   //    c
   //   / \
   //  f   d
   //   \ /
   //    e
   //
   public double is_square(int c, int pieces[]) {
       int d = SQ_RIGHT_TOP(c);
       int e = SQ_LEFT_TOP(d);
       int f = SQ_RIGHT_BOTTOM(e);
       // c = SQ_LEFT_TOP(f)
       
       // convert all kings to draughts (this makes the final checking easier
       // WHITEDRAUGHT = 1, BLACKDRAUGHT = 2, WHITEKING = 3, BLACKING = 4
       if(pieces[c] > 2) pieces[c] -= 2;
       if(pieces[d] > 2) pieces[d] -= 2;
       if(pieces[e] > 2) pieces[e] -= 2;
       if(pieces[f] > 2) pieces[f] -= 2;
       
       // So if they are all white they are all 1, if they are all black they are all 2. Easy checking:
       if((pieces[c] == pieces[d]) && (pieces[e] == pieces[d]) && (pieces[f] == pieces[d])) {
           int our_color = (isWhite) ? WHITEPIECE : BLACKPIECE;
           
           if(pieces[c] == our_color) {
               return 1;
           }else{
               return 0;
           }
       }
       return 0;
   }
    
    
    
    
    // Determine the heuristic value for a certain state s
    public double calculateVal(DraughtsState s, int depth) {
        int[] pieces = s.getPieces();        
        
        // TIJS: Our heuristic is symmetric (our pieces are worth just as much as their pieces) so you do not need
        // a large if isWhite here. Just do it at the bottom.
        
        // This is where the actual heuristic is calculated. because the situation is slightly different for the white and black
        // player their heuristic is calculated seperately.
        double h = 0;
        for (int i = 1; i < pieces.length; i++) {
            int piece = pieces[i];
            
            
             
            
            
            //for the kings it is not checked whether they are on the sides of the board or not since they have more movement freedom
            //and their is no real benifit for them to be at the sides.
            h += SQ_LEFT(i) * SQ_RIGHT(i) * ((piece == WHITEPIECE) ? 1 : 0) - SQ_LEFT(i) * SQ_RIGHT(i) * ((piece == BLACKPIECE) ? 1 : 0) + 
                    ((piece == WHITEKING) ? 8 : 0) - ((piece == BLACKKING) ? 8 : 0) + 2 * is_square(i, pieces);
            
        }
        
        // TIJS: Does not work: we check non zero constants with 0 :S
        // You want to check this in the loop with a for all constraint. (simply count the black pieces in the loop, use that here)
        //if(BLACKPIECE == 0 & BLACKKING == 0){
          //      h = h + 100;
            //}

        
        if (isWhite) {
            return h;
        } else
            
            return -h;
        }
    }
