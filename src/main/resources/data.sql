MERGE INTO mpa(name) KEY (name)
VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');
 -- ON CONFLICT (name) DO NOTHING;

Merge INTO genres (name) KEY (name)
VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');
--ON CONFLICT (name) DO NOTHING;

