-- Banks
INSERT INTO bank (bank_number, bank_name) VALUES
  (1, 'First National Bank'),
  (2, 'City Capital Bank');

-- Clients
INSERT INTO client (name, rank) VALUES
  ('Alice Cohen', 9),
  ('Ben Levi',    6),
  ('Dana Azulay', 8);

-- Accounts (one of each type)
INSERT INTO account (account_number, bank_number, manager_name, balance, creation_date, account_type) VALUES
  (1001, 1, 'Rachel Manager',  2500.00, CURRENT_DATE, 'CHECKING'),
  (2002, 2, 'Moshe Manager',  15000.00, CURRENT_DATE, 'SAVINGS'),
  (3003, 1, 'Noa Manager',  -120000.00, CURRENT_DATE, 'MORTGAGE');

-- Client Account (M:N, no role column)
INSERT INTO account_client (account_number, client_id) VALUES
  (1001, 1),
  (1001, 2),
  (2002, 1),
  (3003, 3);

-- Subtype details
INSERT INTO checking_account (account_number, credit_limit, is_business) VALUES
  (1001, 5000.00, FALSE);

INSERT INTO savings_account (account_number, deposit_amount, years) VALUES
  (2002, 12000.00, 3);

INSERT INTO mortgage_account (account_number, original_mortgage_amount, monthly_payment, years) VALUES
  (3003, 1300000.00, 5200.00, 25);
