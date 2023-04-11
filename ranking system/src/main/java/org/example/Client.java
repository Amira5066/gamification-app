package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static String username;
    private static String password;
    private static ArrayList<String> commands;

    static {
        commands = new ArrayList<String>();
        commands.add("List questions");
        commands.add("List answers <questionID>");
        commands.add("Answer question <questionID>");
        commands.add("Get ranking <username>");
        commands.add("Get full ranking");
        commands.add("Give tokens <answerID> <no of tokens>");
        commands.add("Exit");
    }

    public static void logIN(Ranking connection) {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print("Enter your username: ");
            username = scanner.nextLine();
            if (username.equalsIgnoreCase("exit"))
                break;
        }while (!connection.logIn(username));
    }

    public static void signUp(Ranking connection) {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print("Enter new username: ");
            username = scanner.nextLine();
            if (username.equalsIgnoreCase("exit"))
                break;
        }while (!connection.insertNewPlayer(username));
    }

    public static void listCommands() {
        System.out.println("Commands:");
        for (String com : commands) {
            System.out.println(com);
        }
    }

    public static void answerQuestion(Ranking connection, int questionID) {
        System.out.print("Your answer: ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        if(answer.equalsIgnoreCase("exit")) return;
        connection.addAnswer(username, questionID, answer);
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
