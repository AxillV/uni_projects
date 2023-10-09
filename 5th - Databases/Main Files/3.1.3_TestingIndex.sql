/*
-- Procedure 1
USE travel_agency;
SET profiling=1;


CALL findOfferInRange(90, 90.1);
-- SELECT COUNT(*) FROM reservation_offers WHERE rsof_downpay BETWEEN 90 AND 110;


SET profiling=0;
CREATE INDEX downpay_index ON reservation_offers(rsof_downpay);
SET profiling=1;


CALL findOfferInRange(90, 90.1);
-- SELECT COUNT(*) FROM reservation_offers WHERE rsof_downpay BETWEEN 90 AND 110;


show profiles;
SET profiling=0;
DROP INDEX downpay_index ON reservation_offers;


-- EXPLAIN
-- SELECT rsof_name 'First Name', rsof_lname 'Last Name'
-- FROM reservation_offers
-- WHERE rsof_downpay BETWEEN 50 AND 70;

*/


-- Procedure 2
USE travel_agency;
SET profiling=1;

-- no index
CALL findInfoByLastName('French');

-- lname index
SET profiling=0;
CREATE INDEX lname_index ON reservation_offers(rsof_lname);
SET profiling=1;

CALL findInfoByLastName('French');

-- both index
SET profiling=0;
CREATE INDEX lname_off_code_index ON reservation_offers(rsof_lname, rsof_off_code);
SET profiling=1;

CALL findInfoByLastName('French');

-- only tuplet index
SET profiling=0;
DROP INDEX lname_index ON reservation_offers;
SET profiling=1;
CALL findInfoByLastName('French');

show profiles;
SET profiling=0;

DROP INDEX lname_off_code_index ON reservation_offers;

-- EXPLAIN
-- SELECT r.rsof_name, r.rsof_lname, o.off_code 
-- FROM reservation_offers r 
-- INNER JOIN offers o ON r.rsof_off_code=o.off_code
-- WHERE rsof_lname LIKE 'French';

-- EXPLAIN
-- SELECT o.off_code 'Offer Code', COUNT(*) 'Amount of Rsrv.'
-- FROM reservation_offers r
-- INNER JOIN offers o ON r.rsof_off_code=o.off_code
-- WHERE r.rsof_lname LIKE 'French'
-- GROUP BY o.off_code;
