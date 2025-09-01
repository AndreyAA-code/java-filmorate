MERGE into rating AS target
    USING (VALUES ('G'),
               ('PG'),
               ('PG-13'),
               ('R'),
               ('NC-17')
        ) AS source (name)
ON target.name = source.name
WHEN NOT MATCHED THEN
    INSERT (name) VALUES (source.name);

MERGE into genre AS target
    USING (VALUES ('Комедия'),
                  ('Драма'),
                  ('Мультфильм'),
                  ('Триллер'),
                  ('Документальный')
        ) AS source (name)
ON target.name = source.name
WHEN NOT MATCHED THEN
    INSERT (name) VALUES (source.name);

MERGE into FRIENDSHIP_STATUS AS target
    USING (VALUES ('Запрос'),
                  ('Подтверждена')
        ) AS source (status)
ON target.STATUS = source.status
WHEN NOT MATCHED THEN
    INSERT (STATUS) VALUES (source.status);