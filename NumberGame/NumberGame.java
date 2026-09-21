import java.util.Random;
import java.util.Scanner;

public class NumberGame {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        int score = 0;
        boolean playAgain = true;

        System.out.println("===== NUMBER GAME =====");

        while (playAgain) {

            // Generate a random number between 1 and 100
            int randomNumber = random.nextInt(100) + 1;

            int attempts = 0;
            int maxAttempts = 10;
            boolean guessedCorrectly = false;

            System.out.println("\nI have selected a number between 1 and 100.");
            System.out.println("You have " + maxAttempts + " attempts to guess it.");

            while (attempts < maxAttempts) {

                System.out.print("Enter your guess: ");
                int guess = scanner.nextInt();

                attempts++;

                if (guess == randomNumber) {
                    System.out.println("Congratulations! You guessed the correct number.");
                    System.out.println("Number of attempts: " + attempts);

                    score++;
                    guessedCorrectly = true;
                    break;

                } else if (guess < randomNumber) {
                    System.out.println("Too low! Try again.");

                } else {
                    System.out.println("Too high! Try again.");
                }
            }

            if (!guessedCorrectly) {
                System.out.println("\nSorry! You used all your attempts.");
                System.out.println("The correct number was: " + randomNumber);
            }

            System.out.println("Current score: " + score);

            System.out.print("\nDo you want to play another round? (yes/no): ");
            String answer = scanner.next();

            if (!answer.equalsIgnoreCase("yes")) {
                playAgain = false;
            }
        }

        System.out.println("\n===== GAME OVER =====");
        System.out.println("Final Score: " + score);
        System.out.println("Thank you for playing!");

        scanner.close();
    }
}