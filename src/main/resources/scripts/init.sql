-- Initialize sequences
CREATE SEQUENCE USERS_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SONGS_SEQ START WITH 1 INCREMENT BY 1;

-- Creating trigger for users table created_at timestamp
CREATE OR REPLACE TRIGGER trg_users_created_at
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF :NEW.created_at IS NULL THEN
        :NEW.created_at := CURRENT_TIMESTAMP;
    END IF;
END;
/

-- Creating trigger for songs table created_at timestamp
CREATE OR REPLACE TRIGGER trg_songs_created_at
BEFORE INSERT ON songs
FOR EACH ROW
BEGIN
    IF :NEW.created_at IS NULL THEN
        :NEW.created_at := CURRENT_TIMESTAMP;
    END IF;
END;
/
