--MPA_RATINGS
INSERT INTO mpa_ratings (name)
SELECT 'G' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name='G');

INSERT INTO mpa_ratings (name)
SELECT 'PG' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name='PG');

INSERT INTO mpa_ratings (name)
SELECT 'PG-13' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name='PG-13');

INSERT INTO mpa_ratings (name)
SELECT 'R' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name='R');

INSERT INTO mpa_ratings (name)
SELECT 'NC-17' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name='NC-17');

--GENRES
INSERT INTO genres (name)
SELECT 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='Комедия');

INSERT INTO genres (name)
SELECT 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='Драма');

INSERT INTO genres (name)
SELECT 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='Мультфильм');

INSERT INTO genres (name)
SELECT 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='Триллер');

INSERT INTO genres (name)
SELECT 'Документальный' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='Документальный');

INSERT INTO genres (name)
SELECT 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='Боевик');
/*
--USERS
INSERT INTO users(login, name, email, birthday) values
('xS5pYhwSCY','Alexis Bergstrom','Enola_McCullough13@gmail.com','1973-04-14');

--FILMS
INSERT INTO films(name, description, release_date, duration, mpa_id) values
('Mprpkn6rKSgRARY','EUnHlaEF3PtL3aDRtQ4MA2ufhJGeNoeKoEcHIh18CdAW0IkemJ','1962-08-09',
 '138','2');
*/
/*
--DIRECTORS
INSERT INTO directors (name)
SELECT 'sdfcvngm' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='sdfcvngm');

INSERT INTO directors (name)
SELECT 'safsdg' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='safsdg');

INSERT INTO directors (name)
SELECT 'sfddgh' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='sfddgh');

INSERT INTO directors (name)
SELECT 'fdhfgkhk' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='fdhfgkhk');

INSERT INTO directors (name)
SELECT 'ffjfgkghk' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='ffjfgkghk');

INSERT INTO directors (name)
SELECT 'hgkhjlkhj' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='hgkhjlkhj');
 */