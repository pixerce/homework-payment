
INSERT INTO payment VALUES (1, 'T0000000000000000001', 'PAYMENT', 11000, 1000, 'DN', now(), now());
INSERT INTO payment VALUES (2, 'T0000000000000000001', 'CANCEL', 11000, 1000, 'DN', now(), now());

INSERT INTO card VALUES (1, 'xxxxxxx', 'yyyyyyyyyy');

INSERT INTO payment_card_detail VALUES (1, 'T0000000000000000001', '1', '0', now());
INSERT INTO payment_card_detail VALUES (2, 'T0000000000000000001', '2', '00', now());


INSERT INTO payment_card VALUES (1, 1);

INSERT INTO payment VALUES (3, 'T0000000000000000002', 'PAYMENT', 11000, 1000, 'DN', now(), now());

INSERT INTO card VALUES (2, 'b6dd097e5f5f68d06f24ba34e515afae81f619ecf286e9733a0dab85aba1e954', 'gu3iyhkdRbdm3q+GJooT9Vknoiwsyu25LCbGEdbDCdUoUZBlTt7pu+K2YKOkpcU6');

INSERT INTO payment_card VALUES (3, 2);

INSERT INTO payment_card_detail VALUES (3, 'T0000000000000000002', '1', '0', now());

/*
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

CREATE TABLE card (
  id         LONG IDENTITY PRIMARY KEY,
  hash_key   VARCHAR NOT NULL,
  info       VARCHAR NOT NULL
);

CREATE TABLE payment_card (
  payment_id VARCHAR(20) NOT NULL,
  card_id     LONG NOT NULL
);
*/

/*INSERT INTO vets VALUES (1, 'James', 'Carter');
INSERT INTO vets VALUES (3, 'Linda', 'Douglas');
INSERT INTO vets VALUES (4, 'Rafael', 'Ortega');
INSERT INTO vets VALUES (5, 'Henry', 'Stevens');
INSERT INTO vets VALUES (6, 'Sharon', 'Jenkins');

INSERT INTO specialties VALUES (1, 'radiology');
INSERT INTO specialties VALUES (2, 'surgery');
INSERT INTO specialties VALUES (3, 'dentistry');

INSERT INTO vet_specialties VALUES (2, 1);
INSERT INTO vet_specialties VALUES (3, 2);
INSERT INTO vet_specialties VALUES (3, 3);
INSERT INTO vet_specialties VALUES (4, 2);
INSERT INTO vet_specialties VALUES (5, 1);

INSERT INTO types VALUES (1, 'cat');
INSERT INTO types VALUES (2, 'dog');
INSERT INTO types VALUES (3, 'lizard');
INSERT INTO types VALUES (4, 'snake');
INSERT INTO types VALUES (5, 'bird');
INSERT INTO types VALUES (6, 'hamster');

INSERT INTO owners VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT INTO owners VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT INTO owners VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT INTO owners VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT INTO owners VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT INTO owners VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT INTO owners VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT INTO owners VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT INTO owners VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT INTO owners VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT INTO pets VALUES (1, 'Leo', '2010-09-07', 1, 1);
INSERT INTO pets VALUES (2, 'Basil', '2012-08-06', 6, 2);
INSERT INTO pets VALUES (3, 'Rosy', '2011-04-17', 2, 3);
INSERT INTO pets VALUES (4, 'Jewel', '2010-03-07', 2, 3);
INSERT INTO pets VALUES (5, 'Iggy', '2010-11-30', 3, 4);
INSERT INTO pets VALUES (6, 'George', '2010-01-20', 4, 5);
INSERT INTO pets VALUES (7, 'Samantha', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (8, 'Max', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (9, 'Lucky', '2011-08-06', 5, 7);
INSERT INTO pets VALUES (10, 'Mulligan', '2007-02-24', 2, 8);
INSERT INTO pets VALUES (11, 'Freddy', '2010-03-09', 5, 9);
INSERT INTO pets VALUES (12, 'Lucky', '2010-06-24', 2, 10);
INSERT INTO pets VALUES (13, 'Sly', '2012-06-08', 1, 10);

INSERT INTO visits VALUES (1, 7, '2013-01-01', 'rabies shot');
INSERT INTO visits VALUES (2, 8, '2013-01-02', 'rabies shot');
INSERT INTO visits VALUES (3, 8, '2013-01-03', 'neutered');
INSERT INTO visits VALUES (4, 7, '2013-01-04', 'spayed');
*/