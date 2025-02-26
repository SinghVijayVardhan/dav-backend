CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    category VARCHAR(255) ,
    publication_year INT,
    total_copies INT DEFAULT 1,
    remaining_copies INT DEFAULT 1
);
