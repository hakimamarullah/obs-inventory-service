INSERT INTO item (created_by, created_date, name, price, updated_by, updated_date, version, id) VALUES ('SYSTEM', CURRENT_TIMESTAMP, 'Pen', 5, 'SYSTEM', CURRENT_TIMESTAMP, 0, NEXT VALUE FOR item_seq);

INSERT INTO item (created_by, created_date, name, price, updated_by, updated_date, version, id) VALUES ('SYSTEM', CURRENT_TIMESTAMP, 'Book', 10, 'SYSTEM', CURRENT_TIMESTAMP, 0, NEXT VALUE FOR item_seq);

INSERT INTO item (created_by, created_date, name, price, updated_by, updated_date, version, id) VALUES ('SYSTEM', CURRENT_TIMESTAMP, 'Bag', 30, 'SYSTEM', CURRENT_TIMESTAMP, 0, NEXT VALUE FOR item_seq);

INSERT INTO item (created_by, created_date, name, price, updated_by, updated_date, version, id) VALUES ('SYSTEM', CURRENT_TIMESTAMP, 'Pencil', 3, 'SYSTEM', CURRENT_TIMESTAMP, 0, NEXT VALUE FOR item_seq);

INSERT INTO item (created_by, created_date, name, price, updated_by, updated_date, version, id) VALUES ('SYSTEM', CURRENT_TIMESTAMP, 'Shoe', 45, 'SYSTEM', CURRENT_TIMESTAMP, 0, NEXT VALUE FOR item_seq);

INSERT INTO item (created_by, created_date, name, price, updated_by, updated_date, version, id) VALUES ('SYSTEM', CURRENT_TIMESTAMP, 'Box', 5, 'SYSTEM', CURRENT_TIMESTAMP, 0, NEXT VALUE FOR item_seq);

INSERT INTO item (created_by, created_date, name, price, updated_by, updated_date, version, id) VALUES ('SYSTEM', CURRENT_TIMESTAMP, 'Cap', 25, 'SYSTEM', CURRENT_TIMESTAMP, 0, NEXT VALUE FOR item_seq);



-- Inventory
INSERT INTO inventory (id, item_id, qty, type, created_by, created_date, updated_by, updated_date, version) VALUES (NEXT VALUE FOR inventory_seq, 1, 5, 'T', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);
INSERT INTO inventory (id, item_id, qty, type, created_by, created_date, updated_by, updated_date, version) VALUES (NEXT VALUE FOR inventory_seq, 2, 10, 'T', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);
INSERT INTO inventory (id, item_id, qty, type, created_by, created_date, updated_by, updated_date, version) VALUES (NEXT VALUE FOR inventory_seq, 3, 30, 'T', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);
INSERT INTO inventory (id, item_id, qty, type, created_by, created_date, updated_by, updated_date, version) VALUES (NEXT VALUE FOR inventory_seq, 4, 3, 'T', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);
INSERT INTO inventory (id, item_id, qty, type, created_by, created_date, updated_by, updated_date, version) VALUES (NEXT VALUE FOR inventory_seq, 5, 45, 'T', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);
INSERT INTO inventory (id, item_id, qty, type, created_by, created_date, updated_by, updated_date, version) VALUES (NEXT VALUE FOR inventory_seq, 6, 5, 'T', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);
INSERT INTO inventory (id, item_id, qty, type, created_by, created_date, updated_by, updated_date, version) VALUES (NEXT VALUE FOR inventory_seq, 7, 25, 'T', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);
INSERT INTO inventory (id, item_id, qty, type, created_by, created_date, updated_by, updated_date, version) VALUES (NEXT VALUE FOR inventory_seq, 4, 7, 'T', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);
INSERT INTO inventory (id, item_id, qty, type, created_by, created_date, updated_by, updated_date, version) VALUES (NEXT VALUE FOR inventory_seq, 5, 10, 'W', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);
