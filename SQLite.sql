

DROP TABLE IF EXISTS t_classification
;

DROP TABLE IF EXISTS t_cost
;

DROP TABLE IF EXISTS t_income
;

DROP TABLE IF EXISTS t_user
;


CREATE TABLE t_classification
(
	classify_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	classify_name VARCHAR(255) not NULL,
	is_use TINYINT NULL DEFAULT 0,
	type TINYINT NULL
)
;

CREATE TABLE t_cost
(
	cost_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	cost NUMERIC NULL,
	cost_time VARCHAR(19) NULL,
	classify_id TINYINT NULL,
	user_id TINYINT NULL,
	CONSTRAINT FK_t_cost_t_classification FOREIGN KEY (classify_id) REFERENCES t_classification (classify_id) ON DELETE No Action ON UPDATE No Action,
	CONSTRAINT FK_t_cost_t_user FOREIGN KEY (user_id) REFERENCES t_user (user_id) ON DELETE No Action ON UPDATE No Action
)
;

CREATE TABLE t_income
(
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	amount NUMERIC NULL,
	income_time VARCHAR(19) not NULL,
	classify_id INTEGER not NULL,
	user_id INTEGER not NULL,
	CONSTRAINT FK_t_income_t_classification FOREIGN KEY (classify_id) REFERENCES t_classification (classify_id) ON DELETE No Action ON UPDATE No Action,
	CONSTRAINT FK_t_income_t_user FOREIGN KEY (user_id) REFERENCES t_user (user_id) ON DELETE No Action ON UPDATE No Action
)
;


CREATE TABLE t_user
(
	user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	account VARCHAR(30) not NULL,
	username VARCHAR(40) not NULL,
	password VARCHAR(20) not NULL
)
;



CREATE INDEX IXFK_t_cost_t_classification
 ON t_cost (classify_id ASC)
;

CREATE INDEX IXFK_t_cost_t_user
 ON t_cost (user_id ASC)
;

CREATE INDEX IXFK_t_income_t_classification
 ON t_income (classify_id ASC)
;

CREATE INDEX IXFK_t_income_t_user
 ON t_income (user_id ASC)
;
