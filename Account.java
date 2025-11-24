import java.util.Date;

public abstract class Account {
    private int accountNumber;
    private int bankNumber;
    private double balance = 20;
    private String managerName;
    private Date creationDate;
    private Client[] clients;
    private int clientCount = 0;


    public Account(int accountNumber, int bankNumber, String managerName) {
        this.accountNumber = accountNumber;
        this.bankNumber = bankNumber;
        this.managerName = managerName;
        this.creationDate = new Date();
        this.clients = new Client[2];
    }

    //Add a client to the account, resizing the array if necessary
    public void addClient(Client client) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].equals(client)) {
                return;
            }
        }
        if (clientCount == clients.length) {
            Client[] newClients = new Client[clients.length * 2];
            System.arraycopy(clients, 0, newClients, 0, clients.length);
            clients = newClients;
        }
        clients[clientCount++] = client;
    }

    // Retrieves a client by their index
    public Client getClient(int index) {
        return clients[index];
    }

    // Returns the total number of clients associated with the account
    public int getClientsCount() {
        return clientCount;
    }

    //Getter for the account number
    public int getAccountNumber() {
        return accountNumber;
    }

    //Getter for the bank number
    public int getBankNumber() {
        return bankNumber;
    }

    //Getter for the account balance
    public double getBalance() {
        return balance;
    }

    //Getter for the manager name
    public String getManagerName() {
        return managerName;
    }

    //Update the client at a specific index
    public void updateClient(int index, Client client) {
        if (index >= 0 && index < clientCount) {
            clients[index] = client;
        }
    }

    public abstract double calculateAnnualProfit();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Account Number: " + accountNumber + "\nManager Name: " + managerName + "\nBalance: " + balance +
                "\nAnnual Profit: " + calculateAnnualProfit() + "\nClients:" );
        for ( Client c : clients){
            sb.append(c.getName() + " ");
        }
        return String.valueOf(sb);
    }
}
