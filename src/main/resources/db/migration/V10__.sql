ALTER TABLE notifications
    ADD is_read BOOLEAN;

ALTER TABLE notifications
    ALTER COLUMN is_read SET NOT NULL;