public class SavingsAccount extends Account {
    private double depositAmount;
    private int years;

    public SavingsAccount(int accountNumber, int bankNumber, String managerName, double depositAmount, int years) {
        super(accountNumber, bankNumber, managerName);
        this.depositAmount = depositAmount;
        this.years = years;
    }

    @Override
    public double calculateAnnualProfit() {
        return 0;
    }
}
