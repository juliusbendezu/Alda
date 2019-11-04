package projects.connect4;

import java.util.ArrayList;

class Board {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final int EMPTY = 0;

    static final int PLAYER_1 = 1;
    static final int PLAYER_2 = 2;

    static final int COLUMN_IS_FULL = -1;

    private int[][] board;

    Board() {
        board = new int[ROWS][COLUMNS];
    }

    void addTile(int row, int col, int value) {
        board[row][col] = value;
    }

    int getNextRow(int col) {
        for (int row = ROWS - 1; row >= 0; row--)
            if (board[row][col] == 0)
                return row;

        return COLUMN_IS_FULL;
    }

    ArrayList<Integer> getValidMoves() {
        ArrayList<Integer> availableColumns = new ArrayList<>();
        for (int x = 0; x < COLUMNS; x++)
            if (board[0][x] == EMPTY)
                availableColumns.add(x);

        return availableColumns;
    }

    Board getCopy() {
        Board copy = new Board();
        copy.board = new int[ROWS][COLUMNS];

        for (int y = 0; y < ROWS; y++)
            for (int x = 0; x < COLUMNS; x++)
                copy.board[y][x] = board[y][x];

        return copy;
    }

    boolean isTerminal() {
        return isFull() || isWonFor(PLAYER_1) || isWonFor(PLAYER_2);
    }

    boolean isFull() {
        for (int y = 0; y < ROWS; y++)
            for (int x = 0; x < COLUMNS; x++)
                if (board[y][x] == 0)
                    return false;

        return true;
    }

    boolean isWonFor(int player) {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLUMNS; c++)
                if (winningPosition(r, c, player))
                    return true;

