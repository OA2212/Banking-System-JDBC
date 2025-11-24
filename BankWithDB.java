import java.sql.*;
import java.util.Scanner;

public class BankWithDB {
    private final Scanner scanner = new Scanner(System.in);

    private int getInt(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int v = Integer.parseInt(scanner.nextLine());
                if (v < min || v > max) {
                    System.out.println("Enter between " + min + " and " + max + ".");
                }
                else {
                    return v;
                }
            }
            catch (NumberFormatException e) { System.out.println("Invalid number."); }
        }
    }

    private double getDouble(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double v = Double.parseDouble(scanner.nextLine());
                if (v < min || v > max) {
                    System.out.println("Enter between " + min + " and " + max + ".");
                }
                else {
                    return v;
                }
            }
            catch (NumberFormatException e) { System.out.println("Invalid number."); }
        }
    }

    private String getNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("Value cannot be empty.");
        }
    }

    private boolean accountExists(Connection c, int accountNumber) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM account WHERE account_number=?")) {
            ps.setInt(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void ensureBankExists(Connection c, int bankNumber) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO bank (bank_number, bank_name) VALUES (?, ?) " +
                        "ON CONFLICT (bank_number) DO NOTHING"
        )) {
            ps.setInt(1, bankNumber);
            ps.setString(2, "Bank " + bankNumber);
            ps.executeUpdate();
        }
    }

    //Create sample accounts and clients
    public void createSampleAccounts() {
        final String insAccount =
                "INSERT INTO account (account_number, bank_number, manager_name, balance, account_type) " +
                        "VALUES (?, ?, ?, ?, ?::account_type_enum)";
        final String insChecking =
                "INSERT INTO checking_account (account_number, credit_limit, is_business) VALUES (?, ?, ?)";
        final String insSavings =
                "INSERT INTO savings_account (account_number, deposit_amount, years) VALUES (?, ?, ?)";
        final String insMortgage =
                "INSERT INTO mortgage_account (account_number, original_mortgage_amount, monthly_payment, years) VALUES (?, ?, ?, ?)";
        final String insClient =
                "INSERT INTO client (name, rank) VALUES (?, ?) RETURNING client_id";
        final String insLink =
                "INSERT INTO account_client (account_number, client_id) VALUES (?, ?)";

        try (Connection connection = DB.get()) {
            connection.setAutoCommit(false);
            try {
                ensureBankExists(connection, 1);
                ensureBankExists(connection, 2);
                // Checking personal
                try (PreparedStatement statement = connection.prepareStatement(insAccount)) {
                    statement.setInt(1, 12345);
                    statement.setInt(2, 1);
                    statement.setString(3, "Alice");
                    statement.setDouble(4, 15000);
                    statement.setString(5, "CHECKING");
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(insChecking)) {
                    statement.setInt(1, 12345);
                    statement.setDouble(2, 10000);
                    statement.setBoolean(3, false);
                    statement.executeUpdate();
                }
                // Checking business
                try (PreparedStatement statement = connection.prepareStatement(insAccount)) {
                    statement.setInt(1, 23456);
                    statement.setInt(2, 1);
                    statement.setString(3, "Dana");
                    statement.setDouble(4, 90000);
                    statement.setString(5, "CHECKING");
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(insChecking)) {
                    statement.setInt(1, 23456);
                    statement.setDouble(2, 50000);
                    statement.setBoolean(3, true);
                    statement.executeUpdate();
                }
                // Savings
                try (PreparedStatement statement = connection.prepareStatement(insAccount)) {
                    statement.setInt(1, 34567);
                    statement.setInt(2, 2);
                    statement.setString(3, "Eli");
                    statement.setDouble(4, 0);
                    statement.setString(5, "SAVINGS");
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(insSavings)) {
                    statement.setInt(1, 34567);
                    statement.setDouble(2, 20000);
                    statement.setInt(3, 5);
                    statement.executeUpdate();
                }
                // Mortgage
                try (PreparedStatement statement = connection.prepareStatement(insAccount)) {
                    statement.setInt(1, 45678);
                    statement.setInt(2, 2);
                    statement.setString(3, "Ron");
                    statement.setDouble(4, 0);
                    statement.setString(5, "MORTGAGE");
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(insMortgage)) {
                    statement.setInt(1, 45678);
                    statement.setDouble(2, 800000);
                    statement.setDouble(3, 4500);
                    statement.setInt(4, 25);
                    statement.executeUpdate();
                }
                // clients + links
                int[] accs = {12345, 23456};
                String[] names = {"Moshe", "Rina"};
                int[] ranks = {5, 8};
                for (int i = 0; i < names.length; i++) {
                    int clientId;
                    try (PreparedStatement statement = connection.prepareStatement(insClient)) {
                        statement.setString(1, names[i]);
                        statement.setInt(2, ranks[i]);
                        try (ResultSet result = statement.executeQuery()) {
                            result.next(); clientId = result.getInt(1);
                        }
                    }
                    try (PreparedStatement statement = connection.prepareStatement(insLink)) {
                        statement.setInt(1, accs[i]);
                        statement.setInt(2, clientId);
                        statement.executeUpdate();
                    }
                }

                connection.commit();
                System.out.println("Sample accounts and clients created successfully!");
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Failed (rolled back): " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    //Create new account
    public void addNewAccount() {
        System.out.println("1. Checking  2. Savings  3. Mortgage");
        int choice = getInt("Enter your choice: ", 1, 3);

        int accountNumber = getInt("Enter account number: ", 1, Integer.MAX_VALUE);
        int bankNumber    = getInt("Enter bank number: ", 1, Integer.MAX_VALUE);
        String manager    = getNonEmpty("Enter manager name: ");
        double balance    = (choice == 1) ? getDouble("Initial balance: ", -1e9, 1e12) : 0.0;

        String type = (choice == 1 ? "CHECKING" : choice == 2 ? "SAVINGS" : "MORTGAGE");

        final String insAccount =
                "INSERT INTO account (account_number, bank_number, manager_name, balance, account_type) " +
                        "VALUES (?, ?, ?, ?, ?::account_type_enum)";
        final String insChecking =
                "INSERT INTO checking_account (account_number, credit_limit, is_business) VALUES (?,?,?)";
        final String insSavings =
                "INSERT INTO savings_account (account_number, deposit_amount, years) VALUES (?,?,?)";
        final String insMortgage =
                "INSERT INTO mortgage_account (account_number, original_mortgage_amount, monthly_payment, years) VALUES (?,?,?,?)";

        try (Connection connection = DB.get()) {
            connection.setAutoCommit(false);
            try {
                ensureBankExists(connection, bankNumber);

                try (PreparedStatement statement = connection.prepareStatement(insAccount)) {
                    statement.setInt(1, accountNumber);
                    statement.setInt(2, bankNumber);
                    statement.setString(3, manager);
                    statement.setDouble(4, balance);
                    statement.setString(5, type);
                    statement.executeUpdate();
                }
                if (choice == 1) {
                    double creditLimit = getDouble("Credit limit (0..1,000,000): ", 0, 1_000_000);
                    boolean isBusiness = Boolean.parseBoolean(getNonEmpty("Is business? (true/false): "));
                    try (PreparedStatement statement = connection.prepareStatement(insChecking)) {
                        statement.setInt(1, accountNumber);
                        statement.setDouble(2, creditLimit);
                        statement.setBoolean(3, isBusiness);
                        statement.executeUpdate();
                    }
                } else if (choice == 2) {
                    double deposit = getDouble("Deposit amount: ", 0, 1e12);
                    int years = getInt("Years (1..30): ", 1, 30);
                    try (PreparedStatement statement = connection.prepareStatement(insSavings)) {
                        statement.setInt(1, accountNumber);
                        statement.setDouble(2, deposit);
                        statement.setInt(3, years);
                        statement.executeUpdate();
                    }
                } else {
                    double original = getDouble("Original mortgage amount: ", 1, 1e12);
                    double monthly  = getDouble("Monthly payment: ", 1, 1e9);
                    int years = getInt("Years (1..30): ", 1, 30);
                    try (PreparedStatement statement = connection.prepareStatement(insMortgage)) {
                        statement.setInt(1, accountNumber);
                        statement.setDouble(2, original);
                        statement.setDouble(3, monthly);
                        statement.setInt(4, years);
                        statement.executeUpdate();
                    }
                }

                connection.commit();
                System.out.println("Account added successfully!");
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Insert failed (rolled back): " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    // UPDATE balance
    public void updateBalance() {
        int accountNum = getInt("Account #: ", 1, Integer.MAX_VALUE);
        double balance = getDouble("New balance: ", -1e9, 1e12);
        try (Connection connection = DB.get();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE account SET balance=? WHERE account_number=?")) {
            statement.setDouble(1, balance);
            statement.setInt(2, accountNum);
            int res = statement.executeUpdate();
            System.out.println(res > 0 ? "Updated." : "Account not found.");
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    // DELETE example (cascades to subtype + links)
    public void deleteAccount() {
        int accountNum = getInt("Account #: ", 1, Integer.MAX_VALUE);
        try (Connection connection = DB.get()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM account WHERE account_number=?")) {
                statement.setInt(1, accountNum);
                int res = statement.executeUpdate();
                connection.commit();
                System.out.println(res > 0 ? "Deleted." : "Account not found.");
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Delete failed (rolled back): " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }



    //Add new client and link to account
    public void addClientToAccount() {
        int accountNumber = getInt("Enter account number to add the client: ", 1, Integer.MAX_VALUE);
        String name = getNonEmpty("Enter client name: ");
        int rank = getInt("Enter client rank (0-10): ", 0, 10);

        final String insClient = "INSERT INTO client (name, rank) VALUES (?, ?) RETURNING client_id";
        final String insLink = "INSERT INTO account_client (account_number, client_id) VALUES (?, ?)";

        try (Connection connection = DB.get()) {
            if (!accountExists(connection, accountNumber)) {
                System.out.println("Account not found.");
                return;
            }
            connection.setAutoCommit(false);
            try {
                int clientId;
                try (PreparedStatement statement = connection.prepareStatement(insClient)) {
                    statement.setString(1, name);
                    statement.setInt(2, rank);
                    try (ResultSet result = statement.executeQuery()) {
                        result.next(); clientId = result.getInt(1);
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(insLink)) {
                    statement.setInt(1, accountNumber);
                    statement.setInt(2, clientId);
                    statement.executeUpdate();
                }
                connection.commit();
                System.out.println("Client added successfully!");
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Error adding client (rolled back): " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    //Display all accounts
    public void printAccounts() {
        String sql =
                "SELECT a.account_number, a.bank_number, a.manager_name, a.balance, a.account_type, " +
                        "       ca.credit_limit, ca.is_business, " +
                        "       sa.deposit_amount, sa.years AS savings_years, " +
                        "       ma.original_mortgage_amount, ma.monthly_payment, ma.years AS mortgage_years " +
                        "FROM account a " +
                        "LEFT JOIN checking_account ca ON ca.account_number = a.account_number " +
                        "LEFT JOIN savings_account  sa ON sa.account_number = a.account_number " +
                        "LEFT JOIN mortgage_account ma ON ma.account_number = a.account_number " +
                        "ORDER BY a.account_number";
        try (Connection connection = DB.get();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                String type = result.getString("account_type");
                System.out.print("Account #" + result.getInt("account_number") +
                        " | Bank " + result.getInt("bank_number") +
                        " | Manager: " + result.getString("manager_name") +
                        " | Type: " + type +
                        " | Balance: " + result.getDouble("balance"));
                if ("CHECKING".equals(type)) {
                    System.out.print(" | CreditLimit: " + result.getDouble("credit_limit") +
                            " | Business: " + result.getBoolean("is_business"));
                } else if ("SAVINGS".equals(type)) {
                    System.out.print(" | Deposit: " + result.getDouble("deposit_amount") +
                            " | Years: " + result.getInt("savings_years"));
                } else if ("MORTGAGE".equals(type)) {
                    System.out.print(" | Original: " + result.getDouble("original_mortgage_amount") +
                            " | Monthly: " + result.getDouble("monthly_payment") +
                            " | Years: " + result.getInt("mortgage_years"));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Select failed: " + e.getMessage());
        }
    }

    //Display only accounts with profit (sorted)
    public void displayProfitAccountsSorted() {
        String sql =
                "WITH profits AS ( " +
                        "  SELECT a.account_number, a.account_type, " +
                        "         CASE " +
                        "           WHEN a.account_type='CHECKING' THEN a.balance*0.02*(CASE WHEN COALESCE(ca.is_business,false) THEN 1.10 ELSE 1 END) " +
                        "           WHEN a.account_type='SAVINGS'  THEN COALESCE(sa.deposit_amount,0)*0.03 " +
                        "           WHEN a.account_type='MORTGAGE' THEN COALESCE(ma.monthly_payment,0)*12*0.05 " +
                        "           ELSE 0 " +
                        "         END AS annual_profit " +
                        "  FROM account a " +
                        "  LEFT JOIN checking_account ca ON ca.account_number = a.account_number " +
                        "  LEFT JOIN savings_account  sa ON sa.account_number = a.account_number " +
                        "  LEFT JOIN mortgage_account ma ON ma.account_number = a.account_number " +
                        ") " +
                        "SELECT * FROM profits WHERE annual_profit > 0 ORDER BY annual_profit DESC, account_number ASC";
        try (Connection connection = DB.get();
             Statement statement = connection.createStatement();
             ResultSet res = statement.executeQuery(sql)) {
            while (res.next()) {
                System.out.println("Account #" + res.getInt("account_number") +
                        " | Type: " + res.getString("account_type") +
                        " | Annual Profit: " + res.getDouble("annual_profit"));
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    //Display one type of account with full data
    public void displayAccountsByType(String accountTypeInput) {
        String sql;
        boolean byBusiness = false, businessFlag = false;

        if (accountTypeInput.equalsIgnoreCase("business")) {
            sql = "SELECT a.*, ca.credit_limit, ca.is_business FROM account a " +
                    "JOIN checking_account ca ON ca.account_number=a.account_number " +
                    "WHERE a.account_type='CHECKING' AND ca.is_business = TRUE ORDER BY a.account_number";
            byBusiness = true; businessFlag = true;
        } else if (accountTypeInput.equalsIgnoreCase("personal")) {
            sql = "SELECT a.*, ca.credit_limit, ca.is_business FROM account a " +
                    "JOIN checking_account ca ON ca.account_number=a.account_number " +
                    "WHERE a.account_type='CHECKING' AND ca.is_business = FALSE ORDER BY a.account_number";
            byBusiness = true; businessFlag = false;
        } else {
            sql = "SELECT a.account_number, a.bank_number, a.manager_name, a.balance, a.account_type " +
                    "FROM account a WHERE a.account_type = ?::account_type_enum ORDER BY a.account_number";
        }

        try (Connection connection = DB.get()) {
            if (byBusiness) {
                try (Statement statement = connection.createStatement();
                     ResultSet res = statement.executeQuery(sql)) {
                    boolean any = false;
                    while (res.next()) {
                        any = true;
                        System.out.println("CHECKING | #" + res.getInt("account_number") +
                                " | Manager: " + res.getString("manager_name") +
                                " | Balance: " + res.getDouble("balance") +
                                " | isBusiness: " + res.getBoolean("is_business") +
                                " | creditLimit: " + res.getDouble("credit_limit"));
                    }
                    if (!any) {
                        System.out.println("No " + (businessFlag ? "business" : "personal") + " checking accounts.");
                    }
                }
            } else {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, accountTypeInput.toUpperCase());
                    try (ResultSet res = statement.executeQuery()) {
                        boolean any = false;
                        while (res.next()) {
                            any = true;
                            System.out.println(res.getString("account_type") +
                                    " | #" + res.getInt("account_number") +
                                    " | Manager: " + res.getString("manager_name") +
                                    " | Balance: " + res.getDouble("balance"));
                        }
                        if (!any) System.out.println("No accounts for type: " + accountTypeInput);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    //Yearly profit in a specific account
    public void displayAnnualProfitOfSpecificAccount(int accountNumber) {
        String sql =
                "SELECT a.account_type, a.balance, ca.is_business, ma.monthly_payment, sa.deposit_amount " +
                        "FROM account a " +
                        "LEFT JOIN checking_account ca ON ca.account_number=a.account_number " +
                        "LEFT JOIN savings_account  sa ON sa.account_number=a.account_number " +
                        "LEFT JOIN mortgage_account ma ON ma.account_number=a.account_number " +
                        "WHERE a.account_number = ?";
        try (Connection connection = DB.get(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountNumber);
            try (ResultSet res = statement.executeQuery()) {
                if (!res.next()) { System.out.println("Account not found."); return; }
                String type = res.getString("account_type");
                double balance = res.getDouble("balance");
                boolean isBusiness = res.getObject("is_business") != null && res.getBoolean("is_business");
                Double monthly = (Double) res.getObject("monthly_payment");
                Double deposit = (Double) res.getObject("deposit_amount");

                double profit;
                if ("CHECKING".equals(type)) {
                    profit = balance * 0.02 * (isBusiness ? 1.10 : 1.0);
                } else if ("MORTGAGE".equals(type)) {
                    profit = (monthly == null ? 0 : monthly * 12) * 0.05;
                } else if ("SAVINGS".equals(type)) {
                    profit = (deposit == null ? 0 : deposit) * 0.03;
                } else profit = 0;

                System.out.println("Account #" + accountNumber + " | Annual Profit: " + profit);
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    //Total yearly profit
    public void displayTotalAnnualProfit() {
        String sql =
                "SELECT SUM(CASE " +
                        "  WHEN a.account_type='CHECKING' THEN a.balance*0.02*(CASE WHEN COALESCE(ca.is_business,false) THEN 1.10 ELSE 1 END) " +
                        "  WHEN a.account_type='MORTGAGE' THEN COALESCE(ma.monthly_payment,0)*12*0.05 " +
                        "  WHEN a.account_type='SAVINGS'  THEN COALESCE(sa.deposit_amount,0)*0.03 " +
                        "  ELSE 0 END) AS total_profit " +
                        "FROM account a " +
                        "LEFT JOIN checking_account ca ON ca.account_number=a.account_number " +
                        "LEFT JOIN savings_account  sa ON sa.account_number=a.account_number " +
                        "LEFT JOIN mortgage_account ma ON ma.account_number=a.account_number";
        try (Connection connection = DB.get(); Statement statement = connection.createStatement(); ResultSet res = statement.executeQuery(sql)) {
            res.next();
            System.out.println("Total annual profit of the bank: " + res.getDouble("total_profit"));
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    //Top profit checking account
    public void displayTopProfitCheckingAccount() {
        String sql =
                "SELECT a.account_number, a.balance, " +
                        "       (a.balance*0.02*(CASE WHEN COALESCE(ca.is_business,false) THEN 1.10 ELSE 1 END)) AS profit " +
                        "FROM account a JOIN checking_account ca ON ca.account_number=a.account_number " +
                        "WHERE a.account_type='CHECKING' " +
                        "ORDER BY profit DESC, a.account_number ASC LIMIT 1";
        try (Connection connection = DB.get(); Statement statement = connection.createStatement(); ResultSet res = statement.executeQuery(sql)) {
            if (res.next()) {
                System.out.println("Checking Account with highest profit: #" +
                        res.getInt("account_number") + " | Profit: " + res.getDouble("profit"));
            } else System.out.println("No checking accounts found.");
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    //Check business account (VIP preview)
    public void checkBusinessAccountProfit(int accountNumber) {
        String sql =
                "SELECT a.balance, ca.is_business FROM account a " +
                        "JOIN checking_account ca ON ca.account_number=a.account_number " +
                        "WHERE a.account_number=? AND a.account_type='CHECKING'";
        try (Connection connection = DB.get(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountNumber);
            try (ResultSet res = statement.executeQuery()) {
                if (!res.next()) {
                    System.out.println("Not a checking account or not found.");
                    return;
                }
                if (!res.getBoolean("is_business")) {
                    System.out.println("This is not a business account.");
                    return;
                }
                double balance = res.getDouble("balance");
                double potential = balance * 0.02 * 1.10;
                System.out.println("Account #" + accountNumber + " | Potential VIP-adjusted profit: " + potential);
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    //Management fees
    public void printManagementFees() {
        String sql =
                "SELECT a.account_number, a.account_type, ca.is_business, ma.original_mortgage_amount " +
                        "FROM account a " +
                        "LEFT JOIN checking_account ca ON ca.account_number=a.account_number " +
                        "LEFT JOIN mortgage_account ma ON ma.account_number=a.account_number " +
                        "ORDER BY a.account_number";
        double total = 0;
        try (Connection connection = DB.get(); Statement statement = connection.createStatement(); ResultSet res = statement.executeQuery(sql)) {
            while (res.next()) {
                String type = res.getString("account_type");
                double fee = 0;
                if ("CHECKING".equals(type) && res.getObject("is_business") != null && res.getBoolean("is_business")) {
                    fee = 1000.0;
                } else if ("MORTGAGE".equals(type)) {
                    Double original = (Double) res.getObject("original_mortgage_amount");
                    if (original != null) {
                        fee = original * 0.001;
                    }
                }
                if (fee > 0) {
                    System.out.println("Account #" + res.getInt("account_number") + " - Management Fee: " + fee + " ₪");
                    total += fee;
                }
            }
            System.out.println("CEO's annual bonus: " + total + " ₪");
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }
}
