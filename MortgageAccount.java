public class MortgageAccount extends Account {
    private double originalMortgageAmount;
    private double monthlyPayment;
    private int years;

    public MortgageAccount(int accountNumber, int bankNumber, String managerName, double originalMortgageAmount, double monthlyPayment, int years) {
        super(accountNumber, bankNumber, managerName);
        this.originalMortgageAmount = originalMortgageAmount;
        this.monthlyPayment = monthlyPayment;
        this.years = years;
    }

    @Override
    public double calculateAnnualProfit() {
        double rateDifference = 0.10;
        return (originalMortgageAmount * 0.8 * rateDifference / years);
    }

    public double getOriginalMortgageAmount() {
        return originalMortgageAmount;
    }
}
