DELETE FROM users;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
INSERT INTO users (email, login, name, birthday)
VALUES ('test@test.ru', 'tester', 'Andrey', '1990-08-08');