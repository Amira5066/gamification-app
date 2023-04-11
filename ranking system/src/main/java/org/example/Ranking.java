package org.example;

import java.sql.*;

public class Ranking{
    private static Connection con;

    public Ranking(){
        connectToDB();
    }

    /**
     * connects to the database
     */
    public void connectToDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/ranking_system","root","");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param username the username of the person you want to see the ranking
     * @return database content in String format
     */
    public String getRanking(String username){
        String queryRank = "SELECT COUNT(*) + 1 AS ranking, username, token_value FROM tokens WHERE token_value > (SELECT token_value FROM tokens WHERE username = ?);";
        String queryTokens = "SELECT username, token_value FROM tokens WHERE username = ?";
        PreparedStatement instr = null;
        String result = "";
        try {
            instr = con.prepareStatement(queryRank);
            instr.setString(1, username);
            ResultSet rank = instr.executeQuery();

            instr = con.prepareStatement(queryTokens);
            instr.setString(1, username);
            ResultSet tokens = instr.executeQuery();
            if (rank.next() && tokens.next()) {
                result += " Rank: " + rank.getString("ranking") + " Name: " + tokens.getString("username") + " Tokens " + tokens.getString("token_value") + "\n";
            }
            else {
                result += "No user found";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * @return database content of the full ranking
     */
    public String getFullRanking(){
        String queryRank = "SELECT ROW_NUMBER() OVER (ORDER BY token_value DESC) AS ranking, username, token_value FROM tokens;";
        PreparedStatement instr = null;
        String result = "";
        try {
            instr = con.prepareStatement(queryRank);
            ResultSet rank = instr.executeQuery();

            while (rank.next()) {
                result += "Rank: " + rank.getString("ranking") + " Name: " + rank.getString("username") + " Tokens " + rank.getString("token_value") + "\n";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Checks whether the database already contains the username given. If it does the user is not created and the function returns false.
     * If the username does not exist in the database, a new user is created, it is inserted into the database and the function returns true.
     * @param username the username of the new user
     * @return true for success, false for failure
     */
    public boolean insertNewPlayer(String username) {
        String query = "SELECT username FROM tokens WHERE username = ?";
        PreparedStatement instr = null;
        try {
            instr = con.prepareStatement(query);
            instr.setString(1, username);
            ResultSet duplicate = instr.executeQuery();

            if (duplicate.next()) {
                System.out.println("Username already exists");
                return false;
            }

            query = "INSERT INTO tokens (username, token_value) VALUES (?, 0)";
            instr = con.prepareStatement(query);
            instr.setString(1, username);
            instr.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("New user inserted");
        return true;
    }

    public boolean logIn(String username) {
        String query = "SELECT username FROM tokens WHERE username = ?";
        PreparedStatement instr = null;
        try {
            instr = con.prepareStatement(query);
            instr.setString(1, username);
            ResultSet duplicate = instr.executeQuery();

            if (!duplicate.next()) {
                System.out.println("Wrong username");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Logged in successfully");
        return true;
    }

    private boolean updateTokens(String username, int tokensAdded) {
        String query = "SELECT token_value FROM tokens WHERE username = ?";
        PreparedStatement instr = null;
        try {
            instr = con.prepareStatement(query);
            instr.setString(1, username);
            ResultSet user = instr.executeQuery();

            if (!user.next()) {
                System.out.println("No user found");
                return false;
            }

            int currentTokens = user.getInt("token_value");
            currentTokens += tokensAdded;

            query = "UPDATE tokens SET token_value = " + currentTokens + " WHERE username = '" + username + "'";
            instr = con.prepareStatement(query);
            instr.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean askQuestion(String username, String questionAsked) {
        String query = "SELECT username, asked_question FROM questions WHERE username = ? AND asked_question = ?";
        PreparedStatement instr = null;
        try {
            instr = con.prepareStatement(query);
            instr.setString(1, username);
            instr.setString(2, questionAsked);
            ResultSet duplicate = instr.executeQuery();

            if (duplicate.next()) {
                System.out.println("Question already posted");
                return false;
            }

            query = "INSERT INTO questions (username, asked_question) VALUES (?, ?)";
            instr = con.prepareStatement(query);
            instr.setString(1, username);
            instr.setString(2, questionAsked);
            instr.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Question submitted");
        return true;
    }

    public String listQuestions(){
        String query = "SELECT questionID, username, asked_question FROM questions";
        PreparedStatement instr = null;
        String result = "";
        try {
            instr = con.prepareStatement(query);
            ResultSet user = instr.executeQuery();

            while (user.next()) {
                result += "Question ID: " + user.getInt("questionID") + " -> " + user.getString("username") + ": " + user.getString("asked_question") + "\n";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public String listAnswers(int questionID) {
        String query = "SELECT questionID, username, answerID, answer FROM answers WHERE questionID = ?";
        PreparedStatement instr = null;
        String result = "";
        try {
            instr = con.prepareStatement(query);
            instr.setInt(1, questionID);
            ResultSet user = instr.executeQuery();

            if (!user.next()) {
                System.out.println("Invalid question ID");
                return "";
            }

            result += "Question ID: " + questionID + "\n";
           do {
                result += "Answer ID: " +user.getInt("answerID") + " -> " + user.getString("username") + ": " + user.getString("answer") + "\n";
            } while (user.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public boolean addAnswer(String username, int questionID, String answer) {
        String query = "SELECT username, answer FROM answers WHERE username = ? AND answer = ?";
        PreparedStatement instr = null;
        try {
            instr = con.prepareStatement(query);
            instr.setString(1, username);
            instr.setString(2, answer);
            ResultSet duplicate = instr.executeQuery();

            if (duplicate.next()) {
                System.out.println("Answer already submitted");
                return false;
            }

            query = "SELECT questionID FROM questions WHERE questionID = ?";
            instr = con.prepareStatement(query);
            instr.setInt(1, questionID);
            duplicate = instr.executeQuery();

            if (!duplicate.next()) {
                System.out.println("Invalid question ID");
                return false;
            }

            query = "INSERT INTO answers (username, questionID, answer) VALUES (?, ?, ?)";
            instr = con.prepareStatement(query);
            instr.setString(1, username);
            instr.setInt(2, questionID);
            instr.setString(3, answer);
            instr.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Answer submitted");
        return true;
    }

    public boolean giveTokens(String username, int answerID, int tokens) {
        String query = "SELECT username, answerID FROM answers WHERE answerID = ?";
        PreparedStatement instr = null;
        try {
            instr = con.prepareStatement(query);
            instr.setInt(1, answerID);
            ResultSet user = instr.executeQuery();

            if (!user.next()) {
                System.out.println("Invalid answer ID");
                return false;
            }
            String toGiveTokens = user.getString("username");

            query = "SELECT questions.username FROM answers INNER JOIN questions ON questions.questionID = answers.questionID WHERE answerID = ?";
            instr = con.prepareStatement(query);
            instr.setInt(1, answerID);
            user = instr.executeQuery();

            user.next();
            if(!user.getString("questions.username").equals(username)) {
                System.out.println("You do not have the permission to give tokens");
                return false;
            }

            updateTokens(toGiveTokens, tokens);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Tokens given");
        return true;
    }

    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}