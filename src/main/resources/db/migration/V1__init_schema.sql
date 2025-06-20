CREATE TABLE IF NOT EXISTS `users` (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    fullname VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO users (fullname, username, password)
VALUES
    ('Administrator', 'admin', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    ('Nguyen Van B', 'nguyenb', '$2a$10$KXJZ1S9sYQo6f3dJc7nX1uhDc2yQMeZ3P7nqzDkXL6Ku9N0E4Uu4W'),
    ('Nguyen Thanh Vinh', 'vinhnt', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy');
