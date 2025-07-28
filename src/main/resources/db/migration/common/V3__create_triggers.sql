
-- Assign ROLE_DUMMY on every new user
DELIMITER $$

CREATE TRIGGER `trg_after_insert_users_assign_default_role`
	AFTER INSERT ON `users`
	FOR EACH ROW
	BEGIN
		DECLARE dummy_role_id BIGINT;
		SELECT id INTO dummy_role_id FROM roles WHERE name = 'ROLE_DUMMY' LIMIT 1;
		
		INSERT INTO users_roles(user_id, role_id) 
		VALUES (NEW.id, dummy_role_id);
	END$$

DELIMITER ;


-- Prevent deletion of system roles 
DELIMITER $$

CREATE TRIGGER `trg_before_delete_roles_prevent_system_roles`
BEFORE DELETE ON roles
FOR EACH ROW
BEGIN
    IF OLD.name = 'ROLE_DUMMY' or OLD.name = 'ROLE_COORD' or OLD.name = 'ROLE_ADMIN' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'System role can''t be deleted';
    END IF;
END$$

DELIMITER ;
