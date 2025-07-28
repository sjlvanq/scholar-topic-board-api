INSERT IGNORE INTO roles (name, is_public) VALUES ("ROLE_DUMMY", FALSE);
INSERT IGNORE INTO roles (name, is_public) VALUES ("ROLE_ADMIN", FALSE);
INSERT IGNORE INTO roles (name, is_public) VALUES ("ROLE_COORD", FALSE);
INSERT IGNORE INTO roles (name, is_public) VALUES ("ROLE_MODERATOR", FALSE);
INSERT IGNORE INTO roles (name, is_public) VALUES ("ROLE_TEACHER", TRUE);
INSERT IGNORE INTO roles (name, is_public) VALUES ("ROLE_STUDENT", TRUE);

INSERT IGNORE INTO users (first_name, last_name, email, passw) VALUES 
	("Admin", "Admin", "admin@123.com", "$2a$12$OMg4BoWdFacvGIeadgpGV.qfBLqQIwp3ttCqBbZMkQDMz.AshDLpa");
