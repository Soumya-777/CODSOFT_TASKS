import java.util.Scanner;

public class ATM {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankAccount account = new BankAccount(0);

        int choice = 0;

        do {
            System.out.println("\n===== ATM MENU =====");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");

            // Validate menu input
            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number (1-4).");
                sc.next();
                continue;
            }

            choice = sc.nextInt();

            if (choice == 1) {

                System.out.println("Current Balance: Rs. " + account.getBalance());

            } else if (choice == 2) {

                System.out.print("Enter amount to deposit: ");

                if (!sc.hasNextDouble()) {
                    System.out.println("Invalid amount! Please enter a number.");
                    sc.next();
                    continue;
                }

                double amount = sc.nextDouble();

                if (amount <= 0) {
                    System.out.println("Invalid amount! Deposit must be greater than 0.");
                } else {
                    account.deposit(amount);
                    System.out.println("Money deposited successfully.");
                    System.out.println("New Balance: Rs. " + account.getBalance());
                }

            } else if (choice == 3) {

                System.out.print("Enter amount to withdraw: ");

                if (!sc.hasNextDouble()) {
                    System.out.println("Invalid amount! Please enter a number.");
                    sc.next();
                    continue;
                }

                double amount = sc.nextDouble();

                if (amount <= 0) {
                    System.out.println("Invalid amount! Withdrawal must be greater than 0.");
                } else if (amount > account.getBalance()) {
                    System.out.println("Insufficient balance!");
                    System.out.println("Available Balance: Rs. " + account.getBalance());
                } else {
                    account.withdraw(amount);
                    System.out.println("Please collect your cash.");
                    System.out.println("Remaining Balance: Rs. " + account.getBalance());
                }

            } else if (choice == 4) {

                System.out.println("Thank you for using the ATM.");

            } else {

                System.out.println("Invalid choice! Please enter a number between 1 and 4.");
            }

        } while (choice != 4);

        sc.close();
    }
}