package projects.connect4;

import java.util.*;

class AI {

    private static final int ALPHA = Integer.MIN_VALUE;
    private static final int BETA = Integer.MAX_VALUE;

    private final int AI;
    private final int OPPONENT;

    int depth;
    int count; //For testing purposes only

    private Random rand = new Random();

    AI(int value, int opponentValue, int depthForMinMax) {
        AI = value;
        OPPONENT = opponentValue;
        depth = depthForMinMax;
    }

    /**
     * Takes in a board with the current game state, sorts the available moves in order
     * of highest score that they can produce and returns a sorted collection.
     *
     * @param board The board with the current game state
     * @return A collection of columns sorted in score for the move, highest first.
     */
    private Collection<Integer> getBestMoves(Board board) {
        PriorityQueue<ScorePlacementPair> heap = new PriorityQueue<>();

        for (int col : board.getValidMoves()) {
            Board copy = board.getCopy();
            int row = copy.getNextRow(col);
            copy.addTile(row, col, AI);
            int score = copy.evaluateBoard(AI);
            heap.add(new ScorePlacementPair(score, col));
        }

        List<Integer> moves = new ArrayList<>();
        for (ScorePlacementPair scp : heap)
            moves.add(scp.column);

        return moves;
    }

    /**
     * Public driver routine for the minmax algorithm. Simply takes in a board
     * from which to start the search and returns the column that gives the best position.
     *
     * @param board The board with the current game state.
     * @return The best column to play on.
     */
    int findBestMoveMinMax(Board board) {
        count = 0;
        ScorePlacementPair scp = minMax(board, depth, true, ALPHA, BETA);
        int col = scp.column;
        System.out.println("Minmax did " + count + " laps.");

        //Only when depth == 0
        //Does not really make sense but an easy way to create a super easy game mode.
        while (!board.getValidMoves().contains(col))
            col = rand.nextInt(7);

        return col;
    }

    /**
     * MinMax algorithm is a depth-first algorithm used to minimize the possible loss and maximize
     * the possible gain of a move. It explores all possible moves n depth down and finds the best moves
     * playable for both it self and the opponent to maximize its own minimum guaranteed gain.
     *
     * This implementation uses Alpha-Beta pruning to cut off branches and be able to make a deeper search.
     * Alpha and Beta are two values that are initially set to negative and positive infinity respectively.
     * Alpha initially represents the worst possible score for the maximizing player and Beta initially represents the
     * worst possible score for the minimizing player. In the algorithm loop they are updated to represent the best
     * already found score for the maximizing and minimizing player respectively. They are then compared between
     * each other and used to prune branches of the search tree to make a more rapid search.
     *
     * This implementation does not work for depth 0. It needs at least one run in the else-clause to
     * simulate a move and find a valid position to move.
     *
     * Minmax algorithm uses heuristics to evaluate a position that is not terminal (won/lost/tied).
     * This implementation is dependent of the evaluateBoard method in class Board.
     * The specified method holds the heuristic values for a specific board state.
     *
     * @param board      The board being evaluated.
     * @param depth      The depth of the simulated tree, must be greater than 0. At depth 9 or greater
     *                   moves take a lot longer to compute.
     * @param maximizing The player who is maximizing, in this case it's the AI. Maximizing refers to the player
     *                   who is looking to maximize his score, as opposed to minimize the opponents score.
     *                   This value alternates from true to false in each recursive call to compute the appropriate values.
     * @param alpha      Alpha is originally -Infinity* (Minimum value for a 32-bit integer). It updates in the algorithm loop
     *                   to make pruning of branches possible.
     * @param beta       Beta is originally +Infinity* (Maximum value for a 32-bit integer). It updates in the algorithm loop
     *                   to make pruning of branches possible.
     * @return ScorePlacementPair holding both the information of the highest score achievable
     * as well as what column produces that score.
     */
    private ScorePlacementPair minMax(Board board, int depth, boolean maximizing, int alpha, int beta) {

        //For minmax with alpha-beta pruning it is most beneficial to consider the best move
        //available at a given position. To find the best move however is not very cheap in
        //the case of this program so when to simply use the available moves in ascending order
        //and when to use the best first order has been manually tested.

        Collection<Integer> validMoves;
        if (depth > 2) //2 is just an arbitrary value, empirically tested to be the best cutoff for when which ordering is most beneficial
            validMoves = getBestMoves(board); //Orders moves from highest scoring to least
        else
            validMoves = board.getValidMoves(); //Orders moves from 0 to 6 (Available columns from 1 through 7)

        count++; //For testing purposes

        if (depth == 0 || board.isTerminal()) {
            if (board.isWonFor(AI))
                return new ScorePlacementPair(10000000, -1);
            else if (board.isWonFor(OPPONENT))
                return new ScorePlacementPair(-10000000, -1);
            else if (board.isFull())
                return new ScorePlacementPair(0, -1);
            else { //Depth is 0
                int score = board.evaluateBoard(AI);
                return new ScorePlacementPair(score, -1);
            }
        } else {
            int newScore;
            int column = 0;
            int score;

            if (maximizing) {
                score = Integer.MIN_VALUE;
                for (int col : validMoves) {
                    int row = board.getNextRow(col);
                    Board copy = board.getCopy();
                    copy.addTile(row, col, AI);

                    newScore = minMax(copy, depth - 1, false, alpha, beta).score;

                    if (newScore > score) {
                        score = newScore;
                        column = col;
                    }

                    alpha = Math.max(alpha, newScore);
                    if (alpha >= beta)
                        break;

                }
                return new ScorePlacementPair(score, column);
            } else { //Minimizing
                score = Integer.MAX_VALUE;
                for (int col : validMoves) {
                    int row = board.getNextRow(col);
                    Board copy = board.getCopy();
                    copy.addTile(row, col, OPPONENT);

                    newScore = minMax(copy, depth - 1, true, alpha, beta).score;

                    if (newScore < score) {
                        score = newScore;
                        column = col;
                    }

                    beta = Math.min(beta, newScore);
                    if (beta <= alpha)
                        break;
                }
                return new ScorePlacementPair(score, column);
            }
        }

    }

    /**
     * Inner class used for the minMax algorithm. From the minMax function we want to
     * be able to both get a score for comparisons, but at the end also a column to know
     * where we can produce the move. The performing of the move is handled in an other class,
     * the minmax function only finds it.
     * <p>
     * CompareTo function is implemented to sort a Scoring position by highest score.
     */
    static class ScorePlacementPair implements Comparable<ScorePlacementPair> {
        int score;
        int column;

        ScorePlacementPair(int s, int c) {
            score = s;
            column = c;
        }

        @Override
        public int compareTo(ScorePlacementPair other) {
            return Integer.compare(other.score, score);
        }

        @Override
        public String toString() {
            return "Score: " + score + " - Column: " + column;
        }
    }
}
