DROP DATABASE IF EXISTS travel_agency;
CREATE DATABASE travel_agency;
USE travel_agency;

CREATE TABLE branch (
br_code INT(11) NOT NULL AUTO_INCREMENT,
br_street VARCHAR(30) NOT NULL,
br_num INT(4) NOT NULL,
br_city VARCHAR(30) NOT NULL,

PRIMARY KEY(br_code)
);

CREATE TABLE phones(
ph_br_code INT(11) NOT NULL,
ph_number CHAR(10) NOT NULL,

PRIMARY KEY(ph_br_code,ph_number),

CONSTRAINT ph_br_FK
FOREIGN KEY (ph_br_code)
REFERENCES branch(br_code)
ON DELETE CASCADE ON UPDATE CASCADE
);
 
CREATE TABLE worker (
wrk_AT CHAR(10) NOT NULL,
wrk_name VARCHAR(20) DEFAULT 'unknown' NOT NULL,
wrk_lname VARCHAR(20) DEFAULT 'unknown' NOT NULL,
wrk_salary FLOAT(7,2) NOT NULL,
wrk_br_code INT(11) NOT NULL, 

PRIMARY KEY (wrk_AT),

CONSTRAINT wrk_br_code_fk 
FOREIGN KEY (wrk_br_code) 
REFERENCES branch(br_code) 
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE driver (
drv_AT CHAR(10) NOT NULL,
drv_license ENUM('A', 'B', 'C', 'D') NOT NULL,
drv_route ENUM('LOCAL', 'ABROAD') NOT NULL,
drv_experience TINYINT(4) NOT NULL,

PRIMARY KEY(drv_AT),

CONSTRAINT drv_AT_FK 
FOREIGN KEY (drv_AT) 
REFERENCES worker(wrk_AT) 
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE admin (
adm_AT CHAR(10) NOT NULL,
adm_type ENUM('LOGISTICS', 'ADMINISTRATIVE', 'ACCOUNTING') NOT NULL,
adm_diploma VARCHAR(200) NOT NULL,

PRIMARY KEY(adm_AT),

CONSTRAINT adm_AT_FK 
FOREIGN KEY (adm_AT) 
REFERENCES worker(wrk_AT) 
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE guide (
gui_AT CHAR(10) NOT NULL,
gui_cv TEXT NOT NULL,

PRIMARY KEY(gui_AT),

CONSTRAINT gui_AT_FK 
FOREIGN KEY (gui_AT) 
REFERENCES worker(wrk_AT) 
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE manages (
mng_adm_AT char(10) NOT NULL,
mng_br_code int(11) NOT NULL,

PRIMARY KEY (mng_adm_AT, mng_br_code),

CONSTRAINT mng_adm_AT_FK
FOREIGN KEY (mng_adm_AT)
REFERENCES admin(adm_AT)
ON UPDATE CASCADE ON DELETE CASCADE,

CONSTRAINT mng_br_code_FK
FOREIGN KEY (mng_br_code)
REFERENCES branch(br_code)
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE languages (
lng_gui_AT char(10) NOT NULL,
lng_language VARCHAR(30) NOT NULL,

PRIMARY KEY (lng_gui_AT,lng_language),

CONSTRAINT lng_gui_AT_FK 
FOREIGN KEY(lng_gui_AT)
REFERENCES guide(gui_AT)
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE trip (
tr_id INT(11) NOT NULL AUTO_INCREMENT,
tr_departure DATETIME NOT NULL,
tr_return DATETIME NOT NULL,
tr_maxseats TINYINT(4) NOT NULL,
tr_cost FLOAT(7,2) NOT NULL,
tr_br_code INT(11) NOT NULL,
tr_gui_AT CHAR(10) NOT NULL,
tr_drv_AT CHAR(10) NOT NULL,

PRIMARY KEY (tr_id),

CONSTRAINT tr_br_code_FK 
FOREIGN KEY(tr_br_code)
REFERENCES branch(br_code)
ON UPDATE CASCADE ON DELETE CASCADE,

CONSTRAINT tr_gui_AT_FK 
FOREIGN KEY(tr_gui_AT)
REFERENCES guide(gui_AT)
ON UPDATE CASCADE ON DELETE CASCADE,

CONSTRAINT tr_drv_AT_FK 
FOREIGN KEY(tr_drv_AT)
REFERENCES driver(drv_AT)
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE event (
ev_tr_id INT(11) NOT NULL AUTO_INCREMENT,
ev_start DATETIME NOT NULL,
ev_end DATETIME NOT NULL,
ev_descr TEXT NOT NULL,

PRIMARY KEY (ev_tr_id, ev_start),

CONSTRAINT ev_tr_id_FK 
FOREIGN KEY(ev_tr_id)
REFERENCES trip(tr_id)
ON UPDATE CASCADE ON DELETE CASCADE
);

-- seatnum can be shared in same tr_id
CREATE TABLE reservation (
res_tr_id int(11) NOT NULL AUTO_INCREMENT,
res_seatnum tinyint(4) NOT NULL,
res_name varchar(20) NOT NULL,
res_lname varchar(20) NOT NULL,
res_isadult enum('ADULT', 'MINOR') NOT NULL,

PRIMARY KEY (res_tr_id, res_seatnum),

CONSTRAINT res_tr_id_FK
FOREIGN KEY (res_tr_id)
REFERENCES trip(tr_id)
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE destination(
dst_id int(11) NOT NULL AUTO_INCREMENT,
dst_name varchar(50) NOT NULL,
dst_descr text NOT NULL,
dst_rtype enum('LOCAL', 'ABROAD') NOT NULL,
dst_language varchar(30) NOT NULL,
dst_location int(11),

PRIMARY KEY(dst_id),

CONSTRAINT dst_loc_FK
FOREIGN KEY (dst_location)
REFERENCES destination(dst_id)
ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE travel_to (
to_tr_id int(11) NOT NULL,
to_dst_id int(11) NOT NULL,
to_arrival datetime NOT NULL,
to_departure datetime NOT NULL,

PRIMARY KEY(to_tr_id, to_dst_id),

CONSTRAINT trv_to_tr_id_FK 
FOREIGN KEY (to_tr_id)
REFERENCES trip(tr_id)
ON UPDATE CASCADE ON DELETE CASCADE,

CONSTRAINT trv_to_dst_id_FK 
FOREIGN KEY (to_dst_id)
REFERENCES destination(dst_id)
ON UPDATE CASCADE ON DELETE CASCADE
);

-- INSERTS

INSERT INTO branch VALUES
(NULL,'Aksiou',4,'Patra'),
(NULL,'Platinos',3,'Patra'),
(NULL,'Kanakari',2,'Patra'),
(NULL,'Gounarh',44,'Patra'),
(NULL,'Giorgou',22,'Patra'),
(NULL,'Spurou',12,'Patra'),
(NULL,'Xartiou',24,'Patra'),
(NULL,'Mantiliou',54,'Thessaloniki'),
(NULL,'Dia',124,'Thessaloniki'),
(NULL,'Riou',22,'Thessaloniki');

INSERT INTO phones VALUES
(1,'2103679080'),
(1,'2106609976'),
(2,'2105813102'),
(2,'2103864592'),
(2,'2105201926'),
(2,'2106081529'),
(3,'2108772622'),
(4,'2105908149'),
(5,'2101017600'),
(5,'2104298896'),
(5,'2105567514'),
(6,'2109819713'),
(7,'2107691209'),
(7,'2106814634'),
(8,'2101812989'),
(8,'2106584920'),
(8,'2100545788'),
(8,'2100955415'),
(8,'2108780556'),
(9,'2100532081'),
(10,'2104623439'),
(10,'2107259204'),
(10,'2100693816'),
(10,'2101355405');


INSERT INTO worker VALUES
-- admins
('84QN23K978','Giorgos','Giorgou',800,1),
('WKL52M4D9G','Xarhs','Magkas',1000,1),
('K6D4WU1HPZ','Spuros','Spurou',800,2),
('1PAAP01ZPW','Spuros','Eutixismenos',1000,2),
('1D4J1ULL6P','Spuros','Mpardakhs',800,3),
('SIY5457UK5','Antwnhs','Mpardakhs',800,4),
('3SKKIXVR5A','Giannis','Xristodoulopoulou',800,5),
('K52Z52HFS8','Thodwrhs','Sorras',1000,5),
('GA3KT18BO6','Giorgos','Villiwths',800,5),
('E2FJ0Z9YN6','Giorgos','Xallas',800,6),
('VJ854CIN0W','Giannis','Villiwths',800,7),
('FJKRL5AOZ9','Giannis','Xallas',800,8),
('3DDHKPFX8L','Giannis','Meraklhs',1000,8),
('CJ896NM1RD','Spuros','Papanikolaou',800,9),
('22NMVDCPLT','Giannis','Psalths',1000,9),
('KW2MKVKQT9','Spuros','Xristodoulopoulou',800,10),
('BBJYF6MIYR','Afroksilanthios','Koutsantwnh',800,10),
-- drivers
('9XJ3TIQUQS','Axilleas','Metras',1000,1),
('S868640HEA','Antwnhs','Voudas',1000,2),
('BTFUL214AU','Giannis','Anestiou',1000,2),
('N84ERH1B1D','Spuros','Papandreou',1000,3),
('7T4R4GUJEH','Vasilhs','Papandreou',1000,3),
('28M3V9OJF5','Vasilhs','Mpakogiannh',1000,3),
('HAK6C32A0L','Giannis','Papandreou',1000,4),
('PNS7SFQMY7','Neoptolemos','Xristou',1000,4),
('FP02ESO9T5','Giorgos','Papathanasiou',1400,5),
('1KJ3W3NEF4','Blessed','Sigmar',1400,6),
('DK9GGY0HY3','Giorgos','Afroksilanthiou',1400,7),
('FVRG67UC7G','Spuros','Fontas',1400,8),
('0PS13UGS7R','Platwnas','Ksenou',1400,9),
('RH31B5HDZ5','Maria','Lampridh',1000,9),
('S88MJCJXOD','Elenh','Lagou',1000,9),
('SCK7F68FHV','Giwrgos','Barwtsos',1000,10),
-- guides
('26EU27BLZ2','Basilikh','Blassh',1000,1),
('L91VRQ11EJ','Sofia','Xristou',1000,1),
('57W862UVH1','Eyaggelia','Nastou',1400,2),
('DU0NPWMWVA','Anastasia','Boutsina',1400,3),
('XSJ73VK013','Eirhnh','Xanthopoulou',1400,4),
('H3Z8OW7P9F','Panagiwta','Panou',1400,4),
('PQ2995IOH5','Panagiwths','Ebert',1400,5),
('BLCNOMFTWA','Athanasios','Kalligas',1000,5),
('WJ8ZWJG6E8','Alexandra','Kaloghrou',1000,6),
('U648POE7T0','Alexandros','Sklavos',1000,7),
('9YWXFK15VB','Athina','Skarbelh',1000,8),
('4K7ICFI2WU','Mixahl','Eleytherianos',1000,9),
('H6AA0HV6QO','Stylianh','Daskalakh',1400,9),
('KC7BJJ2RWA','Andreas','Papakwnstantinou',1400,9),
('NI5S6NTKW6','Apostolos','Athanasiadhs',1400,9),
('XUO90F1U2I','Petros','Anastasiou',1400,10),
('EMNLLE1BAW','Thwmas','Kontoleon',1400,10);


INSERT INTO admin VALUES
('84QN23K978','ADMINISTRATIVE','FUSIKO'),
('WKL52M4D9G','ACCOUNTING','MATHIMATIKO'),
('K6D4WU1HPZ','ADMINISTRATIVE','MATHIMATIKO'),
('1PAAP01ZPW','ACCOUNTING','DIOIKHSH'),
('1D4J1ULL6P','ADMINISTRATIVE','DIOIKHSH'),
('SIY5457UK5','ADMINISTRATIVE','DIOIKHSH'),
('3SKKIXVR5A','ADMINISTRATIVE','DIOIKHSH'),
('K52Z52HFS8','ACCOUNTING','CEID'),
('GA3KT18BO6','LOGISTICS','CEID'),
('E2FJ0Z9YN6','ADMINISTRATIVE','DIOIKHSH'),
('VJ854CIN0W','ADMINISTRATIVE','LOGISTIKH'),
('FJKRL5AOZ9','ADMINISTRATIVE','LOGISTIKH'),
('3DDHKPFX8L','ACCOUNTING','PAPEI'),
('CJ896NM1RD','ADMINISTRATIVE','LOGISTIKH'),
('22NMVDCPLT','LOGISTICS','ARXITEKTWNWN'),
('KW2MKVKQT9','ADMINISTRATIVE','BUSINESS'),
('BBJYF6MIYR','LOGISTICS','WAREHOUSE MANAGEMENT');


INSERT INTO manages VALUES
('84QN23K978',1),
('K6D4WU1HPZ',2),
('1D4J1ULL6P',3),
('SIY5457UK5',4),
('3SKKIXVR5A',5),
('E2FJ0Z9YN6',6),
('VJ854CIN0W',7),
('FJKRL5AOZ9',8),
('CJ896NM1RD',9),
('KW2MKVKQT9',10);


INSERT INTO guide VALUES
('26EU27BLZ2', '19 xronia sthn douleia'),
('L91VRQ11EJ', '3 xronia sthn douleia'),
('DU0NPWMWVA', '4 xronia sthn douleia'),
('57W862UVH1', '11 xronia sthn douleia'),
('XSJ73VK013', '18 xronia sthn douleia'),
('H3Z8OW7P9F', '20 xronia sthn douleia'),
('PQ2995IOH5', '5 xronia sthn douleia'),
('BLCNOMFTWA', '7 xronia sthn douleia'),
('WJ8ZWJG6E8', '20 xronia sthn douleia'),
('U648POE7T0', '6 xronia sthn douleia'),
('9YWXFK15VB', '17 xronia sthn douleia'),
('4K7ICFI2WU', '4 xronia sthn douleia'),
('H6AA0HV6QO', '3 xronia sthn douleia'),
('KC7BJJ2RWA', '24 xronia sthn douleia'),
('NI5S6NTKW6', '5 xronia sthn douleia'),
('XUO90F1U2I', '0 xronia sthn douleia'),
('EMNLLE1BAW', '3 xronia sthn douleia');


INSERT INTO driver VALUES
('9XJ3TIQUQS','B','ABROAD',54),
('S868640HEA','C','ABROAD',119),
('BTFUL214AU','D','ABROAD',118),
('N84ERH1B1D','C','LOCAL',98),
('7T4R4GUJEH','A','LOCAL',91),
('28M3V9OJF5','D','ABROAD',0),
('HAK6C32A0L','D','ABROAD',75),
('PNS7SFQMY7','C','ABROAD',97),
('1KJ3W3NEF4','B','ABROAD',4),
('FP02ESO9T5','D','ABROAD',38),
('DK9GGY0HY3','C','ABROAD',56),
('FVRG67UC7G','A','LOCAL',85),
('0PS13UGS7R','B','LOCAL',98 ),
('RH31B5HDZ5','B','LOCAL',37 ),
('S88MJCJXOD','D','ABROAD',91),
('SCK7F68FHV','C','LOCAL',56 );


INSERT INTO languages VALUES
('26EU27BLZ2', 'Agglika'),
('L91VRQ11EJ', 'Agglika'),
('DU0NPWMWVA', 'Gallika'),
('DU0NPWMWVA', 'Agglika'),
('57W862UVH1', 'Agglika'),
('57W862UVH1', 'Kinezika'),
('XSJ73VK013', 'Agglika'),
('H3Z8OW7P9F', 'Agglika'),
('PQ2995IOH5', 'Agglika'),
('PQ2995IOH5', 'Germanika'),
('BLCNOMFTWA', 'Agglika'),
('WJ8ZWJG6E8', 'Agglika'),
('WJ8ZWJG6E8', 'Italika'),
('U648POE7T0', 'Agglika'),
('9YWXFK15VB', 'Agglika'),
('4K7ICFI2WU', 'Agglika'),
('4K7ICFI2WU', 'Ispanika'),
('H6AA0HV6QO', 'Agglika'),
('KC7BJJ2RWA', 'Agglika'),
('KC7BJJ2RWA', 'Iapwnika'),
('NI5S6NTKW6', 'Agglika'),
('XUO90F1U2I', 'Agglika'),
('EMNLLE1BAW', 'Agglika');


INSERT INTO trip VALUES
(NULL, '2022-05-20', '2022-05-28', 25, 1000, 6,'WJ8ZWJG6E8' ,'1KJ3W3NEF4'),
(NULL, '2022-01-13', '2022-01-20', 15, 1133, 5,'PQ2995IOH5' ,'FP02ESO9T5'),
(NULL, '2022-06-06', '2022-06-16', 20, 1200, 10,'EMNLLE1BAW','SCK7F68FHV'),
(NULL, '2022-12-15', '2022-12-20', 15, 875, 9, 'KC7BJJ2RWA' ,'RH31B5HDZ5'),
(NULL, '2023-11-17', '2023-11-30', 30, 2500, 1,'26EU27BLZ2' ,'9XJ3TIQUQS'),
(NULL, '2022-03-01', '2022-03-15', 40, 3320, 2,'57W862UVH1' ,'S868640HEA'),
(NULL, '2022-06-08', '2022-06-12', 15, 770, 8, '9YWXFK15VB' ,'FVRG67UC7G'),
(NULL, '2022-07-22', '2022-07-25', 12, 335, 4, 'XSJ73VK013' ,'PNS7SFQMY7'),
(NULL, '2023-11-29', '2023-12-02', 35, 175, 10,'XUO90F1U2I' ,'SCK7F68FHV'),
(NULL, '2022-04-27', '2022-05-02', 27, 220, 9, 'KC7BJJ2RWA' ,'RH31B5HDZ5'),
(NULL, '2023-05-20', '2023-05-28', 25, 1000, 7,'U648POE7T0' ,'DK9GGY0HY3'),
(NULL, '2023-01-13', '2023-01-20', 15, 1133, 1,'26EU27BLZ2' ,'9XJ3TIQUQS'),
(NULL, '2023-06-06', '2023-06-16', 20, 1200, 7,'U648POE7T0' ,'DK9GGY0HY3'),
(NULL, '2023-12-15', '2023-12-20', 15, 875, 6, 'WJ8ZWJG6E8' ,'1KJ3W3NEF4'),
(NULL, '2021-11-17', '2021-11-30', 30, 2500, 9,'KC7BJJ2RWA' ,'RH31B5HDZ5'),
(NULL, '2023-03-01', '2023-03-15', 40, 3320, 3,'DU0NPWMWVA' ,'N84ERH1B1D'),
(NULL, '2023-06-08', '2023-06-12', 15, 770, 6, 'WJ8ZWJG6E8' ,'1KJ3W3NEF4'),
(NULL, '2023-07-22', '2023-07-25', 12, 335, 5, 'PQ2995IOH5' ,'FP02ESO9T5'),
(NULL, '2021-11-29', '2021-12-02', 35, 175, 10,'XUO90F1U2I' ,'SCK7F68FHV'),
(NULL, '2023-04-27', '2023-05-02', 27, 220, 7, 'U648POE7T0' ,'DK9GGY0HY3'),
(NULL, '2021-05-20', '2021-05-28', 25, 1000, 8,'9YWXFK15VB' ,'FVRG67UC7G'),
(NULL, '2021-01-13', '2021-01-20', 15, 1133, 6,'WJ8ZWJG6E8' ,'1KJ3W3NEF4'),
(NULL, '2021-06-06', '2021-06-16', 20, 1200, 5,'PQ2995IOH5' ,'FP02ESO9T5'),
(NULL, '2021-12-15', '2021-12-20', 15, 875, 5, 'BLCNOMFTWA' ,'FP02ESO9T5'),
(NULL, '2024-11-17', '2024-11-30', 30, 2500, 10,'XUO90F1U2I','SCK7F68FHV'),
(NULL, '2021-03-01', '2021-03-15', 40, 3320, 10,'EMNLLE1BAW','SCK7F68FHV'),
(NULL, '2021-06-08', '2021-06-12', 15, 770, 5, 'PQ2995IOH5' ,'FP02ESO9T5'),
(NULL, '2021-07-22', '2021-07-25', 12, 335, 7, 'U648POE7T0' ,'DK9GGY0HY3'),
(NULL, '2024-11-29', '2024-12-02', 35, 175, 9,'NI5S6NTKW6'  ,'0PS13UGS7R'),
(NULL, '2021-04-27', '2021-05-02', 27, 220, 9, 'NI5S6NTKW6' ,'0PS13UGS7R');


INSERT INTO event VALUES
(1, '2022-05-20', '2022-05-25', 'tsagaki'),
(1, '2022-05-25', '2022-05-26', 'vouno'),
(1, '2022-05-26', '2022-05-28', 'ski'),
(2, '2022-01-13', '2022-01-14', 'epiviwsh sthn fysh'),
(2, '2022-01-14', '2022-01-17', 'live metallica'),
(2, '2022-01-17', '2022-01-20', 'sauna'),
(3, '2022-06-06', '2022-06-07', 'make-a-wish'),
(3, '2022-06-09', '2022-06-15', 'akrivo estiatorio'),
(3, '2022-06-15', '2022-06-16', 'oxi toso akrivo estiatorio'),
(4, '2022-12-15', '2022-12-16', 'polemontas elf'),
(4, '2022-12-17', '2022-12-18', 'dnd session'),
(4, '2022-12-18', '2022-12-20', 'epistrofh sthn pragmatikothta'),
(5, '2023-11-17', '2023-11-18', '50cent live'),
(5, '2023-11-19', '2023-11-22', 'argyros live'),
(5, '2023-11-23', '2023-11-25', 'telos ta live, tyropita'),
(5, '2023-11-25', '2023-11-30', 'tyropita KAI tsai'),
(6, '2022-03-01', '2022-03-05', 'kalosorisma'),
(6, '2022-03-07', '2022-03-09', 'anavash sto everest'),
(6, '2022-03-10', '2022-03-12', 'nerotsoulithres'),
(6, '2022-03-12', '2022-03-15', 'exodos'),
(7, '2022-06-08', '2022-06-09', 'thalassa'),
(7, '2022-06-09', '2022-06-10', 'taverna'),
(7, '2022-06-10', '2022-06-11', 'kai allh taverna'),
(7, '2022-06-11', '2022-06-12', 'ksekourash sto aerodromio'),
(8, '2022-07-22', '2022-07-23', 'ypodoxh, check-in'),
(8, '2022-07-23', '2022-07-25', 'aksiotheata'),
(9, '2023-11-29', '2023-12-01', 'ki alla aksiotheata'),
(9, '2023-12-01', '2023-12-02', 'ksekourash prin thn epistrofh'),
(10,'2022-04-27', '2022-04-29', 'kalosorisma'),
(10,'2022-04-29', '2022-05-02', 'zwologikos khpos'),
(11,'2023-05-20', '2023-05-25', 'tsagaki'),
(11,'2023-05-25', '2023-05-26', 'vouno'),
(11,'2023-05-26', '2023-05-28', 'ski'),
(12,'2023-01-13', '2023-01-14', 'epiviwsh sthn fysh'),
(12,'2023-01-14', '2023-01-17', 'live metallica'),
(12,'2023-01-17', '2023-01-20', 'sauna'),
(13,'2023-06-06', '2023-06-07', 'make-a-wish'),
(13,'2023-06-09', '2023-06-15', 'akrivo estiatorio'),
(13,'2023-06-15', '2023-06-16', 'oxi toso akrivo estiatorio'),
(14,'2023-12-15', '2023-12-16', 'polemontas elf'),
(14,'2023-12-17', '2023-12-18', 'dnd session'),
(14,'2023-12-18', '2023-12-20', 'epistrofh sthn pragmatikothta'),
(15,'2021-11-17', '2021-11-18', '50cent live'),
(15,'2021-11-19', '2021-11-22', 'argyros live'),
(15,'2021-11-23', '2021-11-25', 'telos ta live, tyropita'),
(15,'2021-11-25', '2021-11-30', 'tyropita KAI tsai'),
(16,'2023-03-01', '2023-03-05', 'kalosorisma'),
(16,'2023-03-07', '2023-03-09', 'anavash sto everest'),
(16,'2023-03-10', '2023-03-12', 'nerotsoulithres'),
(16,'2023-03-12', '2023-03-15', 'exodos'),
(17,'2023-06-08', '2023-06-09', 'thalassa'),
(17,'2023-06-09', '2023-06-10', 'taverna'),
(17,'2023-06-10', '2023-06-11', 'kai allh taverna'),
(17,'2023-06-11', '2023-06-12', 'ksekourash sto aerodromio'),
(18,'2023-07-22', '2023-07-23', 'ypodoxh, check-in'),
(18,'2023-07-23', '2023-07-25', 'aksiotheata'),
(19,'2021-11-29', '2021-12-01', 'ki alla aksiotheata'),
(19,'2021-12-01', '2021-12-02', 'ksekourash prin thn epistrofh'),
(20,'2023-04-27', '2023-04-29', 'kalosorisma'),
(20,'2023-04-29', '2023-05-02', 'zwologikos khpos'),
(21,'2021-05-20', '2021-05-25', 'tsagaki'),
(21,'2021-05-25', '2021-05-26', 'vouno'),
(21,'2021-05-26', '2021-05-28', 'ski'),
(22,'2021-01-13', '2021-01-14', 'epiviwsh sthn fysh'),
(22,'2021-01-14', '2021-01-17', 'live metallica'),
(22,'2021-01-17', '2021-01-20', 'sauna'),
(23,'2021-06-06', '2021-06-07', 'make-a-wish'),
(23,'2021-06-09', '2021-06-15', 'akrivo estiatorio'),
(23,'2021-06-15', '2021-06-16', 'oxi toso akrivo estiatorio'),
(24,'2021-12-15', '2021-12-16', 'polemontas elf'),
(24,'2021-12-17', '2021-12-18', 'dnd session'),
(24,'2021-12-18', '2021-12-20', 'epistrofh sthn pragmatikothta'),
(25,'2024-11-17', '2024-11-18', '50cent live'),
(25,'2024-11-19', '2024-11-22', 'argyros live'),
(25,'2024-11-23', '2024-11-25', 'telos ta live, tyropita'),
(25,'2024-11-25', '2024-11-30', 'tyropita KAI tsai'),
(26,'2021-03-01', '2021-03-05', 'kalosorisma'),
(26,'2021-03-07', '2021-03-09', 'anavash sto everest'),
(26,'2021-03-10', '2021-03-12', 'nerotsoulithres'),
(26,'2021-03-12', '2021-03-15', 'exodos'),
(27,'2021-06-08', '2021-06-09', 'thalassa'),
(27,'2021-06-09', '2021-06-10', 'taverna'),
(27,'2021-06-10', '2021-06-11', 'kai allh taverna'),
(27,'2021-06-11', '2021-06-12', 'ksekourash sto aerodromio'),
(28,'2021-07-22', '2021-07-23', 'ypodoxh, check-in'),
(28,'2021-07-23', '2021-07-25', 'aksiotheata'),
(29,'2024-11-29', '2024-12-01', 'ki alla aksiotheata'),
(29,'2024-12-01', '2024-12-02', 'ksekourash prin thn epistrofh'),
(30,'2021-04-27', '2021-04-29', 'kalosorisma'),
(30,'2021-04-29', '2021-05-02', 'zwologikos khpos');

INSERT INTO reservation VALUES
(1, 23, 'Giannis', 'Aggelidis', 'ADULT'),
(1, 22, 'Giannis', 'Aggelidis', 'ADULT'),
(1, 21, 'Giannis', 'Aggelidis', 'ADULT'),
(1, 20, 'Giannis', 'Aggelidis', 'ADULT'),
(1, 10, 'Thanos', 'Mylwnas', 'ADULT'),
(1, 9, 'Giannhs', 'Mylwnas', 'MINOR'),
(2, 15, 'Markos', 'Loberdos', 'ADULT'),
(2, 14, 'Markos', 'Loberdos', 'ADULT'),
(2, 13, 'Markos', 'Loberdos', 'ADULT'),
(2, 9, 'Barbara', 'Stathopoulou', 'ADULT'),
(2, 7, 'Barbara', 'Stathopoulou', 'ADULT'),
(2, 10, 'Barbara', 'Iremh', 'ADULT'),
(3, 20, 'Dhmhtrhs', 'Bergas', 'MINOR'),
(3, 19, 'Giannhs', 'Tsapas', 'MINOR'),
(3, 18, 'Eirhnh', 'Mpezou', 'ADULT'),
(3, 11, 'Dhmhtra', 'Karavia', 'ADULT'),
(4, 15, 'Makhs', 'Mamos', 'ADULT'),
(5, 30, 'Anastashs', 'Boulgarhs', 'ADULT'),
(5, 19, 'Grhgorhs', 'Grhgoriou', 'ADULT'),
(5, 09, 'Anna', 'Gennhmata', 'ADULT'),
(6, 40, 'Giannis', 'Markopoulos', 'ADULT'),
(7, 12, 'Takhs', 'Palamas', 'ADULT'),
(7, 08, 'Giannis', 'Mamos', 'ADULT'),
(8, 08, 'Elenh', 'Mosxou', 'ADULT'),
(8, 4, 'Xaralampos', 'Kexagias', 'ADULT'),
(9, 10, 'Maria', 'Panou', 'ADULT'),
(10, 09, 'Giannis', 'Samaras', 'ADULT'),
(10, 12, 'Anastasia', 'Gewrgiou', 'ADULT'),
(10, 11, 'Anastasis', 'Nirvanas', 'ADULT'),
(10, 5, 'Super', 'Marios', 'ADULT'),
(10, 6, 'Super', 'Luigis', 'ADULT');


INSERT INTO destination VALUES
(null, 'Gallia', 'Xwra ths dutikhs Eurwphs', 'ABROAD', 'Gallika', null),
(null, 'Parisi', 'Romantikh polh', 'ABROAD', 'Gallika', 1),
(null, 'Ellada', 'H Ellada', 'LOCAL', 'Ellhnika', null),
(null, 'Salamina', 'Tapeino xorioudaki', 'LOCAL', 'Ellhnika', 3),
(null, 'Dubai', 'Plousia polh', 'ABROAD', 'Ellhnika', null),
(null, 'Tokyo', 'Polh ths iapwnias me jdm amaksia', 'ABROAD', 'Iapwnika', null),
(null, 'Norbhgia', 'Polu krua', 'ABROAD', 'Norbhgika', null),
(null, 'Mosxa', 'Polu krua', 'ABROAD', 'Rwssika', null),
(null, 'Benetia', 'Wraia polh me kanalia', 'ABROAD', 'Italika', null),
(null, 'Florida', 'Polh Amerikhs pou exei disneyland', 'ABROAD', 'Agglika', null),
(null, 'Boulgaria', 'Fthina emporika kentra', 'ABROAD', 'Boulgarika', null),
(null, 'Notia afrikh', 'Tropiko klima', 'ABROAD', 'Gallika', null),
(null, 'Elbetia', 'Wraia xwra me CERN', 'ABROAD', 'Elbetika', null),
(null, 'Athina', 'H prwteuousa ths Elladas', 'LOCAL', 'Ellhnika', 3),
(null, 'Kwstantinoupolh', 'Istorikh polh', 'ABROAD', 'Tourkika', null),
(null, 'Iraq', 'Empolemh zwnh', 'ABROAD', 'Arabika', null),
(null, 'Kina', 'Megalh xwra me wraia bouna', 'ABROAD', 'Kinezika', null),
(null, 'Finlandia', 'Polu krua', 'ABROAD', 'Finlandika', null),
(null, 'Fantastikos Komsmos', 'Den yparxei', 'ABROAD', 'Ola', null),
(null, 'Boreia korea', 'Hremh xwra', 'ABROAD', 'Koreatika', 19),
(null, 'Mordor', 'Wraia xwra me zesto klima', 'ABROAD', 'Orkis', 19),
(null, 'Tatooine', 'Polu zesto klima', 'ABROAD', 'Xoutetzika', 19),
(null, 'Death star', 'Polu makria', 'ABROAD', 'Agglika', 19),
(null, 'Roumania', 'Xwra pou menei o top G', 'ABROAD', 'Roumanika', null),
(null, 'Antarktikh', 'Foreste mpoufan', 'ABROAD', 'Agglika', null),
(null, 'Patra', 'H trith megalyterh polh ths elladws', 'LOCAL', 'Ellhnika', 3),
(null, 'Thessaloniki', 'H voreia polh', 'LOCAL', 'Ellhnika', 3),
(null, 'Volos', 'Exei pollous foithtes', 'LOCAL', 'Ellhnika', 3),
(null, 'Ios', 'Megalo Nhsi', 'LOCAL', 'Ellhnika', 3),
(null, 'Krhth', 'H gnwsth se olous krhth', 'LOCAL', 'Ellhnika', 3);


INSERT INTO travel_to VALUES
(1, 17,'2022-05-20', '2022-05-28'),
(2, 25,'2022-01-13', '2022-01-20'),
(3, 26,'2022-06-06', '2022-06-16'),
(4, 4,'2022-12-15', '2022-12-17'),
(4, 14,'2022-12-17', '2022-12-18'),
(4, 27,'2022-12-18', '2022-12-20'),
(5, 18,'2023-11-17', '2023-11-30'),
(6, 23,'2022-03-01', '2022-03-15'),
(7, 14,'2022-06-08', '2022-06-12'),
(8, 3,'2022-07-22', '2022-07-23'),
(8, 20,'2022-07-23', '2022-07-25'),
(9, 14,'2023-11-29', '2023-12-02'),
(10,4, '2022-04-27', '2022-05-02'),
(11,5, '2023-05-20', '2023-05-28'),
(12,7, '2023-01-13', '2023-01-20'),
(13,21, '2023-06-06', '2023-06-13'),
(13,2, '2023-06-13', '2023-06-16'),
(14,22, '2023-12-15', '2023-12-20'),
(15,27, '2021-11-17', '2021-11-30'),
(16,29, '2023-03-01', '2023-03-15'),
(17,23, '2023-06-08', '2023-06-12'),
(18,24, '2023-07-22', '2023-07-25'),
(19,30, '2021-11-29', '2021-12-02'),
(20,6, '2023-04-27', '2023-05-02'),
(21,4, '2021-05-20', '2021-05-28'),
(22,8, '2021-01-13', '2021-01-20'),
(23,2, '2021-06-06', '2021-06-13'),
(23,16, '2021-06-13', '2021-06-16'),
(24,2, '2021-12-15', '2021-12-20'),
(25,26, '2024-11-17', '2024-11-30'),
(26,4, '2021-03-01', '2021-03-15'),
(27,15, '2021-06-08', '2021-06-12'),
(28,16, '2021-07-22', '2021-07-25'),
(29,27, '2024-11-29', '2024-12-02'),
(30,4, '2021-04-27', '2021-05-02'),
(30,14, '2021-04-01', '2021-05-02');
