public class CheckingAccount extends Account {
    private double creditLimit;
    private boolean isBusinessAccount;

    public CheckingAccount(int accountNumber, int bankNumber, String managerName, double creditLimit, boolean isBusinessAccount) {
        super(accountNumber, bankNumber, managerName);
        this.creditLimit = creditLimit;
        this.isBusinessAccount = isBusinessAccount;
    }

    public boolean isBusinessAccount() {
        return isBusinessAccount;
    }

    //Calculate the potential VIP profit by simulating a scenario with modified clients
    public double checkProfitVIP() {
        CheckingAccount copiedAccount = new CheckingAccount(this.getAccountNumber(), this.getBankNumber(), this.getManagerName(), this.creditLimit, this.isBusinessAccount);
        for (int i = 0; i < copiedAccount.getClientsCount(); i++) {
            Client client = copiedAccount.getClient(i);
            Client modifiedClient = new Client(client.getName(), 0);
            copiedAccount.updateClient(i, modifiedClient);
        }
        return copiedAccount.calculateAnnualProfit();
    }

    public boolean getIsBusinessAccount() {
        return isBusinessAccount;
    }

    //Calculate the annual profit based on the account type
    @Override
    public double calculateAnnualProfit() {
        return isBusinessAccount ? (creditLimit * 0.10) : (creditLimit * 0.05);
    }

    @Override
    public CheckingAccount clone() {
        return new CheckingAccount(this.getAccountNumber(), this.getBankNumber(), this.getManagerName(), this.creditLimit, this.isBusinessAccount);
    }
}
