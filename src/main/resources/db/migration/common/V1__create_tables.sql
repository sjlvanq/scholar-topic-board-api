
-- Table that stores all users registered in the forum system
CREATE TABLE IF NOT EXISTS  users(
	id SMALLINT UNSIGNED AUTO_INCREMENT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(35) NOT NULL,
	email VARCHAR(255) UNIQUE NOT NULL,
	passw VARCHAR(255) NOT NULL,
	banned BOOLEAN DEFAULT FALSE,
	deleted BOOLEAN DEFAULT FALSE,
	
	CONSTRAINT pk_users PRIMARY KEY (id)
);

-- Table that represents forum boards, where each one is associated with a course
CREATE TABLE IF NOT EXISTS  courses(
	id SMALLINT UNSIGNED AUTO_INCREMENT,
	name VARCHAR(100) UNIQUE NOT NULL,
	description VARCHAR(500),
	closed BOOLEAN DEFAULT FALSE NOT NULL,
	
	CONSTRAINT pk_courses PRIMARY KEY (id)
);

-- Table that stores the topics created by users
CREATE TABLE IF NOT EXISTS topics(
	id MEDIUMINT UNSIGNED AUTO_INCREMENT,
	author_id SMALLINT UNSIGNED NOT NULL,
	course_id SMALLINT UNSIGNED NOT NULL,
	title VARCHAR(50) NOT NULL,
	creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	body TEXT NOT NULL,
	closed BOOLEAN DEFAULT FALSE,
	
	CONSTRAINT pk_topics PRIMARY KEY (id),
	
	CONSTRAINT fk_topics_author_id
		FOREIGN KEY (author_id) REFERENCES users(id)
			ON DELETE CASCADE ON UPDATE CASCADE,
			
	CONSTRAINT fk_topics_course_id
		FOREIGN KEY (course_id) REFERENCES courses(id)
			ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table that stores the replies to topics created by users
CREATE TABLE IF NOT EXISTS replies(
	id MEDIUMINT UNSIGNED AUTO_INCREMENT,
	parent_id MEDIUMINT UNSIGNED,
	topic_id MEDIUMINT UNSIGNED NOT NULL,
	author_id SMALLINT UNSIGNED, -- NOT NULL,
	creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	body TEXT NOT NULL,
	
	CONSTRAINT pk_replies PRIMARY KEY (id),
	
	CONSTRAINT fk_replies_topic_id
		FOREIGN KEY (topic_id) REFERENCES topics(id)
			ON DELETE CASCADE ON UPDATE CASCADE,
			
	CONSTRAINT fk_replies_author_id
		FOREIGN KEY (author_id) REFERENCES users(id)
			ON DELETE SET NULL ON UPDATE CASCADE
);

-- Table that defines available user roles (e.g., admin, student)
CREATE TABLE IF NOT EXISTS roles(
	id TINYINT UNSIGNED AUTO_INCREMENT,
	name VARCHAR(30) UNIQUE NOT NULL,
	is_public BOOLEAN NOT NULL DEFAULT FALSE,
	
	CONSTRAINT pk_roles PRIMARY KEY (id)
);
