public class AccountsFactory {

    //Create sample accounts and clients and adds them to the bank
    public static void createSampleAccounts(Bank bank) {
        try {
            addAccounts(bank);
            addClientsToAccounts(bank);
        } catch (DuplicationException e) {
            System.err.println("Error adding accounts: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    //Add predefined accounts to the bank
    private static void addAccounts(Bank bank) throws DuplicationException {
        bank.addAccount(new SavingsAccount(101, 1, "Manager1", 10000, 5));
        bank.addAccount(new SavingsAccount(102, 1, "Manager2", 15000, 3));
        bank.addAccount(new SavingsAccount(103, 2, "Manager3", 20000, 7));
        bank.addAccount(new SavingsAccount(104, 2, "Manager4", 5000, 10));

        bank.addAccount(new CheckingAccount(201, 1, "Manager1", 5000, false));
        bank.addAccount(new CheckingAccount(202, 1, "Manager2", 8000, true));
        bank.addAccount(new CheckingAccount(203, 2, "Manager3", 6000, false));
        bank.addAccount(new CheckingAccount(204, 2, "Manager4", 10000, true));

        bank.addAccount(new MortgageAccount(301, 1, "Manager1", 100000, 1200, 20));
        bank.addAccount(new MortgageAccount(302, 1, "Manager2", 150000, 1500, 15));
        bank.addAccount(new MortgageAccount(303, 2, "Manager3", 200000, 2000, 25));
        bank.addAccount(new MortgageAccount(304, 2, "Manager4", 120000, 1300, 10));
    }

    //Add predefined clients to the existing accounts in the bank
    private static void addClientsToAccounts(Bank bank) {
        Client client1 = new Client("Client1", 5);
        Client client2 = new Client("Client2", 6);
        Client client3 = new Client("Client3", 7);
        Client client4 = new Client("Client4", 8);
        bank.getAccountByNumber(101).addClient(client1);
        bank.getAccountByNumber(101).addClient(client2);
        bank.getAccountByNumber(102).addClient(client3);
        bank.getAccountByNumber(102).addClient(client4);
        bank.getAccountByNumber(103).addClient(client1);
        bank.getAccountByNumber(103).addClient(client4);
        bank.getAccountByNumber(104).addClient(client2);
        bank.getAccountByNumber(104).addClient(client3);

        bank.getAccountByNumber(201).addClient(client1);
        bank.getAccountByNumber(201).addClient(client2);
        bank.getAccountByNumber(202).addClient(client3);
        bank.getAccountByNumber(202).addClient(client4);
        bank.getAccountByNumber(203).addClient(client1);
        bank.getAccountByNumber(203).addClient(client3);
        bank.getAccountByNumber(204).addClient(client2);
        bank.getAccountByNumber(204).addClient(client4);

        bank.getAccountByNumber(301).addClient(client1);
        bank.getAccountByNumber(301).addClient(client2);
        bank.getAccountByNumber(302).addClient(client3);
        bank.getAccountByNumber(302).addClient(client4);
        bank.getAccountByNumber(303).addClient(client1);
        bank.getAccountByNumber(303).addClient(client4);
        bank.getAccountByNumber(304).addClient(client2);
        bank.getAccountByNumber(304).addClient(client3);
    }
}
