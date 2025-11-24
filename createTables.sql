
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'account_type_enum') THEN
    CREATE TYPE account_type_enum AS ENUM ('CHECKING','SAVINGS','MORTGAGE');
  END IF;
END$$;

CREATE TABLE bank (
  bank_number INT PRIMARY KEY,
  bank_name   TEXT NOT NULL
);

CREATE TABLE client (
  client_id BIGSERIAL PRIMARY KEY,
  name      TEXT NOT NULL,
  rank      INT  NOT NULL CHECK (rank BETWEEN 0 AND 10)
);

CREATE TABLE account (
  account_number  INT PRIMARY KEY,
  bank_number     INT NOT NULL REFERENCES bank(bank_number) ON UPDATE CASCADE ON DELETE RESTRICT,
  manager_name    TEXT NOT NULL,
  balance         DOUBLE PRECISION NOT NULL DEFAULT 0,
  creation_date   TIMESTAMP NOT NULL DEFAULT NOW(),
  account_type    account_type_enum NOT NULL
);

CREATE TABLE checking_account (
  account_number INT PRIMARY KEY REFERENCES account(account_number) ON DELETE CASCADE,
  credit_limit   DOUBLE PRECISION NOT NULL,
  is_business    BOOLEAN NOT NULL
);

CREATE TABLE savings_account (
  account_number INT PRIMARY KEY REFERENCES account(account_number) ON DELETE CASCADE,
  deposit_amount DOUBLE PRECISION NOT NULL,
  years          INT NOT NULL CHECK (years BETWEEN 1 AND 30)
);

CREATE TABLE mortgage_account (
  account_number            INT PRIMARY KEY REFERENCES account(account_number) ON DELETE CASCADE,
  original_mortgage_amount  DOUBLE PRECISION NOT NULL,
  monthly_payment           DOUBLE PRECISION NOT NULL,
  years                     INT NOT NULL CHECK (years BETWEEN 1 AND 30)
);

CREATE TABLE account_client (
  account_number INT    NOT NULL REFERENCES account(account_number) ON DELETE CASCADE,
  client_id      BIGINT NOT NULL REFERENCES client(client_id)       ON DELETE CASCADE,
  PRIMARY KEY (account_number, client_id)
);

