package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static String username;
    private static ArrayList<String> commands;

    static {
        commands = new ArrayList<String>();
        commands.add("List questions");
        commands.add("List answers <questionID>");
        commands.add("Answer question <questionID>");
        commands.add("Ask a question");
        commands.add("Get ranking <username>");
        commands.add("Get full ranking");
        commands.add("Give tokens <answerID> <no of tokens>");
        commands.add("Exit");
    }

    /**
     * Logs in to the database with the username read with a Scanner object and calls the logIn method from Ranking class.
     * @param connection
     */
    public static void logIN(Ranking connection) {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print("Enter your username: ");
            username = scanner.nextLine();
            if (username.equalsIgnoreCase("exit"))
                break;
        }while (!connection.logIn(username));
    }

    /**
     * Calls the method insertNewPlayer from Ranking class with a username read by a Scanner object.
     * @param connection
     */
    public static void signUp(Ranking connection) {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print("Enter new username: ");
            username = scanner.nextLine();
            if (username.equalsIgnoreCase("exit"))
                break;
        }while (!connection.insertNewPlayer(username));
    }

    /**
     * Lists all the possible commands that a user can enter.
     */
    public static void listCommands() {
        System.out.println("Commands:");
        for (String com : commands) {
            System.out.println(com);
        }
    }

    /**
     * Calls the method addAnswer from Ranking class with an answer read by a Scanner Object and a question identifier retrieved from the command.
     * @param connection
     * @param questionID
     */
    public static void answerQuestion(Ranking connection, int questionID) {
        System.out.print("Your answer: ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        if(answer.equalsIgnoreCase("exit")) return;
        connection.addAnswer(username, questionID, answer);
    }

    /**
     * Calls the method askQuestion from Ranking class with a question read by a Scanner Object.
     * @param connection
     */
    public static void askQuestion(Ranking connection) {
        System.out.print("Your question: ");
        Scanner scanner = new Scanner(System.in);
        String question = scanner.nextLine();
        if(question.equalsIgnoreCase("exit")) return;
        connection.askQuestion(username, question);
    }

    public static void main(String[] args) {
        Ranking connection = new Ranking();
        Scanner scanner = new Scanner(System.in);
        String command = null;

        while (true) {
            do{
                System.out.print("Log in/Sign up/Exit\nEnter command: ");
                command = scanner.nextLine();
            }while (!command.equalsIgnoreCase("log in") && !command.equalsIgnoreCase("sign up") && !command.equalsIgnoreCase("exit"));

            if (command.equalsIgnoreCase("log in")) {
                logIN(connection);
                if (username.equalsIgnoreCase("exit")) continue;
                else break;
            }

            if (command.equalsIgnoreCase("sign up")) {
                signUp(connection);
                if (username.equalsIgnoreCase("exit")) continue;
                else break;
            }

            if (command.equalsIgnoreCase("exit")){
                connection.closeConnection();
                return;
            }
        }

        while (true) {
            listCommands();
            System.out.print("Enter command: ");
            command = scanner.nextLine();
            String cpyCommand = new String(command);

            if (command.equalsIgnoreCase("exit")) break;

            if (command.equalsIgnoreCase("list questions")) {
                System.out.println(connection.listQuestions());
            }

            if (command.equalsIgnoreCase("get full ranking")) {
                System.out.println(connection.getFullRanking());
            }

            if (command.toLowerCase().startsWith("list answers")) {
                String[] strArr = command.split(" ");
                System.out.println(connection.listAnswers(Integer.parseInt(strArr[2])));
            }

            if (command.toLowerCase().startsWith("answer question")) {
                String[] strArr = command.split(" ");
                answerQuestion(connection, Integer.parseInt(strArr[2]));
            }

            if (command.toLowerCase().startsWith("ask a question")) {
                askQuestion(connection);
            }

            if (command.toLowerCase().startsWith("get ranking")) {
                String[] strArr = cpyCommand.split(" ");
                System.out.println(connection.getRanking(strArr[2]));
            }

            if (command.toLowerCase().startsWith("give tokens")) {
                String[] strArr = cpyCommand.split(" ");
                connection.giveTokens(username, Integer.parseInt(strArr[2]), Integer.parseInt(strArr[3]));
            }
        }

        connection.closeConnection();
    }
}
