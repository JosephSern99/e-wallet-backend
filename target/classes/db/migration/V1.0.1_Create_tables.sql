-- Table: users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                       created_by VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       enabled BOOLEAN NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(255) NOT NULL,
                       updated_by VARCHAR(255),
                       updated_date TIMESTAMP WITH TIME ZONE,
                       username VARCHAR(255) NOT NULL
);


-- Table: wallets
CREATE TABLE wallets (
                         id BIGSERIAL PRIMARY KEY,
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                         created_by VARCHAR(255) NOT NULL,
                         updated_by VARCHAR(255),
                         updated_date TIMESTAMP WITH TIME ZONE,
                         balance NUMERIC(38, 2) NOT NULL,
                         wallet_number VARCHAR(255) NOT NULL UNIQUE,
                         user_id BIGINT NOT NULL UNIQUE,
                         FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE transactions (
                              id BIGINT PRIMARY KEY,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                              created_by VARCHAR(255) NOT NULL,
                              updated_by VARCHAR(255),
                              updated_date TIMESTAMP WITH TIME ZONE,
                              amount NUMERIC(38, 2) NOT NULL,
                              description VARCHAR(255),
                              recipient_wallet_number VARCHAR(255),
                              status VARCHAR(255) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
                              transaction_reference VARCHAR(255) NOT NULL UNIQUE,
                              type VARCHAR(255) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
                              wallet_id BIGINT NOT NULL,
                              FOREIGN KEY (wallet_id) REFERENCES wallets (id)
);

