package projects.connect4;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SinglePlayerGame extends Game {

    private static final int PLAYER = Board.PLAYER_1;
    private static final int AI = Board.PLAYER_2;

    private static final String DEFAULT_PLAYER_SYMBOL = "#";
    private static final String DEFAULT_AI_SYMBOL = "X";

    private String playerSymbol;
    private String aiSymbol;
    private boolean finished = false;
    private int winner;
    private int currentPlayer;
    private Board board;

    private AI ai1;
    private AI ai2;

    private Scanner scan = new Scanner(System.in);
    private Random rand = new Random();

    SinglePlayerGame() {
        board = new Board();

        int depthForMinMAx = chooseDifficulty();

        ai1 = new AI(AI, PLAYER, depthForMinMAx);
        ai2 = new AI(PLAYER, AI, depthForMinMAx);

        chooseSymbols();

        currentPlayer = rand.nextBoolean() ? PLAYER : AI;

        do {
            if (currentPlayer == PLAYER)
                //playerTurn();
                aiTurn(ai2);
            else //AI's turn
                aiTurn(ai1);

            handleGameStatus(checkGameStatus());
            currentPlayer = (currentPlayer == PLAYER) ? AI : PLAYER;

        } while (!finished);

    }

    private GameStatus checkGameStatus() {
        if (!board.isTerminal())
            return GameStatus.NOT_CONCLUDED;

        if (gameIsTied())
            return GameStatus.TIE;

        winner = board.isWonFor(PLAYER) ? PLAYER : AI;
        return GameStatus.WON;
    }

    /**
     * Handles appropriate action for a concluded game.
     * Does nothing if the game is still alive.
     *
     * @param status The status of the game, WON, TIE or NOT_CONCLUDED
     */
    private void handleGameStatus(GameStatus status) {
        if (status == GameStatus.NOT_CONCLUDED)
            return;

        //To show the concluding move
        printBoard();

        //Can be either win or tie;
        String conclusion = (status == GameStatus.WON) ? winnerText() : tieText();
        System.out.println(conclusion);
        finished = true;
    }

    private void aiTurn(AI ai) {
        printBoard();
        System.out.println("Computer is thinking...");

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //int start = (int) System.currentTimeMillis(); //For testing running time

        int col = ai.findBestMoveMinMax(board);

        //System.out.println("Time took for minmax algorithm to find a move with optimization of alpha/beta-pruning at depth " +
          //      ai.depth + ": " + ((double) ((int) System.currentTimeMillis() - start) / 100) + " seconds.");

        int row = getNextRow(col);
        placeTile(row, col);
    }

    private void playerTurn() {
        boolean valid = false;

        do {
            printBoard();
            System.out.println("Your turn:");
            String in = scan.nextLine();
            switch (in) {
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                    int col = Integer.parseInt(in) - 1;
                    int row = getNextRow(col);

                    if (row == Board.COLUMN_IS_FULL)
                        System.out.println("That column is full, try another one!");
                    else {
                        placeTile(row, col);
                        valid = true;
                    }
                    break;
                case "exit":
                    System.out.println("Game over! You quit the game!");
                    valid = true;
                    finished = true;
                    break;
                default:
                    System.out.println("Input the column you want to place your tile and press enter, " +
                            "or exit to quit the current game.");
            }

        } while (!valid);
    }

    private void placeTile(int row, int col) {
        board.addTile(row, col, currentPlayer);
    }

    private int getNextRow(int col) {
        return board.getNextRow(col);
    }

    private boolean gameIsTied() {
        return board.isFull();
    }

    private int chooseDifficulty() {

        int depth = 0;

        boolean valid = true;
        System.out.println("Choose difficulty:\n" +
                "Laughable: 1\n" +
                "Easy: 2\n" +
                "Modest: 3\n" +
                "Hard: 4\n" +
                "Brutal: 5");
        do {

            System.out.println("Choose wisely... ");
            String in = scan.nextLine();

            switch (in) {
                case "1":
                    depth = 0; //Den suger, kan inte titta framåt ens på sitt eget drag så väljer bara random
                    break;
                case "2":
                    depth = 1;
                    break;
                case "3":
                    depth = 2;
                    break;
                case "4":
                    depth = 4;
                    break;
                case "5":
                    depth = 8;
                    break;
                default:
                    System.out.println("No backing down, choose a difficulty! (1, 2, 3, 4 or 5)");
                    valid = false;
                    break;
            }
        } while (!valid);

        return depth;
    }

    private void chooseSymbols() {
        String playerChoice = "Player 1, choose your symbol and press enter.\n" +
                "For default symbol just press enter.";
        String aiChoice = "Choose a symbol for the computer? Press enter for default.";

        System.out.println(playerChoice);
        playerSymbol = scan.nextLine();
        playerSymbol = playerSymbol.length() < 1 ? DEFAULT_PLAYER_SYMBOL : playerSymbol.substring(0, 1);

        System.out.println(aiChoice);
        aiSymbol = scan.nextLine();
        aiSymbol = aiSymbol.length() < 1 ? DEFAULT_AI_SYMBOL : aiSymbol.substring(0, 1);

        System.out.printf("Your symbols are \"%s\" for Player 1 and \"%s\" for the computer.\n", playerSymbol, aiSymbol);
    }

    private String winnerText() {
        //Winner texts must be 28 characters.
        String playerWinner = "Congratulations! You've won!";
        String aiWinner = "Game Over! The computer won.";
        return String.format("##################################\n" +
                "#                                #\n" +
                "#  %s  #\n" +
                "#                                #\n" +
                "##################################", (winner == PLAYER) ? playerWinner : aiWinner);

    }

    private String tieText() {
        return "##################################\n" +
                "#                                #\n" +
                "#     A tie! What a shame...     #\n" +
                "#                                #\n" +
                "##################################";
    }

    private void printBoard() {
        board.printBoard(playerSymbol, aiSymbol);
    }

    public static void main(String[] args) {
        //Used to more quickly test out a single player game.
        new SinglePlayerGame();
    }
}

