
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
