CREATE TABLE IF NOT EXISTS users (
    uuid           VARCHAR(36) PRIMARY KEY,
    name           VARCHAR(50) NOT NULL,
    age            INTEGER,
    updated_at     TIMESTAMP          NOT NULL,
    created_at     TIMESTAMP          NOT NULL
);