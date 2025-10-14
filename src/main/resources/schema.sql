
drop table FILMS_LIKES;

drop table FRIENDS;

drop table GENRES_FILMS;

drop table GENRES;

drop table REVIEWS_USERS;

drop table REVIEWS;

drop table FILMS;

drop table MPA;

drop table USERS;

CREATE TABLE IF NOT EXISTS mpa (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS genres (
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
    review_id BIGINT NOT NULL REFERENCES reviews(review_id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    isUseful BIGINT
);
