INSERT INTO mpa (name)
VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17')
ON CONFLICT (name) DO NOTHING;

INSERT INTO genres (name)
VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик')
ON CONFLICT (name) DO NOTHING;