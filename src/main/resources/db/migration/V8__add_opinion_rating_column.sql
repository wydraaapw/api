ALTER TABLE opinions
    ADD rating INTEGER;

ALTER TABLE opinions
    ALTER COLUMN rating SET NOT NULL;