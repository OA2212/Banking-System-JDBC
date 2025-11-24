import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BankWithDB bank = new BankWithDB();
        Scanner scanner = new Scanner(System.in);
        String option;

        do {
            System.out.println("=== Bank Management ===");
            System.out.println("1. Create sample accounts and clients");
            System.out.println("2. Create new account");
            System.out.println("3. Create new client");
            System.out.println("4. Display all the accounts");
            System.out.println("5. Display only accounts with profit");
            System.out.println("6. Display one type of account with full data");
            System.out.println("7. Display the yearly profit in specific account");
            System.out.println("8. Display the yearly profit in all the accounts");
            System.out.println("9. Display the checking account with the best profit");
            System.out.println("10. Check business account");
            System.out.println("11. Display Management Fees");
            System.out.println("E. Exit");
            System.out.print("Choose an option: ");
            option = scanner.nextLine();

            switch (option) {
                case "1":
                    bank.createSampleAccounts();
                    break;
                case "2":
                    bank.addNewAccount();
                    break;
                case "3":
                    bank.addClientToAccount();
                    break;
                case "4":
                    bank.printAccounts();
                    break;
                case "5":
                    bank.displayProfitAccountsSorted();
                    break;
                case "6":
                    System.out.print("Choose Type (Personal/ Business): ");
                    String ans = scanner.nextLine();
                    bank.displayAccountsByType(ans);
                    break;
                case "7":
                    System.out.print("Enter account number: (e.g., 12345)");
                    int ans1 = Integer.parseInt(scanner.nextLine());
                    bank.displayAnnualProfitOfSpecificAccount(ans1);
                    break;
                case "8":
                    bank.displayTotalAnnualProfit();
                    break;
                case "9":
                    bank.displayTopProfitCheckingAccount();
                    break;
                case "10":
                    System.out.print("Enter account number: (e.g., 12345)");
                    int ans2 = Integer.parseInt(scanner.nextLine());
                    bank.checkBusinessAccountProfit(ans2);
                    break;
                case "11":
                    bank.printManagementFees();
                    break;
                case "e":
                case "E":
                    System.out.println("Exiting the program...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (!option.equalsIgnoreCase("e"));

        scanner.close();
    }
}
