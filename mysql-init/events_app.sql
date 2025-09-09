-- DROP DATABASES if you want to start fresh (optional)
-- DROP DATABASE IF EXISTS events_db;
-- DROP DATABASE IF EXISTS auth;
-- DROP DATABASE IF EXISTS booking_db;

-- Create databases
CREATE DATABASE IF NOT EXISTS events_db CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS auth CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS booking_db CHARACTER SET utf8mb4;

-- ================================
-- Events DB - bands and events tables
-- ================================

USE events_db;

DROP TABLE IF EXISTS bands;
DROP TABLE IF EXISTS events;

CREATE TABLE bands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    description TEXT,
    formed_year INT,
    website VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO bands (name, genre, description, formed_year, website) VALUES
('Coldplay', 'Alternative Rock', 'British rock band known worldwide for their melodic sound and energetic concerts.', 1996, 'https://www.coldplay.com'),
('Imagine Dragons', 'Pop Rock', 'American pop rock band famous for their catchy hits and dynamic performances.', 2008, 'https://www.imaginedragonsmusic.com'),
('Foo Fighters', 'Rock', 'American rock band formed by Dave Grohl, known for powerful live shows and numerous hits.', 1994, 'https://www.foofighters.com'),
('The Rolling Stones', 'Rock', 'Legendary British rock band active since the 1960s.', 1962, 'https://www.rollingstones.com'),
('Adele', 'Pop/Soul', 'British singer-songwriter known for her powerful voice.', 2006, 'https://www.adele.com');

CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_code VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    location VARCHAR(255),
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    band_id BIGINT,
    event_status ENUM('CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    -- foreign key constraint omitted because of cross-db reference
);

INSERT INTO events (event_code, title, description, event_date, location, total_seats, available_seats, band_id, event_status) VALUES
('EVT1', 'Coldplay Live in Milan', 'Live concert by Coldplay at the Milan Arena.', '2025-09-15 20:00:00', 'Milan Arena', 5000, 4995, 1, 'CONFIRMED'),
('EVT2', 'Imagine Dragons Acoustic Set', 'Special acoustic performance by Imagine Dragons.', '2025-10-01 19:00:00', 'Rome Convention Center', 1200, 1199, 2, 'CONFIRMED'),
('EVT3', 'Foo Fighters Marathon Afterparty', 'Foo Fighters concert following the City Marathon event.', '2025-11-20 21:00:00', 'Central Park', 2500, 2499, 3, 'CONFIRMED'),
('EVT4', 'The Rolling Stones Anniversary Tour', 'Celebrating 60 years with The Rolling Stones.', '2025-12-05 20:30:00', 'London Stadium', 15000, 14996, 4, 'CONFIRMED'),
('EVT5', 'Adele Intimate Concert', 'Adele performing her greatest hits in an intimate setting.', '2025-12-20 20:00:00', 'Royal Albert Hall', 2500, 2498, 5, 'CONFIRMED');

-- ================================
-- Auth DB - users table
-- ================================

USE auth;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(45) NOT NULL,
  last_name VARCHAR(45) NOT NULL,
  email VARCHAR(45) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(45)
);

INSERT INTO users (first_name, last_name, email, password, role) VALUES
('Admin', 'Admin', 'admin@mail.it', '$2a$10$PbtyiAqu1TuZkJ9.C33Fs.nb4mZWVl6enwNiHZfS7BTEndlr0cMgq', 'ADMIN'),
('Mario', 'Rossi', 'mario.rossi@example.com', '$2a$10$PbtyiAqu1TuZkJ9.C33Fs.nb4mZWVl6enwNiHZfS7BTEndlr0cMgq', 'USER'),
('Anna', 'Bianchi', 'anna.bianchi@example.com', '$2a$10$PbtyiAqu1TuZkJ9.C33Fs.nb4mZWVl6enwNiHZfS7BTEndlr0cMgq', 'USER'),
('Luca', 'Verdi', 'luca.verdi@example.com', '$2a$10$PbtyiAqu1TuZkJ9.C33Fs.nb4mZWVl6enwNiHZfS7BTEndlr0cMgq', 'USER'),
('Giulia', 'Neri', 'giulia.neri@example.com', '$2a$10$PbtyiAqu1TuZkJ9.C33Fs.nb4mZWVl6enwNiHZfS7BTEndlr0cMgq', 'USER'),
('Admin', 'User', 'admin@example.com', '$2a$10$PbtyiAqu1TuZkJ9.C33Fs.nb4mZWVl6enwNiHZfS7BTEndlr0cMgq', 'ADMIN');

-- ================================
-- Booking DB - bookings table
-- ================================

USE booking_db;

DROP TABLE IF EXISTS bookings;

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    seats_reserved INT NOT NULL,
    status ENUM('CONFIRMED', 'CANCELLED', 'USER_CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    booking_code VARCHAR(20) NOT NULL,
    booked_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO bookings (event_id, user_id, seats_reserved, status, booking_code) VALUES
(1, 1, 2, 'CONFIRMED', 'EVT1-00001'),
(2, 2, 1, 'CONFIRMED', 'EVT2-00001'),
(3, 1, 1, 'CANCELLED', 'EVT3-00001'),
(4, 3, 4, 'CONFIRMED', 'EVT4-00001'),
(5, 4, 2, 'CONFIRMED', 'EVT5-00001'),
(1, 5, 3, 'CONFIRMED', 'EVT1-00002');
