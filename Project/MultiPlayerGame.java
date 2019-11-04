package projects.connect4;

import java.util.Scanner;

class MultiPlayerGame extends Game {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;

    private static final int PLAYER_1 = 1;
    private static final int PLAYER_2 = 2;
    private static final int EMPTY = 0;

    private static final String DEFAULT_P1 = "#";
    private static final String DEFAULT_P2 = "X";
    private String p1Symbol;
    private String p2Symbol;
    private boolean finished = false;
    private int winner;


    private int currentPlayer = PLAYER_1;
    private int[][] board = new int[ROWS][COLUMNS];

    private Scanner scan = new Scanner(System.in);

    MultiPlayerGame() {

        chooseSymbol();

        printBoard();
        System.out.println("Player " + currentPlayer + "s turn:");

        do {
            String in = scan.nextLine();

            switch (in) {
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                    handleTileCommand(Integer.valueOf(in));
                    break;
                case "exit":
                    finished = true;
                    System.out.println("Current game quit, going back to main menu.");
                    break;
                default:
                    System.out.println("What");
            }

        } while (!finished);

    }

    private void handleTileCommand(int col) {

        if (!placeTile(col)) {
            System.out.println("Not valid! Try again");
            printBoard();
            System.out.println("Player " + currentPlayer + "s turn");
            return;
        }

        printBoard();

        if (doWeHaveAWinner()) {
            finished = true;
            System.out.println(winnerText());
            return;
        }

        if (checkForTie()) {
            System.out.println("There are no more valid moves! And nobody won, what a shame....\n");
            finished = true;
            return;
        }

        System.out.println("Player " + currentPlayer + "s turn:");
    }

    private boolean placeTile(int col) {
        int x = col - 1;
        boolean valid = true;

        for (int y = ROWS - 1; y >= 0; y--) {
            if (board[y][x] != EMPTY) {
                valid = false;
            } else {
                board[y][x] = currentPlayer;
                valid = true;
                break;
            }
        }

        if (!valid)
            return false;

        currentPlayer = (currentPlayer == PLAYER_1) ? PLAYER_2 : PLAYER_1;
        return true;
    }

    private boolean checkForTie() {
        boolean tie = true;
        for (int y = 0; y < ROWS; y++)
            for (int x = 0; x < COLUMNS; x++)
                if (board[y][x] == 0)
                    tie = false;

        return tie;
    }

    /* Evaluate board, set finish to what ever is evaluated and
     * set winner to currentPlayer (if finished is set to true)
     */
    private boolean doWeHaveAWinner() {
        //Evaluate 4 things
        //Horizontal
        //Vertical
        //Diagonal bottom up
        //Diagonal top down

        //Checks horizontal
        for (int y = 0; y < ROWS; y++)
            for (int x = 0; x < COLUMNS; x++)
                if (piece(y, x) != 0 && piece(y, x) == piece(y, x + 1) &&
                        piece(y, x) == piece(y, x + 2) && piece(y, x) == piece(y, x + 3)) {
                    //The piece being evaluated == the winner
                    winner = piece(y, x);
                    return true;
                }

        //Checks vertical
        for (int y = 0; y < ROWS; y++)
            for (int x = 0; x < COLUMNS; x++)
                if (piece(y, x) != 0 && piece(y, x) == piece(y + 1, x) &&
                        piece(y, x) == piece(y + 2, x) && piece(y, x) == piece(y + 3, x)) {

                    winner = piece(y, x);
                    return true;
                }

        //Checks diagonal bottom up
        for (int y = 0; y < ROWS; y++)
            for (int x = 0; x < COLUMNS; x++)
                if (piece(y, x) != 0 && piece(y, x) == piece(y + 1, x - 1) &&
                        piece(y, x) == piece(y + 2, x - 2) && piece(y, x) == piece(y + 3, x - 3)) {

                    winner = piece(y, x);
                    return true;
                }

        //Checks diagonal top down
        for (int y = 0; y < ROWS; y++)
            for (int x = 0; x < COLUMNS; x++)
                if (piece(y, x) != 0 && piece(y, x) == piece(y + 1, x + 1) &&
                        piece(y, x) == piece(y + 2, x + 2) && piece(y, x) == piece(y + 3, x + 3)) {

                    winner = piece(y, x);
                    return true;
                }

        return false;
    }

    /* Helps with piece evaluation. With this method we don't have to worry about
     * going out of bounds in the evaluations in doWeHaveAWinner()
     */
    private int piece(int y, int x) {
        return (x >= COLUMNS || y >= ROWS || x < 0 || y < 0) ? 0 : board[y][x];
    }

    private void chooseSymbol() {
        String choice = "Player %s, choose your symbol and press enter.\n" +
                "For default symbol just press enter.\n";

        System.out.printf(choice, PLAYER_1);
        p1Symbol = scan.nextLine();
        p1Symbol = p1Symbol.length() < 1 ? DEFAULT_P1 : p1Symbol.substring(0, 1);

        System.out.printf(choice, PLAYER_2);
        p2Symbol = scan.nextLine();
        p2Symbol = p2Symbol.length() < 1 ? DEFAULT_P2 : p2Symbol.substring(0, 1);

        System.out.printf("Your symbols are \"%s\" for Player 1 and \"%s\" for player 2.\n", p1Symbol, p2Symbol);
    }

    private String winnerText() {
        return "##################################\n" +
                "#                                #\n" +
                "#   And the winner is Player " + winner + "!  #\n" +
                "#                                #\n" +
                "##################################";


    }

    private void printBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                int tile = board[i][j];
                String toPrint = "0";

                if (tile != EMPTY)
                    toPrint = (tile == PLAYER_1) ? p1Symbol : p2Symbol;

                System.out.print(toPrint + " ");
            }
            System.out.println();
        }
        System.out.println("=============\n1 2 3 4 5 6 7");
    }
}
