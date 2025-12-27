import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

// This is a better comment, trust me

public class Main {

    static final String FILE_NAME = "leaderboard.json";
    static Gson gson = new Gson();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine();


        Difficulty difficulty = chooseDifficulty(scanner);
        int maxNumber = difficulty.maxNumber;
        int lives = difficulty.lives;

        Random random = new Random();
        int secretNumber = random.nextInt(maxNumber) + 1;
        int attempts = 0;

        System.out.println("Enter a number between 1 and " + maxNumber);


        while (lives > 0) {
            System.out.print("Your guess: ");
            int guess = scanner.nextInt();
            attempts++;

            if (guess == secretNumber) {
                System.out.println("Correct! Attempts: " + attempts);

                // SAVE + SHOW LEADERBOARD
                saveScore(playerName, difficulty.name(), attempts);
                showLeaderboard();
                return;
            }

            if (guess < secretNumber) {
                System.out.println("Higher!");
            } else {
                System.out.println("Lower!");
            }

            lives--;
            System.out.println("Lives left: " + lives);
        }

        System.out.println("The number was: " + secretNumber);
    }


    enum Difficulty {
        EASY(10, 5),
        MEDIUM(100, 7),
        HARD(300, 10);

        int maxNumber;
        int lives;

        Difficulty(int maxNumber, int lives) {
            this.maxNumber = maxNumber;
            this.lives = lives;
        }
    }

    static Difficulty chooseDifficulty(Scanner scanner) {
        System.out.println("Choose difficulty:");
        System.out.println("1 - Easy (1-10)");
        System.out.println("2 - Medium (1-100)");
        System.out.println("3 - Hard (1-300)");

        int choice = scanner.nextInt();

        if (choice == 2) return Difficulty.MEDIUM;
        if (choice == 3) return Difficulty.HARD;
        return Difficulty.EASY;
    }


    static void saveScore(String name, String difficulty, int attempts) {
        Map<String, Map<String, Object>> leaderboard = loadLeaderboard();

        if (!leaderboard.containsKey(difficulty)) {
            Map<String, Object> newScore = new HashMap<>();
            newScore.put("name", name);
            newScore.put("attempts", attempts);
            leaderboard.put(difficulty, newScore);
        } else {
            Map<String, Object> bestScore = leaderboard.get(difficulty);
            int bestAttempts = ((Number) bestScore.get("attempts")).intValue();

            if (attempts < bestAttempts) {
                bestScore.put("name", name);
                bestScore.put("attempts", attempts);
            }
        }

        writeLeaderboard(leaderboard);
    }


    static Map<String, Map<String, Object>> loadLeaderboard() {
        try {
            FileReader reader = new FileReader(FILE_NAME);
            Map leaderboard = gson.fromJson(reader, Map.class);
            reader.close();

            if (leaderboard == null) {
                return new HashMap<>();
            }

            return leaderboard;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }


    static void writeLeaderboard(Map<String, Map<String, Object>> leaderboard) {
        try {
            FileWriter writer = new FileWriter(FILE_NAME);
            gson.toJson(leaderboard, writer);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving leaderboard.");
        }
    }


    static void showLeaderboard() {
        Map<String, Map<String, Object>> leaderboard = loadLeaderboard();

        System.out.println("\n Leaderboard");

        for (String diff : leaderboard.keySet()) {
            Map<String, Object> entry = leaderboard.get(diff);
            System.out.println(
                    diff + " -> " +
                            entry.get("name") + " (" +
                            entry.get("attempts") + " attempts)"
            );
        }
    }
}

