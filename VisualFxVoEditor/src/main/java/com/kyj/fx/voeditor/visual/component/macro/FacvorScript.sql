

CREATE TABLE MEERKAT.TBM_SYS_FAV_MACRO_SQL (
	MACRO_ID VARCHAR(50) NOT NULL,
	PARENT_MACRO_ID VARCHAR(50) DEFAULT NULL,
	MACRO_NAME VARCHAR(505) NOT NULL,
	MACRO_ITEM_TYPE VARCHAR(50) NOT NULL DEFAULT 'UNKNOWN',
	CONTENT TEXT ,
	PRIMARY KEY (MACRO_ID)
);

INSERT INTO MEERKAT.TBM_SYS_FAV_MACRO_SQL (MACRO_ID, PARENT_MACRO_ID, MACRO_ITEM_TYPE, MACRO_NAME, CONTENT) VALUES ('1', NULL, 'D', '학교', NULL);
INSERT INTO MEERKAT.TBM_SYS_FAV_MACRO_SQL (MACRO_ID, PARENT_MACRO_ID, MACRO_ITEM_TYPE, MACRO_NAME, CONTENT) VALUES ('2', '1', 'F', 'HTML5', 'ㅋㅋㅋ');
INSERT INTO MEERKAT.TBM_SYS_FAV_MACRO_SQL (MACRO_ID, PARENT_MACRO_ID, MACRO_ITEM_TYPE, MACRO_NAME, CONTENT) VALUES ('3', '1', 'F', '빅데이터', 'ㅇㅇ');
INSERT INTO MEERKAT.TBM_SYS_FAV_MACRO_SQL (MACRO_ID, PARENT_MACRO_ID, MACRO_ITEM_TYPE, MACRO_NAME, CONTENT) VALUES ('4', NULL, 'D', '회사', NULL);
INSERT INTO MEERKAT.TBM_SYS_FAV_MACRO_SQL (MACRO_ID, PARENT_MACRO_ID, MACRO_ITEM_TYPE, MACRO_NAME, CONTENT) VALUES ('5', NULL, 'D', '연구회', NULL);
INSERT INTO MEERKAT.TBM_SYS_FAV_MACRO_SQL (MACRO_ID, PARENT_MACRO_ID, MACRO_ITEM_TYPE, MACRO_NAME, CONTENT) VALUES ('6', '5', 'D', '동적비즈니스', NULL);
INSERT INTO MEERKAT.TBM_SYS_FAV_MACRO_SQL (MACRO_ID, PARENT_MACRO_ID, MACRO_ITEM_TYPE, MACRO_NAME, CONTENT) VALUES ('7', '6', 'D', '20160904', NULL);

