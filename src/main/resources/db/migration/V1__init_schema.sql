CREATE TABLE IF NOT EXISTS `users`
(
    user_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    department_id BIGINT(20) NOT NULL,
    fullname VARCHAR(255) NOT NULL,
    katakana VARCHAR(255),
    birthdate DATE,
    email VARCHAR(255) NOT NULL,
    telephone VARCHAR(50),
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY(`user_id`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO users (department_id, fullname, katakana, birthdate, email, telephone, username, password)
VALUES (1, 'Administrator', 'アドミン', '1990-01-01', 'admin@luvina.net', '0901234567', 'admin',
        '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
       (2, 'Nguyen Van B', 'グエンバンビー', '1992-05-12', 'b@luvina.com', '0902233445', 'nguyenb',
        '$2a$10$KXJZ1S9sYQo6f3dJc7nX1uhDc2yQMeZ3P7nqzDkXL6Ku9N0E4Uu4W'),
       (2, 'Nguyen Thanh Vinh', 'グエンタインヴィン', '1998-09-30', 'vinhnt2@gmail.com', '0911122233', 'vinhnt',
        '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy');

CREATE TABLE IF NOT EXISTS `departments`
(
    department_id bigint(20) NOT NULL AUTO_INCREMENT,
    department_name VARCHAR(50) NOT NULL,
    PRIMARY KEY(`department_id`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO departments (department_name)
VALUES ('Phòng DEV1'),
       ('Phòng DEV2'),
       ('Phòng DEV3'),
       ('Phòng DEV5'),
       ('Phòng DEV6'),
       ('Phòng DEV7'),
       ('Phòng DEV8'),
       ('Phòng DEV9'),
       ('Phòng QAT'),
       ('Phòng MKT');

ALTER TABLE `users`
    ADD CONSTRAINT `fk_users_department_id`
        FOREIGN KEY (`department_id`)
            REFERENCES `departments` (`department_id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

CREATE TABLE certifications
(
    certification_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    certification_name  VARCHAR(50) NOT NULL,
    certification_level INT         NOT NULL
);

INSERT INTO certifications (certification_id, certification_name, certification_level)
VALUES (1, 'JLPT N1', 1),
       (2, 'JLPT N2', 2),
       (3, 'JLPT N3', 3),
       (4, 'JLPT N4', 4),
       (5, 'JLPT N5', 5);

CREATE TABLE user_certification
(
    user_certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT NOT NULL,
    certification_id          BIGINT NOT NULL,
    start_date                DATE   NOT NULL,
    end_date                  DATE   NOT NULL,
    score                     DECIMAL,

    CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_certification
        FOREIGN KEY (certification_id) REFERENCES certifications (certification_id)
);

INSERT INTO user_certification (user_id, certification_id, start_date, end_date, score)
VALUES (1, 1, '2022-01-01', '2022-06-01', 150.0),
       (1, 2, '2023-01-01', '2023-06-01', 160.0),
       (2, 3, '2021-03-01', '2021-08-01', 140.0),
       (2, 4, '2022-04-01', '2022-09-01', 145.0),
       (3, 1, '2020-05-01', '2020-10-01', 155.0),
       (3, 5, '2021-02-01', '2021-07-01', 165.0);
