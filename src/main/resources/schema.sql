
DROP table IF EXISTS user_events;
DROP table IF EXISTS reviews_users;
DROP table IF EXISTS reviews;
DROP table IF EXISTS films_likes;
DROP table IF EXISTS friends;
DROP table IF EXISTS genres_films;
DROP table IF EXISTS directors_films;
DROP table IF EXISTS films;
DROP table IF EXISTS users;
DROP table IF EXISTS mpa;
DROP table IF EXISTS genres;
DROP table IF EXISTS directors;

CREATE TABLE IF NOT EXISTS mpa (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS genres (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL UNIQUE
    );

    CREATE TABLE IF NOT EXISTS directors (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name VARCHAR(40) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS films (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(40),
    description VARCHAR(200),
    release_date DATE CHECK (release_date >= '1895-12-28'),
    duration BIGINT NOT NULL,
    mpa_id BIGINT REFERENCES mpa(id)
    );

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(40) NOT NULL UNIQUE,
    login VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(40) NOT NULL,
    birthday DATE NOT NULL CHECK (birthday <= CURRENT_DATE)
    );

CREATE TABLE IF NOT EXISTS genres_films (
    genre_id BIGINT NOT NULL REFERENCES genres(id),
    film_id BIGINT NOT NULL REFERENCES films(id),
    PRIMARY KEY (genre_id, film_id)
    );

CREATE TABLE IF NOT EXISTS films_likes (
    user_id BIGINT NOT NULL REFERENCES users(id),
    film_id BIGINT NOT NULL REFERENCES films(id),
    PRIMARY KEY (user_id, film_id)
    );

CREATE TABLE IF NOT EXISTS friends (
    user_id BIGINT NOT NULL REFERENCES users(id),
    friend_id BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (user_id, friend_id)
    );

CREATE TABLE IF NOT EXISTS reviews (
    review_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content VARCHAR(200),
    isPositive BOOL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    film_id BIGINT NOT NULL REFERENCES films(id)
);

CREATE TABLE IF NOT EXISTS reviews_users (
    review_id BIGINT NOT NULL REFERENCES reviews(review_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    useful BIGINT,
    PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS directors_films (
    director_id BIGINT NOT NULL REFERENCES directors(id) ON DELETE CASCADE,
    film_id BIGINT NOT NULL REFERENCES films(id),
    PRIMARY KEY (director_id, film_id)
);

CREATE TABLE IF NOT EXISTS user_events (
    eventId BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    userId BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    entityId BIGINT NOT NULL,
    eventType ENUM ('LIKE', 'REVIEW', 'FRIEND'),
    operation ENUM ('REMOVE', 'ADD', 'UPDATE'),
    timestamp BIGINT NOT NULL
);
