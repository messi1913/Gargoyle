

-- meerkat 의 데이터베이스 구조 덤핑

CREATE schema if not exists meerkat;


-- 테이블 meerkat의 구조를 덤프합니다. tbm_sys_dao
CREATE TABLE IF NOT EXISTS meerkat.tbm_sys_dao (
  PACKAGE_NAME varchar(200) NOT NULL,
  CLASS_NAME varchar(50) NOT NULL,
  LOCATION varchar(255) DEFAULT NULL,
  CLASS_DESC mediumtext,
  TABLE_NAME varchar(50) DEFAULT NULL,
  PRIMARY KEY (PACKAGE_NAME,CLASS_NAME)
);


CREATE TABLE IF NOT EXISTS meerkat.tbp_sys_dao_columns (
  PACKAGE_NAME varchar(200) NOT NULL,
  CLASS_NAME varchar(50) NOT NULL,
  METHOD_NAME varchar(50) NOT NULL,
  COLUMN_NAME varchar(50) NOT NULL,
  COLUMN_TYPE varchar(50) DEFAULT NULL,
  PROGRAM_TYPE VARCHAR(30) DEFAULT NULL,
   LOCK_YN VARCHAR(1) DEFAULT 'N',
  PRIMARY KEY (PACKAGE_NAME,CLASS_NAME,METHOD_NAME,COLUMN_NAME)
);


CREATE TABLE IF NOT EXISTS meerkat.tbp_sys_dao_fields (
  PACKAGE_NAME varchar(200) NOT NULL,
  CLASS_NAME varchar(50) NOT NULL,
  METHOD_NAME varchar(50) NOT NULL,
  FIELD_NAME varchar(50) NOT NULL,
  TEST_VALUE varchar(50) DEFAULT NULL,
  TYPE varchar(50) DEFAULT NULL,
  PRIMARY KEY (PACKAGE_NAME,CLASS_NAME,METHOD_NAME,FIELD_NAME)
) ;

CREATE TABLE IF NOT EXISTS meerkat.tbp_sys_dao_methods (
  PACKAGE_NAME varchar(200) NOT NULL,
  CLASS_NAME varchar(50) NOT NULL,
  METHOD_NAME varchar(50) NOT NULL,
  RESULT_VO_CLASS varchar(100) DEFAULT NULL,
  SQL_BODY BLOB,
  METHOD_DESC BLOB,
  PRIMARY KEY (PACKAGE_NAME,CLASS_NAME,METHOD_NAME)
) ;



CREATE TABLE IF NOT EXISTS meerkat.tbp_sys_dao_methods_h (
	HIST_TSP VARCHAR(33) NOT NULL,
	PACKAGE_NAME VARCHAR(200) NOT NULL,
	CLASS_NAME VARCHAR(50) NOT NULL,
	METHOD_NAME VARCHAR(50) NOT NULL,
	RESULT_VO_CLASS VARCHAR(100) NULL DEFAULT NULL,
	SQL_BODY BLOB NULL,
	METHOD_DESC BLOB NULL,
	DML_TYPE CHAR(1) NOT NULL,
	FST_REG_DT DATE DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (HIST_TSP, PACKAGE_NAME, CLASS_NAME, METHOD_NAME)
);
