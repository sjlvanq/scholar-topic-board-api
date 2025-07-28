INSERT IGNORE INTO courses (name, description) VALUES 
	("Example Course", "An example course living in our system!");

INSERT IGNORE INTO users (first_name, last_name, email, passw) VALUES 
	("Carol", "Coordinator", "coord@123.com", "$2a$12$OMg4BoWdFacvGIeadgpGV.qfBLqQIwp3ttCqBbZMkQDMz.AshDLpa");
INSERT IGNORE INTO users (first_name, last_name, email, passw) VALUES 
	("Madeleine", "Moderator", "moderator@123.com", "$2a$12$OMg4BoWdFacvGIeadgpGV.qfBLqQIwp3ttCqBbZMkQDMz.AshDLpa");
INSERT IGNORE INTO users (first_name, last_name, email, passw) VALUES 
	("Jesualdo", "Teacher", "teacher@123.com", "$2a$12$OMg4BoWdFacvGIeadgpGV.qfBLqQIwp3ttCqBbZMkQDMz.AshDLpa");
INSERT IGNORE INTO users (first_name, last_name, email, passw) VALUES 
	("Prometeo", "Student", "student@123.com", "$2a$12$OMg4BoWdFacvGIeadgpGV.qfBLqQIwp3ttCqBbZMkQDMz.AshDLpa");


INSERT IGNORE INTO users_roles(user_id, role_id) VALUES(
	(SELECT id FROM users WHERE email = "admin@123.com"),
	(SELECT id FROM roles WHERE name = "ROLE_ADMIN"));
INSERT IGNORE INTO users_roles(user_id, role_id) VALUES(
	(SELECT id FROM users WHERE email = "coord@123.com"),
	(SELECT id FROM roles WHERE name = "ROLE_COORD"));
INSERT IGNORE INTO users_roles(user_id, role_id) VALUES(
	(SELECT id FROM users WHERE email = "moderator@123.com"),
	(SELECT id FROM roles WHERE name = "ROLE_MODERATOR"));
INSERT IGNORE INTO users_roles(user_id, role_id) VALUES(
	(SELECT id FROM users WHERE email = "teacher@123.com"),
	(SELECT id FROM roles WHERE name = "ROLE_TEACHER"));
INSERT IGNORE INTO users_roles(user_id, role_id) VALUES(
	(SELECT id FROM users WHERE email = "student@123.com"),
	(SELECT id FROM roles WHERE name = "ROLE_STUDENT"));

INSERT IGNORE INTO users_courses(user_id, course_id) VALUES (
	(SELECT id FROM users WHERE email = "moderator@123.com"),
	(SELECT id FROM courses WHERE name = "Example Course"));
INSERT IGNORE INTO users_courses(user_id, course_id) VALUES (
	(SELECT id FROM users WHERE email = "student@123.com"),
	(SELECT id FROM courses WHERE name = "Example Course"));
INSERT IGNORE INTO users_courses(user_id, course_id) VALUES (
	(SELECT id FROM users WHERE email = "teacher@123.com"),
	(SELECT id FROM courses WHERE name = "Example Course"));

INSERT IGNORE INTO topics(author_id, course_id, title, body) VALUES
	((SELECT id FROM users WHERE email = "teacher@123.com"), 
	 (SELECT id FROM courses WHERE name = "Example Course"),
	 "Example Topic", "An example topic.");

INSERT IGNORE INTO replies(topic_id, author_id, body) VALUES
  ((SELECT id FROM topics WHERE title = "Example Topic"),
   (SELECT id FROM users WHERE email = "student@123.com"),
   "This is an example reply from the student.");
   
INSERT IGNORE INTO replies(topic_id, author_id, parent_id, body) VALUES
  ((SELECT id FROM topics WHERE title = "Example Topic"),
   (SELECT id FROM users WHERE email = "student@123.com"),
   LAST_INSERT_ID(),
   "This is a child reply.");

