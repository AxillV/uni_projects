USE travel_agency;

DROP TABLE IF EXISTS log;
CREATE TABLE log(
lg_tableName VARCHAR(15),
lg_actionName VARCHAR(10),
lg_actionNum INT(100) NOT NULL AUTO_INCREMENT,
lg_wrk_lname VARCHAR(20) DEFAULT 'unknown' NOT NULL,
lg_timeofchange DATETIME NOT NULL,

PRIMARY KEY (lg_actionNum)
);


DROP TABLE IF EXISTS currentUser;
CREATE TABLE currentUser(
name VARCHAR(20) DEFAULT 'null' NOT NULL
);

-- TRIGGER 1---------------------------
-- TRIP--------------------------------

DELIMITER $
DROP TRIGGER IF EXISTS logTripInsert;
CREATE TRIGGER logTripInsert
AFTER INSERT ON trip
FOR EACH ROW
    INSERT INTO log VALUES ('trip', 'Insert', NULL, (SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;


DELIMITER $
DROP TRIGGER IF EXISTS logTripUpdate;
CREATE TRIGGER logTripUpdate
AFTER UPDATE ON trip
FOR EACH ROW
    INSERT INTO log VALUES ('trip', 'Update', NULL, (SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;

DELIMITER $
DROP TRIGGER IF EXISTS logTripDelete;
CREATE TRIGGER logTripDelete
AFTER DELETE ON trip
FOR EACH ROW
    INSERT INTO log VALUES ('trip', 'Delete', NULL, (SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;


-- RESERVATION--------------------------------

DELIMITER $
DROP TRIGGER IF EXISTS logReservationInsert;
CREATE TRIGGER logReservationInsert
AFTER INSERT ON reservation
FOR EACH ROW
    INSERT INTO log VALUES ('reservation', 'Insert', NULL, (SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;

DELIMITER $
DROP TRIGGER IF EXISTS logReservationUpdate;
CREATE TRIGGER logReservationUpdate
AFTER UPDATE ON reservation
FOR EACH ROW
    INSERT INTO log VALUES ('reservation', 'Update', NULL, (SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;

DELIMITER $
DROP TRIGGER IF EXISTS logReservationDelete;
CREATE TRIGGER logReservationDelete
AFTER DELETE ON reservation
FOR EACH ROW
    INSERT INTO log VALUES ('reservation', 'Delete', NULL, (SELECT name FROM currentUser), CURRENT_TIMESTAMP)$
DELIMITER ;

-- EVENT--------------------------------

DELIMITER $
DROP TRIGGER IF EXISTS logEventInsert;
CREATE TRIGGER logEventInsert
AFTER INSERT ON event
FOR EACH ROW
    INSERT INTO log VALUES ('Event', 'Insert', NULL, (SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;

DELIMITER $
DROP TRIGGER IF EXISTS logEventUpdate;
CREATE TRIGGER logEventUpdate
AFTER UPDATE ON event
FOR EACH ROW
    INSERT INTO log VALUES ('Event', 'Update', NULL, (SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;

DELIMITER $
DROP TRIGGER IF EXISTS logEventDelete;
CREATE TRIGGER logEventDelete
AFTER DELETE ON event
FOR EACH ROW
    INSERT INTO log VALUES ('Event', 'Delete', NULL,(SELECT name FROM currentUser), CURRENT_TIMESTAMP)$
DELIMITER ;


-- DESTINATION--------------------------------

DELIMITER $
DROP TRIGGER IF EXISTS logDestinationInsert;
CREATE TRIGGER logDestinationInsert
AFTER INSERT ON destination
FOR EACH ROW
    INSERT INTO log VALUES ('destination', 'Insert', NULL, (SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;

DELIMITER $
DROP TRIGGER IF EXISTS logDestinationUpdate;
CREATE TRIGGER logDestinationUpdate
AFTER UPDATE ON destination
FOR EACH ROW
    INSERT INTO log VALUES ('destination', 'Update', NULL, (SELECT name FROM currentUser), CURRENT_TIMESTAMP)$
DELIMITER ;
SELECT @name

DELIMITER $
DROP TRIGGER IF EXISTS logDestinationDelete;
CREATE TRIGGER logDestinationDelete
AFTER DELETE ON destination
FOR EACH ROW
    INSERT INTO log VALUES ('destination', 'Delete', NULL, (SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;


-- TRAVEL_TO--------------------------------

DELIMITER $
DROP TRIGGER IF EXISTS logTravel_toInsert;
CREATE TRIGGER logTravel_toInsert
AFTER INSERT ON travel_to
FOR EACH ROW
    INSERT INTO log VALUES ('travel_to', 'Insert', NULL,(SELECT name FROM currentUser) , CURRENT_TIMESTAMP)$
DELIMITER ;

DELIMITER $
DROP TRIGGER IF EXISTS logTravel_toUpdate;
CREATE TRIGGER logTravel_toUpdate
AFTER UPDATE ON travel_to
FOR EACH ROW
    INSERT INTO log VALUES ('travel_to', 'Update', NULL, (SELECT name FROM currentUser), CURRENT_TIMESTAMP)$
DELIMITER ;

DELIMITER $
DROP TRIGGER IF EXISTS logTravel_toDelete;
CREATE TRIGGER logTravel_toDelete
AFTER DELETE ON travel_to
FOR EACH ROW
    INSERT INTO log VALUES ('travel_to', 'Delete', NULL, (SELECT name FROM currentUser), CURRENT_TIMESTAMP)$
DELIMITER ;


-- TRIGGER 2------------



DROP TRIGGER IF EXISTS noChangeAfterReserve;
DELIMITER $
CREATE TRIGGER noChangeAfterReserve BEFORE UPDATE ON trip
FOR EACH ROW
BEGIN
	DECLARE reservationCount INT;
	SET reservationCount=0;
	SELECT COUNT(*) INTO reservationCount
    FROM reservation
	INNER JOIN trip ON NEW.tr_id=res_tr_id
    WHERE NEW.tr_id=res_tr_id;
	IF (reservationCount>0 AND (NEW.tr_cost!=OLD.tr_cost OR NEW.tr_departure!=OLD.tr_departure OR NEW.tr_return!=OLD.tr_return))
	THEN SIGNAL SQLSTATE VALUE '45000'
	SET MESSAGE_TEXT = 'You cannot change the departure date, return date or the cost after reservations are made!';
	END IF;
END$
DELIMITER ;


-- TRIGGER 3


DROP TRIGGER IF EXISTS noPayCuts;
DELIMITER $
CREATE TRIGGER noPayCuts BEFORE UPDATE ON worker
FOR EACH ROW
BEGIN
	IF (NEW.wrk_salary<OLD.wrk_salary)
	THEN SIGNAL SQLSTATE VALUE '45000'
	SET MESSAGE_TEXT = 'You cannot decrease a workers salary!';
	END IF;
END$
DELIMITER ;

