USE travel_agency;

CREATE TABLE it (
it_AT CHAR(10) NOT NULL,
it_password VARCHAR(20) DEFAULT 'password' NOT NULL,
it_date_start DATETIME NOT NULL,
it_date_end DATETIME,

PRIMARY KEY (it_AT),

CONSTRAINT it_wrk_FK
FOREIGN KEY (it_AT)
REFERENCES worker(wrk_AT)
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE offers (
off_code INT(11) NOT NULL AUTO_INCREMENT,
off_start_date DATETIME NOT NULL,
off_end_date DATETIME NOT NULL,
off_cost FLOAT(7,2) NOT NULL,
off_dst INT(11) NOT NULL,

PRIMARY KEY(off_code),

CONSTRAINT off_dst_FK
FOREIGN KEY (off_dst)
REFERENCES destination(dst_id)
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE reservation_offers (
rsof_code INT(11) NOT NULL AUTO_INCREMENT,
rsof_name VARCHAR(20) DEFAULT 'unknown' NOT NULL,
rsof_lname VARCHAR(20) DEFAULT 'unknown' NOT NULL,
rsof_off_code INT(11) NOT NULL,
rsof_downpay FLOAT(7,2) NOT NULL,

PRIMARY KEY(rsof_code),

CONSTRAINT rsof_off_FK
FOREIGN KEY (rsof_off_code)
REFERENCES offers(off_code)
ON UPDATE CASCADE ON DELETE CASCADE
);



INSERT INTO worker VALUES
('ODQKWLEIDK', 'John', 'root', 1000.00, 3),
('ODQKX1EIDK', 'Vasileios', 'Bardakis', 1000.00, 3),
('VDQKWLZIDK', 'Xaris', 'Xallas', 1000.00, 3),
('TRQKDLEIDK', 'Axilleas', 'Villiotis', 1000.00, 3);

INSERT INTO it VALUES
('ODQKWLEIDK', 'root','2015-03-25 00:00:00',null),
('ODQKX1EIDK', 'kwdikos','2016-03-23 00:00:00',null),
('VDQKWLZIDK', 'xalvas','2017-08-21 00:00:00',null),
('TRQKDLEIDK', 'eveonline','2013-07-16 00:00:00',null);


INSERT INTO offers VALUES
(NULL, '2023-01-01', '2023-12-31', 399.99, 20),
(NULL, '2023-01-01', '2023-12-31', 999.99, 4),
(NULL, '2023-01-01', '2023-12-31', 1299.99, 6);