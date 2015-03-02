/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group06;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 *
 * @author johanmaas
 */
public class MinMaxPlayer extends DraughtsPlayer {

    /**
     * If we are white then we start by max, otherwise we start tree with min.
     *
     * @param depth
     * @param s
     * @param isWhite
     */
    public Move MiniMax(int depth, DraughtsState s, boolean isWhite) {
        Entry<Integer, Move> firstmove = null;
        for(int d = 0; d < depth; d++) {
            if(!isWhite){
                firstmove = (d == 0) ? Max(s) : firstmove;
                if(Min(s).getValue() == null) {
                    // Break out of the loop if no possible moves any more (game end in sight)
                    System.out.println("2ID90-GROUP-06: Victory is in sight!");
                    break;
                } 
            } else {
                firstmove = (d == 0) ? Min(s) : firstmove;
                if(Max(s).getValue() == null) {
                    System.out.println("2ID90-GROUP-06: Victory is in sight!");
                    break;
                }
            }
            System.out.println("minmax d="+d);
        }
        s.reset();
        
        if(firstmove == null) {
            System.err.println("2ID90-GROUP-06: AAAHH we are losing!! Try something random :P");
            List<Move> moves = s.getMoves();
            Random r = new Random();
            try {
                Move randomMove = moves.get(r.ints(0, moves.size()).iterator().next());
                return randomMove;
            } catch(Exception e) {
                // we fail terribly if we end up here...
                return moves.get(0);
            }
        }
        // choose the one with highest evaluation function value
        return firstmove.getValue();
    }
    
    public Entry<Integer, Move> Max(DraughtsState s) {
        Move maxmove = null;
        int heuristicmax = -100;
        for(Move m : s.getMoves()) {
            int hval = heuristicVal(m, 0);
            if(hval > heuristicmax) {
                heuristicmax = hval;
                maxmove = m;
            }
        }
        if(maxmove == null)  {
            return new MoveEntry(0, null);
        }
        s.doMove(maxmove);
        return new MoveEntry(heuristicVal(maxmove, 0), maxmove);
    }
    
    public Entry<Integer, Move> Min(DraughtsState s) {
        Move minmove = null;
        int heuristicmin = 100;
        for(Move m : s.getMoves()) {
            int hval = heuristicVal(m, 0);
            if(hval < heuristicmin) {
                heuristicmin = hval;
                minmove = m;
            }
        }
        if(minmove == null)  {
            return new MoveEntry(0, null);
        }
        s.doMove(minmove);
        return new MoveEntry(heuristicVal(minmove, 0), minmove);
    }
    
    public int heuristicVal(Move move, int depth) {
        return -depth + move.getCaptureCount() * 5 + (move.isPromotion() ? 30 : 0);
    }

    @Override
    public Move getMove(DraughtsState s) {
        boolean isWhite = s.isWhiteToMove();
        Move bestmove = MiniMax(10, s, isWhite);
        return bestmove;
    }

    @Override
    public void stop() {

    }

    @Override
    public Integer getValue() {
        return super.getValue(); //To change body of generated methods, choose Tools | Templates.
    }

    class MoveEntry implements Entry<Integer, Move> {

        Integer key;
        Move value;

        public MoveEntry(Integer key, Move value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public Move getValue() {
            return value;
        }

        @Override
        public Move setValue(Move value) {
            this.value = value;
            return value;
        }

    }

}
