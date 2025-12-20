ALTER TABLE restaurant_tables
    ADD is_active BOOLEAN;

ALTER TABLE restaurant_tables
    ALTER COLUMN is_active SET NOT NULL;