-- for use sakila on user profile
GRANT ALL PRIVILEGES ON events_db.* TO 'user'@'%' IDENTIFIED BY '123_Stella';
FLUSH PRIVILEGES;
-- for use world on user profile
GRANT ALL PRIVILEGES ON booking_db.* TO 'user'@'%' IDENTIFIED BY '123_Stella';
FLUSH PRIVILEGES;
-- for use auth on user profile
GRANT ALL PRIVILEGES ON auth.* TO 'user'@'%' IDENTIFIED BY '123_Stella';
FLUSH PRIVILEGES;