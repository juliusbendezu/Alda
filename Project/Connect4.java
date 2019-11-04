package projects.connect4;

import java.util.Scanner;

public class Connect4 {
    private String mainMenu;

    Connect4() {
        Scanner scan = new Scanner(System.in);
        boolean exit = false;
        mainMenu = createMainMenu();

        System.out.println(mainMenu);
        do {
            System.out.println("What do you want to do?");
            String command = scan.nextLine();
            command = command.toLowerCase();
            switch (command) {
                case "1":
                    //doSomething();
                    startGame(new SinglePlayerGame());
                    break;
                case "2":
                    startGame(new MultiPlayerGame());
                    break;
                case "e":
                    exit = true;
                    System.out.println("Connect4 closed. See you soon!");
                    break;
                default:
                    System.out.println("Not a valid command, try 1, 2 or e!");
            }


        } while (!exit);
    }

    private void startGame(Game game) {
        //After game has concluded, returned to main menu.
        System.out.println(mainMenu);
    }

    private String createMainMenu() {

        String body =
                "#       Welcome to this game of connect 4!      #\n" +
                        "#                                                #\n" +
                        "#       Write your command and press enter.      #\n" +
                        "#        For a singleplayer game press 1.        #\n" +
                        "#         For a multiplayer game press 2.        #\n" +
                        "#                To exit press e.                #";

        StringBuilder mainMenu = new StringBuilder();

        //Creates and appends top padding
        for (int i = 0; i < 50; i++)
            mainMenu.append("#");
        mainMenu.append("\n#");
        for (int i = 0; i < 48; i++)
            mainMenu.append(" ");
        mainMenu.append("#\n");

        //Appends the menu in between the top and bottom padding
        mainMenu.append(body);

        //Creates and appends bottom padding
        mainMenu.append("\n#");
        for (int i = 0; i < 48; i++)
            mainMenu.append(" ");
        mainMenu.append("#\n");
        for (int i = 0; i < 50; i++)
            mainMenu.append("#");


        return mainMenu.toString();
    }

    public static void main(String[] args) {
        new Connect4();
    }
}
