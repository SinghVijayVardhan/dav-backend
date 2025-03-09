CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    role ENUM('member', 'librarian') NOT NULL,
    profile_pic VARCHAR(500)
);

INSERT INTO users (email, role) VALUES ('vijoybardhan3@gmail.com','librarian');
