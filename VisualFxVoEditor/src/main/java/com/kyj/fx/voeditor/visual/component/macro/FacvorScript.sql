##mariadb

CREATE TABLE `tbp_sys_dao_columns` (
	`PACKAGE_NAME` VARCHAR(200) NOT NULL,
	`CLASS_NAME` VARCHAR(50) NOT NULL,
	`METHOD_NAME` VARCHAR(50) NOT NULL,
	`COLUMN_NAME` VARCHAR(50) NOT NULL,
	`COLUMN_TYPE` VARCHAR(50) NULL DEFAULT NULL,
	`PROGRAM_TYPE` VARCHAR(30) NULL DEFAULT NULL,
	`LOCK_YN` VARCHAR(1) NULL DEFAULT NULL,
	PRIMARY KEY (`PACKAGE_NAME`, `CLASS_NAME`, `METHOD_NAME`, `COLUMN_NAME`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;


INSERT INTO `tbm_sys_sql_magt` (`id`, `parent_id`, `type`, `name`, `content`) VALUES ('1', NULL, 'D', '학교', NULL);
INSERT INTO `tbm_sys_sql_magt` (`id`, `parent_id`, `type`, `name`, `content`) VALUES ('2', '1', 'F', 'HTML5', 'ㅋㅋㅋ');
INSERT INTO `tbm_sys_sql_magt` (`id`, `parent_id`, `type`, `name`, `content`) VALUES ('3', '1', 'F', '빅데이터', 'ㅇㅇ');
INSERT INTO `tbm_sys_sql_magt` (`id`, `parent_id`, `type`, `name`, `content`) VALUES ('4', NULL, 'D', '회사', NULL);
INSERT INTO `tbm_sys_sql_magt` (`id`, `parent_id`, `type`, `name`, `content`) VALUES ('5', NULL, 'D', '연구회', NULL);
INSERT INTO `tbm_sys_sql_magt` (`id`, `parent_id`, `type`, `name`, `content`) VALUES ('6', '5', 'D', '동적비즈니스', NULL);
INSERT INTO `tbm_sys_sql_magt` (`id`, `parent_id`, `type`, `name`, `content`) VALUES ('7', '6', 'D', '20160904', NULL);

