public class DuplicationException extends Exception {
    public DuplicationException(int accountNumber) {
        super("Account number " + accountNumber + " already exists.");
    }
}
