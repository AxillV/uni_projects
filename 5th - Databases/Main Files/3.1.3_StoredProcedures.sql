-- 3.1.3.1
DROP PROCEDURE IF EXISTS insertNewDriver;
DELIMITER //
CREATE PROCEDURE insertNewDriver (AT CHAR(10), name VARCHAR(20), lname VARCHAR(20), salary FLOAT(7,2), license ENUM('A','B','C','D'), route ENUM('LOCAL', 'ABROAD'), experience TINYINT(4))
BEGIN
    DECLARE br_code INT(11);

    SELECT b.br_code
    INTO br_code
    FROM branch b JOIN worker w ON b.br_code = w.wrk_br_code JOIN driver d ON w.wrk_AT = d.drv_AT
    GROUP BY b.br_code
    ORDER BY count(*) ASC
    LIMIT 1;
    
    INSERT INTO worker VALUES
    (AT, name, lname, salary, br_code);

    INSERT INTO driver VALUES
    (AT, license, route, experience);
END//
DELIMITER ;


-- 3.1.3.2
DROP PROCEDURE IF EXISTS viewTripsInRange; 
DELIMITER //
CREATE PROCEDURE viewTripsInRange (br_code INT, departure_low DATETIME, departure_high DATETIME)
BEGIN
    SELECT b.br_code, t.tr_id, t.tr_cost as Cost, t.tr_maxseats as Maxseats, COUNT(r.res_tr_id) as Reservations, (t.tr_maxseats - COUNT(r.res_tr_id)) as 'Vacant_seats', CONCAT(w1.wrk_name, ' ', w1.wrk_lname) as Guide, CONCAT(w2.wrk_name, ' ', w2.wrk_lname) as Driver, t.tr_departure as Departure, t.tr_return as 'Return'
    FROM branch b JOIN trip t ON b.br_code = t.tr_br_code LEFT JOIN reservation r ON t.tr_id = r.res_tr_id
                  JOIN guide g ON t.tr_gui_AT = g.gui_AT JOIN worker w1 ON g.gui_AT = w1.wrk_AT
                  JOIN driver d ON t.tr_drv_AT = d.drv_AT JOIN worker w2 ON d.drv_AT = w2.wrk_AT
    WHERE b.br_code = br_code AND (t.tr_departure BETWEEN departure_low AND departure_high)
    GROUP BY t.tr_id;
END//
DELIMITER ;


/*
CALL viewTripsInRange(5, '2000-05-01 00:00:00', '2024-05-01 00:00:00');
CALL viewTripsInRange(5, '2000-05-01', '2022-01-12');
SELECT * FROM trip WHERE trip.tr_br_code = 5;
SELECT * FROM RESERVATION WHERE res_tr_id IN (2,18,23,24,27);
*/

-- 3.1.3.3
-- thewroume oti den uparxoun atoma me idio onoma
DROP PROCEDURE IF EXISTS deleteWorker;
DELIMITER //
CREATE PROCEDURE deleteWorker (name VARCHAR(20), lname VARCHAR(20))
BEGIN
    DECLARE adm_AT CHAR(10);

    SELECT wrk_AT INTO adm_AT
    FROM worker
    WHERE wrk_name = name AND wrk_lname = lname
    LIMIT 1;

    IF ((SELECT COUNT(mng_adm_AT) from manages WHERE mng_adm_AT = adm_AT) = 0) THEN
        DELETE FROM admin WHERE admin.adm_AT = adm_AT;
    ELSE
        SELECT "This admin is a branch manager. Deletion not allowed.";
    END IF;

END//
DELIMITER ;

/*
CALL deleteWorker('Afroksilanthios', 'Koutsantwnh');
'BBJYF6MIYR'

CALL deleteWorker('Giorgos', 'Giorgou');
'84QN23K978'
*/


/*SELECT wrk_AT 
    FROM worker
    WHERE wrk_name = 'Afroksilanthios' AND wrk_lname = 'Koutsantwnh'
    LIMIT 1,1;
*/


-- 3.1.3.4.1
DROP PROCEDURE IF EXISTS findOfferInRange;
DELIMITER //
CREATE PROCEDURE findOfferInRange(IN minAmount INT, IN maxAmount INT)
BEGIN
	SELECT rsof_name 'First Name', rsof_lname 'Last Name'
	FROM reservation_offers
	WHERE rsof_downpay BETWEEN minAmount AND maxAmount;
END//
DELIMITER ;


-- 3.1.3.4.2
DROP PROCEDURE IF EXISTS findInfoByLastName;
DELIMITER //
CREATE PROCEDURE findInfoByLastName(IN inputLName VARCHAR(20))
BEGIN
	DECLARE tempFName VARCHAR(20);
	DECLARE tempLName VARCHAR(20);
	DECLARE tempCode INT;
	DECLARE multipleFound INT;
	DECLARE CONTINUE HANDLER FOR 1172 SET multipleFound=1;

	SET multipleFound=0;

	SELECT r.rsof_name, r.rsof_lname, o.off_code 
	INTO tempFName, tempLName, tempCode
	FROM reservation_offers r 
	INNER JOIN offers o ON r.rsof_off_code=o.off_code
	WHERE rsof_lname LIKE inputLName;

	-- If more than 1 found, we need to select counts instead!
	IF (multipleFound=1) THEN
		SELECT o.off_code 'Offer Code', COUNT(*) 'Amount of Rsrv.'
		FROM reservation_offers r
		INNER JOIN offers o ON r.rsof_off_code=o.off_code
		WHERE r.rsof_lname LIKE inputLName
		GROUP BY o.off_code;
	ELSE
		SELECT tempFName 'First Name', tempLName 'Last Name', tempCode 'Offer Code';
	END IF;

END//
DELIMITER ;
