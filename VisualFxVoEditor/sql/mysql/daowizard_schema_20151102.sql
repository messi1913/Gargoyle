-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.0.17-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  9.1.0.4867
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- meerkat 의 데이터베이스 구조 덤핑
CREATE DATABASE IF NOT EXISTS `meerkat` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `meerkat`;


-- 테이블 meerkat의 구조를 덤프합니다. tbm_sys_dao
CREATE TABLE IF NOT EXISTS `tbm_sys_dao` (
  `PACKAGE_NAME` varchar(200) NOT NULL,
  `CLASS_NAME` varchar(50) NOT NULL,
  `LOCATION` varchar(255) DEFAULT NULL,
  `CLASS_DESC` mediumtext,
  `TABLE_NAME` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`PACKAGE_NAME`,`CLASS_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table meerkat.tbm_sys_dao: ~1 rows (대략적)
DELETE FROM `tbm_sys_dao`;
/*!40000 ALTER TABLE `tbm_sys_dao` DISABLE KEYS */;


-- 테이블 meerkat의 구조를 덤프합니다. tbp_sys_dao_columns
CREATE TABLE IF NOT EXISTS `tbp_sys_dao_columns` (
  `PACKAGE_NAME` varchar(200) NOT NULL,
  `CLASS_NAME` varchar(50) NOT NULL,
  `METHOD_NAME` varchar(50) NOT NULL,
  `COLUMN_NAME` varchar(50) NOT NULL,
  `COLUMN_TYPE` varchar(50) DEFAULT NULL,
  `PROGRAM_TYPE` varchar(30) DEFAULT NULL,
  `LOCK_YN` varchar(1) DEFAULT 'N',
  PRIMARY KEY (`PACKAGE_NAME`,`CLASS_NAME`,`METHOD_NAME`,`COLUMN_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table meerkat.tbp_sys_dao_columns: ~16 rows (대략적)
DELETE FROM `tbp_sys_dao_columns`;
/*!40000 ALTER TABLE `tbp_sys_dao_columns` DISABLE KEYS */;


-- 테이블 meerkat의 구조를 덤프합니다. tbp_sys_dao_fields
CREATE TABLE IF NOT EXISTS `tbp_sys_dao_fields` (
  `PACKAGE_NAME` varchar(200) NOT NULL,
  `CLASS_NAME` varchar(50) NOT NULL,
  `METHOD_NAME` varchar(50) NOT NULL,
  `FIELD_NAME` varchar(50) NOT NULL,
  `TEST_VALUE` varchar(50) DEFAULT NULL,
  `TYPE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`PACKAGE_NAME`,`CLASS_NAME`,`METHOD_NAME`,`FIELD_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table meerkat.tbp_sys_dao_fields: ~16 rows (대략적)
DELETE FROM `tbp_sys_dao_fields`;
/*!40000 ALTER TABLE `tbp_sys_dao_fields` DISABLE KEYS */;

-- 테이블 meerkat의 구조를 덤프합니다. tbp_sys_dao_methods
CREATE TABLE IF NOT EXISTS `tbp_sys_dao_methods` (
  `PACKAGE_NAME` varchar(200) NOT NULL,
  `CLASS_NAME` varchar(50) NOT NULL,
  `METHOD_NAME` varchar(50) NOT NULL,
  `RESULT_VO_CLASS` varchar(100) DEFAULT NULL,
  `SQL_BODY` mediumtext,
  `METHOD_DESC` mediumtext,
  PRIMARY KEY (`PACKAGE_NAME`,`CLASS_NAME`,`METHOD_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table meerkat.tbp_sys_dao_methods: ~4 rows (대략적)
DELETE FROM `tbp_sys_dao_methods`;
/*!40000 ALTER TABLE `tbp_sys_dao_methods` DISABLE KEYS */;


-- 테이블 meerkat의 구조를 덤프합니다. tbp_sys_dao_methods_h
CREATE TABLE IF NOT EXISTS `tbp_sys_dao_methods_h` (
	`HIST_TSP` VARCHAR(33) NOT NULL,
	`PACKAGE_NAME` VARCHAR(200) NOT NULL,
	`CLASS_NAME` VARCHAR(50) NOT NULL,
	`METHOD_NAME` VARCHAR(50) NOT NULL,
	`RESULT_VO_CLASS` VARCHAR(100) NULL DEFAULT NULL,
	`SQL_BODY` MEDIUMTEXT NULL,
	`METHOD_DESC` MEDIUMTEXT NULL,
	`DML_TYPE` CHAR(1) NOT NULL,
	`FST_REG_DT` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`HIST_TSP`, `PACKAGE_NAME`, `CLASS_NAME`, `METHOD_NAME`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
