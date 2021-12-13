package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class ApplicationRunner {

    public static void main(String[] args) {

        int level = openingMenu(); // assigned value from user choice of level

        ArrayList<String> words = setupReadFile(level); // assigned shuffled arraylist containing words from txt file

        int totalMatchingWords = getTotalMatching(level); // assigned total matching words 

        int boardSize = (int) Math.sqrt(words.size()); // gets board size by square rooting the number of words in arraylist

        String[][] wordBoard = setWordBoard(words, boardSize); // function returns multiarray of indexes containing the hidden words

        String[][] placeholderBoard = setTempBoard(boardSize); // function that returns the multiarray of indexes containing the placeholders

        System.out.println("");

        drawBoard(boardSize, placeholderBoard); // draws initial board containing all placeholders.

        mainLoop(boardSize, totalMatchingWords, placeholderBoard, wordBoard); // executes main block of code

    }

    public static int openingMenu() { // displays startup menu and asks for level. Based on choice, returns int levels

        Scanner input = new Scanner(System.in);

        System.out.println("----------------------------------");
        System.out.println("Welcome to the memory square game!");
        System.out.println("----------------------------------");
        System.out.println("");
        System.out.println("Easy................1");
        System.out.println("Intermediate........2");
        System.out.println("Difficult...........3");
        System.out.println("Exit................0");
        System.out.println("");
        System.out.print("Enter your preferred level: ");

        int choice = input.nextInt();

        int levels = 0;

        switch (choice) {
            case 1:
                levels = 1;
                break;
            case 2:
                levels = 2;
                break;
            case 3:
                levels = 3;
                break;
            case 0:
                System.out.println("Exiting program...");
                System.exit(0);
            default:
                System.out.println("Out of range. Please restart the program.");
                System.exit(0);
                break;
        }
        return levels;
    }

    // function to get number of matching words. Without it, program will not be able to terminate when game over. Total set to number of words in each txt file
    public static int getTotalMatching(int n) {

        int total = 0;

        switch (n) {
            case 1:
                total = 8;
                break;
            case 2:
                total = 18;
                break;
            default:
                total = 32;
                break;
        }

        return total;
    }

    // function to take level choice, choose specific txt file and add it to words arraylist. This is then shuffled randomly with the collections class
    public static ArrayList setupReadFile(int lvl) {

        String textFile;

        switch (lvl) {
            case 1:
                textFile = "small.txt";
                break;
            case 2:
                textFile = "medium.txt";
                break;
            default:
                textFile = "large.txt";
                break;
        }

        String dataFile = System.getProperty("user.dir") + File.separator + textFile;

        File file = new File(dataFile);

        Scanner readFile = null;

        ArrayList<String> words = null;

        try {
            readFile = new Scanner(file);

            words = new ArrayList<String>();

            while (readFile.hasNext()) {
                String word = readFile.nextLine();
                String formattedWord = String.format("%-12s", word); // formats word so that 12 whitespaces are added to end of word
                words.add("[" + formattedWord + "]");
                words.add("[" + formattedWord + "]"); // added two instances of each word so that they are in pairs
            }
            Collections.shuffle(words); // randomises word order in arraylist

        } catch (FileNotFoundException fnf) {
            System.out.println("Can't find file");
        }
        return words;
    }

    // passed in words arraylist and sets each word to an index of wordBoard array
    public static String[][] setWordBoard(ArrayList words, int size) {

        String[][] wordBoard = new String[size][size];

        int position = 0; // seperate incrementer that gets each word and assigned to wordBoard in the nested for loop

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                wordBoard[i][j] = (String) words.get(position);
                position++;
            }
        }
        return wordBoard;
    }

    // passed in size of board (based on level choice) and sets placeholderBoard values to "[XXXXXXXXXXXX]"
    public static String[][] setTempBoard(int size) {

        String[][] tempBoard = new String[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tempBoard[i][j] = "[XXXXXXXXXXXX]";
            }
        }
        return tempBoard;
    }

    public static void mainLoop(int boardSize, int totalMW, String[][] placeholderBoard, String[][] wordBoard) {

        Scanner input = new Scanner(System.in);

        boolean playing = true;
        int guesses = 0;
        int matchingWords = 0;

        while (playing) {

            boolean inputValid = true;

            while (inputValid) {

                System.out.print("Enter 1st row: ");
                int r1 = input.nextInt();
                System.out.print("Enter 1st col: ");
                int c1 = input.nextInt();

                if ((c1 >= 0 && c1 < boardSize) && (r1 >= 0 && r1 < boardSize)) { // check if user input is out of bounds. if false, display error message and ask for input again

                    String firstWord = takeInput(r1, c1, wordBoard); // gets word from index r1, c2 in wordBoard and assigned to string firstWord

                    if (placeholderBoard[r1][c1].equals("[XXXXXXXXXXXX]")) { // if not revealed already, draw board with word. Else draw previous with placeholder and reset current turn
                        newBoard(r1, c1, boardSize, placeholderBoard, wordBoard);
                    } else {
                        System.out.println("Already entered. Try again \n");
                        previousBoard(r1, c1, boardSize, placeholderBoard, wordBoard);
                        break;
                    }

                    System.out.print("Enter 2nd row: ");
                    int r2 = input.nextInt();
                    System.out.print("Enter 2nd col: ");
                    int c2 = input.nextInt();

                    if ((r2 >= 0 && r2 < boardSize) && (c2 >= 0 && c2 < boardSize)) { // check if user input is out of bounds. if false, display error message and ask for input again

                        String secondWord = takeInput(r2, c2, wordBoard);

                        if (placeholderBoard[r2][c2].equals("[XXXXXXXXXXXX]")) { // draws board if true, else set first and second input index pairs, to placeholder and draw it
                            newBoard(r2, c2, boardSize, placeholderBoard, wordBoard);
                        } else {
                            System.out.println("Already entered. Restarting current turn \n");
                            placeholderBoard[r1][c1] = "[XXXXXXXXXXXX]";
                            previousBoard(r2, c2, boardSize, placeholderBoard, wordBoard);
                            break;
                        }

                        if (inputValid) { // if both inputs are valid i.e user did not choose an already revealed word, then increment guesses and check if matching.

                            guesses++;

                            if (firstWord.equals(secondWord)) { // if matching, draw new board with both inputs permanently revealed and incrememnt matchingWords.

                                newBoard(r2, c2, boardSize, placeholderBoard, wordBoard);

                                matchingWords++;

                                System.out.println("Match!");
                                System.out.println("Number of guesses: " + guesses);

                                if (matchingWords == totalMW) { // if matchingWords is equal to totalMatchingWords, then prompt user with game over message and exit program.
                                    System.out.println("You've matched all words! [GAME OVER]");
                                    System.exit(0);
                                }

                            } else { // if not matching, take both inputs and draw placeholders at both index pairs and prompt user with message
                                previousBoard(r1, c1, r2, c2, boardSize, placeholderBoard, wordBoard);
                                System.out.println("Not a match");
                                System.out.println("Number of guesses: " + guesses);
                            }
                        }
                    }
                    else {
                        System.out.println("\nOne of the inputs is out of bounds. \nPlease try again. \n"); // error message for out of bounds input
                        placeholderBoard[r1][c1] = "[XXXXXXXXXXXX]"; // for second input, sets first input back to placeholder value and draws it to console
                        drawBoard(boardSize, placeholderBoard);
                        break;
                    }
                } else {
                    System.out.println("\nOne of the inputs is out of bounds. \nPlease try again. \n");
                    break;
                }
            }
        }
    }

    public static void drawBoard(int size, String[][] board) { // draws game board

        System.out.println("");

        System.out.print("  ");
        for (int i = 0; i < size; i++) { // prints column 
            System.out.print("            " + i + "|");
        }

        System.out.println("");
        System.out.print("  ");
        for (int i = 0; i < size; i++) { // prints column line
            System.out.print("--------------");
        }
        System.out.println("");

        for (int i = 0; i < size; i++) { // for each column, print out the row number, followed by a whitespace and then row of multiarray board
            System.out.print(i + " ");
            for (int j = 0; j < size; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println("");
        }

        System.out.print("  ");
        for (int i = 0; i < size; i++) { // prints bottom line
            System.out.print("--------------");
        }

        System.out.println("");
    }

    public static String takeInput(int r, int c, String[][] wordBoard) { // takes input from user and returns the word at r and c, from wordBoard array

        String word = wordBoard[r][c];

        return word;
    }

    // draws newboard by passing in user input, with placeholder + wordBoard array (only executed if condition is true)
    public static void newBoard(int r, int c, int size, String[][] board, String[][] wordBoard) {

        if (board[r][c].equals("[XXXXXXXXXXXX]")) { // checks if placeholder index has not been already revealed. If true, then sets word from wordBoard to placeholder index and draws it to console.
            board[r][c] = wordBoard[r][c];
            drawBoard(size, board);
        }
    }

    // function to reset board when words don't match. Takes in both inputs and sets the placeholder board to "[XXXXXXXXXXXX]"
    public static void previousBoard(int r1, int c1, int r2, int c2, int size, String[][] board, String[][] wordBoard) {

        board[r1][c1] = "[XXXXXXXXXXXX]";
        board[r2][c2] = "[XXXXXXXXXXXX]";

        drawBoard(size, board);
    }

    // overloading function to reset only one input and draws the board
    public static void previousBoard(int r, int c, int size, String[][] board, String[][] wordBoard) {

        if (board[r][c].equals("[XXXXXXXXXXXX]")) {
            board[r][c] = "[XXXXXXXXXXXX]";
        }

        drawBoard(size, board);
    }
}
