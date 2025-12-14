ALTER TABLE restaurant_tables
    ADD table_type VARCHAR(255);

ALTER TABLE restaurant_tables
    ALTER COLUMN table_type SET NOT NULL;

ALTER TABLE restaurant_tables
    ALTER COLUMN seats DROP NOT NULL;

ALTER TABLE restaurant_tables
    ALTER COLUMN table_number DROP NOT NULL;