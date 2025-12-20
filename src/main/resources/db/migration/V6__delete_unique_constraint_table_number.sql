ALTER TABLE restaurant_tables
    ALTER COLUMN table_number SET NOT NULL;

ALTER TABLE restaurant_tables
    DROP CONSTRAINT uc_restaurant_tables_tablenumber;