        return false;
    }


    /**
     * @param piece The value of the piece. Should be 1 or 2 representing either
     *              player 1 or player 2, throws IllegalArgumentException otherwise
     * @return The total score the given move gives at the given position
     */
    int evaluateBoard(int piece) {
        if (!(piece == PLAYER_1 || piece == PLAYER_2))
            throw new IllegalArgumentException();

        int score = 0;

        //OM OPPONENT HAR 3 I RAD OCH EN TOM PLATS, SÅ ÄR ALLT DÅLIGT UTOM WIN ELLER DEN BLOCKERANDE PLATSEN
        //GE -100 OM OPPONENT HAR 3 I RAD OCH 1 LEDIG
        //I CONNECT 3, GE POÄNG OM 3 ÄR CONNECTED OCH MAN HAR MINST EN TOM SOM KAN GE POÄNG NÄSTA RUNDA

        //EN LOOP GENOM ALLT OCH BOARD() SER TILL ATT VI EJ GÅR OFF BOUNDS

        //Funkar för att 2 olika 2/3 i rad kollas nästan aldrig samtidigt utan tex om ett drag skapar 3 i rad
        //både diagonalt och horizontellt kommer dessa kollas av i olika ställen i loopen och ge poäng för båda
        //Kan finnas fall där dubbelpoäng missas (Måste testas mer)

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (winningPosition(r, c, piece))
                    score += 1000;
                if (opponentWin(r, c, piece))
                    score -= 1000;
                if (connect3(r, c, piece))
                    score += 50;
                if (connect2(r, c, piece))
                    score += 10;
                if (centerPosition(r, c, piece))
                    score += 2;
            }
        }

        return score;
    }

    private boolean centerPosition(int r, int c, int piece) {
        if (c != COLUMNS / 2)
            return false;
        return piece == board(r, c);
    }

    private boolean winningPosition(int r, int c, int piece) {
        boolean horizontal = piece == board(r, c) && piece == board(r, c + 1) &&
                piece == board(r, c + 2) && piece == board(r, c + 3);

        boolean vertical = piece == board(r, c) && piece == board(r - 1, c) &&
                piece == board(r - 2, c) && piece == board(r - 3, c);

        boolean diagonalBottomUp = piece == board(r, c) && piece == board(r - 1, c + 1) &&
                piece == board(r - 2, c + 2) && piece == board(r - 3, c + 3);

        boolean diagonalTopDown = piece == board(r, c) && piece == board(r + 1, c + 1) &&
                piece == board(r + 2, c + 2) && piece == board(r + 3, c + 3);

        return horizontal || vertical || diagonalBottomUp || diagonalTopDown;
    }

    private boolean opponentWin(int r, int c, int piece) {
        int opponentPiece = piece == PLAYER_1 ? PLAYER_2 : PLAYER_1;

        //XXX0
        boolean pos4 = opponentPiece == board(r, c) && opponentPiece == board(r, c + 1) &&
                opponentPiece == board(r, c + 2) && board(r, c + 3) == EMPTY &&
                moveIsPlayable(r, c + 3);
        //XX0X
        boolean pos3 = opponentPiece == board(r, c) && opponentPiece == board(r, c + 1) &&
                opponentPiece == board(r, c + 3) && board(r, c + 2) == EMPTY &&
                moveIsPlayable(r, c + 2);
        //X0XX
        boolean pos2 = opponentPiece == board(r, c) && opponentPiece == board(r, c + 2) &&
                opponentPiece == board(r, c + 3) && board(r, c + 1) == EMPTY &&
                moveIsPlayable(r, c + 1);
        //0XXX
        boolean pos1 = opponentPiece == board(r, c + 1) && opponentPiece == board(r, c + 2) &&
                opponentPiece == board(r, c + 3) && board(r, c) == EMPTY && moveIsPlayable(r, c);

        // System.out.println("XXX0 is present on the board: " + pos4 + "XX0X is present on the board: " +
        //       pos3 + "X0XX is present on the board: " + pos2 + "0XXX is present on the board: " + pos1);

        boolean vertical = opponentPiece == board(r, c) && opponentPiece == board(r - 1, c) && opponentPiece == board(r - 2, c) &&
                board(r - 3, c) == EMPTY && moveIsPlayable(r - 3, c);

        return pos4 || pos3 || pos2 || pos1 || vertical;
    }

    private boolean connect3(int r, int c, int piece) {
        boolean horizontal = piece == board(r, c) && piece == board(r, c + 1) && piece == board(r, c + 2) &&
                (board(r, c - 1) == EMPTY || board(r, c + 4) == EMPTY) &&
                (moveIsPlayable(r, c - 1) || moveIsPlayable(r, c + 3));
        //System.out.printf("This is r: %s c: %s, and this is board(r, c-1): %s and board(r, c + 4): %s\n", r, c, board(r, c - 1), board(r, c + 4));
        boolean vertical = piece == board(r, c) && piece == board(r - 1, c) &&
                piece == board(r - 2, c) && board(r - 3, c) == EMPTY;

        boolean diagonalBottomUp = piece == board(r, c) && piece == board(r - 1, c + 1) && piece == board(r - 2, c + 2) &&
                (board(r + 1, c - 1) == EMPTY || board(r - 3, c + 3) == EMPTY) &&
                (moveIsPlayable(r + 1, c - 1) || moveIsPlayable(r - 3, c + 3));

        boolean diagonalTopDown = piece == board(r, c) && piece == board(r + 1, c + 1) && piece == board(r + 2, c + 2) &&
                (board(r - 1, c - 1) == EMPTY || board(r + 3, c + 3) == EMPTY) &&
                (moveIsPlayable(r - 1, c - 1) || moveIsPlayable(r + 3, c + 3));

        /*
        if (horizontal)
            System.out.println("Horizontal 3 in a row");
        if (vertical)
            System.out.println("Vertical 3 in a row");
        if (diagonalBottomUp)
            System.out.println("DiaBottomUp 3 in a row");
        if (diagonalTopDown)
            System.out.println("DiaTopDown 3 in a row");
        */
        return horizontal || vertical || diagonalBottomUp || diagonalTopDown;
    }

    //Kan ev förbättras med avseende på moveIsPlayable
    private boolean connect2(int r, int c, int piece) {
        boolean horizontal = piece == board(r, c) && piece == board(r, c + 1) &&
                ((board(r, c - 1) == EMPTY && board(r, c - 2) == EMPTY) || // 2 to the left
                        (board(r, c - 1) == EMPTY && board(r, c + 2) == EMPTY) || // 1 on either side
                        (board(r, c + 2) == EMPTY && board(r, c + 3) == EMPTY)) && // 2 to the right
                (moveIsPlayable(r, c - 1) || moveIsPlayable(r, c + 2)); //At least 1 of the adjacent are playable

        boolean vertical = piece == board(r, c) && piece == board(r - 1, c) &&
                board(r - 2, c) == EMPTY && board(r - 3, c) == EMPTY;

        boolean diagonalBottomUp = piece == board(r, c) && piece == board(r - 1, c + 1) &&
                ((board(r + 1, c - 1) == EMPTY && board(r + 2, c - 2) == EMPTY) || // 2 to the left
                        (board(r + 1, c - 1) == EMPTY && board(r - 2, c + 2) == EMPTY) || // 1 on either side
                        (board(r - 2, c + 2) == EMPTY) && board(r - 3, c + 3) == EMPTY) &&  // 2 to the right
                (moveIsPlayable(r + 1, c - 1) || moveIsPlayable(r - 2, c + 2));

        boolean diagonalTopDown = piece == board(r, c) && piece == board(r + 1, c + 1) &&
                ((board(r - 1, c - 1) == EMPTY && board(r - 2, c - 2) == EMPTY) || // 2 to the left
                        (board(r - 1, c - 1) == EMPTY && board(r + 2, c + 2) == EMPTY) || // 1 on either side
                        (board(r + 2, c + 2) == EMPTY) && board(r + 3, c + 3) == EMPTY) &&  // 2 to the right
                (moveIsPlayable(r - 1, c - 1) || moveIsPlayable(r + 2, c + 2));

        return horizontal || vertical || diagonalBottomUp || diagonalTopDown;
    }

    /**
     * This function is used when there is a possibility that the board at given indices is out
     * of bounds. For example when evaluating a 3 in a row and checking if there is space on either
     * side of the 3 connected tiles.
     *
     * @param r The given row
     * @param c The given column
     * @return Returns the piece on that given coordinate, 0 for EMPTY, 1 for PLAYER_1 and 2 for PLAYER_2,
     * Returns -1 if the board is out of bounds on the given row/column
     */
    private int board(int r, int c) {
        return (r >= ROWS || c >= COLUMNS || r < 0 || c < 0) ? -1 : board[r][c];
    }

    private boolean moveIsPlayable(int r, int c) {
        if (c < 0 || c > 6)
            return false;
        return r == getNextRow(c);
    }

    void printBoard(String player1, String player2) {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                int tile = board[y][x];
                String toPrint = "0";

                if (tile != EMPTY)
                    toPrint = (tile == PLAYER_1) ? player1 : player2;

                System.out.print(toPrint + " ");
            }
            System.out.println();
        }
        System.out.println("=============\n1 2 3 4 5 6 7");
    }
}
