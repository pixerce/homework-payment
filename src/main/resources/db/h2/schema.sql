DROP TABLE payment_card IF EXISTS;
DROP TABLE card_company_transaction IF EXISTS;
DROP TABLE payment IF EXISTS;
DROP TABLE card IF EXISTS;

DROP TABLE payment_card_detail IF EXISTS;


CREATE TABLE payment (
  id         INTEGER IDENTITY PRIMARY KEY,
  tid        VARCHAR(20) NOT NULL,
  type     VARCHAR(7) NOT NULL,
  amount     INTEGER NOT NULL,
  vat         INTEGER ,
  status  VARCHAR(10) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP ,
  updated_at DATE
);

CREATE INDEX idx_payment_tid ON payment (tid);

CREATE TABLE card (
  id         LONG IDENTITY PRIMARY KEY,
  hash_key   VARCHAR NOT NULL,
  info       VARCHAR NOT NULL
);

CREATE INDEX idx_card_hash ON card (hash_key);

CREATE TABLE payment_card (
--   id LONG IDENTITY PRIMARY KEY,
  payment_id VARCHAR(20) NOT NULL,
  card_id     LONG NOT NULL
);

ALTER TABLE payment_card ADD CONSTRAINT fk_payment_card_payment FOREIGN KEY (payment_id) REFERENCES payment (id);
ALTER TABLE payment_card ADD CONSTRAINT fk_payment_card_card FOREIGN KEY (card_id) REFERENCES card (id);


CREATE TABLE payment_card_detail (
  id         LONG IDENTITY PRIMARY KEY,
  tid        VARCHAR(20) NOT NULL,
  card_tid   VARCHAR(20) NOT NULL,
  installment CHAR(2),
  approved_at DATE
);

CREATE INDEX idx_payment_card_detail_tid ON payment_card_detail (tid);

CREATE TABLE card_company_transaction (
  id LONG IDENTITY PRIMARY KEY,
  request CLOB NOT NULL
);
